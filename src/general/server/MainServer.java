/* program where the main code for the server is implemented*/

package general.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import general.Compress;
import general.Encrypt;
import general.Parse;
import general.FixedArray;
import general.CodeMessage;

public class MainServer {

	protected static MessageAppQueue sysQueue;		// declaring the system messages queue
	protected static MessageTextQueue textQueue;	// declaring the text messages queue
	protected static FixedArray<Client> clients;			// array list of clients
	protected static FixedArray<ArrayList<Integer>> privateRooms;	// array list for members in private room
	protected static FixedArray<ArrayList<Integer>> publicRooms; // array list for members in public room
	protected static FixedArray<String> publicRoomNames;
	protected static FixedArray<String> privateRoomNames;
	protected static Server socketListener;
	protected static ReinstateClient reinstateClient;
	protected static Encrypt encrypt;
	protected static FixedArray<Parse> files;
	protected static Compress compress;

	public static void main(String... args) {
		init();
	}

	/* object creation for all the functionalities in the application*/
	public static void init() {
		encrypt = new Encrypt();
		compress = new Compress();
		sysQueue = new MessageAppQueue();
		textQueue = new MessageTextQueue();
		clients = new FixedArray<Client>(10);
		privateRooms = new FixedArray<ArrayList<Integer>>(10);
		publicRooms = new FixedArray<ArrayList<Integer>>(10);
		publicRoomNames = new FixedArray<String>(10);
		privateRoomNames = new FixedArray<String>(10);
		files = new FixedArray<Parse>(5);
		socketListener = new Server();
		reinstateClient = new ReinstateClient();
		publicRooms.addItem(new ArrayList<Integer>());
		publicRoomNames.addItem("General");
		sysQueue.start();
		textQueue.start();
		socketListener.start();
	}

	/*function to remove a client*/
	protected synchronized static void removeClient(int user) {
		int r = 0;
		for (ArrayList<Integer> room : publicRooms) {
			if (room == null)
				break;
			room.remove(new Integer(user));
			if (room.size() == 0 && r != 0)
				closeRoom(r, false);
			r++;
		}
		r = 0;
		for (ArrayList<Integer> room : privateRooms) {
			if (room == null)
				break;
			room.remove(new Integer(user));
			if (room.size() == 0)
				closeRoom(r, true);
			r++;
		}

		ByteBuffer reply = ByteBuffer.allocate(1 + 4);
		reply.put(CodeMessage.DISCONNECT_INFORM);
		reply.putInt(user);
		for (Client c : clients) {
			if (c == null || c.getUserId() == user)
				continue;
			c.sendMessage(reply.array());
		}
		clients.get(user).close();
		clients.removeItem(user);
	}

	protected synchronized static ArrayList<Integer> getRoomClients(int room,
			boolean privateRoom) {
		return (privateRoom) ? privateRooms.get(room) : publicRooms.get(room);
	}

	/*function to create a client*/ 
	protected synchronized static void createClient(Client client) {
		String name = client.getUserName();
		for (Client c : clients) {
			if (c == null)
				break;
			if (name.equals(c.getUserName())) {
				byte[] message = new byte[1];
				message[0] = CodeMessage.LOGIN_REPLY_FAIL;
				client.sendMessage(message);
				client.close();
				return;
			}
		}

		int clientID = clients.addItem(client);
		client.setId(clientID);
		publicRooms.get(0).add(clientID);
		ByteBuffer message = ByteBuffer.allocate(5);
		message.put(CodeMessage.LOGIN_REPLY_SUCC);
		message.putInt(clientID);
		client.sendMessage(message.array());
		ArrayList<Integer> roomClients = publicRooms.get(0);
		message = ByteBuffer.allocate(1 + 4 + name.getBytes().length);
		message.put(CodeMessage.LOGIN_REPLY_INFORM);
		message.putInt(clientID);
		message.put(name.getBytes());
		for (int c : roomClients) {
			if (c == clientID)
				continue;
			MainServer.clients.get(c).sendMessage(message.array());
		}
	}

	/*function to get the list of users*/
	protected synchronized static void getUserList(int sender) {
		ByteBuffer clientList = ByteBuffer.allocate(5 + 4 * clients.size());
		clientList.put(CodeMessage.USER_LIST_REPLY);
		clientList.putInt(clients.numberOfElements());
		for (Client c : clients) {
			if (c == null)
				continue;
			clientList.putInt(c.getUserId());
			byte[] name = c.getUserName().getBytes();

			ByteBuffer temp = ByteBuffer.allocate(clientList.capacity()
					+ name.length + 1);
			temp.put(clientList.array());
			temp.position(clientList.position());
			clientList = temp;

			clientList.put(c.getUserName().getBytes());
			clientList.put((byte) -1);
		}
		clients.get(sender).sendMessage(clientList.array());

	}

