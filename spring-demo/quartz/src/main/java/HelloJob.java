import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author zyj
 * @date 2019/5/19
 */
public class HelloJob implements Job {


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        String name = jobExecutionContext.getMergedJobDataMap().getString("name");
        System.out.println("hello:" + name);
    }
}
