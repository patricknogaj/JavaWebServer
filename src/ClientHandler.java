import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Rutgers University -- Information Technology CS352
 * @author Patrick Nogaj (NET ID: pn220), Stephen Fu (NET ID: svf13), ??? (NET ID: ???)
 * 
 */

public class ClientHandler extends Thread {
	
	private Socket socket;
	
	//HashMap value of {COMMAND_NAME, SUPPORTED {1: true, 0: false}
	private final HashMap<String, Integer> COMMANDS = new HashMap<String, Integer>() {{
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
			PrintStream out = new PrintStream(socket.getOutputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
			
			socket.setSoTimeout(5000);
			try {
				String requestLine = reader.readLine();
				String modifiedDate = reader.readLine();
				String[] header = requestLine.split("\\s+");
				
				if(header.length != 3) {
					out.print(getResponse(400));
				} else {
					if(!COMMANDS.containsKey(header[0])) {
						out.print(getResponse(400));
					} else if(COMMANDS.containsKey(header[0]) && COMMANDS.get(header[0]) == 0) {
						out.print(getResponse(501));
					} else if(!header[2].equals("HTTP/1.0")) {
						out.print(getResponse(505));
					} else {
						File file = new File(header[1].substring(1, header[1].length())).getAbsoluteFile();
						int fileLength = (int) file.length();
						
						if(!file.isFile()) {
							out.print(getResponse(404));
						} else if(!file.canRead()){
							out.print(getResponse(403));
						} else {
							long lastModified = file.lastModified();
							Date date = new Date(lastModified);
							Date currentDate = new Date();
							SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM YYYY HH:mm:ss zzz");
							dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
							byte[] payload = readFileData(file, fileLength);
							
							if(modifiedDate.length() > 0) {
								if(modifiedDate.substring(19, modifiedDate.length()).length() > 28) {
									Date ifModifiedDate = new Date(modifiedDate.substring(19, modifiedDate.length()));
								
									if(date.compareTo(ifModifiedDate) == -1) {
										if(!header[0].contains("HEAD")) {
											out.print(getResponse(304));
											out.print("Expires: " + dateFormat.format(new Date(currentDate.getYear() + 1, currentDate.getMonth(), currentDate.getDay()))+ "\r\n\r\n");
										}
									} 
								}
							}
							out.print(getResponse(200));
							out.print("Content-Type: " + contentType(header[1]) + "\r\n" +
									  "Content-Length: " + fileLength + "\r\n" +
									  "Last-Modified: " + dateFormat.format((new Date(lastModified))) + "\r\n" +
									  "Content-Encoding: identity" + "\r\n" +
									  "Allow: GET, POST, HEAD" + "\r\n" +
									  "Expires: " + dateFormat.format(new Date(currentDate.getYear() + 1, currentDate.getMonth(), currentDate.getDay()))+ "\r\n\r\n");
							outStream.write(payload);
						}
					}
				}
			} catch (SocketTimeoutException se) {
				out.print(getResponse(408));
			}
			
			outStream.flush();
			outStream.close();
			out.flush();
			out.close();
			inputStream.close();
			reader.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private byte[] readFileData(File file, int fileLength) throws IOException {
		FileInputStream fileInput = null;
		byte[] fileData = new byte[fileLength];
		
		try {
			fileInput = new FileInputStream(file);
			fileInput.read(fileData);
		} finally {
			if (fileInput != null) 
				fileInput.close();
		}
		return fileData;
	}
	
	/**
	 * Return response type based on code passed in. Utilized for writing response back to user.
	 * @param code - response code.
	 * @return response code message
	 */
	public String getResponse(int code) {
		if(code == 200)
			return "HTTP/1.0 200 OK\r\n";
		if(code == 304)
			return "HTTP/1.0 304 Not Modified\r\n";
		if(code == 400)
			return "HTTP/1.0 400 Bad Request\r\n";
		if(code == 403)
			return "HTTP/1.0 403 Forbidden\r\n";
		if(code == 404)
			return "HTTP/1.0 404 Not Found\r\n";
		if(code == 408)
			return "HTTP/1.0 408 Request Timeout\r\n";
		if(code == 500)
			return "HTTP/1.0 500 Internal Server Error\r\n";
		if(code == 501)
			return "HTTP/1.0 501 Not Implemented\r\n";
		if(code == 503)
			return "HTTP/1.0 503 Service Unavailable\r\n";
		if(code == 505)
			return "HTTP/1.0 505 HTTP Version Not Supported\r\n";
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