	/*function to get the list of rooms*/
	protected synchronized static void getRoomList(int sender) {
		ByteBuffer roomList = ByteBuffer.allocate(5 + 4 * publicRooms.size());
		roomList.put(CodeMessage.ROOM_LIST_REPLY);
		roomList.putInt(publicRooms.numberOfElements());
		int i = 0;
		for (String room : publicRoomNames) {
			if (room != null) {
				roomList.putInt(i);
				byte[] roomName = room.getBytes();

				ByteBuffer temp = ByteBuffer.allocate(roomList.capacity()
						+ roomName.length + 1);
				temp.put(roomList.array());
				temp.position(roomList.position());
				roomList = temp;

				roomList.put(roomName);
				roomList.put((byte) -1);
			}
			i++;
		}
		clients.get(sender).sendMessage(roomList.array());

	}

	/*function to create the room*/
	protected synchronized static void createRoom(int sender, String roomName,
			boolean privateRoom, boolean multicast) {
		if (privateRoom) {
			int roomID = privateRooms.addItem(new ArrayList<Integer>());
			privateRooms.get(roomID).add(sender);
			privateRoomNames.addItem(roomName);
			byte[] roomNameBytes = roomName.getBytes();
			ByteBuffer reply = ByteBuffer.allocate(5 + roomNameBytes.length);
			reply.put(CodeMessage.NEW_ROOM_REPLY_PRIV);
			reply.putInt(roomID);
			reply.put(roomNameBytes);
			clients.get(sender).sendMessage(reply.array());

		} else {
			int roomID = publicRooms.addItem(new ArrayList<Integer>());
			publicRooms.get(roomID).add(sender);
			publicRoomNames.addItem(roomName);
			byte[] roomNameBytes = roomName.getBytes();
			ByteBuffer reply = ByteBuffer
					.allocate(1 + 4 + roomNameBytes.length);
			reply.put(CodeMessage.NEW_ROOM_REPLY_INFORM);
			reply.putInt(roomID);
			reply.put(roomNameBytes);
			ArrayList<Integer> roomClients = publicRooms.get(0);
			for (int c : roomClients) {
				if (c == sender)
					continue;
				MainServer.clients.get(c).sendMessage(reply.array());
			}
			reply.clear();
			reply.put(CodeMessage.NEW_ROOM_REPLY_PUBL);
			reply.putInt(roomID);
			reply.put(roomNameBytes);
			System.out.println(Arrays.toString(reply.array()));
			clients.get(sender).sendMessage(reply.array());
		}
	}

	/*function to invite the user*/
	protected synchronized static void inviteUser(int sender, int invited,
			int room) {
		privateRooms.get(room).add(invited);
		byte[] roomName = privateRoomNames.get(room).getBytes();
		ByteBuffer reply = ByteBuffer.allocate(roomName.length + 1 + 4 + 4);
		reply.put(CodeMessage.INVITE_USER_REPLY);
		reply.putInt(sender);
		reply.putInt(room);
		reply.put(roomName);
		clients.get(invited).sendMessage(reply.array());
	}
	protected synchronized static void joinRoom(int sender, int room) {
		publicRooms.get(room).add(sender);
		}

	protected synchronized static void closeRoom(int room, boolean privateRoom) {
		ArrayList<Integer> roomClients = (privateRoom) ? privateRooms.get(room)
				: publicRooms.get(room);
		if (privateRoom) {
			privateRooms.removeItem(room);
			privateRoomNames.removeItem(room);
		} else {
			publicRooms.removeItem(room);
			publicRoomNames.removeItem(room);
		}
		ByteBuffer reply = ByteBuffer.allocate(1 + 4);
		reply.put((privateRoom) ? CodeMessage.CLOSE_ROOM_PRIV_INFORM
				: CodeMessage.CLOSE_ROOM_PUBL_INFORM);
		reply.putInt(room);
		for (int client : roomClients) {
			clients.get(client).sendMessage(reply.array());
		}
	}

