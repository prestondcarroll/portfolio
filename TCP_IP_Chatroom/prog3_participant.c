/* prog3_participant.c - code for participant*/

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <ctype.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>

int parse_username(char* username);
int contains_whitespace(char* message);
/*------------------------------------------------------------------------
* Program: prog3_participant.c
*
* Purpose: a participant client sends messages to the sever where the message
*		   is sent to all the observers or the single user if a private message
* Syntax: ./demo_client server_address participant_port
*
* server_address - name of a computer on which server is executing
* participant_port - protocol port number server is using
*
*------------------------------------------------------------------------
*/
int main( int argc, char **argv) {
	struct hostent *ptrh; /* pointer to a host table entry */
	struct protoent *ptrp; /* pointer to a protocol table entry */
	struct sockaddr_in sad; /* structure to hold an IP address */
	int sd; /* socket descriptor */
	int port; /* protocol port number */
	char *host; /* pointer to host name */
	int n; /* number of characters read */
	char buf[1000] = {0}; /* buffer for data from the server */
	char* username = malloc(sizeof(char) * 1001);
	char full; /* place to hold full character */
	char* message = malloc(sizeof(char) * 11);
	uint8_t user_len = 0;
	uint16_t msg_len = 0;
	uint16_t net_msg_len = 0; /* network byte order*/
	int valid_username = 0;
	int valid_message = 0;


	memset((char *)&sad,0,sizeof(sad)); /* clear sockaddr structure */
	sad.sin_family = AF_INET; /* set family to Internet */

	if( argc != 3 ) {
		fprintf(stderr,"Error: Wrong number of arguments\n");
		fprintf(stderr,"usage:\n");
		fprintf(stderr,"./client server_address server_port\n");
		exit(EXIT_FAILURE);
	}

	port = atoi(argv[2]); /* convert to binary */
	if (port > 0) /* test for legal value */
	sad.sin_port = htons((u_short)port);
	else {
		fprintf(stderr,"Error: bad port number %s\n",argv[2]);
		exit(EXIT_FAILURE);
	}

	host = argv[1]; /* if host argument specified */

	/* Convert host name to equivalent IP address and copy to sad. */
	ptrh = gethostbyname(host);
	if ( ptrh == NULL ) {
		fprintf(stderr,"Error: Invalid host: %s\n", host);
		exit(EXIT_FAILURE);
	}

	memcpy(&sad.sin_addr, ptrh->h_addr, ptrh->h_length);

	/* Map TCP transport protocol name to protocol number. */
	if ( ((long int)(ptrp = getprotobyname("tcp"))) == 0) {
		fprintf(stderr, "Error: Cannot map \"tcp\" to protocol number");
		exit(EXIT_FAILURE);
	}

	/* Create a socket. */
	sd = socket(PF_INET, SOCK_STREAM, ptrp->p_proto);
	if (sd < 0) {
		fprintf(stderr, "Error: Socket creation failed\n");
		exit(EXIT_FAILURE);
	}

	/* Connect the socket to the specified server. */
	if (connect(sd, (struct sockaddr *)&sad, sizeof(sad)) < 0) {
		fprintf(stderr,"connect failed\n");
		exit(EXIT_FAILURE);
	}


	n = recv(sd, &full, 1, MSG_WAITALL);
	if(full == 'N'){
		printf("Server is full. Now exiting.");
		close(sd);
		exit(EXIT_FAILURE);
	}
	char ch;

	do{
		valid_username = 0;
		while(!valid_username){
			printf("\nPlease enter a username 1-10 characters long: ");
			fgets(username, 12, stdin);
			if(strlen(username) == 11 && username[10] != '\n'){
				while (((ch = getchar()) != EOF) && (ch != '\n')){
					//eat up characters that are after the first 10
				}
			}
			else{
				username[strlen(username) - 1] = '\0';
			}
			valid_username = parse_username(username);
		}

		//send username and check for timeout
		user_len = strlen(username);
		n = send(sd,&user_len,1,MSG_NOSIGNAL);
		if(n == -1){
			printf("Timeout expired! Now exiting\n");
			close(sd);
			exit(EXIT_FAILURE);
		}
		n = send(sd,username,user_len, MSG_NOSIGNAL);
		if(n == -1){
			printf("Timeout expired! Now exiting\n");
			close(sd);
			exit(EXIT_FAILURE);
		}

		//receive answer
		n = recv(sd,buf,1,MSG_WAITALL);
		if(buf[0] == 'T'){
			printf("username already taken\n");
		}
		if(buf[0] == 'I'){
			printf("An invalid username was received\n");
		}
	}while(buf[0] != 'Y');


	printf("You are now connected\n");
	while (1) {
		valid_message = 0;
		while(!valid_message){
			printf("Enter message: ");
			fgets(message, 1001, stdin);
			if(strlen(message) == 1000 && message[999] != '\n'){
				while (((ch = getchar()) != EOF) && (ch != '\n')){
					//eat up characters after the 1000 character limit
				}
			}
			else{
				username[strlen(message) - 1] = '\0';
			}
			valid_message = contains_whitespace(message);
			if(valid_message == 0)
				printf("message must contain whitespace character\n");
		}

		msg_len = strlen(message);
		net_msg_len = htons(msg_len);
		send(sd,&net_msg_len,2,0);
		send(sd,message,msg_len,0);
	}

	close(sd);
	exit(EXIT_SUCCESS);
}

/* parse the username so and return if it is valid */
int parse_username(char* username){
	int len = strlen(username);
	if(len == 11)
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

/* make sure message contains whitespace */
int contains_whitespace(char* message){
	for(int i = 0; message[i] != '\0'; i++){
		if(isspace(message[i])){
			return 1;
		}
	}
	return 0;
}























//
