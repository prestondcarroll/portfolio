# TCP/IP Chatroom

![Video Demo](https://raw.githubusercontent.com/prestondcarroll/projects/master/TCP_IP_Chatroom/chat_client_demo.mp4)

This program is a simple online chat application where up to 255 participants can connect and send messages to each other.
A new connection can either be a observer or a participant. Participants have to pick a username and can send messages, while
observers have to pick a username of a corresponding participant so they can then see messages. A more advanced program would have
both of these combined into a single display, but this was done to simplify the implementation. 

This was programmed in C for a Computer Networks class, March 2018. It utilizes the select() linux function and the UNIX socket API to keep track of the clients. - Temporarily Public

There are three components to this program
* Server: The main server that all of the clients connect to. It will send forward message to the corrects user(s) and is also
responsible for affiliating participants with observers
* Participant: A client that has a username and can send messages
* Observer: A client that displays users that have connected/disconnected, and the stream of messages being sent across the server

### Features 
* Username support: users must enter a unique username within 60 seconds or they will be timed out
* Private messaging: users can PM another user by putting @username in front of their message
* Up to 255 clients: 255 participants & observers can be connected to the server. After that new users will be notified that
the server is full
* Message limit: messages over 1000 in length will be truncated
