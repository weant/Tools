package com.otn.tool.common.utils;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

public class SchedulerUtil {

	private static Log log = LogFactory.getLog(SchedulerUtil.class);
	private final static String JOB_GROUP = "JOB";
	private final static String TRIGGER_GROUP = "TR";

	private Scheduler sched;
	private static SchedulerUtil schedUtil = new SchedulerUtil();

	private SchedulerUtil() {
		try {
			SchedulerFactory sf = new StdSchedulerFactory();
			sched = sf.getScheduler();
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	public static SchedulerUtil instance() {
		return schedUtil;
	}

	/**
	 * 周期性调度
	 * 
	 * @param jobName
	 * @param jobClass
	 * @param schedTimeTemp
	 * @throws SchedulerException
	 * @throws Exception
	 */
	public void addJobs(String jobName, Class<? extends Job> jobClass,
			String schedTimeTemp, Date startDate, Date endDate) throws SchedulerException,
			Exception {

		JobDetail job = JobBuilder.newJob(jobClass)
				.withIdentity(jobName, JOB_GROUP).build();

		TriggerBuilder<CronTrigger> tb = TriggerBuilder.newTrigger()
				.withIdentity(jobName, TRIGGER_GROUP)
				.withSchedule(CronScheduleBuilder.cronSchedule(schedTimeTemp));
		if(startDate != null) {
			if(startDate.before(new Date())){//如果开始时间在当前时间之前，那么job会被调度器自动调用一次，所以此处将有效期开始时间重新设置了
		           tb.startNow();
		       }else{
		           tb.startAt(startDate);
		       }
		}
		else {
			tb.startNow();
		}
		if(null != endDate) {
			tb.endAt(endDate);
		}

		Trigger trigger = tb.build();

		sched.scheduleJob(job, trigger);
		System.out.println("add job in schedule pool [jobName=" + jobName
				+ ", cronExpress=" + schedTimeTemp + "] success");
		log.info("add job in schedule pool [jobName=" + jobName
				+ ", cronExpress=" + schedTimeTemp + "] success");

	}

	/**
	 * 增加只调度一次的任务
	 * 
	 * @param jobName
	 * @param jobClass
	 * @param scheduleTime
	 *            调度时间
	 * @throws SchedulerException
	 */
	public void addSimpleJobs(String jobName, Class<? extends Job> jobClass,
			Date scheduleTime) throws SchedulerException {
		JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jobName)
				.build();
		SimpleTrigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity(jobName + "trigger")
				.startAt(scheduleTime)
				.withSchedule(
						SimpleScheduleBuilder.simpleSchedule().withRepeatCount(
								0)).build();
		sched.scheduleJob(job, trigger);
	}

	public void run() {
		try {
			sched.start();
		} catch (SchedulerException e) {
			// throw new Exception(e);
			log.error(e.getMessage(), e);
		}
	}

	public void stop() throws Exception {
		try {
			sched.shutdown();
		} catch (SchedulerException e) {
			throw new Exception(e);
		}
	}

	public boolean deleteJob(String jobName, String jobGroup) throws Exception {
		boolean deleted = false;
		if (jobGroup == null) {
			jobGroup = JOB_GROUP;
		}
		JobKey jk = JobKey.jobKey(jobName, jobGroup);
		boolean exists = sched.checkExists(jk);
		if (!exists) {// 如果不存在 则直接返回
			return true;
		}
		try {

			TriggerKey tk = TriggerKey.triggerKey(jobName, TRIGGER_GROUP);

			sched.pauseTrigger(tk);// 停止触发器
			sched.unscheduleJob(tk);// 移除触发器
			sched.deleteJob(jk);
			deleted = !sched.checkExists(jk);// 如果不存在了,则说明删除成功.由于deleteJob方法返回值一直是false
			log.info("remove job in schedule pool [jobName=" + jobName
					+ "] success");
			System.out.println("remove job in schedule pool [jobName="
					+ jobName + "] success");
		} catch (SchedulerException e) {
			throw new Exception(e);

		}
		return deleted;
	}

	public boolean deleteJob(String jobName) throws Exception {
		return deleteJob(jobName, null);
	}

	public static class TestJob implements Job {

		@Override
		public void execute(JobExecutionContext arg0)
				throws JobExecutionException {
			// TODO Auto-generated method stub
			System.out.println("execute");
		}

	}
}