	protected synchronized static void getPublicRoomListOnRefresh(int user) {
		ByteBuffer roomList = ByteBuffer.allocate(5 + 1 + 12 * publicRooms
				.size());
		roomList.put(CodeMessage.LOGIN_REPLY_REFRESH_PUBL);
		int i = 0;
		for (ArrayList<Integer> room : publicRooms) {
			if (room != null && room.contains(user))
				roomList.putInt(i);
			i++;
		}
		roomList.put((byte) -2);
		clients.get(user).sendMessage(roomList.array());

	}

	protected synchronized static void getPrivateRoomListOnRefresh(int user) {
		ByteBuffer reply = ByteBuffer.allocate(1 + 4 + 1 + 4
				* privateRooms.size());
		reply.put(CodeMessage.LOGIN_REPLY_REFRESH_PRIV);
		reply.putInt(user);
		int i = 0;
		for (ArrayList<Integer> room : privateRooms) {
			if (room != null && room.contains(user)) {
				reply.putInt(i);
				byte[] roomName = privateRoomNames.get(i).getBytes();
				if (reply.remaining() < roomName.length) {
					ByteBuffer temp = ByteBuffer.allocate(reply.capacity()
							+ roomName.length + 1);
					temp.put(reply);
					reply = temp;
				}
				reply.put(roomName);
				reply.put((byte) -1);
			}
			i++;
		}
		reply.put((byte) -2);

		clients.get(user).sendMessage(reply.array());
	}

	/*function to get the fle transfer request from the client*/
	protected synchronized static void getFileTransferRequest(int sender,
			int reciever, long timeStamp, String fileName, byte[] fileBytes) {
		Parse file = new Parse(files, fileName, fileBytes);
		int fileID = files.addItem(file);

		ByteBuffer reply = ByteBuffer.allocate(1 + 8);
		reply.put(CodeMessage.FILE_TRANSFER_ECHO);
		Date currentTime = new Date();
		reply.putLong(currentTime.getTime() - timeStamp);
		clients.get(sender).sendMessage(reply.array());

		reply = ByteBuffer.allocate(1 + 4 + 4 + fileName.getBytes().length);
		reply.put(CodeMessage.FILE_TRANSFER_TCP_REPLY);
		reply.putInt(sender);
		reply.putInt(fileID);
		reply.put(fileName.getBytes());
		clients.get(reciever).sendMessage(reply.array());
	}

	protected synchronized static void getFileTransferTCPReply(int sender,
			int fileID, boolean accept) {
		if (!accept) {
			Parse fp = files.get(fileID);
			fp.cancel();
			fp.deleteFile();
			files.removeItem(fileID);
		}
		Parse fp = files.get(fileID);
		byte[] nameBytes = fp.getName().getBytes();
		byte[] fileBytes = fp.getFileBytes();
		ByteBuffer reply = ByteBuffer.allocate(1 + nameBytes.length + 1
				+ fileBytes.length);
		reply.put(CodeMessage.FILE_TRANSFER_REPLY_DATA);
		reply.put(nameBytes);
		reply.put((byte) -1);
		reply.put(fileBytes);
		clients.get(sender).sendMessage(reply.array());
	}
	protected synchronized static void getMembersList(int sender, int room,
			boolean privRoom) {
		ArrayList<Integer> membersId;
		membersId = MainServer.getRoomClients(room, privRoom);
		ByteBuffer roomMembersId = ByteBuffer.allocate(1 + 4 + 4 * membersId
				.size());
		roomMembersId.put(CodeMessage.ROOM_MEMBERS_IDS_LIST_REPLY);
		roomMembersId.putInt(room);
		for (int i = 0; i < membersId.size(); i++) {
			roomMembersId.putInt(membersId.get(i).intValue());
		}
		clients.get(sender).sendMessage(roomMembersId.array());
	}
	protected synchronized static void RemoveRoomMember(int sender, int room,
			boolean isPriv, int member) {
		if (isPriv) {
			for (int i = 0; i < privateRooms.get(room).size(); i++) {
				if ((privateRooms.get(room).get(i)) == member) {
					privateRooms.get(room).remove(i);
				}
			}
		} else {
			for (int i = 0; i < publicRooms.get(room).size(); i++) {
				if ((publicRooms.get(room).get(i)) == member) {
					publicRooms.get(room).remove(i);
					System.out.println("shalet");
				}
			}
		}

		ByteBuffer reply = ByteBuffer.allocate(1 + 4 + 1);
		reply.put(CodeMessage.SEND_REMOVE_MEMBER_REPLY);
		reply.putInt(room);
		reply.put((isPriv) ? (byte) 1 : (byte) 0);
		clients.get(member).sendMessage(reply.array());
	}
}
