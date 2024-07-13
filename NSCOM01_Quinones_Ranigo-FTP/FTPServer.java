import java.io.*;
import java.net.*;
import java.util.Random;

/**
 * FTPServer class represents a simple FTP server implementation.
 * It supports basic FTP commands for user authentication, directory navigation,
 * file operations, and data transfer.
 */
public class FTPServer {
    private static final int PORT = 21;
    private static final String[] USERS = {"john:1234", "jane:5678", "joe:qwerty"};
    private static boolean userInputted = false;
    private static boolean userAuthenticated = false;
    private static String currentDirectory = "/";

    private static String userSelected;

    /**
     * Main method to start the FTP server.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        boolean serverRunning = true;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("FTP server running on port " + PORT);

            while (serverRunning) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket.getInetAddress());

                handleClient(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles communication with a single FTP client.
     * This method reads commands from the client, processes them, and sends appropriate responses.
     * It supports basic FTP commands such as USER, PASS, PWD, CWD, CDUP, MKD, RMD, PASV, LIST, RETR, DELE, STOR, HELP, TYPE, MODE, STRU, and QUIT.
     * 
     * @param clientSocket The socket representing the client connection.
     */
    private static void handleClient(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            out.println("220 Welcome to NSCOM01 FTP server");

            String request;
            while ((request = in.readLine()) != null) {
                String[] parts = request.split(" ");
                String command = parts[0].toUpperCase();

                switch (command) {
                    case "USER":
                        String username = parts[1];
                        if (isValidUser(username)) {
                            out.println("331 Password required for " + username);
                            userInputted = true;
                        } else {
                            out.println("530 Invalid username");
                        }
                        break;
                    case "PASS":
                        String password = parts[1];
                        if(userInputted){
                            authenticatePassword(password);
                            if (userAuthenticated) {
                                out.println("230 User logged in, proceed");
                            } else {
                                out.println("530 Login incorrect");
                            }
                        } else {
                            out.println("503 Login with USER first");
                        }
                        break;

                    case "PWD":
                        out.println("257 \"" + currentDirectory + "\" is the current directory");
                        break;
                    case "CWD":
                        String newDirectory = parts[1];
                        if (changeDirectory(newDirectory)) {
                            out.println("250 Directory changed to " + currentDirectory);
                        } else {
                            out.println("550 Directory not found");
                        }
                        break;
                    case "CDUP":
                        if (changeToParentDirectory()) {
                            out.println("250 Directory changed to " + currentDirectory);
                        } else {
                            out.println("550 Already at root directory");
                        }
                        break;
                    case "MKD":
                        String dirToCreate = parts[1];
                        if (createDirectory(dirToCreate)) {
                            out.println("257 \"" + dirToCreate + "\" directory created");
                        } else {
                            out.println("550 Directory creation failed");
                        }
                        break;
                    case "RMD":
                        String dirToRemove = parts[1];
                        if (removeDirectory(dirToRemove)) {
                            out.println("250 \"" + dirToRemove + "\" directory removed");
                        } else {
                            out.println("550 Directory removal failed");
                        }
                        break;
                    case "PASV":
                        int port = generateDataPort();
                        if (port != -1) {
                            String ipAddress = clientSocket.getInetAddress().getHostAddress().replace(".", ",");
                            out.println("227 Entering Passive Mode (" + ipAddress + "," + (port / 256) + "," + (port % 256) + ")");
                            ServerSocket serverSocket = new ServerSocket(port);
                            Socket dataSocket = serverSocket.accept();
                        } else {
                            out.println("425 Can't open data connection");
                        }
                        break;
                    case "LIST":
                        out.println("150 Here comes the directory listing.");
                        sendDirectoryListing(out);
                        out.println("226 Directory send OK.");
                        break;
                    case "RETR":
                        String retrieveFileName = parts[1];
                        sendFile(retrieveFileName, clientSocket.getOutputStream());
                        break;
                    case "DELE":
                        String deleteFileName = parts[1];
                        if (deleteFile(deleteFileName)) {
                            out.println("250 File deleted successfully.");
                        } else {
                            out.println("550 File deletion failed.");
                        }
                        break;
                    case "STOR":
                        String storeFileName = parts[1];
                        storeFile(storeFileName, clientSocket.getInputStream());
                        break;
                    case "HELP":
                        sendHelpMessage(out);
                        break;
                    case "TYPE":
                        setTransferType(parts[1], out);
                        break;
                    case "MODE":
                        setTransferMode(parts[1], out);
                        break;
                    case "STRU":
                        setFileStructure(parts[1], out);
                        break;
                    case "QUIT":
                        out.println("221 Goodbye");
                        break;
                    default:
                        out.println("502 Command not implemented");
                        break;
                }
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the provided username is valid by comparing it against a list of authorized users.
     *
     * @param username The username to validate.
     * @return True if the username is valid and authentication succeeds, otherwise false.
     */
    private static boolean isValidUser(String username) {
        int i = 0;
        for (String user : USERS) {
            if (ExtractNthColon(user, 1).equals(username)) {
                userSelected = USERS[i];
                return true;
            }
        }
        return false;
    }

     /**
     * Checks if the provided password is valid by comparing it against a list of authorized users.
     *
     * @param password The username to validate.
     * @return True if the username is valid and authentication succeeds, otherwise false.
     */
    private static void authenticatePassword(String password) {
        if(ExtractNthColon(userSelected, 2).equals(password)){
            userAuthenticated = true;
        }
    }

            /**
     * Method : Separates a following input string into multiple words with the colon as a separator
     * @param input : input line of client
     * @param nth : the nth word of the client input string
     * @return : separated words from the input string
     */
    public static String ExtractNthColon(String input, int nth) {
        // Split words from input line with space as the separator
        String[] words = input.split(":");
        // Return the nth word 
        return words[nth - 1];
    }

    /**
     * Changes the current working directory to the specified directory.
     *
     * @param newDirectory The path of the directory to change to.
     * @return True if the directory exists and is a valid directory, false otherwise.
     */
    private static boolean changeDirectory(String newDirectory) {
        File directory = new File(newDirectory);

        if (directory.exists() && directory.isDirectory()) {
            currentDirectory = newDirectory;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Changes the current working directory to its parent directory.
     *
     * @return True if the parent directory exists and the current working directory is successfully changed to it, false otherwise.
     */
    private static boolean changeToParentDirectory() {
        File currentDir = new File(currentDirectory);

        File parentDir = currentDir.getParentFile();

        if (parentDir != null && parentDir.isDirectory()) {
            currentDirectory = parentDir.getAbsolutePath();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Creates a new directory with the specified name in the current working directory.
     *
     * @param directoryName The name of the directory to create.
     * @return True if the directory is successfully created, false otherwise.
     */
    private static boolean createDirectory(String directoryName) {
        File newDir = new File(currentDirectory + File.separator + directoryName);

        return newDir.mkdir();
    }

    /**
     * Removes the directory with the specified name from the current working directory.
     *
     * @param directoryName The name of the directory to remove.
     * @return True if the directory is successfully removed, false otherwise.
     */
    private static boolean removeDirectory(String directoryName) {
        File dirToRemove = new File(currentDirectory + File.separator + directoryName);

        if (dirToRemove.isDirectory() && dirToRemove.exists()) {
            return dirToRemove.delete();
        } else {
            return false;
        }
    }

    /**
     * Generates a random port number within the range 1025 to 65535 (inclusive).
     *
     * @return A randomly generated port number.
     */
    private static int generateDataPort() {
        Random random = new Random();
        int minPort = 1025;
        int maxPort = 65535;
        return random.nextInt(maxPort - minPort + 1) + minPort;
    }

    /**
     * Sends the list of files and directories in the current working directory to the client.
     *
     * @param out The PrintWriter to write the directory listing to.
     */
    private static void sendDirectoryListing(PrintWriter out) {
        File directory = new File(currentDirectory);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                out.println(file.getName());
            }
        } else {
            out.println("550 Directory listing failed.");
        }
    }

    /**
     * Sends the content of the specified file to the client through the provided OutputStream.
     *
     * @param fileName The name of the file to be sent.
     * @param out      The OutputStream to write the file content to.
     */
    private static void sendFile(String fileName, OutputStream out) {
        try {
            File file = new File("./" + fileName);
            if (file.exists() && file.isFile()) {
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);
                byte[] buffer = new byte[1024];
                int bytesRead;
                out.write(("150 Opening data connection for " + fileName + "\r\n").getBytes());
                while ((bytesRead = bis.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                bis.close();
                out.write(("226 Transfer complete.\r\n").getBytes());
            } else {
                out.write(("550 File not found.\r\n").getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Deletes the file with the specified name from the current working directory.
     *
     * @param fileName The name of the file to delete.
     * @return True if the file is successfully deleted, false otherwise.
     */
    private static boolean deleteFile(String fileName) {
        File file = new File(currentDirectory + "/" + fileName);
        return file.delete();
    }

    /**
     * Stores the content received from the provided InputStream into a file with the specified name in the current working directory.
     *
     * @param fileName The name of the file to store.
     * @param in       The InputStream from which to read the file content.
     */
    private static void storeFile(String fileName, InputStream in) {
        try {
            File file = new File(currentDirectory + "/" + fileName);
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }    

    /**
     * Sends a help message listing the recognized FTP commands to the client through the provided PrintWriter.
     *
     * @param out The PrintWriter to write the help message to.
     */
    private static void sendHelpMessage(PrintWriter out) {
        out.println("214-The following commands are recognized:\r\n" +
                " USER PASS PWD CWD CDUP MKD RMD PASV LIST RETR DELE STOR HELP TYPE MODE STRU QUIT\r\n" +
                "214 Help OK.");
    }

    /**
     * Sets the transfer type for the FTP data connection and sends a response to the client through the provided PrintWriter.
     *
     * @param type The transfer type to be set (e.g., "A" for ASCII, "I" for binary).
     * @param out  The PrintWriter to send the response to the client.
     */
    private static void setTransferType(String type, PrintWriter out) {
        if (type.equals("A") || type.equals("I")) {
            out.println("200 Type set to " + type);
        } else {
            out.println("500 Invalid type");
        }
    }

    /**
     * Sets the transfer mode for the FTP data connection and sends a response to the client through the provided PrintWriter.
     *
     * @param mode The transfer mode to be set (e.g., "S" for Stream, "B" for Block, "C" for Compressed).
     * @param out  The PrintWriter to send the response to the client.
     */
    private static void setTransferMode(String mode, PrintWriter out) {
        if (mode.equals("S") || mode.equals("B") || mode.equals("C")) {
            out.println("200 Mode set to " + mode);
        } else {
            out.println("500 Invalid mode");
        }
    }

    /**
     * Sets the file structure for the FTP data connection and sends a response to the client through the provided PrintWriter.
     *
     * @param structure The file structure to be set (e.g., "F" for File, "R" for Record, "P" for Page).
     * @param out       The PrintWriter to send the response to the client.
     */
    private static void setFileStructure(String structure, PrintWriter out) {
        if (structure.equals("F") || structure.equals("R") || structure.equals("P")) {
            out.println("200 Structure set to " + structure);
        } else {
            out.println("504 Command not implemented for that parameter");
        }
    }

}
