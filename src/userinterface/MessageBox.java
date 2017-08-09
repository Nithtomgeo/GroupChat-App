/* user interface that will show how the chat widnow should look like*/
package userinterface;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

import Emoji.Emoji;

import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

@SuppressWarnings("serial")
public class MessageBox extends JTextPane {

	private static Color [] colors = new Color[0];
	private static Random r = new Random();
	private Style nameStyle;
	private StyledDocument doc;
	private int id;
	private Object images;
	private static Font myFont = new Font("Segoe UI Emoji", Font.PLAIN, 12); //setting up the font for emoji's
	
	public MessageBox() {
		nameStyle = addStyle(null, null);
		StyleConstants.setItalic(nameStyle, true);
		setEditable(false);
		setFont(myFont);
		doc = getStyledDocument();
	}
	
	public static void addColors(int length) //adding colors to the messages and user names
	{
		if (colors.length < length)
		{
			Color [] temp = new Color[length];
			for (int i = 0; i < colors.length; i++)
			{
				temp[i] = colors[i];
			}
			for (int i = colors.length; i < temp.length; i++)
			{
				temp[i] = new Color(r.nextInt(256),r.nextInt(256),r.nextInt(256));
			}
			colors = temp;
		}
	}
	@SuppressWarnings("restriction")
	/*function that will display the messages*/
	protected void displayMessage(int index, String name, String message1) {
		try {
			StyleConstants.setForeground(nameStyle, colors[index]);
			doc.insertString(doc.getLength(), name + ":\n", nameStyle);
			//string message = message1.replaceAll(":)","kk" );
			String message = Emoji.replaceInText(message1);
			doc.insertString(doc.getLength(), "  " + message + "\n", getLogicalStyle());
		} catch (BadLocationException e) {
			System.out.println(e);
		}
	}
	
	protected void setID(int id)
	{
		this.id = id;
	}
	
	protected int getID()
	{
		return id;
	}

}
