package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {

    Socket socket;

    public Client(Socket socket){
        this.socket = socket;
        receive();
    }

    //클라이언트로 부터 메세지를 전달받는 메소드드
    public void receive(){
        Runnable thread = new Runnable() {
            @Override
            public void run() {
                try{
                    while(true){
                        InputStream in = socket.getInputStream();
                        byte[] buffer = new byte[512];
                        int length = in.read(buffer);
                        while(length == -1) throw new IOException();
                        System.out.println("[메세지 수신 성공]"
                            + socket.getRemoteSocketAddress()
                            + ": " + Thread.currentThread().getName());
                        String message = new String(buffer, 0, length,"UTF-8");
                        for(Client client : Main.clients){
                            client.send(message);
                        }
                    }
                }catch(Exception e){
                    try{
                        System.out.println("[메세지 수신 오류]"
                            + socket.getRemoteSocketAddress()
                            + ": " + Thread.currentThread());
                    }catch(Exception e2){
                        e2.printStackTrace();
                    }
                }
            }
        };
        Main.threadPool.submit(thread);
    }

    public void send(String message){
        Runnable thread = new Runnable() {
            @Override
            public void run() {
                try {
                    OutputStream out = socket.getOutputStream();
                    byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
                    out.write(buffer);
                    out.flush();
                } catch (IOException e) {
                    try{
                        System.out.println("[메세지 송신 오류]"
                                + socket.getRemoteSocketAddress()
                                + ": " + Thread.currentThread());
                        Main.clients.remove(Client.this);
                        socket.close();
                    }catch(Exception e2){
                        e2.printStackTrace();
                    }
                }
            }
        };
        Main.threadPool.submit(thread);
    }
}
