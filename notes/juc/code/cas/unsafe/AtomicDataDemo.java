package juc.code.cas.unsafe;

import juc.code.cas.Account;
import sun.misc.Unsafe;

public class AtomicDataDemo {
    public static void main(String[] args) {

        Account.demo(new Account() {

            final AtomicData atomicData = new AtomicData(10000);

            @Override
            public Integer getBalance() {
                return atomicData.getValue();
            }

            @Override
            public void withdraw(Integer amount) {
                atomicData.decrease(amount);
            }
        });
    }
}

class AtomicData {

    static final Unsafe unsafe;
    private volatile int value;
    static final long DATA_OFFSET;

    static {
        unsafe = UnsafeAccessor.getUnsafe();
        try {
            DATA_OFFSET = unsafe.objectFieldOffset(AtomicData.class.getDeclaredField("value"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public AtomicData(int value){
        this.value = value;
    }

    public void decrease(int amount) {
        int prev = value;
        int next = prev - amount;
        while (true) {
            if (unsafe.compareAndSwapInt(this, DATA_OFFSET, prev, next)) {
                break;
            }
        }
    }

    public int getValue() {
        return value;
    }
}
