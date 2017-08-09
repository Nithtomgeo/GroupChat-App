/* program that is used to remove the members from the group*/

package userinterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import general.client.MainClient;

@SuppressWarnings("serial")
public class RemoveMembers extends JPanel {

	private DefaultListModel model;
	private JList list;
	private static JFrame frame;
	private JPanel panel_1;
	private JButton kickButton;

	/*function that will remove the members from the group*/
	public RemoveMembers(String[] roomMembersNames) {
		setBackground(new Color(0, 0, 139));
		setLayout(new BorderLayout());
		model = new DefaultListModel();
		list = new JList(model);
		model.clear();
		for (int i = 0; i < roomMembersNames.length; i++) {
			if (roomMembersNames[i] != null) {
				System.out.println(roomMembersNames[i]);
				model.add(i, roomMembersNames[i]);
			}
		}
		list.setModel(model);
		list.setForeground(new Color(0, 0, 139));
		list.setBackground(Color.WHITE);
		list.setVisible(true);
		JScrollPane pane = new JScrollPane(list);

		pane.setPreferredSize(new Dimension(100, 230));

		this.add(pane, BorderLayout.NORTH);

		panel_1 = new JPanel();
		add(panel_1, BorderLayout.SOUTH);

		kickButton = new JButton("Remove User");
		panel_1.add(kickButton);
		addListeners();
	}

	/*function that will set the frame for the panels*/
	public static void setFrame(RemoveMembers panel) {
		frame = new JFrame("Room Members");
		frame.setVisible(true);
		frame.getContentPane().add(panel);
		frame.setSize(380, 302);
		frame.setResizable(false);
	}

	/* listener function that will remove the user on the click of remove button*/
	private void addListeners() {

		ActionListener RemoveMemberReq = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (list.isSelectionEmpty()) {
					return;
				} else {
					String member = (String) list.getSelectedValue();
					int room = MainUI.chatFrame.getSelectedRoomId();
					boolean isPriv = MainUI.chatFrame.selectedRoomPriv();
					MainClient.sendRemoveMemberRequest(room, isPriv, member);
				}
			}

		};
		kickButton.addActionListener(RemoveMemberReq);
	}
}
