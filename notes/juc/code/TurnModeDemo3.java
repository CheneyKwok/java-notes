package juc.code;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

@Slf4j
public class TurnModeDemo3 {

    static Thread t1;
    static Thread t2;
    static Thread t3;

    public static void main(String[] args) {
        ParkUnPark p = new ParkUnPark(3);
        t1 = new Thread(() -> p.print("a", t2));
        t2 = new Thread(() -> p.print("b", t3));
        t3 = new Thread(() -> p.print("c", t1));
        t1.start();
        t2.start();
        t3.start();
        LockSupport.unpark(t1);
    }

}

class ParkUnPark{
    int loopNumber;

    ParkUnPark(int loopNumber) {
        this.loopNumber = loopNumber;
    }

    public void print(String str, Thread next) {
        for (int i = 0; i < loopNumber; i++) {
            LockSupport.park();
            System.out.print(str);
            LockSupport.unpark(next);
        }
    }
}