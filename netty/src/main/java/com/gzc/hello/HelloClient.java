package com.gzc.hello;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

@Slf4j
public class HelloClient {

    private final Socket socket;
    public HelloClient(String host, int port) {
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(String msg) {
        try {
            socket.getOutputStream().write((msg + "\n").getBytes());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String s;
            while ((s = reader.readLine()) != null) {
                System.out.println("s = " + s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        HelloClient client = new HelloClient("127.0.0.1", 9002);
        client.send("sfjsfs");
    }
}
