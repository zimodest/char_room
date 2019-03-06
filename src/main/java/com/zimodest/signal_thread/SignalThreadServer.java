package com.zimodest.signal_thread;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SignalThreadServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        Scanner readFromClient = null;
        PrintStream sendMsgToClient = null;
        Socket clientSocket = null;
        try {
            serverSocket = new ServerSocket(6666);

            System.out.println("等待客户端的连接：");
            clientSocket = serverSocket.accept();
            readFromClient = new Scanner(clientSocket.getInputStream());

            sendMsgToClient = new PrintStream(clientSocket.getOutputStream(), true, "UTF-8");

            if(readFromClient.hasNext()){
                System.out.print("客户端说:"+readFromClient.nextLine());
            }

            sendMsgToClient.print("Hi i am server\n");

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            serverSocket.close();
            readFromClient.close();
            sendMsgToClient.close();
        }


    }
}
