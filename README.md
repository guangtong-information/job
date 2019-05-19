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



