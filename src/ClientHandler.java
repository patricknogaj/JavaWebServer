import java.net.Socket;

/**
 * Rutgers University -- Information Technology CS352
 * @author Patrick Nogaj (NET ID: pn220)
 * 
 */

public class ClientHandler extends Thread {
	
	private Socket socket;
	
	public ClientHandler(Socket socket) {
		this.socket = socket;
		start();
	}
	
	public void run() {
		
	}

}
