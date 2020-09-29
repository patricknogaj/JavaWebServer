<h2>Tasks</h2>
Below, we will denote each task, and we will update this document when a certain task is complete to minimize miscommunication.<br><br>

Name | Contact Information
------------ | -------------
Patrick Nogaj | C: (732) 580-0207<br>Discord: myth#3559<br>E-mail: patrick.nogaj@rutgers.edu
xxx | xxx
xxx | xxx

<h2>Requirements</h2>

* [x] Accept the port to listen on as args[0] in the main method. The port should be parsed into an int or Integer.<br><b>Completion date:</b> September 16th, 2020

* [x] Construct a ServerSocket that accepts connections on the port specified in the command-line argument. <br><b>Completion date:</b> September 16th, 2020

* [ ] When a client connects to your ServerSocket, you are to hand off the created Socket to a Thread that handles all the client's communication. Absolutely no reading or writing to or from a Socket should occur in the class that the ServerSocket is accepting connections in except when all your Threads are busy (see below).<br><br>Your server should support at most 50 simultaneous connections efficiently. You should have new connections serviced by Threads in an extensible data structure or Thread pool that holds space for no more than 5 Threads when the server is idle. Your pool of available Threads, or space for Threads, should expand and contract commensurate with the average rate of incoming connections. Your server loop should NOT ACCEPT new connections if all 50 Threads are busy and should instead send a "503 Service Unavailable" response and immediately close the connection. This is the only time the main class that holds your ServerSocket is allowed to use a Socket it created for communication.

* [ ] In your communication Thread you should read a single String from the client, parse it as an HTTP 1.0 request and send back an appropriate response according to the HTTP 1.0 protocol.

* [ ] You must support GET, POST and HEAD commands. You must support MIME types (see which below). Your server must also include all elements of a properly-formatted HTTP 1.0 response. You need not implement the HTTP 1.0 commands DELETE, PUT, LINK or UNLINK. <br><b>Completion date:</b> September 27th, 2020

* [x] Once your response has been sent, you should flush() your output streams, wait a quarter second, close down all communication objects and cleanly exit the communication Thread. <br><b>Completion date:</b> September 27th, 2020

* [ ] Be careful to handle Exceptions intelligently. No Exceptions should be capable of crashing your server.

<h2>Test Case Completion</h2>

Test cases 1-20 work.
