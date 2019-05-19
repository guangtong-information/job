import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author zyj
 * @date 2019/5/19
 */
public class MyElasticJob  implements DataflowJob<String> {


    @Override
    public List<String> fetchData(ShardingContext shardingContext) {
        int totalCount =  shardingContext.getShardingTotalCount();
        int index = shardingContext.getShardingItem();
        String sql = " select * from user100w  where id % "+totalCount+" =" + index;
        System.out.println( Thread.currentThread() + sql);
        List<String> list = new ArrayList<>();
        list.add(UUID.randomUUID().toString());
        list.add(UUID.randomUUID().toString());

        boolean random = new Random().nextBoolean();
        System.out.println("random:" + random);
        if (random) {
            return new ArrayList<>();
        }
        return list;
    }

    @Override
    public void processData(ShardingContext shardingContext, List<String> data) {
        System.out.println(Thread.currentThread() + "分片id：" + shardingContext.getShardingItem() +"，获取到list:" + data);

    }
}
