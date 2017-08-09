/* program that will implment the ui for the main chat window page*/

package userinterface;

//import Emoji.Emoji;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import Voice.Voice;
import general.client.MainClient;

import java.io.File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

@SuppressWarnings("serial")
public class MainChat extends JFrame {

	private JPanel contentPane;
	private TextArea messageBox;
	private JMenu chattingMenu;
	private JMenuItem menuClose;
	private JMenuItem menuLogOut;
	private MessageBox general;
	private MessageBox messagebox;
	private JTabbedPane tabbedPane;
	private JTabbedPane tabbedPane1;
	private JTabbedPane tabbedPane2;
	private JMenu ChatRoomsMenu;
	private JMenuItem menuCreateRoom;
	private JMenuItem menuInvite;
	private JMenuItem menuPRoomsList;
	private DefaultListModel theModel;
	private JList onLineUsers;
	private JButton sendButton;
	private JButton btrecord;
	private JButton play;
	private JButton sendvoice;
	private JPanel users;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane1;
	private JScrollPane pane;
	private JMenuItem sendFileMenuItem;
	private JDesktopPane desktopPane;
	private JDesktopPane desktopPane_1;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel1;

	Voice voice;
	public boolean voiceRecord = false;
	public String voicePath;
	public DataOutputStream dos = null;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	
    private final int BUFFER_SIZE = 128000;
    private File soundFile;
    private AudioInputStream audioStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceLine;
	
	public String uname;
	
