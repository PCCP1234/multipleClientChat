import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

        private final ServerSocket serverSocket;
        private final Map<String, List<ClientHandler>> chatRooms;

        public Server (ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
            this.chatRooms = new HashMap<>();
            chatRooms.put("public", new ArrayList<>());
        }

        public void startServer() {
            try {

                while (!serverSocket.isClosed()) {
                    Socket socket = serverSocket.accept();
                    System.out.println("A new client has connected");
                    ClientHandler clientHandler = new ClientHandler(socket, this);

                    Thread thread = new Thread(clientHandler);
                    thread.start();
                    printChatRooms();
                }
            } catch (IOException e) {
                closeServerSocket();
            }
        }

        public synchronized void addChatRoom(String roomName) {
            if (!chatRooms.containsKey(roomName)) {
                chatRooms.put(roomName, new ArrayList<>());
                System.out.println("Nova sala criada: " + roomName);
            } else {
                System.out.println("A sala " + roomName + " já existe.");
            }
        }
        

    public synchronized void changeChatRoom(ClientHandler clientHandler, String newChatRoom) {
        
        if (!chatRooms.containsKey(newChatRoom)) {
            chatRooms.put(newChatRoom, new ArrayList<>());
        }
    
        List<ClientHandler> currentRoom = chatRooms.get(clientHandler.getCurrentChatRoom());
        currentRoom.remove(clientHandler);
    
        List<ClientHandler> newRoom = chatRooms.get(newChatRoom);
        newRoom.add(clientHandler);
    
        clientHandler.setCurrentChatRoom(newChatRoom);
        }

    public void addClienToChatRoom(ClientHandler clientHandler, String chatRoom){
        chatRooms.computeIfAbsent(chatRoom, k -> new ArrayList<>()).add(clientHandler);
        
    }

    public void removeClientFromChatRoom(ClientHandler clientHandler, String chatRoom){
        List<ClientHandler> handlers = chatRooms.get(chatRoom);
        if(handlers != null){
            handlers.remove(clientHandler);
        }
    }

    public Map<String, List<ClientHandler>> getChatRooms() {
        return chatRooms;
    }

            public void closeServerSocket(){
                try{
                    if(serverSocket != null){
                        serverSocket.close();
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

            public void printChatRooms() {
                for (Map.Entry<String, List<ClientHandler>> entry : chatRooms.entrySet()) {
                    String roomName = entry.getKey();
                    List<ClientHandler> clients = entry.getValue();

                    
                    
                    System.out.println("Sala: " + roomName);
                    System.out.println("Membros:");
                    for (ClientHandler client : clients) {
                        System.out.println("- " + client.getClientUsername());
                    }
                    System.out.println();
                }
            }
            

            public static void main (String[] args) throws IOException {
                ServerSocket serverSocket = new ServerSocket(8080);
                Server server = new Server(serverSocket);
                server.startServer();
            }

        }

