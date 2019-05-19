import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
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
                JobCoreConfiguration.newBuilder("demoSimpleJob",
                        "0/15 * * * * ?", 10).build();
        // 定义SIMPLE类型配置
        SimpleJobConfiguration simpleJobConfig =
                new SimpleJobConfiguration(simpleCoreConfig, MyElasticJob.class.getCanonicalName());
        // 定义Lite作业根配置
        LiteJobConfiguration simpleJobRootConfig =
                LiteJobConfiguration.newBuilder(simpleJobConfig).build();
        return simpleJobRootConfig;
    }

    private static CoordinatorRegistryCenter createRegistryCenter() {
        CoordinatorRegistryCenter regCenter =
                new ZookeeperRegistryCenter(
                        new ZookeeperConfiguration("localhost:2181", "elastic-job-demo1"));
        regCenter.init();
        return regCenter;
    }
}
