import java.io.*;
import java.net.*;

/**
 * FTPClient class sends requests to the FTPServer through different commands.
 * THe user inputs commands that would be read by the server and send the responses back othe client.
 * The commands are mainsly derived from the specs.
 */
public class FTPClient {
    private static String SERVER_ADDRESS;
    private static String PORT;
    private static String dir = "/";
    
    /**
     * Main method to start the FTP client.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            // Validate command line arguments to connect to the ip and port
            if (args.length != 2) {
                System.out.println("Usage: java FTPClient <ip> <port>");
                return;
            }
            // Assign both arguments to SERVER_ADDRESS and PORT
            SERVER_ADDRESS = args[0];
            PORT = args[1];

            int SERVER_PORT = Integer.parseInt(PORT);

            //Initiates sockets and input related objects
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Connected to FTP server.");

            // Reading initial welcome message from server
            String response = reader.readLine();
            System.out.println("Server: " + response);

            String command;
            // Loop to read user input and send commands to server
            while (true) {
                System.out.print("Command: ");
                command = userInput.readLine();

                // Validate user input
                if (!isValidCommand(command)) {
                    System.out.println("Invalid command. Please try again.");
                    continue; //Resets the loop to ask for another command
                }

                // Sending command to server
                writer.println(command);

                // Reading response from server
                response = reader.readLine();
                System.out.println("Server: " + response);

                // Additional handling for commands that require extra responses
                if (command.startsWith("HELP")){
                    while (!(response = reader.readLine()).equals("214 Help OK.")) {
                        System.out.println("Server: " + response); //Prints the content from the server until condition is met
                    }
                    System.out.println("Server: " + response);
                } else if (command.startsWith("USER")) {
                    if (response.startsWith("331")) { 
                        
                    }
                } else if (command.startsWith("PASS")) {
                    if (response.startsWith("230")) { // If login is successful
                        writer.println("TYPE I"); // Set binary transfer mode
                        writer.println("MODE S"); // Set stream transfer mode
                        writer.println("STRU F"); // Set file structure

                        response = reader.readLine();
                        System.out.println("Server: " + response);
                        response = reader.readLine();
                        System.out.println("Server: " + response);
                        response = reader.readLine();
                        System.out.println("Server: " + response);
                    }
                } else if (command.startsWith("PASV")) {
                    if (response.startsWith("227")) { // Check if PASV response
                        // Parse IP address and port number from response
                        String[] parts = response.split("\\(")[1].split("\\)")[0].split(",");
                        String ipAddress = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
                        System.out.println(ipAddress);
                        int port = Integer.parseInt(parts[4]) * 256 + Integer.parseInt(parts[5]);
                        System.out.println(port);
                        // Establish data connection
                        Socket dataSocket = new Socket(ipAddress, port);

                        System.out.print("Command: ");
                        command = userInput.readLine();
                        writer.println(command);
                        response = reader.readLine();
                        System.out.println("Server: " + response);
                        // Handling commands that require data connection
                        if (command.startsWith("RETR")) {
                            String fileName = ExtractNthWord(command, 2);
                            System.out.println("test1");
                            handleRETRCommand(response, dataSocket, fileName);
                        }else if (command.startsWith("STOR")) {
                            String fileName = ExtractNthWord(command, 2);
                            handleSTORCommand(response, dataSocket, fileName);
                        }else{
                            dataSocket.close();
                        }
                    }
                } else if (command.startsWith("LIST")) {
                    // Additional responses for LIST command
                    while (!(response = reader.readLine()).equals("226 Directory send OK.")) {
                        System.out.println("Server: " + response); //Prints all items from the directory
                    }
                    System.out.println("Server: " + response); //Final response
                } else if (command.startsWith("CWD")||command.startsWith("CDUP")){
                    if(response.startsWith("250")){
                        dir = ExtractNthWord(response, 5); //Extracts the changed directory name
                    }
                }

                // Break the loop if QUIT command is entered
                if (command.equalsIgnoreCase("QUIT")) {
                    break;
                }
            }

            socket.close();
            System.out.println("Disconnected from FTP server.");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }



    /**
     * Method : Checks if the given command is a valid FTP command
     * @param command : client command input
     * @return : true if the command is valid, false otherwise
     */
    private static boolean isValidCommand(String command) {
        command = ExtractNthWord(command, 1);
        String keyword = command.toUpperCase();

        // All possible FTP Commands
        String[] validCommands = {"USER", "PASS", "PWD", "CWD", "CDUP", "MKD", "RMD",
                                "PASV", "LIST", "RETR", "DELE", "STOR", "HELP", 
                                "TYPE", "MODE", "STRU", "QUIT"};
        //A loop too check if the command matches one word in the validCommands array
        for (String validCommand : validCommands) {
            if (keyword.equals(validCommand)) {
                return true;
            }
        }

        // If not a valid command
        return false;
    }


