package kr.wrightbrothers.framework.support.quartz;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.util.RandomKey;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class BaseJob implements Job {

	private static final Logger log = LoggerFactory.getLogger(BaseJob.class);
	private JobExecutionContext context;
	private ObjectMapper mapper =  new ObjectMapper();
	private boolean isRunning = true;
	private String uniqId;
	private String appCd;
	private String flag;
	private String errMsg;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		this.context = context;
		this.mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
		this.mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.mapper.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, false);
	    
		this.uniqId = this.context.getJobDetail().getKey().getGroup() + RandomKey.getUniqueId();
		this.appCd = this.context.getJobDetail().getKey().getGroup();
		this.flag = WBKey.Success;
		
		// schedule history insert
		this.beforeExecute();
		
		// schedule execute
		try {
			this.executeInternal();
		} catch (Exception e) {
			this.flag = WBKey.Error;
			this.errMsg =  e.getMessage();
		}
		this.afterExecute();
		
		this.scheduleNextJob();
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return this.context.getJobDetail().getKey().getName();
	}

	/**
	 * 
	 * @param parameterKey
	 * @return
	 */
	public String getParameter(String parameterKey) {
		return this.context.getJobDetail().getJobDataMap().getString(parameterKey);
	}

	/**
	 * 
	 * @param parameterKey
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getParameter(String parameterKey, Class<?> clazz) {
		String parameterValue = this.context.getJobDetail().getJobDataMap().getString(parameterKey);
		try {
			return (T) this.mapper.readValue(parameterValue, clazz);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return null;
	}
	 
	private void beforeExecute() {
		
		try {
//			String logFilePath = FileTools.getLogFilePath(HmmKey.LOG.LOCAL_TJ_LOG_PATH, this.uniqId);
//			MDC.put(HmmKey.UUID, logFilePath);
//			ComQuartzHisVo comQuartzHisVo = new ComQuartzHisVo();
//			comQuartzHisVo.setHdUniqId(this.uniqId);
//			comQuartzHisVo.setAppCd(this.appCd);
//			comQuartzHisVo.setSkdNm(getName());
//			comQuartzHisVo.setLogFilePath(logFilePath);
//			comQuartzHisVo.setStusCd("R");
//			comQuartzHisVo.setSvrNm(HmmKey.ServerName);
//			comQuartzHisVo.setCreUsrId(HmmKey.SystemUserId);
//			comQuartzHisVo.setUpdUsrId(HmmKey.SystemUserId);
//			HistoryAsyncService history = ContextUtil.getBean(HistoryAsyncService.class);
//			history.addQuartzJobHis(comQuartzHisVo);
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	private void afterExecute() {
		this.isRunning = false;
		try {
//			ComQuartzHisVo comQuartzHisVo = new ComQuartzHisVo();
//			comQuartzHisVo.setHdUniqId(this.uniqId);
//			comQuartzHisVo.setStusCd(this.flag);
//			if(HmmKey.ERROR_KEY.equals(this.flag)) comQuartzHisVo.setErrMsg(this.errMsg);
//			comQuartzHisVo.setSvrNm(HmmKey.ServerName);
//			comQuartzHisVo.setUpdUsrId(HmmKey.SystemUserId);
//			HistoryAsyncService history = ContextUtil.getBean(HistoryAsyncService.class);
//			history.endQuartzJobHis(comQuartzHisVo);
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	private void scheduleNextJob() {
	}
	
	protected abstract void executeInternal() throws Exception;

	public boolean isRunning() {
		return isRunning;
	}
	
}
