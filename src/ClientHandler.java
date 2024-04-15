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
            sendAvailableChatRooms();
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
        while (socket.isConnected()) {
            messageFromClient = bufferedReader.readLine();
            messageFromClient = messageFromClient.substring(clientUsername.length() + 1).trim();

            if (messageFromClient == null) {
                break;
            }

            // Verificar se a mensagem começa com o nome de usuário seguido de ":"
            if (messageFromClient.startsWith("/join")) {
                // Remover o nome de usuário e espaços em branco antes do comando

                // Separar o comando e os argumentos
                String[] parts = messageFromClient.split(" ", 2);
                String command = parts[0];
                String argument = parts.length > 1 ? parts[1] : "";

                // Processar o comando
                switch (command) {
                    case "/join":
                        if (!argument.isEmpty()) {
                            String newChatRoom = argument;
                            server.addChatRoom(newChatRoom);
                            changeChatRoom(newChatRoom);
                            System.out.println("Sala adicionada: " + newChatRoom);
                        } else {
                            System.out.println("Comando '/join' inválido. Use '/join <nome_da_sala>'.");
                        }
                        break;
                    default:
                        System.out.println("Comando não reconhecido: " + command);
                }
            } else {
                // Se a mensagem não começar com o nome de usuário, é tratada como uma mensagem de bate-papo regular
            }
        }
        broadcastMessage(messageFromClient);
    } catch (IOException e) {
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

public void sendAvailableChatRooms() {
    try {
        StringBuilder message = new StringBuilder("Available Chat Rooms:\n");
        for (String roomName : server.getChatRooms().keySet()) {
            message.append(roomName).append("\n");
        }
        bufferedWriter.write(message.toString());
        bufferedWriter.flush();
    } catch (IOException e) {
        e.printStackTrace();
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
