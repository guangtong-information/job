import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

/**
 * @author zyj
 * @date 2019/5/19
 */
public class Demo {

    public static void main(String[] args) {

        new JobScheduler(createRegistryCenter(), createJobConfiguration()).init();
    }

    private static LiteJobConfiguration createJobConfiguration() {
        JobCoreConfiguration simpleCoreConfig =
                JobCoreConfiguration.newBuilder("demoDataFlowJob",
                        "0/15 * * * * ?", 7).build();
        // 定义dataflow类型配置

        //不为流数据处理
        //如果为流数据， 会一直抓取数据， 只到为 空 为止
        boolean streamingProcess = true;

        DataflowJobConfiguration dataflowJobConfiguration =
                new DataflowJobConfiguration(simpleCoreConfig, MyElasticJob.class.getCanonicalName(), streamingProcess);
        // 定义Lite作业根配置
        LiteJobConfiguration dataFlowJobRootConfig =
                LiteJobConfiguration.newBuilder(dataflowJobConfiguration).build();
        return dataFlowJobRootConfig;
    }

    private static CoordinatorRegistryCenter createRegistryCenter() {
        CoordinatorRegistryCenter regCenter =
                new ZookeeperRegistryCenter(
                        new ZookeeperConfiguration("localhost:2181", "elastic-job-demo2"));
        regCenter.init();
        return regCenter;
    }
}