    /**
     * Method : Handles the RETR command by writing the file data to the data socket
     * @param response : response from the server containing the status code
     * @param dataSocket : socket created from the PASV command
     * @param fileName : name of the file to be stored
     */
    private static void handleRETRCommand(String response, Socket dataSocket, String fileName) {
        try {
            if (response.startsWith("1") || response.startsWith("2")) {
                System.out.println("test2");
                if (ExtractNthWord(response, 1).equals("150")) {
                    System.out.println("test3");
                    InputStream dataInputStream = dataSocket.getInputStream();
        
                    // Create a FileOutputStream to write the received file
                    FileOutputStream fileOutputStream = new FileOutputStream(dir + File.separator + fileName);
                    System.out.println("test4");
                    // Read data from the data connection and write it to the file
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = dataInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
        
                    // Close all streams and sockets
                    fileOutputStream.close();
                    dataInputStream.close();
                    dataSocket.close();
                    System.out.println("File downloaded successfully.");
                } else {
                    System.out.println("Error: Failed to open data connection.");
                }
            } else {
                System.out.println("Error: Unable to retrieve file. Server response: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method : Handles the STOR command by writing the file data to the data socket
     * @param response : response from the server containing the status code
     * @param dataSocket : socket created from the PASV command
     * @param fileName : name of the file to be stored
     */
    private static void handleSTORCommand(String response, Socket dataSocket, String fileName) {
        try {
            if (response.startsWith("1") || response.startsWith("2")) { 
                if (ExtractNthWord(response, 1).equals("150")) {
                    OutputStream dataOutputStream = dataSocket.getOutputStream();
        
                    // Create a FileInputStream to read the file to be stored
                    FileInputStream fileInputStream = new FileInputStream(dir + File.separator + fileName);
        
                    // Read data from the file and write it to the data connection
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        dataOutputStream.write(buffer, 0, bytesRead);
                    }
        
                    // Close streams and sockets
                    fileInputStream.close();
                    dataOutputStream.close();
                    dataSocket.close();
                    System.out.println("File uploaded successfully.");
                } else {
                    System.out.println("Error: Failed to open data connection.");
                }
            } else {
                System.out.println("Error: Unable to store file. Server response: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        /**
     * Method : Separates a following input string into multiple words with the space as a separator
     * @param input : input line of client
     * @param nth : the nth word of the client input string
     * @return : separated words from the input string
     */
    public static String ExtractNthWord(String input, int nth) {
        // Split words from input line with space as the separator
        String[] words = input.split(" ");
        // Return the nth word 
        return words[nth - 1];
    }
}


// public class FTPClient {
//     private static final String SERVER_ADDRESS = "127.0.0.1"; // Replace with your server address
//     private static final int SERVER_PORT = 21;
//     private static final int DATA_PORT = 5000; // Arbitrary port for data transfer

//     public static void main(String[] args) {
//         try {
//             Socket controlSocket = new Socket(SERVER_ADDRESS, SERVER_PORT);
//             BufferedReader controlReader = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
//             PrintWriter controlWriter = new PrintWriter(controlSocket.getOutputStream(), true);

//             // Read welcome message from server
//             String response = controlReader.readLine();
//             System.out.println(response);

//             // Send username
//             controlWriter.println("USER john"); // Replace with your username
//             response = controlReader.readLine();
//             System.out.println(response);

//             // Send password
//             controlWriter.println("PASS 1234"); // Replace with your password
//             response = controlReader.readLine();
//             System.out.println(response);

//             // Request PASV mode
//             controlWriter.println("PASV");
//             response = controlReader.readLine();
//             System.out.println(response);

//             // Extracting IP address and port from PASV response
//             String[] parts = response.split("\\(")[1].split("\\)")[0].split(",");
//             String ipAddress = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
//             int port = Integer.parseInt(parts[4]) * 256 + Integer.parseInt(parts[5]);

//             // Establish data connection
//             Socket dataSocket = new Socket(ipAddress, port);

//             // Send LIST command to retrieve directory listing
//             PrintWriter dataWriter = new PrintWriter(dataSocket.getOutputStream(), true);
//             dataWriter.println("LIST");

//             // Read and print directory listing from server
//             BufferedReader dataReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
//             String line;
//             while ((line = dataReader.readLine()) != null) {
//                 System.out.println(line);
//             }

//             // Close data connection
//             dataSocket.close();

//             // Send QUIT command to terminate the session
//             controlWriter.println("QUIT");

//             // Close control connection
//             controlSocket.close();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }
// }


// import java.io.*;
// import java.net.*;

// public class FTPClient {
//     private static final String SERVER_ADDRESS = "127.0.0.1";
//     private static final int SERVER_PORT = 2121;
//     private static final String[] USERS = {"john:1234", "jane:5678", "joe:qwerty"};
//     private static final String CRLF = "\r\n";

//     private static Socket controlSocket;
//     private static BufferedReader controlReader;
//     private static PrintWriter controlWriter;

//     public static void main(String[] args) {
//         connectToServer();
//         login("john", "1234");

//         // Set transfer type, mode, and structure
//         sendCommand("TYPE I");
//         sendCommand("MODE S");
//         sendCommand("STRU F");

//         // Example: List files
//         listFiles();

//         // Example: Download a file
//         downloadFile("example.txt");

//         // Example: Upload a file
//         uploadFile("example_upload.txt");

//         // Close control connection
//         sendCommand("QUIT");
//         closeConnection();
//     }

//     private static void connectToServer() {
//         try {
//             controlSocket = new Socket(SERVER_ADDRESS, SERVER_PORT);
//             controlReader = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
//             controlWriter = new PrintWriter(controlSocket.getOutputStream(), true);

//             System.out.println("Connected to FTP server");
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     private static void closeConnection() {
//         try {
//             controlReader.close();
//             controlWriter.close();
//             controlSocket.close();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     private static void sendCommand(String command) {
//         try {
//             controlWriter.println(command);
//             String response = controlReader.readLine();
//             System.out.println("Response: " + response);
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     private static void login(String username, String password) {
//         // Send USER command
//         sendCommand("USER " + username);

//         // Send PASS command
//         sendCommand("PASS " + password);
//     }

//     private static void listFiles() {
//         // Send PASV command
//         sendCommand("PASV");

//         // Receive response for PASV
//         String response = null;
//         try {
//             response = controlReader.readLine();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }

//         // Parse response to extract IP address and port
//         String[] parts = response.split("\\(|,|\\)");
//         String ipAddress = parts[1] + "." + parts[2] + "." + parts[3] + "." + parts[4];
//         int port = Integer.parseInt(parts[5]) * 256 + Integer.parseInt(parts[6]);

//         // Connect to data port
//         try (Socket dataSocket = new Socket(ipAddress, port);
//              BufferedReader dataReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()))) {
//             String line;
//             while ((line = dataReader.readLine()) != null) {
//                 System.out.println(line);
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     private static void downloadFile(String fileName) {
//         // Send PASV command
//         sendCommand("PASV");

//         // Receive response for PASV
//         String response = null;
//         try {
//             response = controlReader.readLine();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }

//         // Parse response to extract IP address and port
//         String[] parts = response.split("\\(|,|\\)");
//         String ipAddress = parts[1] + "." + parts[2] + "." + parts[3] + "." + parts[4];
//         int port = Integer.parseInt(parts[5]) * 256 + Integer.parseInt(parts[6]);

//         // Connect to data port and receive file
//         try (Socket dataSocket = new Socket(ipAddress, port);
//              InputStream inputStream = dataSocket.getInputStream();
//              FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
//             byte[] buffer = new byte[1024];
//             int bytesRead;
//             while ((bytesRead = inputStream.read(buffer)) != -1) {
//                 fileOutputStream.write(buffer, 0, bytesRead);
//             }
//             System.out.println("File downloaded successfully.");
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     private static void uploadFile(String fileName) {
//         // Send PASV command
//         sendCommand("PASV");

//         // Receive response for PASV
//         String response = null;
//         try {
//             response = controlReader.readLine();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }

//         // Parse response to extract IP address and port
//         String[] parts = response.split("\\(|,|\\)");
//         String ipAddress = parts[1] + "." + parts[2] + "." + parts[3] + "." + parts[4];
//         int port = Integer.parseInt(parts[5]) * 256 + Integer.parseInt(parts[6]);

//         // Connect to data port and upload file
//         try (Socket dataSocket = new Socket(ipAddress, port);
//              OutputStream outputStream = dataSocket.getOutputStream();
//              FileInputStream fileInputStream = new FileInputStream(fileName)) {
//             byte[] buffer = new byte[1024];
//             int bytesRead;
//             while ((bytesRead = fileInputStream.read(buffer)) != -1) {
//                 outputStream.write(buffer, 0, bytesRead);
//             }
//             System.out.println("File uploaded successfully.");
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }
// }