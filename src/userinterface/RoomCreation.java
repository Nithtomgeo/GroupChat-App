/* program that is required for creation of the room*/

package userinterface;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import general.client.MainClient;

@SuppressWarnings("serial")
public class RoomCreation extends JFrame {

	private JPanel contentPane;
	private JTextField roomField;
	private JRadioButton publicRoom;
	private JRadioButton privateRoom;
	private JButton createButton;
	private ButtonGroup roomType;

	/* constructor for room creation initialization*/
	public RoomCreation() {
		setTitle("Room information");
		setBounds(500, 350, 700,600);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(120, 10, 10));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		roomField = new JTextField();
		roomField.setBounds(200, 70, 240, 40);
		contentPane.add(roomField);
		roomField.setColumns(10);

		/* setting the jlabel for the room name*/
		JLabel lblRoomName = new JLabel("Room name: ");
		lblRoomName.setForeground(Color.WHITE);
		lblRoomName.setFont(new Font("TIMES NEW ROMAN", Font.BOLD, 18));
		lblRoomName.setBounds(30, 40, 150, 100);
		contentPane.add(lblRoomName);

		/*setting the jlabel for the room type*/
		JLabel lblRoomType = new JLabel("Room type:");
		lblRoomType.setForeground(Color.WHITE);
		lblRoomType.setFont(new Font("TIMES NEW ROMAN", Font.BOLD, 18));
		lblRoomType.setBounds(30, 140, 150, 100);
		contentPane.add(lblRoomType);
		roomType = new ButtonGroup();
		publicRoom = new JRadioButton("public");
		publicRoom.setFont(new Font("TIMES NEW ROMAN", Font.PLAIN, 18));
		publicRoom.setForeground(Color.WHITE);
		publicRoom.setBackground(new Color(120, 10, 10));
		publicRoom.setBounds(220, 165, 120, 60);
		contentPane.add(publicRoom);
		roomType.add(publicRoom);
		publicRoom.setSelected(true);

		/* setting the radio button for the private room*/
		privateRoom = new JRadioButton("private");
		privateRoom.setFont(new Font("TIMES NEW ROMAN", Font.PLAIN, 18));
		privateRoom.setForeground(Color.WHITE);
		privateRoom.setBackground(new Color(120, 10, 10));
		privateRoom.setBounds(220, 195, 120, 60);
		contentPane.add(privateRoom);
		roomType.add(privateRoom);

		/* setting the create button for creating the room*/
		createButton = new JButton("Create");
		createButton.setBounds(220, 265, 100, 50);
		contentPane.add(createButton);

		addListeners();
		setVisible(true);
	}

	/*adding the listeners for create room button*/
	private void addListeners() {
		Action createRoom = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				String roomName = roomField.getText();
				if (roomName.equals("")) {
					JOptionPane.showMessageDialog(RoomCreation.this,
							"Room name can not be empty!");
					return;
				}
				if (publicRoom.isSelected()) {
						if (!MainClient.sendNewRoomRequest(roomName, false,
								false))
							JOptionPane.showMessageDialog(
									RoomCreation.this,
									"Couldn't Create Public Room!");
				} else {
					if (!MainClient.sendNewRoomRequest(roomName + "*", true,
							false))
						JOptionPane.showMessageDialog(RoomCreation.this,
								"Couldn't Create Private Room!");
				}
				RoomCreation.this.dispose();
			}

		};
		createButton.addActionListener(createRoom);

		ActionMap aMap = createButton.getActionMap();
		InputMap iMap = createButton
				.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "create");
		aMap.put("create", createRoom);

	}
}
