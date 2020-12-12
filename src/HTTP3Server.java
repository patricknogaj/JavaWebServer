import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Rutgers University -- Information Technology CS352
 * @author Patrick Nogaj (NET ID: pn220) 
 * 		   Stephen Fu (NET ID: svf13) 
 *         Jayson Pitta (NET ID: jrp289)
 * 
 */

public class HTTP3Server {
	
	/**
	 * We start by attempting to parse arg[0] as an Integer which will be stored into port variable.
	 * We create a ServerSocket, create a socket object, and accept the incoming connection, and pass it to ClientHandler
	 * @param args :: arg[0] -> port number (integer)
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		
		ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 50, 1, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
		int port = 0;
		
		if(args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
				
				/**
				 * Check to see if port is in appropriate range
				 * Port cannot be less than 0, or higher than 65,535 according to standards
				 */
				if(port < 0 || port > 65535) { 
					System.out.println("ERR: Port number out of range...closing program");
					System.exit(1);
				}
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
				executor.execute(new ClientHandler(socket));
				
				if(executor.getActiveCount() == 50) {
					PrintStream out = new PrintStream(socket.getOutputStream());
					out.print("HTTP/1.0 503 Service Unavailable\r\n");
				}
			}
		} catch (RejectedExecutionException ex) {
			System.err.println("Rejected: " + ex.getMessage());
		} catch (Exception e) {
			System.err.println("ERR: " + e.getMessage());
		}
		
		executor.shutdown();
	}
	
}
