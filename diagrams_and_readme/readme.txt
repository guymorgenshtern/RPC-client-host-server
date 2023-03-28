Premise:
To send messages from client —> host —> server with two independent hosts each responsible for one half of the communication. The client is to encode a message into a byte array and start the interaction by sending the Datagram to a port used by the ClientHost who echos the message, from there the server will request the data by sending a request to the ServerHost. The two shared buffers allow the either Host to access data received by the other Host.

Client: It begins the interaction and sends messages out first, then receives
Host: Acts as an intermediate between the Client and Server. Receives first, and echos.
ClientHost: operates by first receiving data and echoing, then requests an acknowledgement from the server which will arrive via ServerHost
ServerHost: operates by first requesting data from client which arrives via ClientHost, then generates an acknowledgement and sends it back
Server: Where “logic” is performed. Receives a message first, decodes it, then sends a reply back to the host to send to the client

Setup:
- Enter Eclipse
- File —> Open Projects from File System 
- Locate and open the downloaded submission titled “101151430_Assignment3”
- Run Host first, followed by either Server or Client
- Running multiple files will create multiple consoles. To see all console output cycle through the consoles of each class using the “Display selected console” button in the Console View window

Question1:
- Two threads are needed because each is responsible for one half of the communication of the system. They function differently than each other with their order of operations being reversed, that's why two different classes are needed. Two threads are needed because for the system to function properly, the hosts need to run concurrently.

Question2:
The intermediate tasks use a buffer with synchronised methods within it. Synchronisation is necessary as to ensure that data within the buffers stays consistent and is not edited until the synch. conditions are met. If the buffer is not synchronised them data can be lost or sent out of order which shouldn't happen as the flow of the system states that one message should be sent and acknowledged at a time. 
