/* program that will do the recording of the user*/

package Voice;

import javax.sound.sampled.*;
import javax.sound.sampled.AudioInputStream;
import javax.swing.JButton;
import java.io.File;
import java.util.Vector;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioSystem;
import java.io.IOException;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

public class Voice {

	final int bufSize = 16384;
	public Capture capture = new Capture();	// object to capture the users voice.
	AudioInputStream audioInputStream;
	public String fileName = "untitled";
	String errStr;
	double duration, seconds;
	public File file;
	public Vector lines = new Vector();

	public void open() {}
	public void createAudioInputStream(File file, boolean updateComponents) {
		if (file != null && file.isFile()) {
			try {
				this.file = file;
				errStr = null;
				audioInputStream = AudioSystem.getAudioInputStream(file);
				fileName = file.getName();
				long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / audioInputStream
						.getFormat().getFrameRate());
				duration = milliseconds / 1000.0;	// sampling of the user input
				if (updateComponents) {
				}
			} catch (Exception ex) {
				reportStatus(ex.toString());
			}
		} else {
			reportStatus("Audio file required.");
		}
	}

	/*function to save the data to the file*/
	public void saveToFile(String name, AudioFileFormat.Type fileType) {
		if (audioInputStream == null) {
			reportStatus("No loaded audio to save");
			return;
		} else if (file != null) {
			createAudioInputStream(file, false);
		}

		// reset to the beginning of the captured data

		try {
			audioInputStream.reset();
		} catch (Exception e) {
			reportStatus("Unable to reset stream " + e);
			return;
		}
		File file = new File(fileName = name);
		try {
			if (AudioSystem.write(audioInputStream, fileType, file) == -1) {
				throw new IOException("Problems writing to file");
			}
		} catch (Exception ex) {
			reportStatus(ex.toString());
		}
	}
	
	/*function to report the status*/
	private void reportStatus(String msg) {
		if ((errStr = msg) != null) {
			System.out.println(errStr);
		}
	}

	/*function that is used to capture the voice of the user*/
	public class Capture implements Runnable {
		TargetDataLine line;
		Thread thread;
		JButton captB;
		public void start(JButton btRecord) {
			captB = btRecord;
			errStr = null;
			thread = new Thread(this);
			thread.setName("Capture");
			thread.start();
		}
		public void stop() {
			thread = null;
		}
		private void shutDown(String message) {
			if ((errStr = message) != null && thread != null) {
				thread = null;
				captB.setText("录音");
				System.err.println(errStr);
			}
		}
		/*function that is used to sample the voice according to pulse code modulation technique*/
		public void run() {
			duration = 0;
			audioInputStream = null;
			AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
			float rate = 8000f;
			int sampleSize = 8;
			String signedString = "signed";
			boolean bigEndian = true;
			int channels = 1;
			AudioFormat format = new AudioFormat(encoding, rate, sampleSize,
					channels, (sampleSize / 8) * channels, rate, bigEndian);
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			if (!AudioSystem.isLineSupported(info)) {
				shutDown("Line matching " + info + " not supported.");
				return;
			}

			try {
				line = (TargetDataLine) AudioSystem.getLine(info);
				line.open(format, line.getBufferSize());
			} catch (LineUnavailableException ex) {
				shutDown("Unable to open the line: " + ex);
				return;
			} catch (SecurityException ex) {
				shutDown(ex.toString());
				return;
			} catch (Exception ex) {
				shutDown(ex.toString());
				return;
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int frameSizeInBytes = format.getFrameSize();
			int bufferLengthInFrames = line.getBufferSize() / 8;
			int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
			byte[] data = new byte[bufferLengthInBytes];
			int numBytesRead;
			line.start();
			while (thread != null) {
				if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
					break;
				}
				out.write(data, 0, numBytesRead);
			}
			line.stop();
			line.close();
			line = null;
			try {
				out.flush();
				out.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			byte audioBytes[] = out.toByteArray();
			ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
			audioInputStream = new AudioInputStream(bais, format,
					audioBytes.length / frameSizeInBytes);
			long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / format
					.getFrameRate());
			duration = milliseconds / 1000.0;
			try {
				audioInputStream.reset();
			} catch (Exception ex) {
				ex.printStackTrace();
				return;
			}
			saveToFile("record.wav", AudioFileFormat.Type.WAVE);
		}
	}
}