# 定时任务



## 1.jdk里面的定时任务



### 1.1while(ture)

```java
Thread thread = new Thread(new Runnable() {
    public void run() {
        while (true) {
            System.out.println("hello");
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
});


thread.run();
```



### 1.2  java.util.Timer

延迟3s 执行

```
        //延迟3s 执行
        Timer timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Hellp");
            }
        };

        timer.schedule(timerTask, 3000 );
```



指定时间

```java
        //延迟3s 执行
        Timer timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println(System.currentTimeMillis());
            }
        };

        System.out.println("当前时间：" + System.currentTimeMillis());
        timer.schedule(timerTask, new Date(System.currentTimeMillis() + 3000L));

```



循环执行

```java
  //延迟3s 执行 每5s循环执行
        Timer timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println(System.currentTimeMillis());
            }
        };

        timer.schedule(timerTask, new Date(System.currentTimeMillis() + 3000L), 5000L);

```





```
1.Timer  timer = new Timer();

初始化
TaskQueue queue = new TaskQueue();   空
TimerThread thread = new TimerThread(queue);  

```

```java
class TimerThread extends Thread {
 public void run() {
        try {
            mainLoop();
        } finally {
            // Someone killed this Thread, behave as if Timer cancelled
            synchronized(queue) {
                newTasksMayBeScheduled = false;
                queue.clear();  // Eliminate obsolete references
            }
        }
    }
}

 private void mainLoop() {
         queue.wait();
 }


```



```
执行timer.schedule


1.将TimerTssk 加入到 queue

2.queue.notify();


//如何停止定时任务

  timer.cancel();
  {
         queue.clear();
         queue.notify(); 
  }
  
  
 //原理：
 class TimerThread {
      
       while (queue.isEmpty())
  }
  

```



异常

多线程并行处理定时任务时，Timer运行多个TimeTask时，只要其中之一没有捕获抛出的异常，其它任务便会自动终止运行，使用ScheduledExecutorService则没有这个问题

```java
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
```



### 1.3 java.util.concurrent.ScheduledExecutorService



延迟3s执行

```java
        //延迟3s执行
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.schedule(new Runnable() {
            public void run() {
                System.out.println("hello");
            }
        }, 3, TimeUnit.SECONDS);
```



循环3s执行

```java

  ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                System.out.println("hello");
            }
        }, 3, 2, TimeUnit.SECONDS);
```



异常情况 （任务2异常，  任务1  还会继续执行）

```java
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
```



## 2.Spring



 ### 初始化项目

https://start.spring.io/

2.15





1.@EnableScheduling

2.@Scheduled

```
@EnableScheduling
@SpringBootApplication
public class SpringDemoApplication {


    @Scheduled(cron = "2/3 * * * * *")
    public void job1() {
        System.out.println("hello");
    }


    public static void main(String[] args) {
        SpringApplication.run(SpringDemoApplication.class, args);
    }

}

```



## 3. quartz

<https://github.com/quartz-scheduler/quartz>

引入依赖

```xml
<dependency>
    <groupId>org.quartz-scheduler</groupId>
    <artifactId>quartz</artifactId>
    <version>2.3.1</version>
</dependency>
```



### 1.实现job

```java
public class HelloJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        System.out.println("hello");
    }
}


```



### 2.执行

```java

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
```

Quartz调度一次任务，会干如下的事：

JobClass jobClass=JobDetail.getJobClass()

Job jobInstance=jobClass.newInstance()。所以Job实现类，必须有一个public的无参构建方法。

jobInstance.execute(JobExecutionContext context)。JobExecutionContext是Job运行的上下文，可以获得Trigger、Scheduler、JobDetail的信息。



Scheduler：调度器。所有的调度都是由它控制。

Trigger： 定义触发的条件。例子中，它的类型是SimpleTrigger，每隔3秒中执行一次

JobDetail & Job： JobDetail 定义的是任务数据，而真正的执行逻辑是在Job中，

 为什么设计成JobDetail + Job，不直接使用Job？这是因为任务是有可能并发执行，如果Scheduler直接使用Job，就会存在对同一个Job实例并发访问的问题。而JobDetail & Job 方式，sheduler每次执行，都会根据JobDetail创建一个新的Job实例，这样就可以规避并发访问的问题。

###  3.cron

