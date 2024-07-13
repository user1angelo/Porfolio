# Server program by S11 Group 4
# Azevedo, Marquus Joss R.
# Quinones, Angelo Y.
# Tumalad, Shawne Michael Q.

import socket
import time
import json
import os

BUFFER_SIZE = 1024  # Size of the message buffer

# Processes commands received from clients
def processClients(entry):
    message = json.loads(entry.decode())
    command = message['command'] 
    
     # Join server
    if command == "join":
        clients.update({address : None})
        print(f"Client {address} has connected")
        jsonData = {'command': 'all', 'message': "A user has connected"}
        jsonData2 = {'command': 'error', 'message': "Error: Connection incomplete. Try joining again"}
        for client_address in clients:
            if client_address != address:
                server_socket.sendto(json.dumps(jsonData).encode(), client_address)
            else:
                server_socket.sendto(json.dumps(jsonData2).encode(), client_address)
    
    # Leave server
    elif command == "leave":
        print(f"Client {clients[address]}:{address} disconnected")
        if clients[address] == None:
            message = "Unregistered user disconnected"
        else:
            message = f"User {clients[address]} disconnected"
        jsonData = {'command': 'all', 'message': message}
        for client_address in clients:
            if client_address != address:
                server_socket.sendto(json.dumps(jsonData).encode(), client_address)
        clients.pop(address)
    
    # Register handle
    elif command == "register":
        handle = message['handle']
        
        if clients[address] != None:
            print(f"{address} ({clients[address]}) Attempted registration")
            jsonData = {'command': 'error', 'message': "Error: Registration failed. You already have a username."}
        elif handle in clients.values():
            print(f"{address} handle registration failed")
            jsonData = {'command': 'error', 'message': "Error: Registration failed. Handle or alias already exists."}
        else:
            clients[address] = handle
            print(f"Username {handle} registered by {address}")
            jsonData = {'command': 'success', 'given' : 'register' , 'message': f"Welcome {handle}!"}
            jsonData2 = {'command': 'all', 'message': f"A user registered as {handle}"}
            for client_address in clients:
                if client_address != address:
                    server_socket.sendto(json.dumps(jsonData2).encode(), client_address)
        server_socket.sendto(json.dumps(jsonData).encode(), address)
    
    # Send message to all
    elif command == "all":
        if clients[address] == None:
            print(f"{address} Attempted /all without username")
            jsonData = {'command': 'error', 'message': "Error: all chat failed. You don't have a handle yet."}
            server_socket.sendto(json.dumps(jsonData).encode(), address)
            return
        message = f"{clients[address]}: {message['message']}"
        print(f"{address} > {message}")
        message_data = {'command': 'all', 'message': message}
        for client_address, client_handle in clients.items():
            if client_handle != None:
                server_socket.sendto(json.dumps(message_data).encode(), client_address)
    
    # Send direct message
    elif command == "msg":
        handle = message['handle']
        message = message['message']
        sender = clients[address]
        
        if clients[address] == None:
            print(f"{address} Attempted /msg without username")
            jsonData = {'command': 'error', 'message': "Error: message failed. You don't have a handle yet."}
            server_socket.sendto(json.dumps(jsonData).encode(), address)
            return
        elif clients[address] == handle:
            print(f"{address} Attempted /msg to self")
            jsonData = {'command': 'error', 'message': "Error: message failed. You can't message yourself."}
            server_socket.sendto(json.dumps(jsonData).encode(), address)
            return
        
        for client_address, client_handle in clients.items():
            if client_handle == handle:
                message_data = {'command': 'msg', 'handle': sender, 'message': message}
                try:
                    server_socket.sendto(json.dumps(message_data).encode(), client_address)
                    print(f"Message from {address} to {client_address}")
                    message = f"[To {handle}]: {message}"
                    jsonData = {'command': 'success', 'given' : 'msg' , 'message': message}
                except:
                    server_socket.sendto(json.dumps(jsonData).encode(), address)
                    jsonData = {'command': 'error' , 'message': "Error: Handle or alias not found"}
                server_socket.sendto(json.dumps(jsonData).encode(), address)  
                return
        print(f"Direct message by {address} to handle {handle} failed")
        jsonData = {'command': 'error' , 'message': "Error: Handle or alias not found"}
        server_socket.sendto(json.dumps(jsonData).encode(), address)

# Pings the connected clients for validation when a connection error occurs
def ping():
    for user in clients:
        print(f"Pinging user {user} : ", end="")
        ping_req = {'command': 'ping'}
        server_socket.sendto(json.dumps(ping_req).encode(), user) #ping if still connected
        time.sleep(0.3)
        try:
            server_socket.recvfrom(BUFFER_SIZE)
            print("ONLINE") # prints when connected
        except:
            print("OFFLINE")
            disconnected_clients.append(user)
        
clients = {} # Client address and username list {address: username}
disconnected_clients = [] # Address list of abruptly disconnected clients

server_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)  # UDP Socket 

# Loops until a successful server is started
while True:
    print("Chat Box UDP Server")
    try:
        ip = input("Enter IP: ")
        port = input("Enter Port: ")
        server_socket.bind((ip, int(port))) # Bind the socket to a specific IP address and port
        print(f"Server running at {ip}:{port}")
        break
    except Exception as e:
        os.system('cls')
        print(f"Error: {str(e)}\nTry again\n")

# Processes incoming data per loop
while True:
    try: 
        entry, address = server_socket.recvfrom(BUFFER_SIZE)
        processClients(entry)
    
    except ConnectionResetError as e: # Abrupt disconnects
        print(f"ConnectionResetError: {e}")  
        ping()   
            
    except Exception as e: # Other errors
        print(f"Error: {e}")
    
    finally: # Always runs per loop
        for user in disconnected_clients:
            print(f"User {clients[user]} : {user} offline. Disconnecting")
            clients.pop(user)
        disconnected_clients.clear()
    
