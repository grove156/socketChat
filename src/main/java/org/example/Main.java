package org.example;

import javafx.application.Application;
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
            return
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

    }

    //프로그램 진입점
    public static void main(String[] args){
        launch(args);
    }
}
