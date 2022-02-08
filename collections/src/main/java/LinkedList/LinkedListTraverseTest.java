package LinkedList;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * 测试 LinkedList 5 中遍历的效率
 *
 * 普通 for 耗时： 4474
 * 增强 for 耗时： 7
 * iterator 耗时： 6
 * foreach 耗时： 55
 * stream 耗时： 5
 */
public class LinkedListTraverseTest {

    static int xx = 0;

    static final LinkedList<Integer> list = new LinkedList<>();

    static {
        for (int i = 0; i < 100000; i++) {
            list.add(i);
        }
    }

    public static void main(String[] args) {
        testFor0();
        testFor1();
        testIterator();
        testForEach();
        testStream();
    }

    public static void testFor0() {
        long startTime = getTime();
        for (int i = 0; i < list.size(); i++) {
            xx += list.get(i);
        }
        System.out.println("普通 for 耗时： " + (getTime() - startTime));
    }

    public static void testFor1() {
        long startTime = getTime();
        for (Integer integer : list) {
            xx += integer;
        }
        System.out.println("增强 for 耗时： " + (getTime() - startTime));
    }

    public static void testIterator() {
        long startTime = getTime();
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            xx += iterator.next();
        }
        System.out.println("iterator 耗时： " + (getTime() - startTime));
    }

    public static void testForEach() {
        long startTime = getTime();
        list.forEach(e -> xx += e);
        System.out.println("foreach 耗时： " + (getTime() - startTime));
    }

    public static void testStream() {
        long startTime = getTime();
        list.stream().forEach(e -> xx += e);
        System.out.println("stream 耗时： " + (getTime() - startTime));
    }


    private static long getTime() {
        return System.currentTimeMillis();
    }
}
