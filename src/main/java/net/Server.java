package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


public class Server {
    final int PORT = 3456;
    ArrayList<ServerThread> connections;

    public Server() throws IOException {
        System.out.println("Starting listening on port " + PORT);
        connections = new ArrayList<>();
        go();
        sc = new Scanner(System.in);
    }

    private Scanner sc;

    public void go() throws IOException {
        ServerSocket s1 = new ServerSocket(PORT);
        while (true) {
            Socket client1 = s1.accept();
            System.out.println(client1.getInetAddress());
            BufferedReader is1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
            PrintWriter os1 = new PrintWriter(client1.getOutputStream(), true);
            os1.print(0);
            os1.flush();
            System.out.println("first is ready");
            Socket client2 = s1.accept();
            System.out.println(client2.getInetAddress());
            BufferedReader is2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
            PrintWriter os2 = new PrintWriter(client2.getOutputStream(), true);
            os2.print(1);
            os2.flush();
            System.out.println("second is ready");
            os1.println(0);
//            os2.println(0);
            os1.flush();
//            os2.flush();
            System.out.println("ready");
            ServerThread game = new ServerThread(os1, os2, is1, is2);
            connections.add(game);
            game.start();
        }
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }
}
