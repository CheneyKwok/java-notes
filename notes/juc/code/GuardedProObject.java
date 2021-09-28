package juc.code;

/**
 * 与 join 的实现原理一致
 */
class GuardedProObject {
    // 结果
    private Object response;
    public Object get(long timeout) {
        synchronized (this) {
            // 开始时间
            long begin = System.currentTimeMillis();
            // 经历的时间
            long passTime = 0;
            while (response == null){
                long waitTime = timeout - passTime;
                // 如果经历的时间超过了最大等待时间，退出循环
                if (waitTime <= 0){
                    break;
                }
                try {
                    this.wait(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 求得经历时间
                passTime = System.currentTimeMillis() - begin;
            }
            return response;
        }
    }
    public void complete(Object obj) {
        synchronized (this){
            this.response = obj;
            this.notifyAll();
        }
    }
}
