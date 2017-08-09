/* program that implements the message sending part of the client*/
package general.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.zip.DataFormatException;

import javax.swing.JOptionPane;

public class Client extends Thread {

	private DataOutputStream outStream;
	private boolean listen;
	private DataInputStream inStream;

	public Client() {
		try {
			outStream = new DataOutputStream(
					MainClient.localSocket.getOutputStream()); // getting the data from socket
			listen = true;
			inStream = new DataInputStream(
					MainClient.localSocket.getInputStream());
		} catch (IOException e) {
			System.out.println(e);
		}

	}

	/*Message queue with encrypt and decrypt fucntionality*/
	public void run() {
		while (listen) {
			try {
				int encryptedSize = inStream.readInt();
				int rawSize = inStream.readInt();
				byte[] message = new byte[encryptedSize];
				int readSoFar = 0;
				while (readSoFar < encryptedSize)
					readSoFar += inStream.read(message, readSoFar,
							message.length - readSoFar);
				message = MainClient.encrypt.decrypt(message);
				if (rawSize != -1) {
					message = MainClient.compress.decompress(
							message, rawSize);
				}
				if (message[0] == 0x10 || message[0] == 0x00)
					MainClient.textQueue.addMessage(message);
				else
					MainClient.sysQueue.addMessage(message);
			} catch (EOFException e) {
				System.out.println("User " + MainClient.userName
						+ " is logging off");
				break;
			} catch (DataFormatException e) {
				System.out.println("Server message corrupted!");
			} catch (IOException e) {
				System.out.println(e);
				if (!listen)
					return;
				listen = false;
		//		JOptionPane.showMessageDialog(null, "Server is down!");
				MainClient.logoff();
			}

		}
	}
    /*function to send the message to the clients with compression and encryption*/
	public boolean sendMessage(byte[] message) {
		try {
			int rawSize = -1;
			int encryptedSize;
			if (rawSize >= 28) {
				message = MainClient.compress.compress(message);
				rawSize = message.length;
			}
			message = MainClient.encrypt.encrypt(message);
			encryptedSize = message.length;
			outStream.writeInt(encryptedSize);
			outStream.writeInt(rawSize);
			outStream.write(message);
			return true;
		} catch (IOException e) {
			System.out.println(e);
			return false;
		}

	}

	/*closing the input and output stream*/
	public void close() throws IOException {
		listen = false;
		outStream.close();
		inStream.close();
	}

}
