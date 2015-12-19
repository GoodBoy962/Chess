package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static int t = 0;

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("0 or 1");
        int you = sc.nextInt();
        int port = 3456;
        String host = "localhost";
        Socket s = new Socket(host, port);
        BufferedReader is = new BufferedReader(new InputStreamReader(s.getInputStream()));
        PrintWriter os = new PrintWriter(s.getOutputStream(), true);
//        InputStream is = s.getInputStream();
//        OutputStream os = s.getOutputStream();
        while (true) {
//            System.out.println("here");
            if (t == you) {
                os.write(sc.nextInt());
                os.flush();
                t ^= 1;
            }
            if (t != you) {
                t ^= 1;
                System.out.println(is.read());
            }
        }
    }
}
