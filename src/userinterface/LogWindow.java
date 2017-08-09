/* User interface for login page*/

package userinterface;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import general.client.MainClient;

@SuppressWarnings("serial")
public class LogWindow extends JFrame {

	private JPanel contentPane;
	private JPasswordField passwordField;
	private JTextField nameField;
	private JButton connectButton;

	/* using the awt package for implementing the desktop pane, content pane etc*/
	public LogWindow() {
		setIconImage(new ImageIcon(LogWindow.class.getResource("/icons/chatting_online.png")).getImage());
		setTitle("Messenger Application...");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
	//	setBounds(100, 100, 457, 382);
		setBounds(500, 200, 900, 800);
		contentPane = new JPanel();
		contentPane.setOpaque(false);
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JDesktopPane desktopPane = new JDesktopPane();
	//	desktopPane.setBounds(504, 95, 1, 1);
		desktopPane.setBounds(300, 50, 1, 1);
		desktopPane.setOpaque(false);

		JLayeredPane layeredPane = new JLayeredPane();
	//	layeredPane.setBounds(49, 36, 1, 1);
		layeredPane.setBounds(80, 70, 1, 1);

		JPanel panel = new JPanel() {
			public void paint(Graphics g) {
				super.paint(g);
				new ImageIcon(LogWindow.class.getResource("/backgrounds/background1.jpg")).paintIcon(this, g, 0, 0);
				
				g.setColor(Color.WHITE);
				g.setFont(new Font("TIMES NEW ROMAN", Font.BOLD, 26));
				g.drawString("Login", 450, 260);
				g.setColor(Color.WHITE);
				g.setFont(new Font("TIMES NEW ROMAN", Font.BOLD, 20));
		//		g.drawString("Username", 192, 218);
				g.drawString("Username", 200, 310);
				g.setColor(Color.WHITE);
				g.setFont(new Font("TIMES NEW ROMAN", Font.BOLD, 20));
		//		g.drawString("Password", 192, 253);
				g.drawString("Password", 200, 360);
				g.setColor(Color.WHITE);
		//		g.setFont(new Font("Calibri", Font.BOLD, 22));
				//g.drawString("Don't have an account?!!", 270, 145);
				this.paintChildren(g);
			}
		};
		//panel.setBounds(0, 0, 451, 354);
		panel.setBounds(0, 0, 900, 800);
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);
		//JButton btnNewButton_1 = new JButton("sign up");
		nameField = new JTextField();
		nameField.setColumns(20);
		passwordField = new JPasswordField();	
		connectButton = new JButton("Submit");
		connectButton.setForeground(getForeground());
		connectButton.setFont(new Font("TIMES NEW ROMAN", Font.PLAIN, 20));
	//	connectButton.setAlignmentX(CENTER_ALIGNMENT);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel
				.createParallelGroup(Alignment.CENTER)
				.addGroup(
						gl_panel.createSequentialGroup()
					//			.addContainerGap(282, Short.MAX_VALUE)
								.addContainerGap(300, Short.MAX_VALUE)
								.addComponent(connectButton).addContainerGap(225,Short.MAX_VALUE))
				.addGroup(
						gl_panel.createSequentialGroup()
					//			.addContainerGap(304, Short.MAX_VALUE)
								.addContainerGap(200, Short.MAX_VALUE)
								//.addComponent(btnNewButton_1,
									//	GroupLayout.PREFERRED_SIZE, 85,
									//	GroupLayout.PREFERRED_SIZE).addGap(51)
								)
				.addGroup(
						gl_panel.createSequentialGroup()
								.addGap(350)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.CENTER)
								//				.addComponent(chckbxNewCheckBox)
												.addComponent(
														nameField,
														GroupLayout.PREFERRED_SIZE,
													//	179, Short.MAX_VALUE)
														200, Short.MAX_VALUE)
												.addComponent(
														passwordField,
														Alignment.CENTER,
														GroupLayout.PREFERRED_SIZE,
													//	179, Short.MAX_VALUE))
														200,Short.MAX_VALUE))
								.addContainerGap(50,Short.MAX_VALUE)));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(
				Alignment.TRAILING).addGroup(
				gl_panel.createSequentialGroup()
						.addContainerGap(50, Short.MAX_VALUE)
						//.addComponent(btnNewButton_1)
						.addGap(0)
						.addComponent(nameField, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(passwordField,GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addGap(50)
						//.addPreferredGap(ComponentPlacement.UNRELATED)
						//.addComponent(chckbxNewCheckBox).addGap(18)
						.addComponent(connectButton).addContainerGap(100, Short.MAX_VALUE)));
		panel.setLayout(gl_panel);
		contentPane.setLayout(null);
		contentPane.add(layeredPane);
		contentPane.add(panel);
		contentPane.add(desktopPane);
		
		addListeners();
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		nameField.requestFocusInWindow();
	}

	private void addListeners() {

		Action connectListener = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (nameField.getText().equals("")) {
					JOptionPane.showMessageDialog(LogWindow.this,
							"Username cannot be empty !");
					return;
				}
				if ((passwordField.getPassword().length)==0) {
					JOptionPane.showMessageDialog(LogWindow.this,
							"Password cannot be empty !");
					return;
				}
				//else {
					if (!MainClient.connect(nameField.getText()))
						JOptionPane.showMessageDialog(LogWindow.this, "Server is down!");
				//}

			}
		};
		InputMap loginInputMap = connectButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap loginActionMap = connectButton.getActionMap();
		loginInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), "connect");
		loginActionMap.put("connect", connectListener);
		connectButton.addActionListener(connectListener);
	}
	public String getUserName(){
		return this.nameField.getText();
	}
}