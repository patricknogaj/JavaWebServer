import java.net.ServerSocket;
import java.net.Socket;

/**
 * Rutgers University -- Information Technology CS352
 * @author Patrick Nogaj (NET ID: pn220) 
 * 	   Stephen Fu (NET ID: svf13) 
 *         Jayson Pitta (NET ID: jrp289)
 * 
 */

public class PartialHTTP1Server {

	/**
	 * We start by attempting to parse arg[0] as an Integer which will be stored into port variable.
	 * We create a ServerSocket, create a socket object, and accept the incoming connection, and pass it to ClientHandler
	 * @param args :: arg[0] -> port number (integer)
	 */
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
