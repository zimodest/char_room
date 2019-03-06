package com.zimodest.multi_thread;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

class ReadFromServer implements  Runnable{
    private Socket client;

    public ReadFromServer(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {

        try {
            Scanner in = new Scanner(client.getInputStream());
            in.useDelimiter("\n");

            while(true){
                if(in.hasNext()){
                    System.out.println("从服务器发来的消息"+in.nextLine());
                }

                if(client.isClosed()){
                    System.out.println("客户端已经关闭");
                    break;
                }
            }
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class SendMsgToServer implements Runnable{

    private Socket client;

    public SendMsgToServer(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {

        try {
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");
            PrintStream out = new PrintStream(client.getOutputStream());

            while (true){
                System.out.println("请输入要发送的信息..");

                String strToServer;

                if(scanner.hasNext()){
                    strToServer = scanner.nextLine();
                    out.println(strToServer);
                    if(strToServer.contains("goodbye")){
                        System.out.println("关闭客户端");
                        scanner.close();
                        out.close();
                        client.close();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


public class MultiThreadClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1",6666);
            Thread readThread = new Thread(new ReadFromServer(socket));
            Thread sendThread = new Thread(new SendMsgToServer(socket));

            readThread.start();
            sendThread.start();
        }catch (IOException e){
            e.printStackTrace();
        }



    }
}
