/* program that store the text messages queue*/

package general.server;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class MessageTextQueue extends Thread{
	
	private Queue<byte []> messageQueue;
	private ByteBuffer messageBreaker;
	
	public MessageTextQueue()
	{
		messageQueue = new LinkedList<byte[]>();
	}
	
	public synchronized void addMessage(byte [] message)
	{
		messageQueue.add(message);
	}
	
	public void run()
	{
		while(true)
		{
			if (messageQueue.isEmpty())
			{
				try
				{
					Thread.sleep(500);
					continue;
				}catch(InterruptedException e)
				{
					System.out.println(e);
				}
			}
			
			byte [] message;
			synchronized (messageQueue){
			message = messageQueue.remove();
			}
			messageBreaker = ByteBuffer.allocate(message.length);
			messageBreaker.put(message);
			handleMessage(message[0]);
			
		}
	}
	
	private void handleMessage(byte code)
	{
		int room = messageBreaker.getInt(1);
		int sender = messageBreaker.getInt(5);
		boolean privateRoom = false;
		if ((code & 0x10) > 0)
			privateRoom = true;
		ArrayList<Integer> roomClients = MainServer.getRoomClients(room, privateRoom);
		for (int c : roomClients)
		{
			if (c == sender)
				continue;
			MainServer.clients.get(c).sendMessage(messageBreaker.array());
		}
	}
	
}
