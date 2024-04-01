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

/**
 * Basic class for the chatroom user
 * @author Egor Akimov
 */
public class ChatClient extends JFrame implements Runnable {

	/**
	 * Default serialization UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Socket to establish connection with the server on
	 */
	Socket socket;
	
	/**
	 * JFrame text area in which messages from the chat room will be displayed
	 */
	JTextArea textArea;
	
	/**
	 * Thread object to run user session
	 */
	Thread thread;
	
	/**
	 * Input stream for recieving messages from the server
	 */
	DataInputStream din;
	
	/**
	 * Output stream for sending messages to the server
	 */
	DataOutputStream dout;
	
	/**
	 * User's name in the chatroom
	 */
	String loginName;
	
	
	
	/**
	 * Class constructor for establishing connection to the chat server.
	 * It uses fixed port 5217 and sends specified user name to the server
	 * @param loginName user name that will be used for identifying the user
	 * @throws HeadlessException
	 * @throws UnknownHostException
	 * @throws IOException
	 * @see ChatServer.AcceptClient#AcceptClient(Socket)
	 */
	public ChatClient(String loginName) throws HeadlessException, UnknownHostException, IOException {
		// calling JFrame constructor to give the window the name of the user
		super(loginName);
		
		// giving user the specified name
		this.loginName = loginName;
		
		textArea = new JTextArea(18, 50);
		
		// establishing connection with the server and getting I/O streams
		socket = new Socket("localhost", 5217);
		din = new DataInputStream(socket.getInputStream());
		dout = new DataOutputStream(socket.getOutputStream());
		
		// when connection established send user's name to the server
		dout.writeUTF(loginName);
		dout.writeUTF(loginName + " " + "LOGIN");
		
		// create new thread object and start a new thread for user session
		thread = new Thread(this);
		thread.start();
		
		// setup the JFrame parameters
		setup();
	}

	/**
	 * Method to initialize the JFrame window
	 */
	private void setup() {
		setSize(1200, 800);
		
		JPanel panel = new JPanel();
		panel.add(new JScrollPane(textArea));
		add(panel);
		
		setVisible(true);
	}

	/**
	 * Method for managing user's session
	 */
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
	
	/**
	 * Method to start a new client app and connect to the chat server
	 * @param args
	 * @throws HeadlessException
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public static void main(String args[]) throws HeadlessException, UnknownHostException, IOException {
		ChatClient client = new ChatClient("user2");
	}
}
