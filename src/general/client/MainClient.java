/*MAIN CLIENT PROGRAM*/
package general.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.swing.JOptionPane;

import general.Compress;
import general.Encrypt;
import general.Parse;
import general.FixedArray;
import general.CodeMessage;
import userinterface.MessageBox;
import userinterface.MainUI;

public class MainClient {

	protected static final String IP = "localhost";	// connecting the ip address of the server
	protected static final int port = 1221;	// connecting to the port number of the server
	protected static Socket localSocket;
	protected static MessageTextQueue textQueue;	// text queue for storing the text messages in a queue
	protected static MessageAppQueue sysQueue;	   // system messages queue for storing the system messages in queue
	protected static Client client;
	protected static ByteBuffer messageWrapper;
	protected static String[] userNames;
	protected static String[] publicRoomNames;
	protected static ArrayList<PrivateRoom> privateRooms;
	protected static Encrypt encrypt;
	protected static String userName;
	protected static int id;
	protected static Compress compress;
	protected static FixedArray<Parse> tempFiles;
	protected static ArrayList<Integer> MutedPublicRooms;
	protected static ArrayList<Integer> MutedPrivateRooms;

	public static void main(String... args) {
		init();
	}

	/*CREATING ENCRYPT AND DECRYPT OBJECT*/
	public static void init() {
		encrypt = new Encrypt();
		compress = new Compress();
	}

	/*CREATING THE SOCKET AND CONNECTING TO THE SERVER*/
	public synchronized static boolean connect(String name) {
		try {
			userName = name;
			localSocket = new Socket(IP, port);
			client = new Client();
			tempFiles = new FixedArray<Parse>(10);
			textQueue = new MessageTextQueue();
			sysQueue = new MessageAppQueue();
			textQueue.start();
			sysQueue.start();
			client.start();
			MutedPrivateRooms = new ArrayList<Integer>();
			MutedPublicRooms = new ArrayList<Integer>();

			byte[] nameBytes = name.getBytes();
			messageWrapper = ByteBuffer.allocate(1 + nameBytes.length);
			messageWrapper.put(CodeMessage.LOGIN_REQUEST);
			messageWrapper.put(nameBytes);
			return client.sendMessage(messageWrapper.array());

		} catch (IOException e) {
			System.out.println(e);

			return false;
		}
	}

public synchronized static boolean sendMessage(String message, int room,
			boolean priv) {
		if (priv) {
			if (MutedPrivateRooms.contains(new Integer(room))) {
				JOptionPane.showMessageDialog(null,
						"you are muted in this room");
				return false;
			}
		} else {
			if (MutedPublicRooms.contains(new Integer(room))) {
				JOptionPane.showMessageDialog(null,
						"you are muted in this room");
				return false;
			}
		}
		byte[] messageBytes = message.getBytes();
		messageWrapper = ByteBuffer.allocate(9 + messageBytes.length);
		messageWrapper.put((priv) ? CodeMessage.TEXT_MESSAGE_PRIV
				: CodeMessage.TEXT_MESSAGE_PUBL);
		messageWrapper.putInt(room);
		messageWrapper.putInt(id);
		messageWrapper.put(messageBytes);
		return client.sendMessage(messageWrapper.array());
	}

	public synchronized static boolean sendUserListRequest() {
		messageWrapper = ByteBuffer.allocate(5);
		messageWrapper.put(CodeMessage.USER_LIST_REQUEST);
		messageWrapper.putInt(id);
		return client.sendMessage(messageWrapper.array());
	}

