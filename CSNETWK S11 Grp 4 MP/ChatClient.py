# Client program by S11 Group 4
# Azevedo, Marquus Joss R.
# Quinones, Angelo Y.
# Tumalad, Shawne Michael Q.

import socket
import threading
import json
import time

BUFFER_SIZE = 1024 # Size of the message buffer
isConnected = False # Connection status
server_address = None 

# Processes commands sent to the server
def toServer(entry):
    global isConnected
    global server_address
    
    if not entry.startswith('/'):
        print("Error: That is not a command! Type /? for help.")
        return
    
    input_list = entry.split()
    command = input_list[0]
    params = input_list[1:]
      
    if command == "/join":
        if not isConnected:
            if len(params) != 2:
                print("Invalid command syntax.\nUsage: /join <server_ip_add> <port>")
            else:
                try:
                    server_address = (params[0], int(params[1]))
                
                    # Send the "join" command to the server
                    client_socket.sendto(json.dumps({"command": "join"}).encode(), server_address)
                    time.sleep(0.1)
                    client_socket.settimeout(3)
                    client_socket.recvfrom(BUFFER_SIZE)
                    print("Connection to the Message Board Server is successful!")
                    client_socket.settimeout(None)
                    isConnected = True
                except Exception as e:
                    print("Error: Connection to the Message Board Server has failed! Please check IP Address and Port Number.")
                    print(f"More detail: {str(e)}")
                    server_address = None
                    return    
        else:
            print('Error. Please leave to the server first.')
      
    elif command == "/leave":
        if isConnected:
            if len(params) > 0:
                print("Error: There should be no parameters for leave.\nUsage: /leave")
            else:
                client_socket.sendto(json.dumps({"command":"leave"}).encode(), server_address)
                print("Connection closed. Thank you!")
                isConnected = False
                server_address = None
        else:
            print('Error: Disconnection failed. Please connect to the server first.')
    elif command == "/register":
        if isConnected:
            if len(params) != 1:
                print("Error: Command parameters do not match or is not allowed.\nUsage: /register <handle>")
            else:
                client_socket.sendto(json.dumps({"command":"register", "handle":params[0]}).encode(), server_address)
        else:
            print('Error. Please connect to the server first.')
    elif command == "/all":
        if isConnected:
            if len(params) == 0:
                print("Error: Command parameters do not match or is not allowed.\nUsage: /all <message>")
            else:
                message = ' '.join(params)
                client_socket.sendto(json.dumps({"command":"all", "message":message}).encode(), server_address)
        else:
            print('Error. Please connect to the server first.')
    elif command == "/msg":
        if isConnected:
            if len(params) < 2:
                print("Error: Command parameters do not match or is not allowed.\nUsage: /msg <handle> <message>")
            else:
                handle = params[0]
                message = ' '.join(params[1:])
                client_socket.sendto(json.dumps({"command":"msg", "handle":handle, "message":message}).encode(), server_address)
        else:
            print('Error. Please connect to the server first.')
    elif command == "/?":
        print("Connect to the server application:        /join <server_ip_add> <port>")
        print("Disconnect to the server application:     /leave")
        print("Register a unique handle or alias:        /register <handle>")
        print("Send a message to all:                    /all <message>")
        print("Send a direct message to a single handle: /msg <handle> <message>")
    else:
        print("Command not found. Type /? for help.")

# Processes commands received from the server
def fromServer(data):
    command = data['command'] 
    
    # Acknowledge ping from server
    if command == 'ping':
        ping_ack = {'command': 'ping'}
        client_socket.sendto(json.dumps(ping_ack).encode(), server_address)
        return
    
    # Receive a global or error message 
    elif command == "all" or command == "error":
        message = data['message']
        
    # Receive a direct message
    elif command == "msg":
        handle = data['handle']
        message = f"[From {handle}]: {data['message']}"
    
    # Return success message
    elif command == "success":
        message = data['message']
    
    print(f">\n{message}\n> ", end="")      

# Function thats being run in a thread
def receive_messages():
    global isConnected
    
    while True:
        if isConnected:
            try:
                message = client_socket.recvfrom(BUFFER_SIZE)
                data = json.loads(message[0].decode())
                fromServer(data)
            except ConnectionResetError:
                print("Error: Connection to the Message Board Server lost! Disconnecting.")
                isConnected = False
            except Exception as e:
                print(f"Error: {str(e)}")

# Setup client UDP socket
client_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        
# Start a thread to continuously receive and print messages from the server
chat_thread = threading.Thread(target=receive_messages)
chat_thread.start()

print("Chat Box Client\nEnter a command. Type /? for help")

while True:
    # Get a command from the user
    entry = input("> ")
    toServer(entry)