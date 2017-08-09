/* Program that will show the application messages queue*/

package general.client;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.JOptionPane;

import general.CodeMessage;

public class MessageAppQueue extends Thread {

	private Queue<byte[]> messageQueue;
	private ByteBuffer messageBreaker;
	private boolean run;

	public MessageAppQueue() {
		messageQueue = new LinkedList<byte[]>();
	}

	public synchronized void addMessage(byte[] message) {
		messageQueue.add(message);
	}

	public void run() {
		run = true;
		while (run) {
			if (messageQueue.isEmpty()) {
				try {
					Thread.sleep(500);
					continue;
				} catch (InterruptedException e) {
					System.out.println(e);
				}
			}

			byte[] message;
			synchronized (messageQueue) {
				message = messageQueue.remove();
			}
			messageBreaker = ByteBuffer.allocate(message.length - 1);
			messageBreaker.put(message, 1, message.length - 1);
			handleMessage(message[0]);

		}
	}

	private void handleMessage(byte code) {

		switch (code) {
		/*login reply for private room*/
		case CodeMessage.LOGIN_REPLY_REFRESH_PRIV: {
			MainClient.id = messageBreaker.getInt(0);
			MainClient.getLoginRefreshPriv(messageBreaker.array());
			break;
		}

		/*login reply for public room*/
		case CodeMessage.LOGIN_REPLY_REFRESH_PUBL: {
			MainClient.getLoginRefreshPubl(messageBreaker.array());
			break;
		}

		/*successful login messages*/
		case CodeMessage.LOGIN_REPLY_SUCC: {
			MainClient.id = messageBreaker.getInt(0);
			MainClient.sendUserListRequest();
			MainClient.sendRoomListRequest();
			break;
		}

		case CodeMessage.LOGIN_REPLY_INFORM: {
			int id = messageBreaker.getInt(0);
			messageBreaker.position(4);
			String name = Charset.defaultCharset().decode(messageBreaker)
					.toString();
			MainClient.getLoginReplyInform(id, name);
			break;
		}

		case CodeMessage.LOGIN_REPLY_FAIL: {
			JOptionPane.showMessageDialog(null, "Name already in use!");
			break;
		}

		case CodeMessage.USER_LIST_REPLY: {
			MainClient.getUserListReply(messageBreaker.array());
			break;
		}

		case CodeMessage.ROOM_LIST_REPLY: {
			MainClient.getRoomListReply(messageBreaker.array());
			break;
		}

		case CodeMessage.NEW_ROOM_REPLY_PRIV: {
			int roomNumber = messageBreaker.getInt(0);
			MainClient.getNewRoomReplyPriv(roomNumber);
			break;
		}
		case CodeMessage.NEW_ROOM_REPLY_PUBL: {
			int roomNumber = messageBreaker.getInt(0);
			messageBreaker.position(4);
			String name = Charset.defaultCharset().decode(messageBreaker)
					.toString();
			MainClient.getNewRoomReplyPubl(roomNumber, name);
			break;
		}
		
		case CodeMessage.NEW_ROOM_REPLY_INFORM: {
			int roomNumber = messageBreaker.getInt(0);
			messageBreaker.position(4);
			String name = Charset.defaultCharset().decode(messageBreaker)
					.toString();
			MainClient.getNewRoomReplyInform(roomNumber, name);
			break;
		}

		/*getting the code for inviting the user*/
		case CodeMessage.INVITE_USER_REPLY: {
			int roomNumber = messageBreaker.getInt(4);
			messageBreaker.position(8);
			String name = Charset.defaultCharset().decode(messageBreaker)
					.toString();
			MainClient.getInvitationReply(roomNumber, name);
			break;
		}
				
		case CodeMessage.DISCONNECT_INFORM: {
			MainClient.getDisconnectInform(messageBreaker.getInt(0));
			break;
		}

		case CodeMessage.CLOSE_ROOM_PRIV_INFORM:
		case CodeMessage.CLOSE_ROOM_PUBL_INFORM: {
			boolean priv = false;
			if ((code & 0x10) > 0)
				priv = true;

			int room = messageBreaker.getInt(0);
			MainClient.getCloseRoomInform(room, priv);
			break;
		}

		/*getting the code for TCP reply for file transfer*/
		case CodeMessage.FILE_TRANSFER_TCP_REPLY: {
			messageBreaker.position(8);
			MainClient.getFileTransferReply(messageBreaker.getInt(0),
					messageBreaker.getInt(4),
					Charset.defaultCharset().decode(messageBreaker).toString());
			break;
		}
		
		case CodeMessage.FILE_TRANSFER_REPLY_DATA: {
			messageBreaker.rewind();
			byte temp = messageBreaker.get();
			int i = 1;
			while (temp != -1) {
				i++;
				temp = messageBreaker.get();
			}
			messageBreaker.position(messageBreaker.position() - i);
			byte[] nameBytes = new byte[i];
			messageBreaker.get(nameBytes);
			String fileName = new String(nameBytes,0,nameBytes.length-1);
			byte[] fileBytes = new byte[messageBreaker.remaining()];
			messageBreaker.get(fileBytes);
			MainClient.getFileTransferData(fileName, fileBytes);
			break;
		}
		
		case CodeMessage.FILE_TRANSFER_ECHO: {
			JOptionPane.showMessageDialog(null, "Time taken to upload file: " + messageBreaker.getLong(0)/1000.0 + " s");
			break;
		}
		
		case CodeMessage.ROOM_MEMBERS_IDS_LIST_REPLY:
		{   
			messageBreaker.position(0);
			int room = messageBreaker.getInt();
			messageBreaker.position(4);
			byte[] info = new byte[messageBreaker.remaining()];
			messageBreaker.get(info);
			MainClient.roomMembersIDList(room, info);
			break;
		}
		
		/*getting the code for sending or removing a memeber*/
		case CodeMessage.SEND_REMOVE_MEMBER_REPLY:
		{
			int roomID = messageBreaker.getInt(0);
			byte privateRoom = messageBreaker.get(4);
			MainClient.userKicked(roomID, (privateRoom > 0) ? true : false);
			break;
		}

		default:
			return;
		}
	}

	public void stopQueue() {
		run = false;
	}
}
