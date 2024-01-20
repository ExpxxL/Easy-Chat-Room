package chatroom;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
    private ArrayList<MySocket> sockets;

    public Server() {
        sockets = new ArrayList<>();
    }

    private class MySocket {
        private Socket socket;
        private String user;

        public MySocket(Socket socket, String user) {
            this.socket = socket;
            this.user = user;
        }
    }

    private class ServerThread implements Runnable {
        private Socket socket;
        private String user;

        public ServerThread(Socket socket, String user) {
            this.socket = socket;
            this.user = user;
        }

        @Override
        public void run() {
            try (InputStream input = socket.getInputStream();
                DataInputStream din = new DataInputStream(input);
                OutputStream output = socket.getOutputStream();
                DataOutputStream dout = new DataOutputStream(output)) {

                while (true) {
                    String message = din.readUTF();
                    if (message.equals("exit")) {
                        //无法再聊天了
                        break;
                    }
                    sendMessage(user + ": " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            removeSocket(user);
        }
    }

    private void sendMessage(String message) {
        //发送消息
        for (MySocket mySocket : sockets) {
            //每个Socket都发
            try {
                OutputStream output = mySocket.socket.getOutputStream();
                DataOutputStream dout = new DataOutputStream(output);
                dout.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void removeSocket(String user) {
        //Socket移除
        MySocket socketToRemove = null;
        for (MySocket mySocket : sockets) {
            if (mySocket.user.equals(user)) {
                socketToRemove = mySocket;
                break;
            }
        }
        if (socketToRemove != null) {
            sockets.remove(socketToRemove);
            sendMessage(user + " has left the chat.");
        }
    }

    public void start() {
        System.out.println("服务器准备就绪,端口为"+6666);
        try (ServerSocket serverSocket = new ServerSocket(6666)) {
            while (true) {
                //每多一个连接
                Socket socket = serverSocket.accept();

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                String user = dis.readUTF();

                MySocket mySocket = new MySocket(socket, user);
                sockets.add(mySocket);

                new Thread(new ServerThread(socket, user)).start();

                sendMessage(user + " has joined the chat.");//加入聊天
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}