/*CODE TO COMPRESS AND DECOMPRESS THE DATA*/
package general;

import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Compress {

	private Inflater inflater;
	private Deflater deflater;
	private byte[] outBuff;

	
	public Compress() {

		inflater = new Inflater();
		deflater = new Deflater();

	}

	/*COMPRESSION OF THE MESSAGE USING DEFLATOR*/
	public byte [] compress(byte [] msg)
	{			
		byte [] temp = new byte[msg.length];
		deflater.setInput(msg);
		deflater.finish();
		int cSize = deflater.deflate(temp);
		deflater.reset();
		ByteBuffer outBuffer = ByteBuffer.wrap(temp);
		outBuff = new byte [cSize];
		outBuffer.get(outBuff);
		return outBuff;
	}
	
	/*DECOMPRESSION OF THE MESSAGE USING INFLATOR*/
	public byte [] decompress(byte [] msg, int size) throws DataFormatException
	{
		outBuff = new byte [size];
		inflater.setInput(msg);
		inflater.inflate(outBuff);
		inflater.reset();
		return outBuff;
	}
}
