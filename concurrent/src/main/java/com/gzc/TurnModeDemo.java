package com.gzc;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TurnModeDemo {

    public static void main(String[] args) {

        WaitNotify wt = new WaitNotify(1, 3);
        new Thread(() -> wt.print("a", 1, 2)).start();
        new Thread(() -> wt.print("b", 2, 3)).start();
        new Thread(() -> wt.print("c", 3, 1)).start();


    }
}

/**
 * 输出内容     等待标记      下个标记
 *   a          1            2
 *   b          2            3
 *   c          3            1
 */
class WaitNotify{
    int flag;
    int number;

    public WaitNotify(int flag, int number) {
        this.flag = flag;
        this.number = number;
    }

    public void print(String s, int tFlag, int nextFlag) {
        for (int i = 0; i < number; i++) {
            synchronized (this) {
                while (tFlag != flag) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println(s);
                this.flag = nextFlag;
                notifyAll();
            }

        }
    }

}
