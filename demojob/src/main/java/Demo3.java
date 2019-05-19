import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zyj
 * @date 2019/5/19
 */
public class Demo3 {

    public static void main(String[] args) {

        //延迟3s执行
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                System.out.println("hello");
            }
        }, 3, 2, TimeUnit.SECONDS);

        executorService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if (new Random().nextInt(10) ==1) {
                    System.out.println("error2");
                    throw new RuntimeException("随机异常");
                }
                System.out.println("hello2");
            }
        }, 2, 1, TimeUnit.SECONDS);
    }
}
