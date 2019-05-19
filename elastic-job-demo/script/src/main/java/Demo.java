import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;

/**
 * @author zyj
 * @date 2019/5/19
 */
public class Demo {

    public static void main(String[] args) throws IOException {
        new JobScheduler(createRegistryCenter(), createJobConfiguration()).init();
    }

    //配置注册中心
    private static CoordinatorRegistryCenter createRegistryCenter() {
        CoordinatorRegistryCenter regCenter =
                new ZookeeperRegistryCenter(new ZookeeperConfiguration("127.0.0.1:2181",
                        "elastic-job-script-demo"));
        regCenter.init();
        return regCenter;
    }

    //配置job
    private static LiteJobConfiguration createJobConfiguration() throws IOException {
        JobCoreConfiguration coreConfiguration =
                JobCoreConfiguration.newBuilder("scriptDemoJob", "0/5 * * * * ?", 1).build();
        ScriptJobConfiguration scriptJobConfiguration =
                new ScriptJobConfiguration(coreConfiguration, buildScriptCommandLine());

        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(scriptJobConfiguration).overwrite(true).build();
        return liteJobConfiguration;
    }

    private static String buildScriptCommandLine() throws IOException {
        //判断当前系统
        if (System.getProperties().getProperty("os.name").contains("Windows")) {
            return Paths.get(Demo.class.getResource("/demo.bat").getPath().substring(1)).toString();
        }
        Path result = Paths.get(Demo.class.getResource("/demo.sh").getPath());
        Files.setPosixFilePermissions(result, PosixFilePermissions.fromString("rwxr-xr-x"));
        return result.toString();
    }

}
