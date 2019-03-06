package com.zimodest.signal_thread;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class SignalTreadClient {

    public static void main(String[] args) throws IOException {

        Socket clientSocket = null;
        Scanner readFromServer = null;
        PrintStream writeMsgToServer = null;
        try {
            clientSocket = new Socket("127.0.0.1", 6666);

            readFromServer = new Scanner(clientSocket.getInputStream());

            writeMsgToServer = new PrintStream(clientSocket.getOutputStream(), true, "UTF-8");

            writeMsgToServer.print("Hi i am client\n");
            if(readFromServer.hasNext()){
                System.out.print("服务器端发来的消息"+readFromServer.nextLine());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            clientSocket.close();
            readFromServer.close();
            writeMsgToServer.close();
        }


    }
}
