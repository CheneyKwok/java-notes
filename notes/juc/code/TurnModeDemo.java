package juc.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TurnModeDemo {
}

/**
 * 输出内容     等待标记      下个标记
 *   a          1            2
 *   b          2            3
 *   c          3            1
 */
class WaitNotify{

    /**
     * 等待标记
     */
    private int flag;

    /**
     * 循环次数
     */
    private int loopNumber;

    public WaitNotify(int flag, int loopNumber) {
        this.flag = flag;
        this.loopNumber = loopNumber;
    }

    public void print(String str, int waitFlag, int nextFlag) {
        synchronized (this) {
            while (waitFlag != flag) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.print(str);
        }
    }
}
