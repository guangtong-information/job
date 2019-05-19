# Ealstic Job



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



## 4.带参数



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



