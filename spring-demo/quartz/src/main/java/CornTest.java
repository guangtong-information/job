import org.quartz.CronExpression;

import java.text.ParseException;

/**
 * @author zyj
 * @date 2019/5/19
 */
public class CornTest {

    public static void main(String[] args) throws ParseException {


        CronExpression cronExpression = new CronExpression("0 0 1 ? * L");
        System.out.println(cronExpression.getExpressionSummary());

    }
}
