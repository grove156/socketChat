package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {

    public static ExecutorService threadPool;
    public static Vector<Client> clients = new Vector<Client>();
    public ServerSocket serverSocket;

    //서버를 구동시키는 메소드
    public void startServer(String ip, int port){
        try{
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ip, port));
        }catch(Exception e){
            if(!serverSocket.isClosed()){
                stopServer();
            }
            return;
        }

        //클라이언트를 기다리는 서버소켓 스레드
        Runnable thread = new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        Socket socket = serverSocket.accept();
                        clients.add(new Client(socket));
                        System.out.println("[클라이언트 접속]"
                                + socket.getRemoteSocketAddress()
                                + ": " + Thread.currentThread().getName()
                        );
                    }catch(Exception e){
                        if(!serverSocket.isClosed()){
                            stopServer();
                        }
                        break;
                    }
                }
            }
        };
        threadPool = Executors.newCachedThreadPool();
        threadPool.submit(thread);
    }

    //서버의 작동을 중지시키는 메소드
    public void stopServer(){
        //현재 작동중인 모든 소켓을 닫음
        try{
            Iterator<Client> iterator = clients.iterator();
            while(iterator.hasNext()){
                Client client = iterator.next();
                client.socket.close();
                iterator.remove();
            }
            if(serverSocket != null && !serverSocket.isClosed()){
                serverSocket.close();
            }
            if(threadPool != null && !threadPool.isShutdown()){
                threadPool.shutdown();
            }
        }catch(Exception e){

        }

    }

    //UI를 생성, 서버를 포함한 UI를 실행시키는 메소드
    @Override
    public void start(Stage primaryStage) throws Exception{
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(5));

        TextArea textArea = new TextArea();
        textArea.setEditable(true);
        textArea.setFont(new Font("나눔고딕", 15));

        root.setCenter(textArea);

        Button toggleButton = new Button("시작하기");
        toggleButton.setMaxWidth(Double.MAX_VALUE);
        BorderPane.setMargin(toggleButton, new Insets(1, 0, 0, 0));
        root.setBottom(toggleButton);

        String ip = "127.0.0.1";
        int port = 9876;

        toggleButton.setOnAction(event -> {
            if(toggleButton.getText().equals("시작하기")){
                startServer(ip, port);
                Platform.runLater(()->{
                    String message = String.format("[서버시작]\n",ip, port);
                    textArea.appendText(message);
                    toggleButton.setText("종료하기");
                });
            }else{
                stopServer();
                Platform.runLater(()->{
                    String message = String.format("[서버종료]\n",ip, port);
                    textArea.appendText(message);
                    toggleButton.setText("시작하기");
                });
            }
        });

        Scene scene = new Scene(root, 400, 400);
        primaryStage.setTitle("[채팅서버]");
        primaryStage.setOnCloseRequest(event -> stopServer());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //프로그램 진입점
    public static void main(String[] args){
        launch(args);
    }
}
