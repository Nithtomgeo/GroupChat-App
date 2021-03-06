/*PARSING THE FILES FOR FILE SENDING*/
package general;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Parse extends TimerTask {
	private static FixedArray<Parse> files;
	private byte[] byteBuffer;
	private BufferedInputStream bInStream;
	private BufferedOutputStream bOutStream;
	private FileInputStream fInStream;
	private FileOutputStream fOutStream;
	private File file;
	private Timer timer;
	private static final int timeout = 120000; //setting up the timer for timeout in case the file takes long time to send.

	public Parse(String name) {
		try {
			
			file = new File(name);
			fInStream = new FileInputStream(file);
			bInStream = new BufferedInputStream(fInStream);
			byteBuffer = new byte[(int) file.length()];
			fInStream.read(byteBuffer);
			fInStream.close();
			bInStream.close();
		} catch (IOException e) {
			try {
				System.out.println(e);
				System.out.println("Caught an exception parsing the file");
				bInStream.close();
				fInStream.close();
			} catch (IOException e2) {
				System.out.println(e2);
			}
		}
	}

	/*function used to parse through the fixed array to get the most recent file*/
	public Parse(FixedArray<Parse> files, String name,
			byte[] array) {
		try {
			Parse.files = files;
			file = new File(name);
			int i = 0;
			while (!file.createNewFile())
			{
				file = new File(name + i++);
			}
			fOutStream = new FileOutputStream(file);
			bOutStream = new BufferedOutputStream(fOutStream);
			bOutStream.write(array);
			bOutStream.close();
			fOutStream.close();


			if (files != null) {
				timer = new Timer();
				timer.schedule(this, timeout);
			}

		} catch (IOException e) {
			System.out.println(e);
		}

	}

	public void run() {
		file.delete();
		files.removeItem(this);
	}

	/* function used to delete the file*/
	public void deleteFile() {
		file.delete();
	}

	/*reading the file byte by byte*/
	public byte[] getFileBytes() {
		if (byteBuffer != null)
			return byteBuffer;

		try {
			fInStream = new FileInputStream(file);
			bInStream = new BufferedInputStream(fInStream);
			byteBuffer = new byte[(int) file.length()];
			fInStream.read(byteBuffer);
			fInStream.close();
			bInStream.close();
			return byteBuffer;
		} catch (IOException e) {
			try {
				System.out.println(e);
				fInStream.close();
				bInStream.close();
				return null;
			} catch (IOException e2) {
				System.out.println(e2);
				return null;
			}
		}
	}

	public String getName() {
		return file.getName();
	}
}
