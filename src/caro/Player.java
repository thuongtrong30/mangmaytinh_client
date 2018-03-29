/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caro;

import javafx.util.Pair;

import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Player extends Thread{

    private String mName;
    //    private Socket mSocket;
    private int mRemain; // seconds // maximum = 60
    private ArrayList<Integer> goes;

    private DataInputStream mInput;
    private DataOutputStream mOutput;

    public Player(DataInputStream input, DataOutputStream output){
        mInput = input;
        mOutput = output;
    }

    public void sendMessage(String message){
        try {
            mOutput.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){

        while(true){
            try {
                String input = mInput.readUTF();
                if (input.length() < 2) continue;
                if (input.charAt(0) == 'g'){ // player went  -> cấu trúc [gX Y]
                    if (sState < 2) continue;
                    if (players.get(sState - 2) != this) continue;

                    String[] go = input.substring(1).split(" ");
                    int x = Integer.parseInt(go[0]);
                    int y = Integer.parseInt(go[1]);

                    sState = ((sState - 2) + 1) % 2 + 2; // 2 -> 3 // 3 -> 2

                    players.get(sState - 2).sendMessage(input);

                    // FIND THE WINNER HERE

                    List<Integer> row = this.goes.stream()
                            .filter((position)-> position / 10 == x)
                            .collect(Collectors.toList()); // same row
                    System.out.println("Same row : " + row.size());

                    List<Integer> col = this.goes.stream()
                            .filter((position)-> position % 10 == y)
                            .collect(Collectors.toList()); // same col
                    System.out.println("Same col : " + col.size());

                    List<Integer> dia_1 = this.goes.stream()
                            .filter((position)-> position % 10 - y == position / 10 - x)
                            .collect(Collectors.toList()); // same dia 1
                    System.out.println("Same dia 1 : " + dia_1.size());

                    List<Integer> dia_2 = this.goes.stream()
                            .filter((position)-> position % 10 - y == x - position / 10)
                            .collect(Collectors.toList()); // same col
                    System.out.println("Same dia 2 : " + dia_2.size()); // dia 2

                    // YOU WILL WIN IF 1 SIZE == 2cls

                    this.goes.add(x * 10 + y);

                }else if (input.charAt(0) == 'm'){ // player send message -> cấu trúc [mMessage typed by user]
                    String message = input.substring(1);
                    players.get(((sState - 2) + 1) % 2).sendMessage(input);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public static int sState;

    // | ready | count / turn
    /*
    *  0 -> 0 client
    *  1 -> 1 client
    *  2 -> 2 client -> turn of 1
    *  3 -> 2 client -> turn of 2
    * */
    public static ArrayList<Player> players;


    public static void main(String[] args) throws IOException {
        int portNumber = 9000;
        ServerSocket ss = new ServerSocket(portNumber);



        while (true){
            Socket client = ss.accept(); // Chờ Client Connect
            DataOutputStream output =  new DataOutputStream(client.getOutputStream());
            int ans = sState < 2 ? 1 : 0; // nếu đủ 2 client rồi thì ngắt kết nối (trả về 0)
            output.write(ans);

            if (ans == 1){ // nếu chưa đủ 2 client
                  Player player = new Player(new DataInputStream(client.getInputStream()), output);
                  players.add(player);
                  player.start();
            }
        }
    }
}

