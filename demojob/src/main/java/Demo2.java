import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author zyj
 * @date 2019/5/19
 */
public class Demo2 {

    public static void main(String[] args) {


        //延迟3s 执行 每5s循环执行  模拟异常
        Timer timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println(System.currentTimeMillis()+ "-1");
            }
        };

        TimerTask timerTask2 = new TimerTask() {
            @Override
            public void run() {
                if (new Random().nextInt(10) ==1) {
                    throw new RuntimeException("随机异常");
                }
                System.out.println(System.currentTimeMillis() +"-2");
            }
        };

        timer.schedule(timerTask, new Date(System.currentTimeMillis() + 3000L), 5000L);
        timer.schedule(timerTask2, new Date(System.currentTimeMillis() + 3000L), 1000L);

    }
}
