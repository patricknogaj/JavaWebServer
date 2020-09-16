# Project 1: Java Web Server for HTTP 1.0 (Due September 30th)

Java Web Server for CS352 (Information Technology) at Rutgers University.

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