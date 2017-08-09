/* program to get the client back if it has been removed prematurely*/

package general.server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ReinstateClient {

	private ArrayList<Client> inmates;
	private Timer timer;
	private static final int sentence = 60000;

	public ReinstateClient() {
		inmates = new ArrayList<Client>();
		timer = new Timer();
	}

	public synchronized void addInmate(Client c) {
		inmates.add(c);
		timer.schedule(new Excutioner(c), sentence);
	}

	public synchronized boolean reinstate(Socket s, String name) {
		for (Client inmate : inmates) {
			if (inmate.getSocket().getInetAddress().toString()
					.equals(s.getInetAddress().toString())
					&& inmate.getUserName().equals(name)) {
				inmates.remove(inmate);
				inmate.reinstate(s);
				return true;
			}
		}

		return false;
	}
	private class Excutioner extends TimerTask {
		private Client inmate;

		public Excutioner(Client inmate) {
			this.inmate = inmate;
		}

		public void run() {
			if (!inmates.contains(inmate))
				return;
			inmates.remove(inmate);
			MainServer.removeClient(inmate.getUserId());
		}
	}
}
