/*program to store the system messages queue*/

package general.server;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Queue;

import general.CodeMessage;

public class MessageAppQueue extends Thread {

	private Queue<byte[]> messageQueue;
	private ByteBuffer messageBreaker;

	public MessageAppQueue() {
		messageQueue = new LinkedList<byte[]>();		// creating the link
	}

	public synchronized void addMessage(byte[] message) {
		messageQueue.add(message);
	}

	public void run() {
		while (true) {
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
			messageBreaker = ByteBuffer.allocate(message.length - 1);	// allocating the message length
			messageBreaker.put(message, 1, message.length - 1);
			handleMessage(message[0]);

		}
	}

	private void handleMessage(byte code) {
		int sender = messageBreaker.getInt(0);
		boolean privateRoom = false;
		if ((0x10 & code) > 0)
			privateRoom = true;
		switch (code) {

		case CodeMessage.USER_LIST_REQUEST: {
			MainServer.getUserList(sender);
			break;
		}

		case CodeMessage.ROOM_LIST_REQUEST: {
			MainServer.getRoomList(sender);
			break;
		}

		case CodeMessage.NEW_ROOM_REQUEST_PRIV:
		case CodeMessage.NEW_ROOM_REQUEST_PUBL: {
			boolean multicast = messageBreaker.get(4) == -1 ? true : false;
			byte[] nameBytes = new byte[messageBreaker.capacity() - 5];
			messageBreaker.position(5);
			messageBreaker.get(nameBytes, 0, nameBytes.length);
			Charset charset = Charset.defaultCharset();
			String roomName = charset.decode(ByteBuffer.wrap(nameBytes))
					.toString();
			MainServer.createRoom(sender, roomName, privateRoom, multicast);
			break;
		}

		/*code to request for user invite*/
		case CodeMessage.INIVITE_USER_REQUEST: {
			int invited = messageBreaker.getInt(4);
			int roomNumber = messageBreaker.getInt(8);
			MainServer.inviteUser(sender, invited, roomNumber);
			break;
		}
		
		/*code to join a public room*/
		case CodeMessage.JOIN_PUBLIC_ROOM: {
			int roomNumber = messageBreaker.getInt(4);
			MainServer.joinRoom(sender, roomNumber);
			break;
		}

		case CodeMessage.DISCONNECT_REQUEST:
			MainServer.removeClient(sender);
			break;

		case CodeMessage.CLOSE_ROOM_PUBL_REQUEST:
		case CodeMessage.CLOSE_ROOM_PRIV_REQUEST: {
			int roomNumber = messageBreaker.getInt(4);
			MainServer.closeRoom(roomNumber, privateRoom);
			break;
		}

		/*code to transfer the files for tcp request*/
		case CodeMessage.FILE_TRANSFER_TCP_REQUEST: {
			long timeStamp = messageBreaker.getLong(4);
			int reciever = messageBreaker.getInt(12);
			messageBreaker.position(16);
			byte temp = messageBreaker.get();
			int i = 1;
			while (temp != -1) {
				i++;
				temp = messageBreaker.get();
			}
			messageBreaker.position(messageBreaker.position() - i);
			byte[] nameBytes = new byte[i];
			messageBreaker.get(nameBytes);
			String fileName = new String(nameBytes, 0, nameBytes.length - 1);
			byte[] fileBytes = new byte[messageBreaker.remaining()];
			messageBreaker.get(fileBytes);

			MainServer.getFileTransferRequest(sender, reciever, timeStamp,
					fileName, fileBytes);
			break;
		}
		
		/* code to transfer the files for tcp accept*/
		case CodeMessage.FILE_TRANSFER_TCP_ACCEPT: {
			MainServer.getFileTransferTCPReply(sender,
					messageBreaker.getInt(4), true);
			break;
		}
		case CodeMessage.FILE_TRANSFER_REFUSE: {
			MainServer.getFileTransferTCPReply(sender,
					messageBreaker.getInt(4), false);
			break;
		}
		case CodeMessage.ROOM_MEMBERS_IDS_LIST_REQUEST: {
			messageBreaker.position(4);
			int room = messageBreaker.getInt();
			messageBreaker.position(8);
			boolean isPriv;
			isPriv = (messageBreaker.get() == 1) ? true : false;
			MainServer.getMembersList(sender, room, isPriv);
			break;
		}

		/*code to remove a member*/
		case CodeMessage.SEND_REMOVE_MEMBER_REQUEST: {
			messageBreaker.position(4);
			int room = messageBreaker.getInt();
			messageBreaker.position(8);
			boolean isPriv;
			isPriv = (messageBreaker.get() == 1) ? true : false;
			messageBreaker.position(9);
			int member = messageBreaker.getInt();
			MainServer.RemoveRoomMember(sender, room, isPriv, member);
			break;
		}
		default:
			return;
		}
	}
}
