<center><h1>CS352 Project 1 Taskers</h1></center>
<h2>Tasks</h2>
Below, we will denote each task, and we will update this document when a certain task is complete to minimize miscommunication.
<br>

Name | Contact Information
------------ | -------------
Patrick Nogaj | C: (732) 580-0207<br>Discord: myth#<br>E-mail: patrick.nogaj@rutgers.edu
xxx | xxx
xxx | xxx

<h2>Requirements</h2>

* [x] Accept the port to listen on as args[0] in the main method. The port should be parsed into an int or Integer.

* [ ] Construct a ServerSocket that accepts connections on the port specified in the command-line argument.

* [ ] When a client connects to your ServerSocket, you are to hand off the created Socket to a Thread that handles all the client's communication. Absolutely no reading or writing to or from a Socket should occur in the class that the ServerSocket is accepting connections in except when all your Threads are busy (see below).

* [ ] In your communication Thread you should read a single String from the client, parse it as an HTTP 1.0 request and send back an appropriate response according to the HTTP 1.0 protocol.

* [ ] You must support GET, POST and HEAD commands. You must support MIME types (see which below). Your server must also include all elements of a properly-formatted HTTP 1.0 response. You need not implement the HTTP 1.0 commands DELETE, PUT, LINK or UNLINK.

* [ ] Once your response has been sent, you should flush() your output streams, wait a quarter second, close down all communication objects and cleanly exit the communication Thread.

* [ ] Be careful to handle Exceptions intelligently. No Exceptions should be capable of crashing your server.

<h2>Test Case Completion</h2>