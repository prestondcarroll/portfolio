/* prog3_server.c - code for the main server */

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <stdio.h>
#include <errno.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/time.h>
#include <ctype.h>
#include <time.h>

#define QLEN 6 /* size of request queue */
#define MAX_PLAYERS 255
#define TIMEOUT_TIME 60

// struct to keep track of active players (observers/participants)
typedef struct player {
	int part_sd;
	int obs_sd;
	char* username;
} player;

//struct to hold data about temporary clients
typedef struct client {
	int sd;
	double time_left;
} client;

void add_temp_obs(int obs_sockets[MAX_PLAYERS], fd_set* fdset, int* max_sd);
void add_temp_part(int part_sockets[MAX_PLAYERS], fd_set* fdset, int* max_sd);
void add_Participant(int part_sockets[MAX_PLAYERS], int new_sock);
void add_Observer(int obs_sockets[MAX_PLAYERS], int new_sock);
void populate_set(fd_set* fdset, player* players[MAX_PLAYERS], int* max_sd);
int username_taken(player* players[MAX_PLAYERS], char* username);
void add_active_part(player* players[MAX_PLAYERS], int sd, char* username);
void add_active_obs(player* players[MAX_PLAYERS], int sd, char* username);
void send_to_all(player* players[MAX_PLAYERS], char* send_msg);
int match_username(player* players[MAX_PLAYERS], char* username);
void disconnect_part(player* temp, int* numObs, int* numPart, int sd);
void disconnect_obs(player* temp, int* numObs, int sd);
void create_msg(char* send_msg, char* recv_msg, char* username, int private);
void send_to_user(player* players[MAX_PLAYERS], char* send_msg, char* recv_msg, int sender_sd);
int parse_username(char* username);
void update_times(client* clients[MAX_PLAYERS*2], double diff_t);
void timeout_clients(client* clients[MAX_PLAYERS*2], int part_sockets[MAX_PLAYERS], int obs_sockets[MAX_PLAYERS], int* numPart, int* numObs, fd_set* fdset);
void add_timeout_client(client* clients[MAX_PLAYERS*2], int sd);
void reset_timer(client* clients[MAX_PLAYERS*2], int sd);
void remove_temp_client(int part_sockets[MAX_PLAYERS], int obs_sockets[MAX_PLAYERS], int sd, int* numPart, int* numObs, fd_set* fdset);
void erase_client(client* clients[MAX_PLAYERS*2], int sd);




/*------------------------------------------------------------------------
* Program: prog3_server.c
*
* Purpose: connect pariticpants and observers to the the server
* 		   users can have a username, can send private messages,
*		   can send public messages and so on.
*
* Syntax: ./prog3_sever observer_port participant_port
*
* obs_port - protocol port number to use
* part_port - protocol port number to use
*
*------------------------------------------------------------------------
*/