	public MainChat(String userName) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				MainChat.class.getResource("/icons/chatting_online.png")));
		setTitle(userName + " : Logged in...");
		uname = userName;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(300, 70, 1100, 900);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		chattingMenu = new JMenu("File");
		menuBar.add(chattingMenu);

		menuLogOut = new JMenuItem("Logout");
		menuLogOut.setIcon(new ImageIcon(MainChat.class
				.getResource("/icons/logout.png")));
		menuLogOut.setAlignmentX(Component.LEFT_ALIGNMENT);
		chattingMenu.add(menuLogOut);

		menuClose = new JMenuItem("Close");
		menuClose.setIcon(new ImageIcon(MainChat.class
				.getResource("/icons/close.png")));
		chattingMenu.add(menuClose);

		JMenu mnNewMenu_1 = new JMenu("Options");
		menuBar.add(mnNewMenu_1);

		// for sending the file
		sendFileMenuItem = new JMenuItem("Send file");
		sendFileMenuItem.setIcon(new ImageIcon(MainChat.class
				.getResource("/icons/send_file.png")));
		mnNewMenu_1.add(sendFileMenuItem);

		//for chat rooms
		ChatRoomsMenu = new JMenu("Rooms");
		menuBar.add(ChatRoomsMenu);

		menuCreateRoom = new JMenuItem("CreateRoom");
		menuCreateRoom.setIcon(new ImageIcon(MainChat.class
				.getResource("/icons/new_room.png")));
		ChatRoomsMenu.add(menuCreateRoom);

		//for friend invite
		menuInvite = new JMenuItem("Friend Invite");
		menuInvite.setIcon(new ImageIcon(MainChat.class
				.getResource("/icons/invite_user.png")));
		menuInvite.setEnabled(false);
		ChatRoomsMenu.add(menuInvite);

		//for public rooms
		menuPRoomsList = new JMenuItem("Public Rooms");
		menuPRoomsList.setIcon(new ImageIcon(MainChat.class
				.getResource("/icons/room_list.png")));
		ChatRoomsMenu.add(menuPRoomsList);

		/*JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);*/

		/*JMenuItem mntmAbout = new JMenuItem("About ");
		mnHelp.add(mntmAbout);*/

		contentPane = new JPanel();
		contentPane.setBackground(new Color(120, 10, 10));
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);

		desktopPane = new JDesktopPane();
		desktopPane.setBounds(10, 24, 596, 286);
		desktopPane.setBackground(Color.GRAY);

		users = new JPanel();
		users.setBounds(644, 24, 100, 426);
		users.setBackground(Color.GRAY);

		desktopPane_1 = new JDesktopPane();
		desktopPane_1.setBounds(10, 346, 596, 55);
		desktopPane_1.setBackground(Color.GRAY);
		desktopPane_1.setLayout(new BorderLayout(500, 500));

		messageBox = new TextArea();
		desktopPane_1.add(messageBox);

		theModel = new DefaultListModel();
		users.setLayout(new BorderLayout(500, 500));
		pane = new JScrollPane();
		users.add(pane);
		contentPane.setLayout(null);
		contentPane.add(desktopPane_1);
		contentPane.add(desktopPane);
		desktopPane.setLayout(new BorderLayout(500, 500));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setOpaque(true);
		tabbedPane.setBackground(new Color(120,10,10));
		desktopPane.add(tabbedPane);
		
	  tabbedPane1 = new JTabbedPane(JTabbedPane.TOP);
	  tabbedPane1.setOpaque(true);
	  tabbedPane1.setBackground(new Color(120,10,10));
	  desktopPane_1.add(tabbedPane1);
	  
	  tabbedPane2 = new JTabbedPane(JTabbedPane.TOP);
	  tabbedPane2.setOpaque(true);
	  tabbedPane2.setBackground(new Color(120,10,10));
	  users.add(tabbedPane2);

		scrollPane = new JScrollPane();
		tabbedPane.addTab("General", scrollPane);
		scrollPane1 = new JScrollPane();
		//tabbedPane1.addTab("MessageBox",scrollPane1);
		messagebox = new MessageBox();
		general = new MessageBox();
		general.setID(0);
		//tabbedPane1.setName(arg0);
		tabbedPane1.add("MessageBox",messageBox);
	//	
		scrollPane.setViewportView(general);
		scrollPane1.setViewportView(messagebox);
		general.setName("General");
		messagebox.setName("MessageBox");
		contentPane.add(users);
		onLineUsers = new JList(theModel);
		users.add(onLineUsers);
		onLineUsers.setVisible(true);
		onLineUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabbedPane2.add("OnlineUsers",pane);
		lblNewLabel1 = new JLabel("Online friends");
		lblNewLabel1.setForeground(Color.WHITE);
		lblNewLabel1.setBounds(100, 100,100, 100);
		contentPane.add(lblNewLabel1);
		
		//label for sending or recording the message
		lblNewLabel = new JLabel("Click on Send to send the message/Record and Send to send a voice message");
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setSize(getMaximumSize());
		lblNewLabel.setBounds(350, 305, 300, 300);
		contentPane.add(lblNewLabel);

		sendButton = new JButton("Send");
		sendButton.setBounds(493, 427, 113, 23);
		contentPane.add(sendButton);
        
		btrecord = new JButton("Record");
		btrecord.setBounds(300, 427, 113, 23);
		contentPane.add(btrecord);
		
		play = new JButton("Play");
		play.setBounds(370, 427, 113, 23);
		contentPane.add(play);
		
		sendvoice = new JButton("SendVoice");
		sendvoice.setBounds(420, 427, 113, 23);
		contentPane.add(sendvoice);
		
		addListeners();
		addResizeListeners();
		setVisible(true);
	}

	/* add listener for room creation*/
	private void addListeners() {
		ActionListener roomCreating = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainUI.roomCreationFrame = new RoomCreation();
			}

		};
		menuCreateRoom.addActionListener(roomCreating);

		/* listener for message sent*/
		ActionListener messageSend = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = messageBox.getText();
			//	message = Emoji.replaceInText(message);
				if (message.equals(""))
					return;
				MessageBox room = (MessageBox) ((JScrollPane) tabbedPane
						.getSelectedComponent()).getViewport().getView();

				boolean priv = (room.getName().charAt(
						room.getName().length() - 1) == '*') ? true : false;
				if (MainClient.sendMessage(message, room.getID(), priv))
					room.displayMessage(MainClient.getUserID(),
							MainClient.getUserName(), message);

				messageBox.setText("");
			}
		};
		
		/*action listener for sending message*/
		sendButton.addActionListener(messageSend);

		ActionListener playback = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String strFilename = System.getProperty("user.dir") + "\\record.wav";

		        try {
		            soundFile = new File(strFilename);
		        } catch (Exception e1) {
		            e1.printStackTrace();
		            System.exit(1);
		        }

		        try {
		            audioStream = AudioSystem.getAudioInputStream(soundFile);
		        } catch (Exception e1){
		            e1.printStackTrace();
		            System.exit(1);
		        }

		        audioFormat = audioStream.getFormat();

		        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		        try {
		            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
		            sourceLine.open(audioFormat);
		        } catch (LineUnavailableException e1) {
		            e1.printStackTrace();
		            System.exit(1);
		        } catch (Exception e1) {
		            e1.printStackTrace();
		            System.exit(1);
		        }

		        sourceLine.start();

		        int nBytesRead = 0;
		        byte[] abData = new byte[BUFFER_SIZE];
		        while (nBytesRead != -1) {
		            try {
		                nBytesRead = audioStream.read(abData, 0, abData.length);
		            } catch (IOException e1) {
		                e1.printStackTrace();
		            }
		            if (nBytesRead >= 0) {
		                @SuppressWarnings("unused")
		                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
		            }
		        }

		        sourceLine.drain();
		        sourceLine.close();
			}
		};
		play.addActionListener(playback);
		
		ActionListener senvoc = new ActionListener(){
		public void actionPerformed(ActionEvent e){
			if (onLineUsers.isSelectionEmpty()) {
				JOptionPane.showMessageDialog(MainChat.this,
				"Sending to whom ?!!");
				return;
			}

			if (onLineUsers.getSelectedValue().equals(
					MainClient.getUserName())) {
				JOptionPane.showMessageDialog(MainChat.this,
				"Sending to yourself ?!!");
				return;
			}

			String[] possibleValues = { "over TCP" };
			String selected = (String) JOptionPane.showInputDialog(null,
					"Please choose a protocol for the transfer:",
					"Method:", JOptionPane.INFORMATION_MESSAGE, null,
					possibleValues, possibleValues[0]);

			//JFileChooser fc = new JFileChooser();
			//fc.setApproveButtonText("Send");
			//fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			//if (fc.showOpenDialog(MainChat.this) == JFileChooser.APPROVE_OPTION) {
				//String filePath = fc.getSelectedFile().getAbsolutePath();
				String filePath =System.getProperty("user.dir") + "\\record.wav";
			//	long fileSize = fc.getSelectedFile().length();
				String userName = (String) onLineUsers.getSelectedValue();
				if (selected.equals("over TCP")) {
					MainClient.sendFileTransferTCPRequest(userName,
							filePath);
				} 
	//		}
		}
		};
		sendvoice.addActionListener(senvoc);
		
		ActionListener recording = new ActionListener(){
		
			public void actionPerformed(ActionEvent e) {
	            if (btrecord.getText().startsWith("Record")) {
                    System.out.println("Press the Record button");
	            	voiceRecord = true;
	            	if( (String) onLineUsers.getSelectedValue() != null ){
						
						voice  = null;
		            	voice = new Voice();
		                voice.file = null;
		                voice.capture.start(btrecord);  
		                voice.fileName = "untitled";
		                btrecord.setText("Stop");
		                messageBox.append("\tRecord...\n");
					}else
						JOptionPane.showMessageDialog(MainChat.this, "Select User", "Error", JOptionPane.ERROR_MESSAGE);
				

	            } else {
                    System.out.println("Press the stop Button");
	            	if(voice != null){
	            	voiceRecord = true;
	            	voicePath = System.getProperty("user.dir") + "\\record.wav";
	                voice.lines.removeAllElements();  
	                voice.capture.stop();
	                btrecord.setText("Recording");
	                messageBox.append("\tSent a Recording...\n");
	            
					try {
						String request = "Request:" + (String) onLineUsers.getSelectedValue()+ "*" + uname+"(";
         System.out.println("Request messages sent after pressing the record button:"+request);
						dos.writeUTF(request);
						dos.flush();
					} catch (IOException e1) {e1.printStackTrace();}
	                voiceRecord = false;
	                
	                }else
	                	System.out.println("voice,null");
	        }  
	    
		}	
		};
		
		btrecord.addActionListener(recording);
				
		
		ActionListener logOutListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainClient.logoff();
			}
		};
		//btrecord.addActionListener(voice);
		menuLogOut.addActionListener(logOutListener);

		ActionListener closeListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainClient.logoff();
			}
		};
		menuClose.addActionListener(closeListener);

		KeyListener theEnterListener = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String message = messageBox.getText();
					if (message.equals(""))
						return;

					MessageBox room = (MessageBox) ((JScrollPane) tabbedPane
							.getSelectedComponent()).getViewport().getView();

					boolean priv = (room.getName().charAt(
							room.getName().length() - 1) == '*') ? true : false;
					if (MainClient.sendMessage(message, room.getID(), priv))
						room.displayMessage(MainClient.getUserID(),
								MainClient.getUserName(), message);

					messageBox.setText("");
				}
			}

			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					messageBox.setText("");
				}
			}

		};
		messageBox.addKeyListener(theEnterListener);

		/* action listener for public room list*/
		ActionListener pRoomList = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				MainUI.showPublicRooms();
			}
		};
		menuPRoomsList.addActionListener(pRoomList);

		/*action listener for inviting the user*/
		ActionListener inviteListener = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (onLineUsers.isSelectionEmpty()) {
					JOptionPane.showMessageDialog(MainChat.this,
					"Who are you inviting ?!!");
					return;
				}
				int roomID = ((MessageBox) ((JScrollPane) tabbedPane
						.getSelectedComponent()).getViewport().getView())
						.getID();
				MainClient.sendInvitationRequest(
						(String) onLineUsers.getSelectedValue(), roomID);

			}
		};
		menuInvite.addActionListener(inviteListener);

		ActionListener sendFileListener = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (onLineUsers.isSelectionEmpty()) {
					JOptionPane.showMessageDialog(MainChat.this,
					"Sending to whom ?!!");
					return;
				}

				if (onLineUsers.getSelectedValue().equals(
						MainClient.getUserName())) {
					JOptionPane.showMessageDialog(MainChat.this,
					"Sending to yourself ?!!");
					return;
				}

				String[] possibleValues = { "over TCP" };
				String selected = (String) JOptionPane.showInputDialog(null,
						"Please choose a protocol for the transfer:",
						"Method:", JOptionPane.INFORMATION_MESSAGE, null,
						possibleValues, possibleValues[0]);

				/* choosing the files from the system*/
				JFileChooser fc = new JFileChooser();
				fc.setApproveButtonText("Send");
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if (fc.showOpenDialog(MainChat.this) == JFileChooser.APPROVE_OPTION) {
					String filePath = fc.getSelectedFile().getAbsolutePath();
					long fileSize = fc.getSelectedFile().length();
					String userName = (String) onLineUsers.getSelectedValue();
					if (selected.equals("over TCP")) {
						MainClient.sendFileTransferTCPRequest(userName,
								filePath);
					} 
				}
			}
		};
		sendFileMenuItem.addActionListener(sendFileListener);

	}

	private void addResizeListeners() {
		HierarchyBoundsListener resizeListener = new HierarchyBoundsAdapter() {

			public void ancestorResized(HierarchyEvent e) {
				if (e.getSource() == users)
					users.setBounds(MainChat.this.getWidth() - 1075, 55, 100,
							MainChat.this.getHeight() - 330);

				// setBounds(100, 100, 770, 520);
				// sendButton.setBounds(493, 427, 113, 23);
				if (e.getSource() == desktopPane_1)
					desktopPane_1.setBounds(10,
							MainChat.this.getHeight() - 225,
							MainChat.this.getWidth() - 40, 100);
				else if (e.getSource() == desktopPane)
					desktopPane.setBounds(140, 24,
							MainChat.this.getWidth() - 174,
							MainChat.this.getHeight() - 300);
				else if (e.getSource() == lblNewLabel)
					lblNewLabel.setBounds(MainChat.this.getWidth() - 550,
							MainChat.this.getHeight() - 360, 500, 200);
				else if (e.getSource() == lblNewLabel1)
					lblNewLabel1.setBounds(20,10, 200, 50);
				else if (e.getSource() == sendButton)
					sendButton.setBounds(MainChat.this.getWidth() - 300,
							MainChat.this.getHeight() - 120, 120, 30);
				else if (e.getSource() == btrecord)
					btrecord.setBounds(MainChat.this.getWidth() - 900,
							MainChat.this.getHeight() - 120, 120, 30);
				else if (e.getSource() == play)
					play.setBounds(MainChat.this.getWidth() - 700,
							MainChat.this.getHeight() - 120, 120, 30);
				else if (e.getSource() == sendvoice)
					sendvoice.setBounds(MainChat.this.getWidth() - 500,
							MainChat.this.getHeight() - 120, 120, 30);

			}
		};
		users.addHierarchyBoundsListener(resizeListener);
		desktopPane_1.addHierarchyBoundsListener(resizeListener);
		desktopPane.addHierarchyBoundsListener(resizeListener);
		lblNewLabel.addHierarchyBoundsListener(resizeListener);
		lblNewLabel1.addHierarchyBoundsListener(resizeListener);
		sendButton.addHierarchyBoundsListener(resizeListener);
		btrecord.addHierarchyBoundsListener(resizeListener);
		play.addHierarchyBoundsListener(resizeListener);
		sendvoice.addHierarchyBoundsListener(resizeListener);
	}

	/*ui for creating the room*/
	protected void createRoom(String roomName, int id, boolean priv) {
		if (priv) {
			menuInvite.setEnabled(true);
		}

		MessageBox area = new MessageBox();
		area.setName(roomName);
		area.setID(id);
		JScrollPane scrollPane = new JScrollPane(area);
		tabbedPane.addTab(roomName, scrollPane);
		tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, new RoomTab(
				tabbedPane, tabbedPane.getTabCount() - 1));
	}

	protected void showUsers(String[] userNames) {
		for (int i = 0; i < userNames.length; i++) {
			if (userNames[i] != null && i != MainClient.getUserID())
				theModel.addElement(userNames[i]);
		}
		MessageBox.addColors(userNames.length);
	}

	protected void addUser(String name, int length) {
		theModel.addElement(name);
		MessageBox.addColors(length);
	}

	/*ui function to display the message*/
	protected void displayMessage(int senderID, String senderName,
			String roomName, String msg){
		//msg  = Emoji.replaceInText(msg);
		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			if (tabbedPane.getTitleAt(i).equals(roomName)) {
				((MessageBox) (((JScrollPane) tabbedPane.getComponentAt(i))
						.getViewport().getView())).displayMessage(senderID,
								senderName, msg);
				break;
			}
		}
	}

	protected void removeUser(String user) {
		theModel.removeElement(user);
	}

	protected void joinRoom(String roomName, int id) {
		MessageBox area = new MessageBox();
		area.setName(roomName);
		area.setID(id);
		JScrollPane scrollPane = new JScrollPane(area);
		tabbedPane.addTab(roomName, scrollPane);
	}

	protected void leaveRoom(String roomName) {
		int tabsCount = tabbedPane.getTabCount();
		for (int i = 0; i < tabsCount; i++) {
			if (tabbedPane.getTitleAt(i).equals(roomName)) {
				tabbedPane.removeTabAt(i);
			}
		}
	}

	protected void closeRoom(String roomName) {
		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			if (tabbedPane.getTitleAt(i).equals(roomName)) {
				if (tabbedPane.getSelectedIndex() == i)
					tabbedPane.setSelectedIndex(0);
				tabbedPane.removeTabAt(i);
				break;
			}
		}
	}

	protected int getSelectedRoomId() {
		MessageBox room = (MessageBox) ((JScrollPane) tabbedPane
				.getSelectedComponent()).getViewport().getView();
		return room.getID();
	}

	protected boolean selectedRoomPriv() {
		MessageBox room = (MessageBox) ((JScrollPane) tabbedPane
				.getSelectedComponent()).getViewport().getView();
		String name = room.getName();
		int n = name.length();
		boolean isPriv;
		if (name.charAt(n - 1) == '*') {
			isPriv = true;
		} else {
			isPriv = false;
		}
		return isPriv;
	}
}

