# 💬 Multiple Client Chat 💬

## Introduction

This project is a simple multiple client chat application built using Java. It allows multiple clients to connect to a server and communicate with each other via text messages.

## Features

- **Chat Rooms**: Clients can join different chat rooms to communicate with other users in the same room.
- **Real-Time Messaging**: Messages are sent and received in real-time, providing a seamless chat experience.
- **Simple Interface**: The client application provides a simple command-line interface for interacting with the server.

## Prerequisites

Before running the application, ensure you have the following installed:

- Java Development Kit (JDK) 8 or later
- A text editor or an Integrated Development Environment (IDE) such as IntelliJ IDEA or Eclipse

## How to Run

### Server

1. Compile the server code:

    ```bash
    javac -d bin src/org/codeForAll/iorns/webServer/*.java
    ```

2. Run the server:

    ```bash
    java -cp bin org.codeForAll.iorns.webServer.Server
    ```

### Client

1. Compile the client code:

    ```bash
    javac -d bin src/org/codeForAll/iorns/webClient/*.java
    ```

2. Run the client:

    ```bash
    java -cp bin org.codeForAll.iorns.webClient.Client
    ```

## 🎮 How to Use

1. Start the server by following the instructions above.
2. Launch multiple instances of the client application.
3. Enter a username when prompted.
5. Start chatting with other users in the same room.

## ⌨️ Contributing

If you'd like to contribute to this project, feel free to submit a pull request. Bug reports and feature requests are also welcome.

## ✉️ Contact

For any questions or concerns, feel free to contact me via email: pmlcosta15@gmail.com.
