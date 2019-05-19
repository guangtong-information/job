import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author zyj
 * @date 2019/5/19
 */
public class Demo3 {

    public static void main(String[] args) throws SchedulerException {


        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        JobDetail job = JobBuilder.newJob(HelloJob.class)
                .usingJobData("name", "zhangsan").build();

        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule("0/3 * * * * ?"))
                .build();
        scheduler.scheduleJob(job, trigger);
        scheduler.start();




    }
}