int main(int argc, char **argv) {
	struct protoent *ptrp; /* pointer to a protocol table entry */
	struct protoent *ptrp2; /* pointer to a protocol table entry */
	struct sockaddr_in sad, sad2; /* structure to hold server's address */
	struct sockaddr_in cad; /* structure to hold client's address */
	struct sockaddr_in cad2; /* structure to hold client's address */
	int obs_sd, part_sd, max_sd, new_sock, sd; /* socket descriptors */
	int port, port2; /* protocol port number */
	socklen_t alen, alen2; /* length of address */
	alen = sizeof( (struct sockaddr *) &cad);
	alen2 = sizeof( (struct sockaddr *) &cad2);
	int optval = 1; /* boolean value when we set socket option */
	char* buf = malloc(sizeof(char) * 1000); /* buffer for string the server sends */
	char* username = malloc(sizeof(char) * 11); /* place for username (10 chars + null) */
	char* recv_msg = malloc(sizeof(char) * 1001); /* place for username (1000 chars + null) */
	char* send_msg = malloc(sizeof(char) * 1015); /* place for username (1014 chars + null) */
	char new_user_msg[28];	/* message that new user has joined */
	char user_left_msg[27]; /* message that user has left */
	int obs_sockets[MAX_PLAYERS] = {0};	/* holds non active observer sockets */
	int part_sockets[MAX_PLAYERS] = {0}; /* holds non active participant sockets */

	player* players[MAX_PLAYERS]; /* pointers to structs of active players (parts & obs) */
	for (int i = 0; i < MAX_PLAYERS; i++) players[i] = NULL; /* Initialize to NULL */
	client* clients[MAX_PLAYERS*2];  /* pointers to structs of clients (non active obs or part) */
	for (int i = 0; i < MAX_PLAYERS*2; i++) clients[i] = NULL; /* Initialize to NULL */

	int numPart = 0;
	int numObs = 0;
	int n; /*used for number of bytes in recv calls*/
	int valid_username = 0; /* boolean */
	int matching_username = 0; /* boolean */
	uint8_t user_len = 0;
	uint16_t recv_msg_len = 0;
	uint16_t net_recv_msg_len = 0; /* network byte order */
	char open = 'Y'; /*server is open */
	char full = 'N'; /* also acts as no matching active pariticpant */
	char taken = 'T'; /*userame is taken, resets timer*/
	char invalid = 'I'; /*Usename is invalid*/
	fd_set fdset; /*fd set containing all sd to monitor in select*/
	time_t before_t, after_t;
	double diff_t;
	struct timeval tv; /*used to timeout select*/
	tv.tv_sec = TIMEOUT_TIME;
	tv.tv_usec = 0;


	if( argc != 3 ) {
		fprintf(stderr,"Error: Wrong number of arguments\n");
		fprintf(stderr,"usage:\n");
		fprintf(stderr,"./server obs_port part_port\n");
		exit(EXIT_FAILURE);
	}

	memset((char *)&sad,0,sizeof(sad)); /* clear sockaddr structure */
	sad.sin_family = AF_INET; /* set family to Internet */
	sad.sin_addr.s_addr = INADDR_ANY; /* set the local IP address */

	port = atoi(argv[1]); /* convert argument to binary */
	if (port > 0) { /* test for illegal value */
		sad.sin_port = htons((u_short)port);
	} else { /* print error message and exit */
		fprintf(stderr,"Error: Bad port number %s\n",argv[1]);
		exit(EXIT_FAILURE);
	}

	/* Map TCP transport protocol name to protocol number */
	if ( ((long int)(ptrp = getprotobyname("tcp"))) == 0) {
		fprintf(stderr, "Error: Cannot map \"tcp\" to protocol number");
		exit(EXIT_FAILURE);
	}

	/* Create a socket */
	obs_sd = socket(PF_INET, SOCK_STREAM, ptrp->p_proto);
	if (obs_sd < 0) {
		fprintf(stderr, "Error: Socket creation failed\n");
		exit(EXIT_FAILURE);
	}

	/* Allow reuse of port - avoid "Bind failed" issues */
	if( setsockopt(obs_sd, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(optval)) < 0 ) {
		fprintf(stderr, "Error Setting socket option failed\n");
		exit(EXIT_FAILURE);
	}

	/* Bind a local address to the socket */
	if (bind(obs_sd, (struct sockaddr *)&sad, sizeof(sad)) < 0) {
		fprintf(stderr,"Error: Bind failed\n");
		exit(EXIT_FAILURE);
	}

	/* Specify size of request queue */
	if (listen(obs_sd, QLEN) < 0) {
		fprintf(stderr,"Error: Listen failed\n");
		exit(EXIT_FAILURE);
	}


	/*Socket 2 */
	memset((char *)&sad2,0,sizeof(sad2)); /* clear sockaddr structure */
	sad2.sin_family = AF_INET; /* set family to Internet */
	sad2.sin_addr.s_addr = INADDR_ANY; /* set the local IP address */

	port2 = atoi(argv[2]); /* convert argument to binary */
	if (port > 0) { /* test for illegal value */
		sad2.sin_port = htons((u_short)port2);
	} else { /* print error message and exit */
		fprintf(stderr,"Error: Bad port number %s\n",argv[1]);
		exit(EXIT_FAILURE);
	}

	/* Map TCP transport protocol name to protocol number */
	if ( ((long int)(ptrp2 = getprotobyname("tcp"))) == 0) {
		fprintf(stderr, "Error: Cannot map \"tcp\" to protocol number");
		exit(EXIT_FAILURE);
	}

	/* Create a socket 2 */
	part_sd = socket(PF_INET, SOCK_STREAM, ptrp2->p_proto);
	if (part_sd < 0) {
		fprintf(stderr, "Error: Socket creation failed\n");
		exit(EXIT_FAILURE);
	}
	/* Allow reuse of port - avoid "Bind failed" issues */
	if( setsockopt(part_sd, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(optval)) < 0 ) {
		fprintf(stderr, "Error Setting socket option failed\n");
		exit(EXIT_FAILURE);
	}

	/* Bind a local address to the socket */
	if (bind(part_sd, (struct sockaddr *)&sad2, sizeof(sad2)) < 0) {
		fprintf(stderr,"Error: Bind failed\n");
		exit(EXIT_FAILURE);
	}

	/* Specify size of request queue */
	if (listen(part_sd, QLEN) < 0) {
		fprintf(stderr,"Error: Listen failed\n");
		exit(EXIT_FAILURE);
	}

	printf("Server Now Running. Waiting for clients...\n");
	/* Main server loop - accept and handle requests */
	while (1) {
		valid_username = 0;
		max_sd = 0;
		FD_ZERO(&fdset);
		FD_SET(obs_sd, &fdset);
		FD_SET(part_sd, &fdset);
		if(obs_sd > part_sd)
			max_sd = obs_sd;
		else
			max_sd = part_sd;

		//add temporary and active clients to set
		add_temp_obs(obs_sockets, &fdset, &max_sd);
		add_temp_part(part_sockets, &fdset, &max_sd);
		populate_set(&fdset, players, &max_sd);

		tv.tv_sec = TIMEOUT_TIME;
		tv.tv_usec = 0;
		time(&before_t);
		n = select( max_sd + 1 , &fdset , NULL , NULL , &tv);
		time(&after_t);
		diff_t = difftime(after_t, before_t);
		update_times(clients, diff_t);
		timeout_clients(clients, part_sockets, obs_sockets, &numPart, &numObs, &fdset);

		if((n < 0) && (errno != EINTR)){
			fprintf(stderr, "Error: select error\n");
			printf("Select has error %s\n", strerror(errno));
			exit(EXIT_FAILURE);
		}
		else if(n){ //data was detected
			//new observer connection on observer port
			if(FD_ISSET(obs_sd, &fdset)){
				if ((new_sock=accept(obs_sd, (struct sockaddr *)&cad, &alen)) < 0) {
					fprintf(stderr, "Error: Accept failed on new obs\n");
					printf("Accept has error %s\n", strerror(errno));
					exit(EXIT_FAILURE);
				}
				if(numObs == MAX_PLAYERS){
					send(new_sock,&full,1,MSG_DONTWAIT);
					close(new_sock);
				}
				else {	//add to "non active" observer
					send(new_sock,&open,1,MSG_DONTWAIT);
					add_Observer(obs_sockets, new_sock);
					add_timeout_client(clients, new_sock);
					numObs++;
				}
			}


			//new participant connection on participant port
			if(FD_ISSET(part_sd, &fdset)){
				if ( (new_sock=accept(part_sd, (struct sockaddr *)&cad2, &alen2)) < 0) {
					fprintf(stderr, "Error: Accept failed on new part\n");
					printf("Accept has error %s\n", strerror(errno));
					exit(EXIT_FAILURE);
				}
				if(numPart == MAX_PLAYERS){
					send(new_sock, &full, 1, MSG_DONTWAIT);
					close(new_sock);
				}
				else { //add to non active participants
					send(new_sock, &open, 1, MSG_DONTWAIT);
					add_Participant(part_sockets, new_sock);
					add_timeout_client(clients, new_sock);
					numPart++;
				}
			}


			//other socket has data to read
			//non active participants
			for (int i = 0; i < MAX_PLAYERS; i++){
				sd = part_sockets[i];
				if(FD_ISSET(sd, &fdset)){
					//check for closing
					if ((recv(sd,&user_len, 1,MSG_DONTWAIT)) == 0){
						printf("Disconnect non active participant\n");
						part_sockets[i] = 0;
						close(sd);
						numPart--;
					}
					else{ //not disconnected
						recv(sd, buf, user_len,0);
						buf[user_len] = '\0';
						username = strdup(buf);
						printf("Part username is %s\n", username);
						valid_username = parse_username(username);
						int user_taken = match_username(players, username);
						if(user_taken == 1){
							printf("Username taken\n");
							send(sd, &taken, 1, MSG_DONTWAIT);
							reset_timer(clients, sd);
						}
						else if(valid_username == 0){
							send(sd, &invalid, 1, MSG_DONTWAIT);
						}
						else if(valid_username){
							send(sd,&open,1,MSG_DONTWAIT);
							add_active_part(players, sd, username);
							erase_client(clients, sd);
							printf("Active participant added\n");
							FD_CLR(sd, &fdset); 	//clear from set so it does not
							part_sockets[i] = 0;	//get treated as an active observer until
													//next round
							//send to all players
							snprintf(new_user_msg, 27, "User %s has joined\n", username);
							send_to_all(players, new_user_msg);
						}
					}
				}
			}


			//non active observers
			for (int i = 0; i < MAX_PLAYERS; i++){
				sd = obs_sockets[i];
				if(FD_ISSET(sd, &fdset)){
					//check for closing
					if ((recv(sd, &user_len, 1, MSG_DONTWAIT)) == 0){
						printf("Disconnect non active observer\n");
						obs_sockets[i] = 0;
						close(sd);
						numObs--;
					}
					else{ //not disconnected
						recv(sd, buf, user_len, 0);
						buf[user_len] = '\0';
						username = strdup(buf);
						printf("Obs username is %s\n", username);
						valid_username = parse_username(username);
						matching_username = match_username(players, username);
						if(username_taken(players, username)){
							printf("Username taken\n");
							send(sd, &taken, 1, MSG_DONTWAIT);
							reset_timer(clients, sd);
						}
						else if(valid_username == 0){
							send(sd, &invalid, 1, MSG_DONTWAIT);
						}
						else if(matching_username == 0){
							printf("No matching_username\n");
							send(sd, &full, 1, MSG_DONTWAIT);
							obs_sockets[i] = 0;
							close(sd);
							numObs--;
						}
						else if(valid_username){
							send(sd,&open,1,MSG_DONTWAIT);
							add_active_obs(players, sd, username);
							erase_client(clients, sd);
							printf("Active observer added\n");
							obs_sockets[i] = 0;
						}
					}
				}
			}


			//active participants/players
			for (int i = 0; i < MAX_PLAYERS; i++){
				player* temp = players[i];

				if(temp != NULL && temp->part_sd != 0){
					sd = temp->part_sd;
					if(FD_ISSET(sd, &fdset)){
						//check for closing
						if ((recv(sd, &net_recv_msg_len, 2, MSG_WAITALL)) == 0){
							//disconnection
							recv_msg_len = ntohs(net_recv_msg_len);
							disconnect_part(temp, &numObs, &numPart, sd);
							snprintf(user_left_msg, 22, "User %s has left\n", temp->username);
							send_to_all(players, user_left_msg);
							players[i] = NULL;
						}
						//connection not closed
						else{
							recv_msg_len = ntohs(net_recv_msg_len);
							if(recv_msg_len > 1000){
								disconnect_part(temp, &numObs, &numPart, sd);
								players[i] = NULL;
							}
							recv(sd, buf, recv_msg_len, MSG_WAITALL);
							buf[recv_msg_len] = '\0';
							recv_msg = strdup(buf);
							if(buf[0] == '@'){
								create_msg(send_msg, recv_msg, temp->username, 1);
								send_to_user(players, send_msg, recv_msg, temp->obs_sd);
							}
							else{
								create_msg(send_msg, recv_msg, temp->username, 0);
								send_to_all(players, send_msg);
							}
						}
					}
					//handle disconnected "active" observers
					sd = temp->obs_sd;
					if(sd != 0 && FD_ISSET(sd, &fdset)){
						if ((recv(sd, &net_recv_msg_len, 2, MSG_DONTWAIT)) == 0){
							recv_msg_len = ntohs(net_recv_msg_len);
							disconnect_obs(temp, &numObs, sd);
						}
						recv_msg_len = ntohs(net_recv_msg_len);
					}
				}
			}

			printf("numObs: %d\n", numObs);
			printf("numPart: %d\n\n", numPart);
		}
		else{ // no data detected, timeout
			printf("timeout!\n");
		}
	}
	free(buf);
	free(username);
	free(recv_msg);
	free(send_msg);
}

