package kr.wrightbrothers.apps.common.util;

import kr.wrightbrothers.framework.support.quartz.dto.QuartzDto;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class QuartzUtil {
	
	  @Autowired
	  private SchedulerFactoryBean schedulerFactory;
	  
	  @Autowired
	  private static ApplicationContext applicationContext;

	/**
	 * SimpleJob
	 * @param QuartzDto
	 * @param IntervalInSeconds
	 * @throws SchedulerException
	 */
	public void addSimpleJob(QuartzDto QuartzDto, int IntervalInSeconds, long baseTime) throws SchedulerException {
        JobDetail jobdetail = createJobDetail(QuartzDto);
//		JobDetail jobdetail = JobBuilder.newJob(QuartzDto.getExecuteJobClass())
//								.withIdentity(QuartzDto.getJobName(), QuartzDto.getGroupName())
//								.usingJobData(QuartzDto.getParameter())		
//								.build();
		Scheduler scheduler = schedulerFactory.getScheduler();
		scheduler.scheduleJob(jobdetail, TriggerBuilder.newTrigger().withIdentity(QuartzDto.getJobName(), QuartzDto.getGroupName()).withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(IntervalInSeconds).repeatForever()).startAt(new Date(baseTime + (IntervalInSeconds * 1000))).build());
	}

	/**
	 * CronJob
	 * @param QuartzDto
	 * @param cronExpression
	 * @throws SchedulerException
	 */
	public void addCronJob(QuartzDto QuartzDto, String cronExpression) throws SchedulerException {
//		JobDetail jobdetail = JobBuilder.newJob(QuartzDto.getExecuteJobClass())
//								.withIdentity(QuartzDto.getJobName(), QuartzDto.getGroupName())
//								.usingJobData(QuartzDto.getParameter())		
//								.build();
		JobDetail jobdetail = createJobDetail(QuartzDto);
		Scheduler scheduler = schedulerFactory.getScheduler();
		scheduler.scheduleJob(jobdetail, TriggerBuilder.newTrigger().withIdentity(QuartzDto.getJobName(), QuartzDto.getGroupName()).withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build());
	}
	
	/**
	 * SimpleJob
	 * @param QuartzDto
	 * @param IntervalInSeconds
	 * @throws SchedulerException
	 */
	public void addQuartzSimpleJob(QuartzDto QuartzDto, int IntervalInSeconds, long startTime) throws SchedulerException {
//		JobDetail jobdetail = JobBuilder.newJob(QuartzDto.getExecuteJobClass())
//								.withIdentity(QuartzDto.getJobName(), QuartzDto.getGroupName())
//								.usingJobData(QuartzDto.getParameter())		
//								.build();
		JobDetail jobdetail = createJobDetail(QuartzDto);
		Scheduler scheduler = schedulerFactory.getScheduler();
		scheduler.scheduleJob(jobdetail, TriggerBuilder.newTrigger().withIdentity(QuartzDto.getJobName(), QuartzDto.getGroupName()).withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(IntervalInSeconds).repeatForever()).startAt(new Date(startTime)).build());
	}
	
	/**
	 * CronJob
	 * @param QuartzDto
	 * @param cronExpression
	 * @throws SchedulerException
	 */
	public void addQuartzCronJob(QuartzDto QuartzDto, String cronExpression, long startTime) throws SchedulerException {
//		JobDetail jobdetail = JobBuilder.newJob(QuartzDto.getExecuteJobClass())
//								.withIdentity(QuartzDto.getJobName(), QuartzDto.getGroupName())
//								.usingJobData(QuartzDto.getParameter())		
//								.build();
		JobDetail jobdetail = createJobDetail(QuartzDto);
		Scheduler scheduler = schedulerFactory.getScheduler();
		scheduler.scheduleJob(jobdetail, TriggerBuilder.newTrigger().withIdentity(QuartzDto.getJobName(), QuartzDto.getGroupName()).withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).startAt(new Date(startTime)).build());
	}
	
	/**
	 * 즉시실행
	 * @param QuartzDto
	 * @throws SchedulerException
	 */
	public void executeJob(QuartzDto QuartzDto) throws SchedulerException,JobPersistenceException {
		Scheduler scheduler = schedulerFactory.getScheduler();
		scheduler.triggerJob(new JobKey(QuartzDto.getJobName(), QuartzDto.getGroupName()));
	}
	
	/**
	 * 삭제
	 * @param QuartzDto
	 * @throws SchedulerException
	 */
	public void deleteJob(QuartzDto QuartzDto) throws SchedulerException {
		Scheduler scheduler = schedulerFactory.getScheduler();
		scheduler.deleteJob(new JobKey(QuartzDto.getJobName(), QuartzDto.getGroupName()));
	}
	
	/**
	 * 일시정시
	 * @param QuartzDto
	 * @throws SchedulerException
	 */
	public void pauseJob(QuartzDto QuartzDto) throws SchedulerException {
		Scheduler scheduler = schedulerFactory.getScheduler();
		scheduler.pauseJob(new JobKey(QuartzDto.getJobName(), QuartzDto.getGroupName()));
	}
	
	/**
	 * 일시정지 해제
	 * @param QuartzDto
	 * @throws SchedulerException
	 */
	public void resumeJob(QuartzDto QuartzDto) throws SchedulerException {
		Scheduler scheduler = schedulerFactory.getScheduler();
		scheduler.resumeJob(new JobKey(QuartzDto.getJobName(), QuartzDto.getGroupName()));
	}
	
	/**
	 * 
	 * @param QuartzDto
	 * @return
	 */
	private JobDetail createJobDetail(QuartzDto QuartzDto) {
		JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(QuartzDto.getJobClass());
        factoryBean.setName(QuartzDto.getJobName());
        factoryBean.setGroup(QuartzDto.getGroupName());
        factoryBean.setJobDataMap(QuartzDto.getParameter());
        factoryBean.setApplicationContext(applicationContext);
//        factoryBean.setApplicationContextJobDataKey("applicationContext");
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
	}
}