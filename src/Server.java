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

        private final Map<String, List<ClientHandler>> privateChatRooms;

        public Server (ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
            this.chatRooms = new HashMap<>();
            this.privateChatRooms = new HashMap<>();
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
                }
            } catch (IOException e) {
                closeServerSocket();
            }
        }

    public synchronized void changeChatRoom(ClientHandler clientHandler, String newChatRoom) {
        List<ClientHandler> currentChatRoom = chatRooms.get(clientHandler.getCurrentChatRoom());
        if (currentChatRoom != null) {
            currentChatRoom.remove(clientHandler);
        } else {
            currentChatRoom = privateChatRooms.get(clientHandler.getCurrentChatRoom());
            if (currentChatRoom != null) {
                currentChatRoom.remove(clientHandler);
            }
        }

        if (newChatRoom.equals("public")) {
            chatRooms.computeIfAbsent(newChatRoom, k -> new ArrayList<>()).add(clientHandler);
        } else {
            privateChatRooms.computeIfAbsent(newChatRoom, k -> new ArrayList<>()).add(clientHandler);
        }
        clientHandler.setCurrentChatRoom(newChatRoom);
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

            public static void main (String[] args) throws IOException {
                ServerSocket serverSocket = new ServerSocket(8080);
                Server server = new Server(serverSocket);
                server.startServer();
            }

        }