/*send a message to all "active" observers */
void send_to_all(player* players[MAX_PLAYERS], char* send_msg){
	for(int i = 0; i < MAX_PLAYERS; i++){
		player* temp = players[i];
		if(temp != NULL && temp->obs_sd != 0){
			int sd = temp->obs_sd;
			uint16_t send_msg_len = strlen(send_msg);
			uint16_t net_send_msg_len = htons(send_msg_len);
			send(sd,&net_send_msg_len,2,MSG_DONTWAIT);
			send(sd,send_msg,send_msg_len,MSG_DONTWAIT);
		}
	}
}

/*send a message to a single user*/
void send_to_user(player* players[MAX_PLAYERS], char* send_msg, char* recv_msg, int sender_sd){
	char recipient[11];
	char fail_msg[43];
	int recp_found = 0;
	int j = 0;
	uint16_t send_msg_len = strlen(send_msg);
	uint16_t net_send_msg_len = htons(send_msg_len);
	//get recipient from message
	for(j = 0; recv_msg[j+1] != ' ' && j != 9; j++){
		recipient[j] = recv_msg[j+1];
	}
	recipient[j] = '\0';

	//find if valid recipient
	for(int i = 0; i < MAX_PLAYERS; i++){
		player* temp = players[i];
		if(temp != NULL && strcmp(temp->username, recipient) == 0){
			recp_found = 1;
			if(temp->obs_sd != 0){ //recpient observer
				int sd = temp->obs_sd;
				send(sd, &net_send_msg_len, 2, MSG_DONTWAIT);
				send(sd, send_msg, send_msg_len, MSG_DONTWAIT);
			}
			if(sender_sd != 0){ //sender observer
				send(sender_sd, &net_send_msg_len, 2, MSG_DONTWAIT);
				send(sender_sd, send_msg, send_msg_len, MSG_DONTWAIT);
			}
			break;
		}
	}

	//fail case
	if (recp_found == 0){
		snprintf(fail_msg, 43, "Warning: user %s doesn’t exist...\n", recipient);
		if(sender_sd != 0){
			uint16_t fail_msg_len = strlen(fail_msg);
			uint16_t net_fail_msg_len = htons(fail_msg_len);
			send(sender_sd, &net_fail_msg_len, 2, MSG_DONTWAIT);
			send(sender_sd, fail_msg, fail_msg_len, MSG_DONTWAIT);
		}
	}
}

