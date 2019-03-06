package com.zimodest.multi_thread;


import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiThreadServer {

    //存储所有注册的客户端
    private static Map<String, Socket> clientMap = new ConcurrentHashMap<>();

    //具体处理与每个客户端通信的内部类
    private static class ExecuteClient implements Runnable{

        //获取客户端的输入流
        //注册 群聊  私聊 退出
        private Socket client;
        public ExecuteClient(Socket client){
            this.client = client;
        }

        @Override
        public void run() {
            Scanner in = null;

            try {
                in = new Scanner(client.getInputStream());
                String strFromClient = null;

                while (true) {
                    if (in.hasNext()) {
                        strFromClient = in.nextLine();
                        //对从客户端获得的字符串进行处理
                        //Windows小将默认换行/r/nr替换成空字符串
                        //将给定的正则表达式编译到模式中 userName"
                        Pattern pattern = Pattern.compile("\r");
                        Matcher matcher = pattern.matcher(strFromClient);
                        strFromClient = matcher.replaceAll("");

                        if(strFromClient.startsWith("username")){
                            String userName = strFromClient.split(":")[1];
                            registerUser(userName, client);
                            continue;
                        }

                        if(strFromClient.startsWith("G")){
                            String msg = strFromClient.split(":")[1];
                            groupChat(msg);
                            continue;
                        }

                        if(strFromClient.startsWith("P")){
                            String temp = strFromClient.split(":")[1];
                            String userName = temp.split("-")[0];
                            String msg = temp.split("-")[1];

                            privateChat(userName, msg);
                            continue;
                        }

                        if(strFromClient.contains("byebye")){
                            String userName = null;

                            for(String keyName : clientMap.keySet()){
                                if(clientMap.get(keyName).equals(client)){
                                    userName = keyName;
                                }
                            }
                            System.out.println("用户"+userName+"下线了");
                            clientMap.remove(userName);
                            continue;
                        }

                    }

                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void registerUser(String userName, Socket client){

            System.out.println("用户名称为:"+userName);
            System.out.println("用户:"+userName+"上线了");
            System.out.println("当前群聊人数为:"+(clientMap.size()+1)+"人");

            clientMap.put(userName,client);

            try {
                PrintStream out = new PrintStream(client.getOutputStream(),
                        true, "UTF-8");
                out.println("用户注册成功！");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void groupChat(String strFromClient){
            //取出clientMap中所有Entry遍历发送群聊消息
            Set<Map.Entry<String, Socket>> sockets =  clientMap.entrySet();

            for(Map.Entry<String, Socket> entry : sockets){
                Socket groupClient = entry.getValue();
                try {
                    PrintStream out = new PrintStream(groupClient.getOutputStream(),
                            true, "UTF-8");

                    out.println("群聊消息:"+strFromClient);
                } catch (IOException e) {
                    System.err.println("群聊异常，错误为"+e);
                }
            }
        }

        public void privateChat(String userName, String strFromClient){

            Socket privateClient = clientMap.get(userName);

            try {
                PrintStream out = new PrintStream(privateClient.getOutputStream(),
                        true, "UTF-8");
                out.println(strFromClient);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public static void main(String[] args) throws IOException {

            ExecutorService executorService = Executors.newFixedThreadPool(20);

            ServerSocket serverSocket = new ServerSocket(6666);
            for(int i=0; i<20; i++) {
                System.out.println("等待客户连接...");
                Socket socket = serverSocket.accept();
                System.out.println("有新的客户连接，端口为:"+socket.getPort());
                executorService.submit(new ExecuteClient(socket));
            }
            executorService.shutdown();
            serverSocket.close();






        }
    }

}
