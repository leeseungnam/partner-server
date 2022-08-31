package kr.wrightbrothers.framework.support.transaction;

public interface WBSynchronizationManager {

	void initSynchronization();

	boolean isSynchronizationActive();

	void clearSynchronization();
	
}
