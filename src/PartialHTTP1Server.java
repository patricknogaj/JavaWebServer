import java.net.ServerSocket;
import java.net.Socket;

/**
 * Rutgers University -- Information Technology CS352
 * @author Patrick Nogaj (NET ID: pn220)
 * 
 */

public class PartialHTTP1Server {

	public static void main(String[] args) {
		
		int port = 0;
		
		if(args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.err.println("ERR: Argument 0 must be a valid integer.");
				System.exit(1);
			}
		} else {
			System.err.println("ERR: No port specified.");
			System.exit(1);
		}
		
		try(ServerSocket server = new ServerSocket(port)) {
			System.out.println("Server is listening on port " + port);
			while(true) {
				Socket socket = server.accept();
				new ClientHandler(socket);
			}
		} catch (Exception e) {
			System.err.println("ERR: " + e.getMessage());
		}
	}

}
