package com.egor.chatroom.messenger;

import java.awt.HeadlessException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ChatClient extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;
	
	Socket socket;
	JTextArea textArea;
	
	Thread thread;
	
	DataInputStream din;
	DataOutputStream dout;
	
	String loginName;
	
	
	
	public ChatClient(String loginName) throws HeadlessException, UnknownHostException, IOException {
		super(loginName);
		this.loginName = loginName;
		
		textArea = new JTextArea(18, 50);
		
		socket = new Socket("localhost", 5217);
		din = new DataInputStream(socket.getInputStream());
		dout = new DataOutputStream(socket.getOutputStream());
		
		dout.writeUTF(loginName);
		dout.writeUTF(loginName + " " + "LOGIN");
		
		thread = new Thread(this);
		thread.start();
		setup();
	}

	private void setup() {
		setSize(1200, 800);
		
		JPanel panel = new JPanel();
		panel.add(new JScrollPane(textArea));
		add(panel);
		
		setVisible(true);
	}

	@Override
	public void run() {
		while (true) {
			try {
				textArea.append("\n" + din.readUTF());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String args[]) throws HeadlessException, UnknownHostException, IOException {
		ChatClient client = new ChatClient("user2");
	}
}
