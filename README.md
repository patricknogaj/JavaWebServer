# Project 1: Java Web Server for HTTP 1.0
Java Web Server for CS352 (Information Technology) at Rutgers University.
Due Date: September 30th, 11:55 PM

## Overview
In this assignment you will design a web server in Java that supports a functional subset of the HTTP 1.0 protocol, the initial full release of the HyperText Transfer Protocol. Your web server will have to accept incoming socket connections, read a request from the client, and answer with the appropriate HTTP status code.

This assignment is expected to take around 12 to 28 hours to complete, though it may take more or less time depending on your abilities and background. It is essential that you work effectively with your group to complete it in a reasonable amount of time.

It is due 11:55PM Sept. 30.  You must submit via the Sakai Assignments 2 tool.  Only on-time assignments submitted through Sakai will be accepted.
  
## Specifics of Operation
Your program's main file must be named "PartialHTTP1Server.java" without any Java packaging. This means that running your compiled program will be identical to the output below:

	java -cp . PartialHTTP1Server 3456

You should be sparing in the use of additional classes, however you should design as you feel fit.

Compress all your source files into a '.zip' (Windows zip) or 'tar.gz' (Linux gzipped tarball) named "PartialHTTP1Server.zip" or "PartialHTTP1Server.tar.gz"

Submit your compressed file. Do not use any Java packaging.

## Requirements
Accept the port to listen on as args[0] in the main method.  The port should be parsed into an int or Integer.

Construct a ServerSocket that accepts connections on the port specified in the command-line argument.

When a client connects to your ServerSocket, you are to hand off the created Socket to a Thread that handles all the client's communication. Absolutely no reading or writing to or from a Socket should occur in the class that the ServerSocket is accepting connections in except when all your Threads are busy (see below).

Your server should support at most 50 simultaneous connections efficiently. You should have new connections serviced by Threads in an extensible data structure or Thread pool that holds space for no more than 5 Threads when the server is idle. Your pool of available Threads, or space for Threads, should expand and contract commensurate with the average rate of incoming connections. Your server loop should NOT ACCEPT new connections if all 50 Threads are busy and should instead send a "503 Service Unavailable" response and immediately close the connection. This is the only time the main class that holds your ServerSocket is allowed to use a Socket it created for communication.

In your communication Thread you should read a single String from the client, parse it as an HTTP 1.0 request and send back an appropriate response according to the HTTP 1.0 protocol.
  
You must support GET, POST and HEAD commands.
You must support MIME types (see which below).
Your server must also include all elements of a properly-formatted HTTP 1.0 response.
You need not implement the HTTP 1.0 commands DELETE, PUT, LINK or UNLINK.

Once your response has been sent, you should flush() your output streams, wait a quarter second, close down all communication objects and cleanly exit the communication Thread.

Be careful to handle Exceptions intelligently. No Exceptions should be capable of crashing your server.

## Grading
Source code organization and comments: 20%
Correct parsing of all request commands: 10%
Correct parsing of MIME types: 10%
Efficient communication thread management: 20%
Correct implementation of all response codes: 10%
Inclusion of all response header fields: 10%
Isolation of Thread Exceptions from server code: 10%    
Closing all Sockets/IOStreams before Thread termination: 10%

## Additional Information
HTTP 1.0 is fully defined in <a href="https://tools.ietf.org/html/rfc1945">RFC-1945</a>

1. Version tags:
    Every HTTP 1.0 request and response must have the HTTP version in it.

    Requests:
        <command> <resource> HTTP/1.0
        
    Response:
        HTTP/1.0 <status code> <explanation>
        <response head>
        
        <response body>
 
    If your server receives a request that does not have a version number, it is considered malformed and should get a "400 Bad Request" response.
    If your server receives a request that has a version number greater than 1.0, the version is higher than what you can support, and you should respond with a "505 HTTP Version Not Supported"

    
2. Additional request commands:
    HTTP 1.0 has multiple additional request commands. You however only need to support HEAD and POST for this version. For this version, treat POST the same as GET.
    
    If you receive a valid HTTP/1.0 request command that you do not support, you should respond with "501 Not Implemented". If you receive request command that is not in HTTP/1.0, you should respond with a "400 Bad Request", so even if you do not implement all the commands in HTTP 1.0, you must still recognize them.
 

3. Header fields:
    HTTP 1.0 responses are much more textured and contain a series of fields that provide additional information about the data. You should be sure to add the following header fields to each "200 OK":
    
    Allow, Content-Encoding, Content-Length, Content-Type, Expires, Last-Modified
    
    HTTP 1.0 requests also have additional header fields. On the whole you can ignore them expect for:
    
    If-Modified-Since
    

    
4. MIME types:
    MIME types were originally engineered for SMTP in order to describe how data should be parsed and interpreted. The Content-Type header field in HTTP 1.0 responses is a MIME type, as defined in RFC-1521. Since MIME types describe the format of the data being delivered, they are based on a resource's file type extension and always consist of <type>/<subtype>. Regular ".html" files are, for instance "text/html". You should support delivering the following MIME types and subtypes:
 
    text/(html and plain)
    image/(gif, jpeg and png)
    application/(octet-stream, pdf, x-gzip, zip)
    
    If you ever receive a request for a resource whose MIME type you do not support or can not determine, you should default to 'application/octet-stream'.
    There are several resources that document MIME types and how they pair up with filename extensions, like this catalogue.
    

5. Response status codes:
    HTTP 1.0 has quite a few additional status codes. You should support:
    
    200    OK
    304    Not Modified
    400    Bad Request
    403    Forbidden
    404    Not Found
    408    Request Timeout
    500    Internal Server Error
    501    Not Implemented
    503    Service Unavailable
    505    HTTP Version Not Supported

6. A Request Timeout is required.

 If the client opens a connection with the server but does not send any request within 5 seconds, your server should send this response.

   408 Request Timeout
