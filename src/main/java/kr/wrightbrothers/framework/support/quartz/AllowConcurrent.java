package kr.wrightbrothers.framework.support.quartz;
import org.quartz.InterruptableJob;
import org.quartz.UnableToInterruptJobException;

public abstract class AllowConcurrent extends BaseJob implements InterruptableJob {
	
	protected void executeInternal() throws Exception{
		this.doExecute();
	}

	protected abstract void doExecute() throws Exception;
	

	public void interrupt() throws UnableToInterruptJobException {
		do {
			try {
				Thread.sleep(5000L);
			} catch (InterruptedException var2) {
				var2.getMessage();
			}

			System.out.println("================== " + this.getName() + " : Is Running ==================");
		} while (this.isRunning());

	}
}
