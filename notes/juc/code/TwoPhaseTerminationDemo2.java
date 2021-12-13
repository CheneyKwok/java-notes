package juc.code;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TwoPhaseTerminationDemo2 {

    public static void main(String[] args) throws InterruptedException {

        TwoPhaseTermination2 tpt = new TwoPhaseTermination2();
        tpt.start();
        TimeUnit.SECONDS.sleep(10);
        log.info("停止监控");
        tpt.stop();
    }
}

@Slf4j
class TwoPhaseTermination2 {

    private Thread monitor;

    private volatile boolean stop;

    // 启动监控线程
    public void start() {
        monitor = new Thread(() ->{
            while (true){
                if(stop){
                    log.info("料理后事");
                    break;
                }
                try {
                    TimeUnit.SECONDS.sleep(2);
                    log.info("执行监控");
                } catch (InterruptedException e) {
                }
            }
        });
        monitor.start();
    }

    // 停止监控线程
    public void stop() {
        stop = true;
        monitor.interrupt();
    }
}
