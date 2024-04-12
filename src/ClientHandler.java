import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable{

    public static List<ClientHandler> clientHandlers = new ArrayList<>();
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
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter (socket.getOutputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            server.addClienToChatRoom(this, currentChatRoom);
            broadcastMessage("SERVER: " + clientUsername + " has entered chat " + currentChatRoom + "!");
        } catch (IOException e){
            closeEverything();
        }
    }

    public void changeChatRoom(String newChatRoom) {
        server.changeChatRoom(this, newChatRoom);
    }


    @Override
    public void run(){
        String messageFromClient;

        try {
        while(socket.isConnected()){
                messageFromClient = bufferedReader.readLine();

                if(messageFromClient == null){
                    break;
                }

                if (messageFromClient.startsWith("/join")) {
                    try{
                        String newChatRoom = messageFromClient.substring(6);                       System.out.println(newChatRoom);
                    
                        if(!server.getChatRooms().containsKey(newChatRoom)){
                            server.getChatRooms().put(newChatRoom, new ArrayList<>());
                        }
                        changeChatRoom(newChatRoom);
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                } else {
                    broadcastMessage(messageFromClient);
                }
            }

            } catch (IOException e){
                closeEverything();
            }
        }


    public void broadcastMessage(String messageToSend){
        for (ClientHandler clientHandler : server.getChatRooms().get(currentChatRoom)){
            if (!clientHandler.clientUsername.equals(clientUsername)){
            try {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                
            }catch (IOException e){
               e.printStackTrace();
            }
        }
    }
}

    public void closeEverything() {
        clientHandlers.remove(this);
        server.removeClientFromChatRoom(this, currentChatRoom);

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

    public String getClientUsername(){
        return clientUsername;
    }
}
