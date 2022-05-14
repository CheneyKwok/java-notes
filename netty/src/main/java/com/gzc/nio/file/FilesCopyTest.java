package com.gzc.nio.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 拷问文件夹
 */
public class FilesCopyTest {

    public static void main(String[] args) throws IOException {
        String source = "G:\\GitHub\\rpc-framework";
        String target = "G:\\GitHub\\rpc-framework-copy";

        Files.walk(Paths.get(source)).forEach(path -> {
            try {
                String targetPath = path.toString().replace(source, target);

                // 如果是文件夹则创建
                if (Files.isDirectory(path)) {
                    Files.createDirectory(Paths.get(targetPath));
                }
                // 如果是文件 copy
                if (Files.isRegularFile(path)) {
                    Files.copy(path, Paths.get(targetPath));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        System.out.println("copy success");
    }
}