```java
 Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        JobDetail job = JobBuilder.newJob(HelloJob.class).build();
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule("0/3 * * * * ? "))
                .build();
        scheduler.scheduleJob(job, trigger);
        scheduler.start();
```

校验表达式

```
CronExpression cronExpression = new CronExpression("0 0 1 ? * L");
System.out.println(cronExpression.getExpressionSummary());


seconds: 0
minutes: 0
hours: 1
daysOfMonth: ?
months: *
daysOfWeek: 7
lastdayOfWeek: false
nearestWeekday: false
NthDayOfWeek: 0
lastdayOfMonth: false
years: *
```





计划任务，是任务在约定的时间执行已经计划好的工作，这是表面的意思。在Linux中，我们经常用到 cron 服务器来完成这项工作。cron服务器可以根据配置文件约定的时间来执行特定的任务。

 public final class CronExpression implements Serializable, Cloneable {    
    protected static final int SECOND = 0;
    protected static final int MINUTE = 1;
    protected static final int HOUR = 2;
    protected static final int DAY_OF_MONTH = 3;
    protected static final int MONTH = 4;
    protected static final int DAY_OF_WEEK = 5;
    protected static final int YEAR = 6;
    ……



| 名称 | 是否必须 | 允许值          |    特殊字符     |
| ---- | -------- | --------------- | :-------------: |
| 秒   | 是       | 0-59            |     , - * /     |
| 分   | 是       | 0-59            |     , - * /     |
| 时   | 是       | 0-23            |     , - * /     |
| 日   | 是       | 1-31            | , - * ? / L W C |
| 月   | 是       | 1-12 或 JAN-DEC |     , - * /     |
| 周   | 是       | 1-7 或 SUN-SAT  | , - * ? / L C # |
| 年   | 否       | 空 或 1970-2099 |     , - * /     |





每隔5秒执行一次：*/5 * * * * ?

每隔1分钟执行一次：0 */1 * * * ?

每天23点执行一次：0 0 23 * * ?

每天凌晨1点执行一次：0 0 1 * * ?

每月1号凌晨1点执行一次：0 0 1 1 * ?

每月最后一天23点执行一次：0 0 23 L * ?

每周星期天凌晨1点实行一次：0 0 1 ? * L

在26分、29分、33分执行一次：0 26,29,33 * * * ?

每天的0点、13点、18点、21点都执行一次：0 0 0,13,18,21 * * ?



### 4.带参数



```java

public class HelloJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String name = jobExecutionContext.getMergedJobDataMap().getString("name");
        System.out.println("hello:" + name);
    }
}
```



执行

```java
  Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        JobDetail job = JobBuilder.newJob(HelloJob.class)
                .usingJobData("name", "zhangsan").build();
        
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule("0/3 * * * * ?"))
                .build();
        scheduler.scheduleJob(job, trigger);
        scheduler.start();
```





## 4.Elastic Job (集群定时任务)

<http://elasticjob.io/>



## 环境要求

### a. Java

请使用JDK1.7及其以上版本。[详情参见](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

### b. Zookeeper

请使用Zookeeper 3.4.6及其以上版本。[详情参见](https://zookeeper.apache.org/doc/trunk/zookeeperStarted.html)

### c. Maven

请使用Maven 3.0.4及其以上版本。[详情参见](http://maven.apache.org/install.html)



### 4.1 分片概念

将整体任务拆解为多个子任务

可通过服务器的增减弹性伸缩任务处理能力

分布式协调，任务服务器上下线的全自动发现与处理



### 4.2 作业类型

1.单一定时任务

2.流数据类型定时任务

3.脚本任务





### Zookeeper

ZooKeeper是一种集中式服务，用于维护配置信息，命名，提供分布式同步和提供组服务。



### 节点和短暂节点



节点：

与标准文件系统不同，ZooKeeper命名空间中的每个节点都可以包含与之关联的数据以及子项。这就像拥有一个允许文件也是目录的文件系统。（ZooKeeper旨在存储协调数据：状态信息，配置，位置信息等，因此存储在每个节点的数据通常很小，在字节到千字节范围内。）我们使用术语*znode*来说明我们正在谈论ZooKeeper数据节点。



短暂节点：

ZooKeeper也有短暂节点的概念。只要创建znode的会话处于活动状态，就会存在这些znode。会话结束时，znode将被删除。当您想要实现*[tbd]*时，短暂节点很有用



1下载：

https://mirrors.tuna.tsinghua.edu.cn/apache/zookeeper/zookeeper-3.4.14/



2解压



3.配置文件

zookeeper-3.4.14\conf\zoo_sample.cfg

修改为:zoo.cfg



4.启动

\zookeeper-3.4.14\bin\zkServer.cmd



### 4.zk客户端使用

zookeeper-3.4.14\bin\zkCli.cmd

查看帮助 help

ls 查看



### 5. 可视化工具ZooInspector

#### [下载地址](https://issues.apache.org/jira/secure/attachment/12436620/)

java -jar zookeeper-dev-ZooInspector.jar





### 6. 单一定时任务

引入依赖

```xml
<dependency>
    <groupId>com.dangdang</groupId>
    <artifactId>elastic-job-lite-core</artifactId>
    <version>2.1.5</version>
</dependency>

```



1.实现作业

```java
public class MyElasticJob  implements SimpleJob {

    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println(Thread.currentThread()+ "hello:" );
    }
}
```





定时任务配置

```java
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
```

注册中心配置

```java

  private static CoordinatorRegistryCenter createRegistryCenter() {
        CoordinatorRegistryCenter regCenter =
                new ZookeeperRegistryCenter(
                        new ZookeeperConfiguration("localhost:2181", "elastic-job-demo"));
        regCenter.init();
        return regCenter;
    }
```



执行任务

```java
      new JobScheduler(createRegistryCenter(), createJobConfiguration()).init();
```





完整的demo

```java
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
                        new ZookeeperConfiguration("localhost:2181", "elastic-job-demo"));
        regCenter.init();
        return regCenter;
    }
```





名字：demoSimpleJob      

任务计划：每15秒执行一次

分片数量： 10



注册中心：localhost:2181

命名空间：elastic-job-demo   





### 数据分片

####        将100w数据拆分为10份

 	select * from user100w

​	 执行时间： 1.46s



​	 select * from user100w  where id % 10 =0 

​	执行时间 0.5s



####  将100w数据拆分为 7份



​	 select * from user100w  where id % 7 =0 

​	执行时间 0.5s





​    

### 任务分片



**1.****平均分配算法（默认）**



   如果分片不能整除，则不能整除的多余分片将依次追加到序号小的服务器。如：



如果有3台服务器，分成9片，则每台服务器分到的分片是：1=[0,1,2], 2=[3,4,5], 3=[6,7,8]

如果有3台服务器，分成8片，则每台服务器分到的分片是：1=[0,1,6], 2=[2,3,7], 3=[4,5]

如果有3台服务器，分成10片，则每台服务器分到的分片是：1=[0,1,2,9], 2=[3,4,5], 3=[6,7,8]



**2.** **哈希值奇偶数算法**



  作业名的哈希值为奇数则IP升序。

  作业名的哈希值为偶数则IP降序。





如果有3台服务器，分成2片，作业名称的哈希值为奇数，则每台服务器分到的分片是：1=[0], 2=[1], 3=[]

如果有3台服务器，分成2片，作业名称的哈希值为偶数，则每台服务器分到的分片是：3=[0], 2=[1], 1=[]



**3.****根据作业名的哈希值对服务器列表进行轮转的分片策略。**



自定义分片

```
public interface JobShardingStrategy {
    
    /**
     * 作业分片.
     * 
     * @param jobInstances 所有参与分片的单元列表
     * @param jobName 作业名称
     * @param shardingTotalCount 分片总数
     * @return 分片结果
     */
    Map<JobInstance, List<Integer>> sharding(List<JobInstance> jobInstances, String jobName, int shardingTotalCount);
}
```







任务启动

 主节点写 服务器信息 + 配置

从节点   只会写服务信息



任务执行

都会先  判断是否需要重新分片



 强制重写配置：

```
LiteJobConfiguration.newBuilder(simpleJobConfig).overwrite(true)
```





### 流数据任务

实现流作业



```java
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
        return list;
    }

    @Override
    public void processData(ShardingContext shardingContext, List<String> data) {
        System.out.println(Thread.currentThread() + "分片id：" + shardingContext.getShardingItem() +"，获取到list:" + data);

    }
}


```



执行

```java

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
        boolean streamingProcess = false;

        DataflowJobConfiguration dataflowJobConfiguration =
                new DataflowJobConfiguration(simpleCoreConfig, MyElasticJob.class.getCanonicalName(), streamingProcess);
        // 定义Lite作业根配置
        LiteJobConfiguration dataFlowJobRootConfig =
                LiteJobConfiguration.newBuilder(dataflowJobConfiguration).overwrite(true).build();
        return dataFlowJobRootConfig;
    }

    private static CoordinatorRegistryCenter createRegistryCenter() {
        CoordinatorRegistryCenter regCenter =
                new ZookeeperRegistryCenter(
                        new ZookeeperConfiguration("localhost:2181", "elastic-job-demo1"));
        regCenter.init();
        return regCenter;
    }
}
```





随机数据为空



实现作业

```java

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

        if (new Random().nextBoolean()) {
            return new ArrayList<>();
        }
        return list;
    }

    @Override
    public void processData(ShardingContext shardingContext, List<String> data) {
        System.out.println(Thread.currentThread() + "分片id：" + shardingContext.getShardingItem() +"，获取到list:" + data);

    }
}

```



运行作业

```java
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

```





```
boolean streamingProcess = true;

DataflowJobConfiguration dataflowJobConfiguration =
        new DataflowJobConfiguration(simpleCoreConfig, MyElasticJob.class.getCanonicalName(), streamingProcess);
```

1.  streamingProcess流数据配置：

   - true .如果能取到数据，就一直取，取到为空为止,再等下一次执行
   - false .取一次数据， 执行一次， 等待下一次执行

   

### 编写脚本：ScriptJob

resources/demo.bat

```bash
@echo Sharding Context: %time% >>  d:aa.txt
```



resources/demo.sh

```sh
#!/bin/bash
echo Sharding Context:  `date`  $*
```





### 运行作业：ScriptJob

```java
public class ScriptJobDemo {

    public static void main(String[] args) throws IOException {
        new JobScheduler(createRegistryCenter(), createJobConfiguration()).init();
    }

    //配置注册中心
    private static CoordinatorRegistryCenter createRegistryCenter() {
        CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration("127.0.0.1:2181", "elastic-job-script-demo"));
        regCenter.init();
        return regCenter;
    }

    //配置job
    private static LiteJobConfiguration createJobConfiguration() throws IOException {
        JobCoreConfiguration coreConfiguration = JobCoreConfiguration.newBuilder("scriptDemoJob", "0/5 * * * * ?", 1).build();
        ScriptJobConfiguration scriptJobConfiguration = new ScriptJobConfiguration(coreConfiguration, buildScriptCommandLine());
        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(scriptJobConfiguration).overwrite(true).build();
        return liteJobConfiguration;
    }

    private static String buildScriptCommandLine() throws IOException {
        //判断当前系统
        if (System.getProperties().getProperty("os.name").contains("Windows")) {
            return Paths.get(ScriptJobDemo.class.getResource("/demo.bat").getPath().substring(1)).toString();
        }
        Path result = Paths.get(ScriptJobDemo.class.getResource("/demo.sh").getPath());
        Files.setPosixFilePermissions(result, PosixFilePermissions.fromString("rwxr-xr-x"));
        return result.toString();
    }

}
```



## 运维平台





作用： 

1监控任务

- 查看任务的状态

2  配置任务

- 全局任务的参数配置
- 分片的参数配置
-  触发，失效，修改等。





拉取源码



 https://gitee.com/elasticjob/elastic-job

https://github.com/elasticjob/elastic-job



git clone https://gitee.com/elasticjob/elastic-job -b 2.1.5  



编译打包

mvn clean install -DskipTests



取出压缩包

elastic-job-lite\elastic-job-lite-console\target\elastic-job-lite-console-2.1.5.tar.gz

解压启动

\elastic-job-lite-console-2.1.5\bin\start.bat



访问http://localhost:8899/

root/root



用户密码配置路径：

\elastic-job-lite-console-2.1.5\conf\auth.properties



新增注册中心





```
new ZookeeperConfiguration("localhost:2181", "elastic-job-demo1"));
```

注册中心地址：   任务对应的地址

命名空间：  对应定时任务的命名空间 





https://gitee.com/zhouyanjie/elastic-job-demo



**自定义参数**： 全局的配置i

**分片序列号/参数对照表**： 例如： 0=11111111,1=xxxxxxxx

根据分片的需要 配置参数  







