package kr.wrightbrothers.framework.support.transaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.transaction.*;

import java.util.*;

import static java.util.Arrays.asList;

public class WBMultiTransactionManager implements PlatformTransactionManager {

	private final static Log logger = LogFactory.getLog(WBMultiTransactionManager.class);

	private final List<PlatformTransactionManager> transactionManagers;
	private final WBSynchronizationManager synchronizationManager;

	public WBMultiTransactionManager(PlatformTransactionManager... transactionManagers) {
		this(WBTransactionSynchronizationManager.INSTANCE, transactionManagers);
	}

	WBMultiTransactionManager(WBSynchronizationManager synchronizationManager, PlatformTransactionManager... transactionManagers) {
		this.synchronizationManager = synchronizationManager;
		this.transactionManagers = asList(transactionManagers);
	}

	public WBMultiTransactionStatus getTransaction(@Nullable TransactionDefinition definition) throws TransactionException {

		WBMultiTransactionStatus mts = new WBMultiTransactionStatus(transactionManagers.get(0));

		if (definition == null)
			return mts;

		if (!synchronizationManager.isSynchronizationActive()) {
			synchronizationManager.initSynchronization();
			mts.setNewSynchonization();
		}

		try {
			for (PlatformTransactionManager transactionManager : transactionManagers)
				mts.registerTransactionManager(definition, transactionManager);
			
		} catch (Exception ex) {
			Map<PlatformTransactionManager, TransactionStatus> transactionStatuses = mts.getTransactionStatuses();
			for (PlatformTransactionManager transactionManager : transactionManagers) {
				try {
					if (transactionStatuses.get(transactionManager) != null)
						transactionManager.rollback(transactionStatuses.get(transactionManager));
					
				} catch (Exception e) {
					logger.error("Rollback exception (" + transactionManager + ") " + e.getMessage(), e);
				}
			}
			if (mts.isNewSynchonization())
				synchronizationManager.clearSynchronization();
			
			throw new CannotCreateTransactionException(ex.getMessage(), ex);
		}
		return mts;
	}

	public void commit(TransactionStatus status) throws TransactionException {

		WBMultiTransactionStatus multiTransactionStatus = (WBMultiTransactionStatus) status;

		boolean commit = true;
		Exception commitException = null;
		PlatformTransactionManager commitExceptionTransactionManager = null;

		for (PlatformTransactionManager transactionManager : reverse(transactionManagers)) {
			if (commit) {
				try {
					multiTransactionStatus.commit(transactionManager);
				} catch (Exception ex) {
					commit = false;
					commitException = ex;
					commitExceptionTransactionManager = transactionManager;
				}
			} else {
				try {
					multiTransactionStatus.rollback(transactionManager);
				} catch (Exception ex) {
					logger.error("Rollback exception (after commit) (" + transactionManager + ") " + ex.getMessage(), ex);
				}
			}
		}

		if (multiTransactionStatus.isNewSynchonization())
			synchronizationManager.clearSynchronization();

		if (commitException != null) {
			boolean firstTransactionManagerFailed = commitExceptionTransactionManager == getLastTransactionManager();
			int transactionState = firstTransactionManagerFailed ? HeuristicCompletionException.STATE_ROLLED_BACK : HeuristicCompletionException.STATE_MIXED;
			throw new HeuristicCompletionException(transactionState, commitException);
		}
	}

	public void rollback(TransactionStatus status) throws TransactionException {
		Exception rollbackException = null;
		WBMultiTransactionStatus multiTransactionStatus = (WBMultiTransactionStatus) status;
		for (PlatformTransactionManager transactionManager : reverse(transactionManagers)) {
			try {
				multiTransactionStatus.rollback(transactionManager);
			} catch (Exception ex) {
				if (rollbackException == null) 
					rollbackException = ex;
				else
					logger.error("Rollback exception (" + transactionManager + ") " + ex.getMessage(), ex);
			}
		}
		if (multiTransactionStatus.isNewSynchonization())
			synchronizationManager.clearSynchronization();
	}

	private <T> Iterable<T> reverse(Collection<T> collection) {
		List<T> list = new ArrayList<>(collection);
		Collections.reverse(list);
		return list;
	}

	private PlatformTransactionManager getLastTransactionManager() {
		return transactionManagers.get(lastTransactionManagerIndex());
	}

	private int lastTransactionManagerIndex() {
		return transactionManagers.size() - 1;
	}
}
