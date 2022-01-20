package com.gzc.concurrenthashmap;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * ConcurrentHashMap 错误用法演示
 */
public class ConcurrentHashMapIncorrectDemo {

    public static void main(String[] args) {
//        generate();
        // HashMap 实现 -> incorrect
//        demo((Supplier<Map<String, Integer>>) HashMap::new, (map, list) -> {
//            for (String word : list) {
//                Integer count = map.get(word);
//                int newCount = count == null ? 1 : count + 1;
//                map.put(word, newCount);
//            }
//        });

        // ConcurrentHashMap 实现 -> incorrect
//        demo((Supplier<Map<String, Integer>>) ConcurrentHashMap::new, (map, list) -> {
//            for (String word : list) {
//                Integer count = map.get(word);
//                int newCount = count == null ? 1 : count + 1;
//                map.put(word, newCount);
//            }
//        });

        // ConcurrentHashMap + synchronized 实现
//        demo((Supplier<Map<String, Integer>>) ConcurrentHashMap::new, (map, list) -> {
//            for (String word : list) {
//                synchronized (map) {
//                    Integer count = map.get(word);
//                    int newCount = count == null ? 1 : count + 1;
//                    map.put(word, newCount);
//                }
//            }
//        });

        // ConcurrentHashMap.merge() 实现
//        demo((Supplier<Map<String, Integer>>) ConcurrentHashMap::new, (map, list) -> {
//            for (String word : list) {
//                map.merge(word, 1, Integer::sum);
//            }
//        });

        // ConcurrentHashMap.computeIfAbsent() + LongAdder 实现
        demo((Supplier<Map<String, LongAdder>>) ConcurrentHashMap::new, (map, list) -> {
            for (String word : list) {
                LongAdder value = map.computeIfAbsent(word, k -> new LongAdder());
                // 累加操作有原子累加器执行
                value.increment();
            }
        });
    }

    static <V> void demo(Supplier<Map<String, V>> supplier, BiConsumer<Map<String, V>, List<String>> consumer) {
        Map<String, V> counterMap = supplier.get();
        List<Thread> ts = new ArrayList<>();
        for (int i = 1; i <= 26; i++) {
            int idx = i;
            Thread thread = new Thread(() -> {
                List<String> words = readFromFile(idx);
                consumer.accept(counterMap, words);
            });
            ts.add(thread);
        }
        ts.forEach(Thread::start);
        for (Thread t : ts) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(counterMap);
    }

    /**
     * 读文件
     */
    static List<String> readFromFile(int i) {
        List<String> words = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("tmp/" + i + ".txt")))) {
            while (true) {
                String word = in.readLine();
                if (word == null) {
                    break;
                }
                words.add(word);
            }
            return words;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成测试数据
     */
    static void generate() {
        String ALPHA = "abcdefghijklmnopqrstuvwxyz";
        int length = ALPHA.length();
        int count = 200;
        List<String> list = new ArrayList<>(length * count);
        for (int i = 0; i < length; i++) {
            char ch = ALPHA.charAt(i);
            for (int j = 0; j < count; j++) {
                list.add(String.valueOf(ch));
            }
        }
        Collections.shuffle(list);
        for (int i = 0; i < 26; i++) {
            try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.dir") + "/tmp/" + (i + 1) + ".txt")))) {
                String collect = list.subList(i * count, (i + 1) * count).stream().collect(Collectors.joining("\n"));
                out.print(collect);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
