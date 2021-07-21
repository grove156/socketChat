package org.example;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.scene.control.TextArea;

import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.Socket;

import java.nio.charset.StandardCharsets;

public class Main extends Application {

    Socket socket;
    TextArea textArea;

    //클라이언트 프로그램 동작 메소드
    public void startClient(String ip, int port){
        Thread thread = new Thread(){
          public void run(){
              try{
                  socket = new Socket(ip, port);
                  receive();
              } catch(Exception e){
                  if(!socket.isClosed()){
                      stopClient();
                      System.out.println("[서버접속실패]");
                      Platform.exit();
                  }
              }
          }
        };
        thread.start();
    }

    //클라이언트 프로그램 종료 메소드
    public void stopClient(){
        try{
            if(socket != null && !socket.isClosed()){
                socket.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //서버로부터 메세지를 전달 받는 메소드
    public void receive(){
        while(true){
            try{
                InputStream in = socket.getInputStream();
                byte[] buffer = new byte[512];
                int length = in.read(buffer);

                if(length == -1)
                    throw new IOException();

                String message = new String(buffer, 0, length, "UTF-8");
                Platform.runLater(()->{
                    textArea.appendText(message);
                });
            }catch(Exception e){
                stopClient();
                break;
            }
        }
    }

    //서버로부터 메세지를 정송하는 메소드
    public void send(String message){
        Thread thread = new Thread(){
            public void run(){
                try{
                    OutputStream out = socket.getOutputStream();
                    byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
                    out.write(buffer);
                    out.flush();
                }catch(Exception e){
                    stopClient();
                }
            }
        };
        thread.start();
    }

    //실제 프로그램을 동작시키는 메소드
    @Override
    public void start(Stage primaryStage) throws Exception{

    }

    //프로그램 진입점
    public static void main(String[] args){
        launch(args);
    }
}
