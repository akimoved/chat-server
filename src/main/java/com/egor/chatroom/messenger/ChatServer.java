package com.egor.chatroom.messenger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Basic class for our chat server
 * @author Egor Akimov
 */
public class ChatServer {
	
	/**
	 * Collection of all client sockets currently connected to the chat server
	 */
	private static Vector<Socket> clientSockets;
	
	/**
	 * List of all client names currently connected to the chat server
	 */
	private static Vector<String> loginNames;
	
	/**
	 * Class constructor for starting the chat server.
	 * @throws IOException in case error occurs when communicating with the client
	 */
	public ChatServer() throws IOException {
		// server will listen on the port 5217
		ServerSocket server = new ServerSocket(5217);
		// initialize the lists for storing all connected sockets and login names
		clientSockets = new Vector<>();
		loginNames = new Vector<>();
		
		while (true) {
			// accept the connection when user has connected
			Socket client = server.accept();
			AcceptClient acceptClient = new AcceptClient(client);
		}
	}
	
	/**
	 * Auxilary class used to managing user connections
	 */
	class AcceptClient extends Thread {
		
		/**
		 * Socket client connected to the server on
		 */
		Socket clientSocket;
		
		/**
		 * Input stream for communicating with the client
		 */
		DataInputStream din;
		
		/**
		 * Output stream for communicating with the client
		 */
		DataOutputStream dout;
		
		/**
		 * Constructor used to establish connection with the user.
		 * On connection it recieves user name sent by {@link com.egor.chatroom.messenger.ChatClient} class
		 * @param client passed from the chat server when it is accepting user connection
		 * @throws IOException if error occurs when communicating with the client
		 * @see com.egor.chatroom.messenger.ChatClient#ChatClient(String)
		 */
		public AcceptClient(Socket client) throws IOException {
			// get client socket from the server
			clientSocket = client;
			
			// initialize data I/O streams for communication with the user
			din = new DataInputStream(clientSocket.getInputStream());
			dout = new DataOutputStream(clientSocket.getOutputStream());
			
			// get user name on connection
			String loginName = din.readUTF();
			
			// add new user's name and his connection socket to the corresponding lists
			loginNames.add(loginName);
			clientSockets.add(clientSocket);
			
			// start new thread for user to begin chatting with others
			start();
		}
		
		/**
		 * Method for managing user's session
		 */
		public void run() {
			while (true) {
				try {
					// get message from client and parse it
					String msgFromClient = din.readUTF();
					StringTokenizer st = new StringTokenizer(msgFromClient);
					String loginName = st.nextToken();
					String msgType = st.nextToken();
					
					// notify all users in the chatroom that new user has connected
					for (int i = 0; i < loginNames.size(); i++) {
						Socket pSocket = clientSockets.elementAt(i);
						DataOutputStream pOut = new DataOutputStream(pSocket.getOutputStream());
						pOut.writeUTF(loginName + " has logged in");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Method to start a new chat server
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {
		ChatServer server = new ChatServer();
	}
}