	public synchronized static void getUserListReply(byte[] list) {
		messageWrapper = ByteBuffer.wrap(list);
		int users = messageWrapper.getInt();
		userNames = new String[users];
		Charset charset = Charset.defaultCharset();
		while (users-- > 0) {
			int userID = messageWrapper.getInt();

			byte tempByte = messageWrapper.get();
			int i = 1;
			while (tempByte != -1) {
				i++;
				tempByte = messageWrapper.get();
			}
			messageWrapper.position(messageWrapper.position() - i);
			byte[] name = new byte[i];
			messageWrapper.get(name);
			ByteBuffer tempBuffer = ByteBuffer.wrap(name, 0, name.length - 1);
			if (userNames.length <= userID) {
				String[] temp = new String[userID + 10];
				for (int j = 0; j < userNames.length; j++)
					temp[j] = userNames[j];
				userNames = temp;

			}
			userNames[userID] = charset.decode(tempBuffer).toString();

		}
		MainUI.showUsers(userNames);
	}

	public synchronized static boolean sendRoomListRequest() {
		messageWrapper = ByteBuffer.allocate(5);
		messageWrapper.put(CodeMessage.ROOM_LIST_REQUEST);
		messageWrapper.putInt(id);
		return client.sendMessage(messageWrapper.array());
	}

	public synchronized static void getRoomListReply(byte[] list) {
		messageWrapper = ByteBuffer.wrap(list);
		int rooms = messageWrapper.getInt();
		publicRoomNames = new String[rooms];
		Charset charset = Charset.defaultCharset();
		while (rooms-- > 0) {
			int roomID = messageWrapper.getInt();
			byte tempByte = messageWrapper.get();
			int i = 1;
			while (tempByte != -1) {
				i++;
				tempByte = messageWrapper.get();
			}
			messageWrapper.position(messageWrapper.position() - i);
			byte[] name = new byte[i];
			messageWrapper.get(name);
			ByteBuffer tempBuffer = ByteBuffer.wrap(name, 0, name.length - 1);
			if (publicRoomNames.length <= roomID) {
				String[] temp = new String[roomID + 10];
				for (int j = 0; j < publicRoomNames.length; j++)
					temp[j] = publicRoomNames[j];
				publicRoomNames = temp;

			}
			publicRoomNames[roomID] = charset.decode(tempBuffer).toString();
		}
	}
    /* getting the response for the  new private room*/
	public synchronized static void getNewRoomReplyPriv(int roomNumber) {
		for (PrivateRoom room : privateRooms) {
			if (room.id == -1) {
				room.id = roomNumber;
				MainUI.createRoom(room.name, room.id, true);
				break;
			}
		}

	}

	/* getting the response for the new public room*/
	public synchronized static void getNewRoomReplyPubl(int roomNumber,
			String name) {
		if (roomNumber >= publicRoomNames.length) {
			String[] temp = new String[roomNumber + 10];
			for (int i = 0; i < publicRoomNames.length; i++) {
				temp[i] = publicRoomNames[i];
			}
			publicRoomNames = temp;
		}

		publicRoomNames[roomNumber] = name;
		MainUI.createRoom(name, roomNumber, false);
	}

	public synchronized static void getNewRoomReplyMCPubl(int roomNumber,
			byte[] address, int port, String name) {
		if (roomNumber >= publicRoomNames.length) {
			String[] temp = new String[roomNumber + 10];
			for (int i = 0; i < publicRoomNames.length; i++) {
				temp[i] = publicRoomNames[i];
			}
			publicRoomNames = temp;
		}

		publicRoomNames[roomNumber] = name;
		try {
			InetAddress add = InetAddress.getByAddress(address);
		} catch (IOException e) {
			e.printStackTrace();
		}
		MainUI.createRoom(name, roomNumber, false);
	}

	public synchronized static void getNewRoomReplyInform(int roomNumber,
			String name) {
		if (roomNumber >= publicRoomNames.length) {
			String[] temp = new String[roomNumber + 10];
			for (int i = 0; i < publicRoomNames.length; i++) {
				temp[i] = publicRoomNames[i];
			}
			publicRoomNames = temp;
		}

		publicRoomNames[roomNumber] = name;
	}
	