/* Create a message to be send out. Will create private or public message*/
void create_msg(char* send_msg, char* recv_msg, char* username, int private){
	int j = 14;
	int numSpaces = 11 - strlen(username);
	if(private){
		send_msg[0] = '-';
	}
	else{
		send_msg[0] = '>';
	}
	for (int i = 1; i <= numSpaces; i++){
		send_msg[i] = ' ';
	}

	for(int i = numSpaces + 1; i < 12; i++){
		send_msg[i] = username[i - (numSpaces + 1)];
	}
	send_msg[12] = ':';
	send_msg[13] = ' ';

	for(; j < strlen(recv_msg) + 14; j++){
		send_msg[j] = recv_msg[j-14];
	}
	send_msg[j] = '\0';
}

/* Disconnect an active participant */
void disconnect_part(player* temp, int* numObs, int* numPart, int sd){
	if(temp->obs_sd != 0){
		printf("Disconnect participant and affiliated observer\n");
		char notify_obs[] = "Participant disconnected\n";
		uint16_t notify_obs_len = strlen(notify_obs);
		uint16_t net_notify_obs_len = htons(notify_obs_len);
		send(temp->obs_sd, &net_notify_obs_len, 2, MSG_DONTWAIT);
		send(temp->obs_sd, notify_obs, notify_obs_len, MSG_DONTWAIT);
		close(temp->obs_sd);
		*numObs = *numObs - 1;
	}
	else{
		printf("Disconnect participant\n");
	}
	close(sd);
	*numPart = *numPart - 1;
	free(temp);
}

