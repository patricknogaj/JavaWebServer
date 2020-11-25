import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.RejectedExecutionException;

/**
 * Rutgers University -- Information Technology CS352
 * @author Patrick Nogaj (NET ID: pn220) 
 *		   Stephen Fu (NET ID: svf13) 
 *         Jayson Pitta (NET ID: jrp289)
 * 
 */

public class ClientHandler implements Runnable {
	
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
	
	static int counter = 1;
	
	//Stores script, and arguments if needed
	private String[] command = new String[2];
	
	//Environmental variables
	private String CONTENT_LENGTH = null,
				   CONTENT_TYPE,
				   SCRIPT_NAME,
				   SERVER_NAME,
				   SERVER_PORT,
				   HTTP_FROM,
				   HTTP_USER_AGENT,
				   QUERY_STRING;
	
	/**
	 * Default constructor which starts the thread in run()
	 * @param socket - socket passed from PartialHTTP1Server.java
	 */
	public ClientHandler(Socket socket) {
		this.socket = socket;
		//run();
	}
	
	/**
	 * Executor will call run() once executed(), which calls processRequest()
	 */
	public void run() {
		try {			
			processRequest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Opens streams for input/output and processes information.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void processRequest() throws IOException, InterruptedException {
		
		if(!socket.isClosed()) {
			//Initial streams that we need
			BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
			PrintStream out = new PrintStream(socket.getOutputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

			//5 second timeout for err 408
			socket.setSoTimeout(5000);
			try {
				if(inputStream == null || reader == null)
					return;	
				
				reader.mark(1);
				
				synchronized(reader) {
					String requestLine = reader.readLine();
					String modifiedDate = reader.readLine();
					
					if(!modifiedDate.startsWith("If-Modified-Since:")) {
						reader.reset();
						requestLine = reader.readLine();
					} 
					
					if(requestLine == null)
						return;
					
					String[] header = requestLine.split("\\s+");
					
					if(header.length != 3) { //if header does not have 3 parts IE method, file, version
						out.print(getResponse(400));
					} else {	
						double versionNumber = 1.0;
						if(header[2].startsWith("HTTP/"))
							versionNumber = Double.parseDouble(header[2].substring(5, header[2].length())); //grabs version number
						
						if(!COMMANDS.containsKey(header[0])) { //sees if method is available
							out.print(getResponse(400));
						} else if(COMMANDS.containsKey(header[0]) && COMMANDS.get(header[0]) == 0) { //checks to see if method is supported
							out.print(getResponse(501));
						} else if(versionNumber < 0.0 || versionNumber > 1.0) { //checks for version number
								out.print(getResponse(505));
						} else {
							File file = new File("." + header[1]);
							int fileLength = (int) file.length();
							
							if(!file.isFile()) { //if FileNotFound -> 404
								if(header[0].equals("POST"))
									out.print(getResponse(405));
								else
									out.print(getResponse(404));
							} else if(!file.canRead()){ //if no read perms -> 403
								out.print(getResponse(403));
							} else {
								Date lastModifiedDate = new Date(file.lastModified());
								Date currentDate = new Date();
								currentDate.setYear(currentDate.getYear() + 1);
								SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM YYYY HH:mm:ss zzz");
								dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
								byte[] payload = readFileData(file, fileLength);
									
								if(modifiedDate.length() > 0) {
									if(modifiedDate.substring(19, modifiedDate.length()).length() > 28) {
										Date ifModifiedDate = new Date(modifiedDate.substring(19, modifiedDate.length()));
										
										if(lastModifiedDate.compareTo(ifModifiedDate) == -1) {
											if(!header[0].contains("HEAD")) {
												out.print(getResponse(304));
												out.print("Expires: " + dateFormat.format(currentDate)+ "\r\n\r\n");
											}
										} 
									}
								}
								
								if(header[0].equals("GET") || header[0].equals("HEAD")) {
									out.print(getResponse(200));
									out.print("Content-Type: " + contentType(header[1]) + "\r\n" +
										  "Content-Length: " + fileLength + "\r\n" +
										  "Last-Modified: " + dateFormat.format(lastModifiedDate) + "\r\n" +
										  "Content-Encoding: identity" + "\r\n" +
										  "Allow: GET, POST, HEAD" + "\r\n" +
										  "Expires: " + dateFormat.format(currentDate)+ "\r\n\r\n");
									if(header[0].equals("GET"))
										out.write(payload);
								} else { //else POST
									command[0] = "." + header[1];
									SCRIPT_NAME = header[1].substring(0, header[1].length());
									String line = null;
								    while(reader.ready())  {
								    	line = reader.readLine();
								    	if(line.startsWith("From:"))
											HTTP_FROM = line.substring(6, line.length());
										if(line.startsWith("User-Agent:"))
											HTTP_USER_AGENT = line.substring(12, line.length());
										if(line.startsWith("Content-Length:"))
											CONTENT_LENGTH = line.substring(16, line.length());	
										if(line.startsWith("Content-Type:"))
											CONTENT_TYPE = line.substring(14, line.length());
								    }
								    
								    if(line.trim().isEmpty() == false) {
								    	QUERY_STRING = line;
								    	command[1] = QUERY_STRING;
								    }
									
									if(CONTENT_LENGTH == null) {
										out.print(getResponse(411));
									} else if(CONTENT_TYPE == null) {
										out.print(getResponse(500));
									} else if(!SCRIPT_NAME.endsWith(".cgi")) {
										out.print(getResponse(405));
									} else {
										String result = "";
										ProcessBuilder pb = new ProcessBuilder(command[0]);
										
										//set environmental variables for Processes
										Map<String, String> env = pb.environment();
										env.put("CONTENT_LENGTH", CONTENT_LENGTH);
										env.put("SCRIPT_NAME", header[1]);
										env.put("SERVER_NAME", String.valueOf(InetAddress.getLocalHost()));
										env.put("SERVER_PORT", String.valueOf(socket.getPort()));
										env.put("HTTP_FROM", HTTP_FROM);
										env.put("HTTP_USER_AGENT", HTTP_USER_AGENT);
										
										Process process = pb.start();
										
										//send arguments to std in if arguments are available
										if(command[1] != null) {
											OutputStream stdin = process.getOutputStream();
											stdin.write(decode(command[1]).getBytes());
											stdin.close();
										}

										BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));

										result = stdout.readLine();

										//generate payload
										while (stdout.ready()) {
											result += '\n' + stdout.readLine();
										}
										
										if(result == null) { //if no content displayed --> err 204
											out.print(getResponse(204));
										} else {
											out.print(getResponse(200));
											out.print("Content-Type: text/html\r\n" +
													  "Content-Length: " + result.length() + "\r\n" +
													  "Allow: GET, POST, HEAD" + "\r\n" +
													  "Expires: " + dateFormat.format(currentDate)+ "\r\n\r\n");
											out.print(result);
											
											System.out.println("Test case #: " + counter);
											System.out.println(result);
											System.out.println();
										}
										stdout.close();
										process.destroy();
									}
								}
							}
						}
					}
				}
			} catch (SocketException se) {
				
			} catch (SocketTimeoutException se) {
				out.print(getResponse(408));
			} catch (RejectedExecutionException ex) {
				out.print(getResponse(500));
			}
			out.flush();
			
			Thread.sleep(250); //wait for 0.25s after flush to close out everything else.
			out.close();
			inputStream.close();
			reader.close();
		}
		socket.close();
	}
	
	/**
	 * Return data from file in byte[] format.
	 * @param file :: File passed as argument, from header[1]
	 * @param fileLength :: length of file to allocate enough byte[] space.
	 * @return :: return byte[] which is content of file
	 * @throws IOException :: if FileNotFound throw Exception
	 */
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
	 * Decodes encoded message
	 * @param input message from script
	 * @return input message but removing the encoding
	 */
	public String decode(String input) {
		if(input.contains("!!"))
			input = input.replaceAll("!!", "!");
		if(input.contains("!*"))
			input = input.replaceAll("!*", "*");
		if(input.contains("!'"))
			input = input.replaceAll("!'", "'");
		if(input.contains("!("))
			input = input.replaceAll("!(", "(");
		if(input.contains("!)"))
			input = input.replaceAll("!)", ")");
		if(input.contains("!;"))
			input = input.replaceAll("!;", ";");
		if(input.contains("!:"))
			input = input.replaceAll("!:", ":");
		if(input.contains("!@"))
			input = input.replaceAll("!@", "@");
		if(input.contains("!$"))
			input = input.replaceAll("!$", "$");
		if(input.contains("!+"))
			input = input.replaceAll("!+", "+");
		if(input.contains("!,"))
			input = input.replaceAll("!,", ",");
		if(input.contains("!/"))
			input = input.replaceAll("!/", "/");
		if(input.contains("!?"))
			input = input.replaceAll("!?", "?");
		if(input.contains("!#"))
			input = input.replaceAll("!#", "#");
		if(input.contains("!["))
			input = input.replaceAll("!\\[", "\\[");
		if(input.contains("!]"))
			input = input.replaceAll("!]", "]");
		return input;
	}
	
	/**
	 * Return response type based on code passed in. Utilized for writing response back to user.
	 * @param code - response code.
	 * @return response code message
	 */
	public String getResponse(int code) {
		if(code == 200)
			return "HTTP/1.0 200 OK\r\n";
		if(code == 204)
			return "HTTP/1.0 204 No Content\r\n";
		if(code == 304)
			return "HTTP/1.0 304 Not Modified\r\n";
		if(code == 400)
			return "HTTP/1.0 400 Bad Request\r\n";
		if(code == 403)
			return "HTTP/1.0 403 Forbidden\r\n";
		if(code == 404)
			return "HTTP/1.0 404 Not Found\r\n";
		if(code == 405)
			return "HTTP/1.0 405 Method Not Allowed\r\n";
		if(code == 408)
			return "HTTP/1.0 408 Request Timeout\r\n";
		if(code == 411)
			return "HTTP/1.0 411 Length Required\r\n";
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