	/* sending new room request*/
	public synchronized static boolean sendNewRoomRequest(String name,
			boolean priv, boolean multicast) {

		if (priv) {
			if (privateRooms == null)
				privateRooms = new ArrayList<MainClient.PrivateRoom>();
			privateRooms.add(new PrivateRoom(name));

		}

		byte[] nameBytes = name.getBytes();
		messageWrapper = ByteBuffer.allocate(1 + 4 + 1 + nameBytes.length);
		messageWrapper.put((priv) ? CodeMessage.NEW_ROOM_REQUEST_PRIV
				: CodeMessage.NEW_ROOM_REQUEST_PUBL);
		messageWrapper.putInt(id);
		if (multicast) {
			messageWrapper.put((byte) -1); // indicates a multicast session
		} else {
			messageWrapper.put((byte) -2); // indicates not a multicast session
		}
		messageWrapper.put(nameBytes);
		return client.sendMessage(messageWrapper.array());
	}

	/*sending new invitation request for the user*/
	public synchronized static boolean sendInvitationRequest(String user,
			int room) {
		int userID = -1;
		if (user.equals(userName)) {
			JOptionPane.showMessageDialog(null, "Inviting yourself ?!!");
			return false;
		} else {
			for (int i = 0; i < userNames.length; i++) {
				if (userNames[i] != null && userNames[i].equals(user)) {
					userID = i;
					break;
				}
			}
		}
			messageWrapper = ByteBuffer.allocate(1 + 4 + 4 + 4);
			messageWrapper.put(CodeMessage.INIVITE_USER_REQUEST);
			messageWrapper.putInt(id);
			messageWrapper.putInt(userID);
			messageWrapper.putInt(room);
			return client.sendMessage(messageWrapper.array());
	}

	/*getting the reply for the invitation*/
	public synchronized static void getInvitationReply(int room, String name) {
		if (privateRooms == null)
			privateRooms = new ArrayList<MainClient.PrivateRoom>();
		PrivateRoom temp = new PrivateRoom(name);
		temp.id = room;
		MainUI.joinRoom(name, room);
		privateRooms.add(temp);
	}

	/*send or join the public room*/
	public synchronized static void sendJoinPublRoom(int room) {
		messageWrapper = ByteBuffer.allocate(1 + 4 + 4);
		messageWrapper.put(CodeMessage.JOIN_PUBLIC_ROOM);
		messageWrapper.putInt(id);
		messageWrapper.putInt(room);
		client.sendMessage(messageWrapper.array());
	}

	public synchronized static int getJoinRoomId(String room) {
		for (int i = 0; i < publicRoomNames.length; i++) {
			if (publicRoomNames[i].equals(room)) {
				sendJoinPublRoom(i);
				return i;
			}
		}
		return -1;
	}

	/*function to display the message*/
	public static synchronized void displayMessage(boolean isPublic,
			int roomID, int senderID, String msg) {
		String roomName = "";
		if (isPublic)
			roomName = publicRoomNames[roomID];
		else {
			for (PrivateRoom p : privateRooms) {
				if (p.id == roomID) {
					roomName = p.name;
					break;
				}

			}
		}

		MainUI.displayMessage(senderID, userNames[senderID], roomName, msg);
	}
	
	/*function to disconnect the user*/
	public synchronized static void getDisconnectInform(int user) {
		MainUI.removeUser(userNames[user]);
		userNames[user] = null;
	}

	/*function to logoff the user*/
	public synchronized static void logoff() {
		messageWrapper = ByteBuffer.allocate(1 + 4);
		messageWrapper.put(CodeMessage.DISCONNECT_REQUEST);
		messageWrapper.putInt(id);
		client.sendMessage(messageWrapper.array());

		try {
			textQueue.stopQueue();
			sysQueue.stopQueue();
			localSocket.close();
		} catch (IOException e) {
			System.out.println(e);
		}

		MainUI.disconnect();

	}

	public synchronized static void getCloseRoomInform(int room, boolean priv) {
		String roomName = "";
		if (priv) {
			for (int i = 0; i < privateRooms.size(); i++) {
				if (privateRooms.get(i).id == room) {
					roomName = privateRooms.get(i).name;
					privateRooms.remove(i);
					break;
				}
			}
		} else {
			roomName = publicRoomNames[room];
			publicRoomNames[room] = null;
		}
		MainUI.closeRoom(roomName);
	}

