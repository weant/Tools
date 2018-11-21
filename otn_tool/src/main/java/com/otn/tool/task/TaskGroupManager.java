package com.otn.tool.task;

import com.otn.tool.common.enums.Period;
import com.otn.tool.common.enums.TaskState;
import com.otn.tool.common.utils.SchedulerUtil;
import com.otn.tool.common.utils.TimeUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TaskGroupManager {
    private static Log log = LogFactory.getLog(TaskGroupManager.class);
    private Map<String, TaskJob> jobsMap = new HashMap<>();
    private static TaskGroupManager ourInstance = new TaskGroupManager();

    public static TaskGroupManager getInstance() {
        return ourInstance;
    }

    private TaskGroupManager() {
    }

    /**
     * delete job from scheduler,remove hashMap and list object,stop thread
     *
     * @param
     * @return
     * @throws Exception
     */
    public boolean delete(TaskJob taskJob) throws Exception {
        boolean isDelete = false;
        String jobName = taskJob.getTaskGroup().getName();
        isDelete = SchedulerUtil.instance().deleteJob(jobName, null);
        jobsMap.remove(jobName);

        return isDelete;
    }

    /**
     *
     * add routineTask as a job.
     *
     * @param
     * @throws Exception
     */
    public void addTask(TaskJob taskJob) throws Exception {
            String jobName = taskJob.getTaskGroup().getName();
            if (jobsMap.containsKey(jobName)) {
                // in truth, it's a update operation.so we delete firstly, then
                // add task
                delete(taskJob);
                jobsMap.remove(jobName);
            }
            try {
                if (!taskJob.getTaskGroup().getState().equals(TaskState.IDLE.getValueString())) {
                    boolean valid = checkTask(taskJob);

                    if (valid) {
                        SchedulerUtil.instance().addJobs(jobName,
                                TaskGroupJob.class,
                                convertSchedTime(taskJob),
                                TimeUtilities.instance().parse("yyyy-MM-dd HH:mm:ss", taskJob.getTaskGroup().getStartTime()),
                                TimeUtilities.instance().parse("yyyy-MM-dd", taskJob.getTaskGroup().getEndTime()));

                        jobsMap.put(jobName, taskJob);
                    }
                }
            } catch (SchedulerException e) {
                if (e.getMessage()
                        .trim()
                        .equals("Based on configured schedule, the given trigger will never fire.")) {
                    taskJob.getTaskGroup().setState(TaskState.IDLE.getValueString());
                }
                log.error(e.getMessage(), e);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
    }

    public boolean checkTaskValidityNow(TaskJob taskJob) {
        boolean valid = TimeUtilities.instance().isTimeValueInScope(
                new Date().getTime(),
                TimeUtilities.instance().parse("yyyy-MM-dd HH:mm:ss", taskJob.getTaskGroup().getStartTime()).getTime(),
                TimeUtilities.instance().parse("yyyy-MM-dd", taskJob.getTaskGroup().getEndTime()).getTime());
        if (!valid) {
            try {
                TaskGroupManager.getInstance().delete(taskJob);
                taskJob.getTaskGroup().setState(TaskState.IDLE.getValueString());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                log.error("delete job failed! ", e);
            }
            return false;
        }
        return true;
    }

    public boolean checkTask(TaskJob taskJob) {
        Date nextExecuteTime = getExecuteTimeAfter(taskJob, new Date());
        if(nextExecuteTime == null) {
            return false;
        }
        return true;
    }

    public Date getExecuteTimeAfter(TaskJob taskJob, Date afterTime) {
        if (afterTime == null || taskJob == null) {
            afterTime = new Date();
        }

        Long currentMillis = System.currentTimeMillis();
        Long startMillis = TimeUtilities.instance().parse("yyyy-MM-dd HH:mm:ss", taskJob.getTaskGroup().getStartTime()).getTime();
        long afterMillis = afterTime.getTime();
        Long endMillis = TimeUtilities.instance().parse("yyyy-MM-dd", taskJob.getTaskGroup().getEndTime()).getTime();
        if (endMillis <= afterMillis) {
            return null;
        }
        if (afterMillis < startMillis) {
            return new Date(startMillis);
        }

        if(afterMillis > currentMillis) {
            return afterTime;
        }

        long secondsAfterStart = (afterMillis - startMillis) / 1000 + 1;
        int repeatInterval = taskJob.getTaskGroup().getInterval();

        int YEAR_TO_GIVEUP_SCHEDULING_AT = LocalDate.MAX.getYear();

        Date time = null;
        long jumpCount = 0;
        LocalDateTime sTime = DateUtils.toLocalDateTime(TimeUtilities.instance().parse("yyyy-MM-dd HH:mm:ss", taskJob.getTaskGroup().getStartTime()));
        LocalDateTime cTime = LocalDateTime.now(ZoneId.systemDefault());

        switch (Period.valueOf(taskJob.getTaskGroup().getUnit())) {
//            case SECOND:
//                jumpCount = secondsAfterStart / repeatInterval;
//                if (secondsAfterStart % repeatInterval != 0) {
//                    jumpCount++;
//                }
//                time = DateUtils.toDate(sTime.plusSeconds(repeatInterval * jumpCount));
//                while(time.getTime() <= currentMillis) {
//                    jumpCount++;
//                    time = DateUtils.toDate(sTime.plusSeconds(repeatInterval * jumpCount));
//                }
//                break;
            case MINUTE:
                jumpCount = secondsAfterStart / (repeatInterval * 60);
                if (secondsAfterStart % (repeatInterval * 60) != 0) {
                    jumpCount++;
                }
                time = DateUtils.toDate(sTime.plusMinutes(repeatInterval * jumpCount));
                while(time.getTime() <= currentMillis) {
                    jumpCount++;
                    time = DateUtils.toDate(sTime.plusMinutes(repeatInterval * jumpCount));
                }
                break;
            case HOUR:
                jumpCount = secondsAfterStart / (repeatInterval * 60 * 60);
                if (secondsAfterStart % (repeatInterval * 60 * 60) != 0) {
                    jumpCount++;
                }
                time = DateUtils.toDate(sTime.plusHours(repeatInterval * jumpCount));
                while(time.getTime() <= currentMillis) {
                    jumpCount++;
                    time = DateUtils.toDate(sTime.plusHours(repeatInterval * jumpCount));
                }
                break;
            case DAY:
                while(!sTime.isAfter(cTime) && (sTime.getYear() < YEAR_TO_GIVEUP_SCHEDULING_AT)) {
                    sTime = sTime.plusDays(repeatInterval);
                }
                time = DateUtils.toDate(sTime);
                break;
            case WEEK:
                while(!sTime.isAfter(cTime) && (sTime.getYear() < YEAR_TO_GIVEUP_SCHEDULING_AT)) {
                    sTime = sTime.plusWeeks(repeatInterval);
                }
                time = DateUtils.toDate(sTime);
                break;
            case MONTH:
                while(!sTime.isAfter(cTime) && (sTime.getYear() < YEAR_TO_GIVEUP_SCHEDULING_AT)) {
                    sTime = sTime.plusMonths(repeatInterval);
                }
                time = DateUtils.toDate(sTime);
                break;
            case QUARTER:
                while(!sTime.isAfter(cTime) && (sTime.getYear() < YEAR_TO_GIVEUP_SCHEDULING_AT)) {
                    sTime = sTime.plusMonths(repeatInterval * 3);
                }
                time = DateUtils.toDate(sTime);
                break;
            case YEAR:
                while(!sTime.isAfter(cTime) && (sTime.getYear() < YEAR_TO_GIVEUP_SCHEDULING_AT)) {
                    sTime = sTime.plusYears(repeatInterval);
                }
                time = DateUtils.toDate(sTime);
                break;
            default:

        }
        if (endMillis < time.getTime()) {
            return null;
        }
        return time;
    }

    private String convertSchedTime(TaskJob taskJob) {
        String schedTimeString = "";
        switch (Period.valueOf(taskJob.getTaskGroup().getUnit())) {
            case MINUTE:
                schedTimeString = convMinuteSchedTime(taskJob);
                break;
            case HOUR:
                schedTimeString = convHourSchedTime(taskJob);
                break;
            case DAY:
                schedTimeString = convDaySchedTime(taskJob);
                break;
            case WEEK:
                schedTimeString = convWeekSchedTime(taskJob);
                break;
            case MONTH:
                schedTimeString = convMonthSchedTime(taskJob);
                break;
            case QUARTER:
                schedTimeString = convQuarterSchedTime(taskJob);
                break;
            case YEAR:
                schedTimeString = convYearSchedTime(taskJob);
                break;
        }

        return schedTimeString;
    }

    private String convMinuteSchedTime(TaskJob taskJob) {
        String schedTimeString = "";
        String exeMinutes = "";
        String tag = "";
        int interval = taskJob.getTaskGroup().getInterval();
        int second = taskJob.getCalendarValue(Calendar.SECOND);
        int minute = taskJob.getCalendarValue(Calendar.MINUTE);
        for (int i = 0; i < 4; i++) {
            int exeMinute = 15 * i + (minute % 15);
            exeMinutes = exeMinutes + tag + exeMinute;
            tag = ",";
        }

        schedTimeString = second + 1 + " " + exeMinutes + " " + "* * * ?";
        return schedTimeString;
    }

    private String convHourSchedTime(TaskJob taskJob) {
        String schedTimeString = "";
        int second = routineTask.getCalendarValue(Calendar.SECOND);
        int minute = routineTask.getCalendarValue(Calendar.MINUTE);
        if (routineTask.getEndTime() != 0 && routineTask.getBeginTime() != 0) {
            schedTimeString = second + 1 + " " + minute + " " + "* * * ?";
        }
        return schedTimeString;
    }

    private String convDaySchedTime(TaskJob taskJob) {
        String schedTimeString = "";
        int second = routineTask.getCalendarValue(Calendar.SECOND);
        int minute = routineTask.getCalendarValue(Calendar.MINUTE);
        int hour = routineTask.getCalendarValue(Calendar.HOUR);
        if (routineTask.getEndTime() != 0 && routineTask.getBeginTime() != 0) {

            schedTimeString = second + 1 + " " + minute + " " + hour + " "
                    + "* * ?";
        }
        return schedTimeString;
    }

    private String convWeekSchedTime(TaskJob taskJob) {
        String schedTimeString = "";

        if (routineTask.getEndTime() != 0 && routineTask.getBeginTime() != 0) {

            Date et = new Date();
            et.setTime(routineTask.getEndTime());

            SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");

            String weekStr = "";
            int dayOfWeek = routineTask.getCalendarValue(Calendar.DAY_OF_WEEK);
            switch (dayOfWeek) {
                case 1:
                    weekStr = "SUN";
                    break;
                case 2:
                    weekStr = "MON";
                    break;
                case 3:
                    weekStr = "TUE";
                    break;
                case 4:
                    weekStr = "WED";
                    break;
                case 5:
                    weekStr = "THU";
                    break;
                case 6:
                    weekStr = "FRI";
                    break;
                case 7:
                    weekStr = "SAT";
                    break;
            }

            int second = routineTask.getCalendarValue(Calendar.SECOND);
            int minute = routineTask.getCalendarValue(Calendar.MINUTE);
            int hour = routineTask.getCalendarValue(Calendar.HOUR);
            int year = routineTask.getCalendarValue(Calendar.YEAR);

            schedTimeString = second + 1 + " " + minute + " " + hour + " ? * "
                    + weekStr + " " + year + "-" + formatYear.format(et);;
        }
        return schedTimeString;
    }

    private String convMonthSchedTime(TaskJob taskJob) {
        String schedTimeString = "";

        if (routineTask.getEndTime() != 0 && routineTask.getBeginTime() != 0) {

            int year = routineTask.getCalendarValue(Calendar.YEAR);
            int date = routineTask.getCalendarValue(Calendar.DATE);
            int hour = routineTask.getCalendarValue(Calendar.HOUR);
            int minute = routineTask.getCalendarValue(Calendar.MINUTE);
            int second = routineTask.getCalendarValue(Calendar.SECOND);

            Date et = new Date();
            et.setTime(routineTask.getEndTime());

            SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");

            schedTimeString = second + 1 + " " + minute + " " + hour + " "
                    + date + " * ? " + year + "-" + formatYear.format(et);
        }
        return schedTimeString;
    }

    /**
     *
     * @param exeTime
     *            1~3
     * @param beginTime
     * @param endTime
     * @return
     */
    private String convQuarterSchedTime(TaskJob taskJob) {
        String schedTimeString = "";

        if (routineTask.getEndTime() != 0 && routineTask.getBeginTime() != 0) {
            int year = routineTask.getCalendarValue(Calendar.YEAR);
            int month = routineTask.getCalendarValue(Calendar.MONTH);
            int date = routineTask.getCalendarValue(Calendar.DATE);
            int hour = routineTask.getCalendarValue(Calendar.HOUR);
            int minute = routineTask.getCalendarValue(Calendar.MINUTE);
            int second = routineTask.getCalendarValue(Calendar.SECOND);

            String exeMonths = "";
            String tag = "";
            for (int i = 0; i < 4; i++) {
                int exeMonth = 3 * i + (month % 3);
                exeMonths = exeMonths + tag + exeMonth;
                tag = ",";
            }

            Date et = new Date();
            et.setTime(routineTask.getEndTime());
            SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");

            schedTimeString = second + 1 + " " + minute + " " + hour + " "
                    + date + " " + exeMonths + " ? " + year + "-" + formatYear.format(et);
        }
        return schedTimeString;
    }

    private String convYearSchedTime(TaskJob taskJob) {
        String schedTimeString = "";

        int year = routineTask.getCalendarValue(Calendar.YEAR);
        int month = routineTask.getCalendarValue(Calendar.MONTH);
        int date = routineTask.getCalendarValue(Calendar.DATE);
        int hour = routineTask.getCalendarValue(Calendar.HOUR);
        int minute = routineTask.getCalendarValue(Calendar.MINUTE);
        int second = routineTask.getCalendarValue(Calendar.SECOND);

        if (routineTask.getEndTime() != 0 && routineTask.getBeginTime() != 0) {

            Date et = new Date();
            et.setTime(routineTask.getEndTime());

            //SimpleDateFormat formatMonth = new SimpleDateFormat("MM");
            SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");

            //int eMonth = Integer.parseInt(formatMonth.format(et));
            int eYear = Integer.parseInt(formatYear.format(et));
			/*if (eMonth < month) {
				eYear = eYear - 1;
			}*/

            schedTimeString = second + 1 + " " + minute + " " + hour + " "
                    + date + " " + month + " ? " + year + "-" + eYear;
        }
        return schedTimeString;
    }

    public Map<String, TaskJob> getJobsMap() {
        return jobsMap;
    }
}
