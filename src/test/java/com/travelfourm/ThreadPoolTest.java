package com.travelfourm;

import org.apache.catalina.Executor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = TravelfourmApplication.class)
public class ThreadPoolTest {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTest.class);

    //jdk普通线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    //jdk可执行定时任务的线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    private void sleep(int m){
        try {
            Thread.sleep(m);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    //1.jdk普通线程池
    @Test
    public void testExecutorService(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello Service");
            }
        };

        for (int i = 0; i < 10 ; i++){
            executorService.submit(task);
        }
        sleep(10000);
    }
}
