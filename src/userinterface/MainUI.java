package userinterface;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import general.client.MainClient;

public class MainUI {

	protected static LogWindow loginFrame;
	protected static MainChat chatFrame;
	protected static RoomCreation roomCreationFrame;
	protected static JFrame publicRoomsFrame;

	public static void main(String... args) {
		MainClient.init();
		init();		
	}

	private static void init() {

		try {
			LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
			UIManager.setLookAndFeel(info[1].getClassName());
		} catch (Exception exp) {
			System.out.println(exp);
		}

		loginFrame = new LogWindow();
		loginFrame.setVisible(true);
	}

	public static void connect() {
		String userName = loginFrame.getUserName();
		loginFrame.setVisible(false);
		chatFrame = new MainChat(userName);
	}

	public static void disconnect() {
		loginFrame.setVisible(true);
		chatFrame.dispose();
		chatFrame = null;
	}

	public static void createRoom(String name, int id, boolean priv) {
		chatFrame.createRoom(name, id, priv);
	}

	protected static void joinRoom(String name) {
		chatFrame.joinRoom(name, MainClient.getJoinRoomId(name));
	}

	public static void joinRoom(String name, int id) {
		chatFrame.joinRoom(name, id);
	}

	protected static void leaveRoom(String name) {
		chatFrame.leaveRoom(name);
	}

	protected static void showPublicRooms() {
		publicRoomsFrame = new JFrame("Public Rooms available");
		publicRoomsFrame.setSize(250, 300);
		publicRoomsFrame.setResizable(false);
		publicRoomsFrame.setVisible(true);
		publicRoomsFrame.add(new JoinLeave(MainClient.getRoomNamesList()));
	}

	public static void showUsers(String[] userNames) {
		connect();
		chatFrame.showUsers(userNames);
	}

	public static void updateUsers(String name, int length) {
		chatFrame.addUser(name, length);
	}

	public static void displayMessage(int senderID, String senderName,
			String roomName, String msg) {
		chatFrame.displayMessage(senderID, senderName, roomName, msg);
	}

	public static void removeUser(String user) {
		chatFrame.removeUser(user);
	}

	public static void closeRoom(String roomName) {
		chatFrame.closeRoom(roomName);
	}

	public static int recieveFile(String userName, String fileName) {
		return JOptionPane.showConfirmDialog(chatFrame, "User "
				+ userName + " has sent you a file.\nFile name: " + fileName
				+ "\nAccept ?", "File transfer request",
				JOptionPane.YES_NO_OPTION);
	}
	
	public static String getFilePath(String fileName)
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Saving... " + fileName);
		fc.setSelectedFile(new File(fileName));
		int response = fc.showSaveDialog(chatFrame);
		if (response == JFileChooser.APPROVE_OPTION)
			return fc.getSelectedFile().getAbsolutePath();
		else
			return null;
		
	}
	
	public static void showRoomMembers(int room, boolean privRoom){
		MainClient.sendRoomMembersRequest(room, privRoom);
	}
	public static void getRoomMembers (int room, String[]roomMembers){
		RemoveMembers panel_kick = new RemoveMembers(roomMembers);
		RemoveMembers.setFrame(panel_kick);
	}
	
}