/* Disconnect an "active" observer */
void disconnect_obs(player* temp, int* numObs, int sd){
	if(sd != 0){
		printf("Disconnect just observer\n");
		close(sd);
		temp->obs_sd = 0;
		*numObs = *numObs - 1;
	}
}

/* check if a username is already used. return 1 if true, 0 if false */
int username_taken(player* players[MAX_PLAYERS], char* username){
	for(int i = 0; i < MAX_PLAYERS; i++){
		player* temp = players[i];
		if(temp != NULL && strcmp(temp->username, username) == 0 && temp->obs_sd != 0){
			return 1;
		}
	}
	return 0;
}

/* Temporarily add a socket to the FD_SET for observers not affiliated yet */
void add_temp_obs(int obs_sockets[MAX_PLAYERS], fd_set* fdset, int* max_sd){
	for(int i = 0; i < MAX_PLAYERS; i++){
		int sd = obs_sockets[i];
		if(sd != 0){
			FD_SET(obs_sockets[i], fdset);
		}
		if(sd > *max_sd)
			*max_sd = sd;
	}
}

/* Temporarily add a socket to the FD_SET for participants not affiliated yet */
void add_temp_part(int part_sockets[MAX_PLAYERS], fd_set* fdset, int* max_sd){
	for(int i = 0; i < MAX_PLAYERS; i++){
		int sd = part_sockets[i];
		if(sd != 0){
			FD_SET(part_sockets[i], fdset);
		}
		if(sd > *max_sd)
			*max_sd = sd;
	}
}

