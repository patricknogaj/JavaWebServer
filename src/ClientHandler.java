import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;

/**
 * Rutgers University -- Information Technology CS352
 * @author Patrick Nogaj (NET ID: pn220)
 * 
 */

public class ClientHandler extends Thread {
	
	private Socket socket;
	
	//HashMap value of {COMMAND_NAME, SUPPORTED {1: true, 0: false}
	final HashMap<String, Integer> COMMANDS = new HashMap<String, Integer>() {{
		put("GET", 1);
		put("HEAD", 1);
		put("POST", 1);
		put("LINK", 0);
		put("UNLINK", 0);
		put("DELETE", 0);
		put("PUT", 0);
	}};
	
	public ClientHandler(Socket socket) {
		this.socket = socket;
		start();
	}
	
	public void run() {
		try {
			InputStream inputStream = socket.getInputStream();
			PrintStream dataOut = new PrintStream(socket.getOutputStream());
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String requestLine = reader.readLine();
			
			String[] header = requestLine.split("\\s+");
			
			if(header.length != 3) {
				writeResponse(dataOut, 400);
				reader.close();
				socket.close();
				return;
			}
			
			if(!COMMANDS.containsKey(header[0])) {
				writeResponse(dataOut, 400);
				reader.close();
				socket.close();
				return;
			}
			
			if(COMMANDS.containsKey(header[0]) && COMMANDS.get(header[0]) == 0) {
				writeResponse(dataOut, 501);
				reader.close();
				socket.close();
				return;
			}
			
			if(!header[2].equals("HTTP/1.0")) {
				writeResponse(dataOut, 505);
				reader.close();
				socket.close();
				return;
			}
			
			System.out.println("RequestLine: " + requestLine);
			
			reader.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeResponse(PrintStream out, int code) {
		out.print("HTTP/1.0 " + code + " " + getResponse(code));
		out.close();
	}
	
	/**
	 * Return response type based on code passed in. Utilized for writing response back to user.
	 * @param code - response code.
	 * @return response code message
	 */
	public String getResponse(int code) {
		if(code == 200)
			return "OK";
		if(code == 304)
			return "Not Modified";
		if(code == 400)
			return "Bad Request";
		if(code == 403)
			return "Forbidden";
		if(code == 404)
			return "Not Found";
		if(code == 408)
			return "Request Timeout";
		if(code == 500)
			return "Internal Server Error";
		if(code == 501)
			return "Not Implemented";
		if(code == 503)
			return "Service Unavailable";
		if(code == 505)
			return "HTTP Version Not Supported";
		return " ";
	}
	
	/**
	 * Returns content type back to user.
	 * @param fileName - file passed as argument.
	 * @return - return type based on file extension.
	 */
	public String contentType(String fileName) {
		if(fileName.endsWith(".txt"))
			return "text/plain";
		if(fileName.endsWith(".htm") || fileName.endsWith(".html"))
			return "text/html";
		if(fileName.endsWith(".gif"))
			return "image/gif";
		if(fileName.endsWith(".jpeg"))
			return "image/jpeg";
		if(fileName.endsWith(".png"))
			return "image/png";
		if(fileName.endsWith(".pdf"))
			return "application/pdf";
		if(fileName.endsWith(".zip"))
			return "application/zip";
		if(fileName.endsWith(".x-gzip"))
			return "application/x-gzip";
		return "application/octet-stream";
	}

}