	public synchronized static void sendCloseRoomRequest(int room, boolean priv) {
		messageWrapper = ByteBuffer.allocate(1 + 4 + 4);
		messageWrapper.put((priv) ? CodeMessage.CLOSE_ROOM_PRIV_REQUEST
				: CodeMessage.CLOSE_ROOM_PUBL_REQUEST);
		messageWrapper.putInt(id);
		messageWrapper.putInt(room);
		client.sendMessage(messageWrapper.array());
	}

	public synchronized static void getLoginRefreshPubl(byte[] list) {
		messageWrapper = ByteBuffer.wrap(list);
		byte nextByte = messageWrapper.get();
		while (nextByte != -2) {
			messageWrapper.position(messageWrapper.position() - 1);
			int roomID = messageWrapper.getInt();
			if (roomID == 0) {
				nextByte = messageWrapper.get();
				continue;
			}

			MainUI.createRoom(publicRoomNames[roomID], roomID, false);
			nextByte = messageWrapper.get();
		}
	}

	public synchronized static void getLoginRefreshPriv(byte[] list) {
		messageWrapper = ByteBuffer.wrap(list, 4, list.length - 4);
		privateRooms = new ArrayList<PrivateRoom>();
		Charset charset = Charset.defaultCharset();
		byte nextByte = messageWrapper.get();
		while (nextByte != -2) {
			messageWrapper.position(messageWrapper.position() - 1);
			int roomID = messageWrapper.getInt();
			byte tempByte = messageWrapper.get();
			int i = 1;
			while (tempByte != -1) {
				i++;
				tempByte = messageWrapper.get();
			}
			messageWrapper.position(messageWrapper.position() - i);
			byte[] name = new byte[i];
			messageWrapper.get(name);
			ByteBuffer tempBuffer = ByteBuffer.wrap(name, 0, name.length - 1);
			String roomName = charset.decode(tempBuffer).toString();
			PrivateRoom newPrivateRoom = new PrivateRoom(roomName);
			newPrivateRoom.id = roomID;
			privateRooms.add(newPrivateRoom);
			MainUI.createRoom(newPrivateRoom.name, newPrivateRoom.id, true);
			nextByte = messageWrapper.get();

			MainUI.showUsers(userNames);
		}

	}

	public synchronized static void getLoginReplyInform(int id, String name) {
		if (id >= userNames.length) {
			String[] temp = new String[id + 10];
			for (int i = 0; i < userNames.length; i++) {
				temp[i] = userNames[i];
			}
			userNames = temp;
		}
		userNames[id] = name;
		MainUI.updateUsers(name, userNames.length);
		MessageBox.addColors(userNames.length);
	}

	/* sending the TCP file transfer request*/
	public synchronized static void sendFileTransferTCPRequest(String user,
			String filePath) {
		int userID = -1;
		for (int i = 0; i < userNames.length; i++) {
			if (userNames[i].equals(user)) {
				userID = i;
				break;
			}
		}

		Parse file = new Parse(filePath);
		byte[] name = file.getName().getBytes();
		byte[] fileBytes = file.getFileBytes();
		Date currentDate = new Date();
		messageWrapper = ByteBuffer.allocate(1 + 4 + 8 + 4 + name.length + 1
				+ fileBytes.length);
		messageWrapper.put(CodeMessage.FILE_TRANSFER_TCP_REQUEST);
		messageWrapper.putInt(id);
		messageWrapper.putLong(currentDate.getTime());
		messageWrapper.putInt(userID);
		messageWrapper.put(name);
		messageWrapper.put((byte) -1);
		messageWrapper.put(fileBytes);
		client.sendMessage(messageWrapper.array());
	}
	