/* Add a socket observer to the array for non active observers */
void add_Observer(int obs_sockets[MAX_PLAYERS], int new_sock){
	for(int i = 0; i < MAX_PLAYERS; i++){
		if(obs_sockets[i] == 0){
			obs_sockets[i] = new_sock;
			printf("temp obs sd %d added to array\n", obs_sockets[i]);
			break;
		}
	}

}
/* Add a socket observer to the array for non active participants */
void add_Participant(int part_sockets[MAX_PLAYERS], int new_sock){
	for(int i = 0; i < MAX_PLAYERS; i++){
		if(part_sockets[i] == 0){
			part_sockets[i] = new_sock;
			printf("temp part sd %d added to array\n", part_sockets[i]);
			break;
		}
	}
}

/* Add an active participant to the players array */
void add_active_part(player* players[MAX_PLAYERS], int sd, char* username){
	for(int i = 0; i < MAX_PLAYERS; i++){
		player* temp = players[i];
		if(temp == NULL){
			temp = (player*) malloc(sizeof(player));
			temp->part_sd = sd;
			temp->obs_sd = 0;
			temp->username = username;
			players[i] = temp;
			break;
		}
	}
}

/* Add an active observer to a player. Affliate this socket with a player*/
void add_active_obs(player* players[MAX_PLAYERS], int sd, char* username){
	for(int i = 0; i < MAX_PLAYERS; i++){
		player* temp = players[i];
		if(temp != NULL && (strcmp(temp->username, username) == 0)){
			temp->obs_sd = sd;
			printf("observer now affiliated with user: %s\n", temp->username);
			players[i] = temp;
			break;
		}
	}
}

/* Populate the set with all the active players */
void populate_set(fd_set* fdset, player* players[MAX_PLAYERS], int* max_sd){
	for(int i = 0; i < MAX_PLAYERS; i++){
		player* temp = players[i];
		if(temp != NULL && temp->part_sd != 0){
			FD_SET(temp->part_sd, fdset);
			if(temp->part_sd > *max_sd){
				*max_sd = temp->part_sd;
			}
		}
		if(temp != NULL && temp->obs_sd != 0){
			FD_SET(temp->obs_sd, fdset);
			if(temp->obs_sd > *max_sd){
				*max_sd = temp->obs_sd;
			}
		}
	}
}

