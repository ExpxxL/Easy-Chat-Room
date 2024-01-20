package chatroom;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;


public class Client extends JFrame{
    private String username;
    private JTextArea chatArea;
    private JTextField inputField;
    private DataOutputStream dout;

    public Client (){
        // 循环直到用户输入非空用户名

         username = JOptionPane.showInputDialog("Enter your username:");
         if (username == null || username.trim().isEmpty()) {
        	 	//!username.trim().isEmpty(): 这个条件确保经过去除空格（trim()）处理后的用户名不为空。
        	 System.exit(0);
        }
        else {
	        setTitle("聊天室应用");
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setSize(400, 300);	
	        setLocationRelativeTo(null);
	
	        chatArea = new JTextArea();
	        chatArea.setEditable(false);//不可编辑
	        JScrollPane scrollPane = new JScrollPane(chatArea);//带有滚动条的容器
	        add(scrollPane, "Center");
	
	        JPanel panel = new JPanel();
	        inputField = new JTextField(20);
	        panel.add(inputField);
	
	        JButton sendButton = new JButton("Send");
	        sendButton.addActionListener(e -> sendMessage());
	        panel.add(sendButton);
	
	        add(panel, "South");
	
	        setVisible(true);//窗口可见
	
	        connectToServer();//连接服务器
        }
    }

    private void connectToServer() {
        //连接服务器
        try {
            Socket socket = new Socket("localhost", 6666);
            dout = new DataOutputStream(socket.getOutputStream());//字节输出流转化为数据输出流
            DataInputStream din = new DataInputStream(socket.getInputStream());//同
            dout.writeUTF(username);//对应Server文件的dis.readUTF();

            new Thread(new Runnable() {
                public void run() {
                    try {
                        while (true){
                            String message = din.readUTF();
                            chatArea.append(message + "\n");
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        try {
            String message = inputField.getText();//获取聊天框消息
            dout.writeUTF(message);
            inputField.setText("");//清空聊天框
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void closeConnection() {
        // 关闭与服务器的连接等清理操作
        try {
            if (dout != null) {
                dout.writeUTF("exit"); // 发送退出消息给服务器
                dout.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        new Client();
    }
}
