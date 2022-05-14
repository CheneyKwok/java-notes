package com.gzc.nio.file;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 删除文件夹
 */
public class FileDeleteTreeTest {

    public static void main(String[] args) throws IOException {

        Files.walkFileTree(Paths.get("G:\\GitHub\\rpc-framework-copy"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
        System.out.println("delete success");

    }
}
