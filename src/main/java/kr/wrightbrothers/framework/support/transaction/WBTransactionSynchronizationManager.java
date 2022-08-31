package kr.wrightbrothers.framework.support.transaction;

import org.springframework.transaction.support.TransactionSynchronizationManager;

enum WBTransactionSynchronizationManager implements WBSynchronizationManager {

	INSTANCE;

	public void initSynchronization() {
		TransactionSynchronizationManager.initSynchronization();
	}

	public boolean isSynchronizationActive() {
		return TransactionSynchronizationManager.isSynchronizationActive();
	}

	public void clearSynchronization() {
		TransactionSynchronizationManager.clear();
	}
}
