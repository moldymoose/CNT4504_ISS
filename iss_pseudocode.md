#  Server

```
#imports (libraries)
Ask user for a port number to listen on
Create a serverSocket(port)
Open serverSocket

While(true) {
	accept(clientCommand)
	run this Linux command
	collect result()
	send results back to client
}
```

# Client

```
#imports (libraries)
Ask user for IP address of the server
Ask user for port to send data to(port)
Create a socket(address, port)

While {
	Present menu of commands
	ask user which command to run
	ask user how many times to run
	create threads
	run threads
	calculate total time
	calculate average time
}

threadHandler method
Start a timer
Send command to server
Collect response from server
Stop timer
End timer - start timer = thread time

```