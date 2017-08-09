/* GUI program to join or leave a room*/

package userinterface;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class JoinLeave extends JPanel {

	private DefaultListModel model;
	private JList list;
	private JButton joinButton;
	private JButton leaveButton;

	/* using the awt packages for implementing the UI*/
	public JoinLeave(String[] publicRoomNames) {
		setBackground(new Color(120, 10, 10));
		setLayout(new BorderLayout());
		model = new DefaultListModel();
		list = new JList(model);
		list.setForeground(new Color(120, 10, 10));
		JScrollPane pane = new JScrollPane(list);
		joinButton = new JButton("Join Room ");
		joinButton.setBorder(new LineBorder(new Color(0, 0, 0)));
		joinButton.setForeground(Color.WHITE);
		joinButton.setBackground(new Color(120, 10, 10));
		leaveButton = new JButton("Leave Room");
		leaveButton.setForeground(Color.WHITE);
		leaveButton.setBackground(new Color(120, 10, 10));
		pane.setPreferredSize(new Dimension(100, 230));
		joinButton.setPreferredSize(new Dimension(140, 0));
		leaveButton.setPreferredSize(new Dimension(140, 0));

		add(pane, BorderLayout.NORTH);
		add(joinButton, BorderLayout.CENTER);
		add(leaveButton, BorderLayout.EAST);

		for (int i = 0; i < publicRoomNames.length; i++) {
			if (publicRoomNames[i] != null && !publicRoomNames[i].equals("General"))
				model.addElement(publicRoomNames[i]);
		}

		addListeners();
	}

	private void addListeners() {
		
		ActionListener joinRoomReq = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (list.isSelectionEmpty()
						|| list.getSelectedValue()
								.equals("General"))
					return;

				MainUI.joinRoom((String)list.getSelectedValue());

			}

		};
		joinButton.addActionListener(joinRoomReq);
		
		//action listener for leave room request
		ActionListener leaveRoomReq = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (list.isSelectionEmpty()
						|| list.getSelectedValue()
								.equals("General"))
					return;

				MainUI.leaveRoom(list.getSelectedValue().toString());

			}

		};
		leaveButton.addActionListener(leaveRoomReq);
	
	}
}