	/*getting the TCP file transfer reply*/
	public synchronized static void getFileTransferReply(int sender,
			int fileID, String fileName) {
		messageWrapper = ByteBuffer.allocate(1 + 4 + 4);
		if (MainUI.recieveFile(userNames[sender], fileName) == 1)
			messageWrapper.put(CodeMessage.FILE_TRANSFER_REFUSE);
		else
			messageWrapper.put(CodeMessage.FILE_TRANSFER_TCP_ACCEPT);

		messageWrapper.putInt(id);
		messageWrapper.putInt(fileID);
		client.sendMessage(messageWrapper.array());
	}

	public synchronized static void getFileTransferData(String fileName,
			byte[] fileBytes) {
		String filePath = MainUI.getFilePath(fileName);
		if (filePath == null)
			return;

		new Parse(null, filePath, fileBytes);
	}
	private synchronized static String[] getRoomMembersNames(int room,
			int[] membersIds) {
		String[] roomMembersNames = new String[membersIds.length];
		int id;
		for (int i = 0; i < membersIds.length; i++) {
			id = membersIds[i];
			roomMembersNames[i] = userNames[id];
		}

		return roomMembersNames;

	}

	public synchronized static void sendRoomMembersRequest(int room,
			boolean isPriv) {
		messageWrapper = ByteBuffer.allocate(1 + 4 + 4 + 1);
		messageWrapper.put(CodeMessage.ROOM_MEMBERS_IDS_LIST_REQUEST);
		messageWrapper.putInt(id);
		messageWrapper.putInt(room);
		if (isPriv) {
			messageWrapper.put((byte) 1);
		} else {
			messageWrapper.put((byte) 0);
		}
		client.sendMessage(messageWrapper.array());
	}
	public synchronized static void sendRemoveMemberRequest(int room,
			boolean isPriv, String member) {
		int memberID = -1;
		for (int i = 0; i < userNames.length; i++) {
			if (userNames[i] != null && userNames[i].equals(member)) {
				memberID = i;
			}
		}
		messageWrapper = ByteBuffer.allocate(1 + 4 + 4 + 1 + 4);
		messageWrapper.put(CodeMessage.SEND_REMOVE_MEMBER_REQUEST);
		messageWrapper.putInt(id);
		messageWrapper.putInt(room);
		if (isPriv) {
			messageWrapper.put((byte) 1);
		} else {
			messageWrapper.put((byte) 0);
		}
		messageWrapper.putInt(memberID);
		client.sendMessage(messageWrapper.array());

	}

	public static void roomMembersIDList(int roomID, byte[] info) {
		ByteBuffer messageBreaker = ByteBuffer.allocate(info.length);
		messageBreaker.put(info);
		int[] membersIds = new int[info.length / 4];
		int j = 0;
		for (int i = 0; i < membersIds.length; i++) {
			messageBreaker.position(j);
			membersIds[i] = messageBreaker.getInt();
			j = j + 4;

		}
		String[] roomMemberNames = getRoomMembersNames(roomID, membersIds);
		MainUI.getRoomMembers(roomID, roomMemberNames);

	}

	/*removing the user from the private room*/
	public synchronized static void userKicked(int roomID, boolean privateRoom) {
		if (privateRoom) {
			for (PrivateRoom p : privateRooms) {
				if (p.id == roomID) {
					JOptionPane
							.showMessageDialog(null,
									"you have been kicked out of room "
											+ p.name + " !");
					MainUI.closeRoom(p.name);
					break;
				}
			}
		} else {
			JOptionPane.showMessageDialog(null,
					"you have been kicked out room " + publicRoomNames[roomID]
							+ "!");
			MainUI.closeRoom(publicRoomNames[roomID]);
		}
	}

	public static int getUserID() {
		return id;
	}

	public static String getUserName() {
		return userName;
	}

	public static String[] getRoomNamesList() {
		return publicRoomNames;
	}
	private static class PrivateRoom {
		private String name;

		private int id;

		public PrivateRoom(String name) {
			this.name = name;
			id = -1;
		}
	}
}
