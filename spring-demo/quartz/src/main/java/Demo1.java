import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;



/**
 * @author zyj
 * @date 2019/5/19
 */
public class Demo1 {
    public static void main(String[] args) throws SchedulerException {


        JobDetail job = JobBuilder.newJob(HelloJob.class)
                .withIdentity("job1", "group1")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(40)
                        .repeatForever())
                .build();


        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.scheduleJob(job, trigger);
        scheduler.start();
    }
}
