import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

/**
 * @author zyj
 * @date 2019/5/19
 */
public class MyElasticJob  implements SimpleJob {


    @Override
    public void execute(ShardingContext shardingContext) {

        int totalCount =  shardingContext.getShardingTotalCount();
        int index = shardingContext.getShardingItem();
        String sql = " select * from user100w  where id % "+totalCount+" =" + index;
        System.out.println(sql);
        System.out.println(shardingContext.toString());
        System.out.println(Thread.currentThread()+ "hello:" + index);
    }
}
