package com.gzc.hello;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class HelloServer {

    private static final int PORT = 9002;  //设置端口号
    private List<Socket> mList = new ArrayList<Socket>(); //用ArrayList实现多个用户
    private ServerSocket server = null;//服务器
    private ExecutorService mExecutorService = null;
    private String receiveMsg;//接受的信息
    private String sendMsg;//发送的信息

    public void start() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                ServerSocket server = new ServerSocket(PORT);
                mExecutorService = Executors.newCachedThreadPool();
                System.out.println("服务器已启动...");
                Socket client;
                while ((client = server.accept()) != null) {
                    log.info("connected {}", client);
                    mList.add(client);//客户端加入ArrayList
                    mExecutorService.execute(new Service(client));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        new HelloServer().start();
    }


    class Service implements Runnable {
        private final Socket socket;
        private BufferedReader in = null;
        private PrintWriter printWriter=null;

        public Service(Socket socket) {
            this.socket = socket;

            try {
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter( socket.getOutputStream(), StandardCharsets.UTF_8)), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                printWriter.println("成功连接服务器"+"（服务器发送）");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {

            try {
                while (true) {
                    if ((receiveMsg = in.readLine())!=null) {
                        System.out.println("receiveMsg:"+receiveMsg);
                        if (receiveMsg.equals("0")) {
                            System.out.println("客户端请求断开连接");
                            printWriter.println("服务端断开连接"+"（服务器发送）");
                            mList.remove(socket);
                            in.close();
                            socket.close();
                            break;
                        } else {
                            sendMsg = "我已接收：" + receiveMsg + "（服务器发送）";
                            printWriter.println(sendMsg);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

