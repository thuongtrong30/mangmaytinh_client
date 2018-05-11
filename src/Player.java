import javafx.geometry.Pos;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/*
* o : out
* w : win
* l : lose
* d : drawer
* m[text]: message
* g[X Y]: move
* n[name]:
* c : yeu cau danh voi may
* */

public class Player extends Thread {

    static class Position {
        Position(int x, int y){
            this.x = x;
            this.y = y;
        }
        public int x, y;
    }

    private String mName;
    boolean vsP;
    //    private Socket mSocket;
    private int mRemain; // seconds // maximum = 60
    private ArrayList<Position> moves = new ArrayList<>();
    static int MAX_WIDTH = 10;

    private DataInputStream mInput;
    private DataOutputStream mOutput;

    public Player(DataInputStream input, DataOutputStream output) {
        mInput = input;
        mOutput = output;
        mName = "NO NAME";
        vsP = true;
    }

    private enum ComProcessRespond {
        MOVED,
        DRAWER,
        LOST,
        MOVED_AND_WON
    };



    public int movesCount(){
        return this.moves.size();
    }

    private long comState;

    public void sendMessage(String message) {

        try {
            mOutput.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static private int extend(boolean[][] matrix, int y, int x, int dx, int dy){
        int tx = x, ty = y, ans = 0;
	    while(tx + dx < MAX_WIDTH && ty +dy < MAX_WIDTH && tx + dx >= 0 && ty + dy >= 0 && matrix[ty +dy][tx + dx]){
	        ty += dy;
	        tx += dx;
	        ans ++;
	    }
	    return ans;
    }

    public static boolean end(ArrayList<Position> moves, int x, int y){
        boolean[][] matrix = new boolean[MAX_WIDTH][MAX_WIDTH];
        int[] extended = new int[8];
        boolean ans = false;

        for(Position m : moves)
            matrix[m.x][m.y] = true;
        for(int i = 0; i< MAX_WIDTH; i ++){
            for (int j = 0; j < MAX_WIDTH; j ++){
                   System.out.print(matrix[i][j]?"1 ":"0 ");
               }
            System.out.println("");
        }
        System.out.println("");
        System.out.println("");
               

        // 0    0     // 6     4
        // 1.5  1     // 7.5   5
        // 3    2     // 9     6
        // 4.5  3     // 10.5  7

        int[] ex = { 0,  1, 1, 1, 0, -1, -1, -1};
        int[] ey = {-1, -1, 0, 1, 1,  1,  0, -1};
        System.out.println("from " + x + " " + y);
        for (int i = 0; i < 8; i ++){
            extended[i] = extend(matrix, x, y, ex[i], ey[i]);
            
            System.out.print(extended[i] + " ");
        }
        System.out.println("");


        for (int i = 0; i < 4; i ++)
            ans = ans || (extended[i] + extended[i + 4] == 4);

        return ans;
    }
//gx y

    public void run() {
        try {
            while (true) {
                String input = mInput.readUTF();
                if (!vsP){
                    ComProcessRespond ans = this.comProcess(input);
                    continue;
                }

                if (input.charAt(0) == 'c'){
                    vsP = false;
                    Random r = new Random();
//                    sState += r.nextInt(2) + 1;
                    this.comState = r.nextInt(2); // 1 : ngư�?i | 0 : máy
//
                    if (this.comState == 0){// Máy đánh trước
                        this.comProcess(null);
                    }else{
                        // Ngư�?i đánh trước
                    }
                }
                if (input.length() < 2) continue;

                if (input.charAt(0) == 'n'){
                    this.mName = input.substring(1);
                    for (Player p:players)
                        if(p != this){
                            p.sendMessage(input);
                            this.sendMessage("n" + p.mName);
                        }

                }


                if (input.charAt(0) == 'g') { // player went  -> cấu trúc [gX Y]

                    if (sState < 2) continue;

                    if (players.get(sState - 2) != this) continue;


                    String[] move = input.substring(1).split(" ");
                    int x = Integer.parseInt(move[0]);
                    int y = Integer.parseInt(move[1]);
                    boolean checked = false;
                    for (Player p:players)
                        for(Position m: p.moves)
                            if (m.x == x && m.y == y) checked = true;
                    if (checked) continue;



                    int current = sState - 2;
                    players.get(sState - 2).sendMessage("r" + x + " " + y);
                    sState = ((sState - 2) + 1) % 2 + 2; // 2 -> 3 // 3 -> 2

                    players.get(sState - 2).sendMessage(input);


                    if (end(this.moves, x, y)) {
                        players.get(current).sendMessage("w");
                        players.get((current + 1) % 2).sendMessage("l");

                        for (Player p: players)
                            p.moves.clear();

                    }else {
                        int count = 0;
                        for (Player p:players) {
                            count += p.movesCount();
                        }

                        if (count == 99)
                            for (Player p:players)
                                p.sendMessage("d");

                        this.moves.add(new Position(x, y));




                    }




                } else if (input.charAt(0) == 'm') { // player send message -> cấu trúc [mMessage typed by user]
                    String message = input.substring(1);
                    for (Player p:players) {
                        p.sendMessage(input + "__" + mName);
                    }
                }

            }
        } catch (IOException e) {
            if (sState == 1) sState = 0;
            else sState = 1;
            players.remove(this);

            for (Player p: players) {
                p.moves.clear();
                p.sendMessage("o");
            }


        }

    }

    private synchronized ComProcessRespond comProcess(String input) {
        if (input == null) {
            //máy đánh trước
            //random first

            return ComProcessRespond.MOVED;
        }
        if (input.charAt(0) == 'c'){
            // end vs Com mode;

        }

        return ComProcessRespond.MOVED;
    }


    public static int sState;

    // | ready | count / turn
    /*
     *  0 -> 0 client
     *  1 -> 1 client
     *  2 -> 2 client -> turn of 1
     *  3 -> 2 client -> turn of 2
     * */
    public static ArrayList<Player> players = new ArrayList<>();


    public static void main(String[] args) throws IOException {





        int portNumber = 9000;
        ServerSocket ss = new ServerSocket(portNumber);



        while (true) {
            Socket client = ss.accept(); // Ch�? Client Connect
            DataOutputStream output = new DataOutputStream(client.getOutputStream());

            int ans = sState < 2 ? 1 : 0; // nếu đủ 2 client rồi thì ngắt kết nối (trả v�? 0)
            output.write(ans);

            if (ans == 1) { // nếu chưa đủ 2 client
                if (sState == 0) sState ++;
                else {Random r = new Random();
                    sState += r.nextInt(2) + 1;

                }


                Player player = new Player(new DataInputStream(client.getInputStream()), output);
                players.add(player);
                player.start();

            }else{

            }
        }
    }
}

// "g 0 0"
// "m fhkjshfkjashdfjhg"