/* program that will store the client program in the server side*/

package general.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.zip.DataFormatException;

import general.CodeMessage;

public class Client extends Thread {

	private int id;
	private String userName;
	private DataInputStream inStream;
	private DataOutputStream outStream;
	private Socket socket;
	private boolean open;
	private boolean doingTime; 
	public Client(Socket socket) {
		open = true;
		doingTime = false;
		try {
			this.socket = socket;
			inStream = new DataInputStream(socket.getInputStream());
			outStream = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void run() {
		try {
			byte[] loginMessage = new byte[inStream.readInt()];
			int rawSize = inStream.readInt();
			inStream.read(loginMessage);
			loginMessage = MainServer.encrypt.decrypt(loginMessage);
			if (rawSize != -1) {
				MainServer.compress
						.decompress(loginMessage, rawSize);
			}
			Charset charset = Charset.defaultCharset();
			ByteBuffer userNameBuffer = ByteBuffer
					.allocate(loginMessage.length - 1);
			userNameBuffer.put(loginMessage, 1, loginMessage.length - 1);
			userNameBuffer.rewind();
			userName = charset.decode(userNameBuffer).toString();
			if (MainServer.reinstateClient.reinstate(socket, userName))
				return;

			MainServer.createClient(this);
		} catch (DataFormatException e) {
			System.out.println("Client " + socket.getInetAddress()
					+ " : messag corrupted!");
		} catch (IOException e) {
			System.out.println(e);
		}

		while (open) {
			try {
				if (doingTime) {
					sleep(1000);
					continue;
				}
				byte[] message = new byte[inStream.readInt()];
				int rawSize = inStream.readInt();
				int readSoFar = 0;
				while (readSoFar < message.length)
					readSoFar += inStream.read(message, readSoFar,
							message.length - readSoFar);
				message = MainServer.encrypt.decrypt(message);
				if (rawSize != -1) {
					message = MainServer.compress.decompress(
							message, rawSize);
				}
				if (message[0] == CodeMessage.TEXT_MESSAGE_PRIV
						|| message[0] == CodeMessage.TEXT_MESSAGE_PUBL)
					MainServer.textQueue.addMessage(message);
				else
					MainServer.sysQueue.addMessage(message);

				if (message[0] == CodeMessage.DISCONNECT_REQUEST)
					open = false;

			} catch (IOException e) {
				System.out.println("Host " + socket.getInetAddress()
						+ " was cutoff, going to holding cell!");
				MainServer.reinstateClient.addInmate(this);
				doingTime = true;
				System.out.println(e);
			} catch (InterruptedException e) {
				System.out.println(e);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	/*function to send the message to the respective client*/
	public void sendMessage(byte[] message) {
		try {
			if (doingTime)
				return;
			int rawSize = -1;
			int encryptedSize;
			if (rawSize >= 28) {
				message = MainServer.compress.compress(message);
				rawSize = message.length;
			}
			message = MainServer.encrypt.encrypt(message);
			encryptedSize = message.length;
			outStream.writeInt(encryptedSize);
			outStream.writeInt(rawSize);
			outStream.write(message);
		} catch (IOException e) {
			System.out.println("Host " + socket.getInetAddress()
					+ " was cutoff, going to holding cell!");
			MainServer.reinstateClient.addInmate(this);
			doingTime = true;
		}
	}

	public String getUserName() {
		return userName;
	}

	public int getUserId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Socket getSocket() {
		return socket;
	}

	/* function that will reinstate the client back if it has disconnected prematurely*/
	public void reinstate(Socket socket) {
		try {
			this.socket = socket;
			inStream = new DataInputStream(socket.getInputStream());
			outStream = new DataOutputStream(socket.getOutputStream());
			doingTime = false;
			MainServer.getPrivateRoomListOnRefresh(id);
			MainServer.getUserList(id);
			MainServer.getRoomList(id);
			MainServer.getPublicRoomListOnRefresh(id);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/*function to close the sockets*/
	public void close() {
		try {
			open = false;
			inStream.close();
			outStream.close();
			socket.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

}