@SuppressWarnings("serial")
class RoomTab extends JPanel {
	private final JTabbedPane tabbedPane;
	private final OptionsMenu menu;
	private final int index;

	public RoomTab(JTabbedPane tabbedPane, int index) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.tabbedPane = tabbedPane;
		this.index = index;

		setOpaque(false);

		JLabel title = new JLabel() {

			public String getText() {
				return RoomTab.this.tabbedPane.getTitleAt(RoomTab.this.index);
			}
		};
		add(title);
		add(new TabButton());

		menu = new OptionsMenu();
	}

	private class TabButton extends JButton implements ActionListener {

		public TabButton() {
			setPreferredSize(new Dimension(25, 25));
			setBorderPainted(false);
			setContentAreaFilled(false);
			addActionListener(this);
			setRolloverEnabled(true);
			setToolTipText("Options");
			setIcon(new ImageIcon(
					MainChat.class.getResource("/icons/options_menu.png")));
		}

		public void actionPerformed(ActionEvent e) {
			menu.show(TabButton.this, getWidth() - 10, getHeight() - 10);
		}

	}

	private class OptionsMenu extends JPopupMenu implements ActionListener {
		JMenuItem menuItemClose;
		JMenuItem menuItemKick;

		public OptionsMenu() {
			menuItemKick = new JMenuItem("Remove user", new ImageIcon(
					OptionsMenu.class.getResource("/icons/user_remove.png")));
			menuItemClose = new JMenuItem("Close room", new ImageIcon(
					OptionsMenu.class.getResource("/icons/close_room.png")));
			add(menuItemKick);
			add(new JSeparator());
			add(menuItemClose);

			menuItemClose.addActionListener(this);
			menuItemKick.addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			RoomTab.this.tabbedPane.setSelectedIndex(RoomTab.this.index);
			if (e.getSource() == menuItemKick) {
				boolean priv = (tabbedPane.getTitleAt(index).charAt(
						tabbedPane.getTitleAt(index).length() - 1) == '*') ? true
								: false;
				int room = ((MessageBox) (((JScrollPane) tabbedPane
						.getComponentAt(index)).getViewport().getView()))
						.getID();
				MainUI.showRoomMembers(room, priv);
			}

			else if (e.getSource() == menuItemClose) {
				boolean priv = (tabbedPane.getTitleAt(index).charAt(
						tabbedPane.getTitleAt(index).length() - 1) == '*') ? true
								: false;
				int room = ((MessageBox) (((JScrollPane) tabbedPane
						.getComponentAt(index)).getViewport().getView()))
						.getID();
				MainClient.sendCloseRoomRequest(room, priv);
				tabbedPane.remove(index);
			}
		}
	}

}
