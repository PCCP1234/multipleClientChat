import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    String currentChatRoom;
    private Server server;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;


    public ClientHandler(Socket socket, Server server){
        try{
            this.socket = socket;
            this.server = server;
            this.currentChatRoom = "public";
            server.getChatRooms().computeIfAbsent("public", k -> new ArrayList<>()).add(this);
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter (socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + clientUsername + " has entered chat " + currentChatRoom + "!");
        } catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void changeChatRoom(String newChatRoom) {
        server.changeChatRoom(this, newChatRoom);
    }


    @Override
    public void run(){
        String messageFromClient;

        while(socket.isConnected()){
            try {
                messageFromClient = bufferedReader.readLine();

                if (messageFromClient.startsWith("/join")) {
                    try {
                        String newChatRoom = messageFromClient.split(" ", 2)[1];
                        System.out.println(newChatRoom);
                        changeChatRoom(newChatRoom);
                    }catch (ArrayIndexOutOfBoundsException e){
                        e.printStackTrace();
                    }
                } else {
                    broadcastMessage(messageFromClient);
                }

            } catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }

        server.getChatRooms().get(currentChatRoom).remove(this);
        server.getChatRooms().computeIfAbsent(currentChatRoom, k -> new ArrayList<>()).add(this);
    }

    public void broadcastMessage(String messageToSend){
        for (ClientHandler clientHandler : clientHandlers){
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler () {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();

        }
        if (bufferedWriter != null) {
            bufferedWriter.close();
        }
        if (socket != null) {
         socket.close();
        }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public String getCurrentChatRoom() {
        return currentChatRoom;
    }

    public void setCurrentChatRoom(String currentChatRoom) {
        this.currentChatRoom = currentChatRoom;
    }
}