/* Find a username that matches the passed in username. If there is a match return 1, else 0*/
int match_username(player* players[MAX_PLAYERS], char* username){
	for(int i = 0; i < MAX_PLAYERS; i++){
		player* temp = players[i];
		if(temp != NULL && temp->username != NULL && (strcmp(temp->username, username) == 0)){
			return 1;
		}
	}
	return 0;
}

/* Parse the username to make sure it is the correct */
int parse_username(char* username){
	int len = strlen(username);
	if(len >= 11)
		return 0;
	if (len == 0)
		return 0;
	for(int i = 0; i < len; i++){
		if((isalnum(username[i]) || username[i] == '_')){
			//do nothing
		}
		else {
			return 0;
		}
	}
	return 1;
}

/* Update the times for each client. Updated after every select call */
void update_times(client* clients[MAX_PLAYERS*2], double diff_t){
	for(int i = 0; i < MAX_PLAYERS*2; i++){
		client* temp = clients[i];
		if(temp != NULL){
			temp->time_left = temp->time_left - diff_t;
		}
	}
}

/* Timeout clients that have expired times */
void timeout_clients(client* clients[MAX_PLAYERS*2], int part_sockets[MAX_PLAYERS], int obs_sockets[MAX_PLAYERS], int* numPart, int* numObs, fd_set* fdset){
	for(int i = 0; i < MAX_PLAYERS*2; i++){
		client* temp = clients[i];
		if(temp != NULL && temp->time_left <= 0){
			remove_temp_client(part_sockets, obs_sockets, temp->sd, numPart, numObs, fdset);
			erase_client(clients, temp->sd);
		}
	}
}

/* Add a client to the client array that can be timed out */
void add_timeout_client(client* clients[MAX_PLAYERS*2], int sd){
	for(int i = 0; i < MAX_PLAYERS*2; i++){
		client* temp = clients[i];
		if(temp == NULL){
			temp = (client*) malloc(sizeof(client));
			temp->sd = sd;
			temp->time_left = TIMEOUT_TIME;
			clients[i] = temp;
			break;
		}
	}
}

/* Reset the timer for a client that tried to use a taken username*/
void reset_timer(client* clients[MAX_PLAYERS*2], int sd){
	for(int i = 0; i < MAX_PLAYERS*2; i++){
		client* temp = clients[i];
		if(temp != NULL && temp->sd == sd){
			printf("client %d time was reset\n", sd);
			temp->time_left = TIMEOUT_TIME;
			return;
		}
	}
}

/* Remove a temporary client from the client list because of a timeout */
void remove_temp_client(int part_sockets[MAX_PLAYERS], int obs_sockets[MAX_PLAYERS], int sd, int* numPart, int* numObs, fd_set* fdset){
	for(int i = 0; i < MAX_PLAYERS; i++){ 	//search obs_sockets
		if(obs_sockets[i] == sd){
			obs_sockets[i] = 0;
			printf("client %d was timed out\n", sd);
			close(sd);
			FD_CLR(sd, fdset);
			*numObs = *numObs - 1;
			return;
		}
	}
	for(int i = 0; i < MAX_PLAYERS; i++){ //search part_sockets
		if(part_sockets[i] == sd){
			part_sockets[i] = 0;
			close(sd);
			printf("client %d was timed out\n", sd);
			FD_CLR(sd, fdset);
			*numPart = *numPart - 1;
			return;
		}
	}
}

/* remove the client from the client array because it should now be in the player array */
void erase_client(client* clients[MAX_PLAYERS*2], int sd){
	for(int i = 0; i < MAX_PLAYERS*2; i++){
		client* temp = clients[i];
		if(temp != NULL && temp->sd == sd){
			free(temp);
			clients[i] = NULL;
			return;
		}
	}
}


///
