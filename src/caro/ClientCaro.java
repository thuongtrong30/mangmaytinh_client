
package caro;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientCaro extends JFrame implements ActionListener{

    static JTextArea content; 
    static JButton send; 
    static JTextField nhap, toName; 
    static String chuoi=""; 
    static String temp="",addrr=""; 
    static Socket s, sB;  
    static PrintWriter gui; 
    static Thread th;
    static DataInputStream input;
    public static void main(String[] args) {
                
          try
          {
            //String ip = JOptionPane.showInputDialog(null, "Nhập IP máy chủ"); 
             new ClientCaro(); 
              sB = new Socket("192.168.3.109",9000);  
            BufferedReader nhan = new BufferedReader(
                  new InputStreamReader(sB.getInputStream())); 
            
            int a = nhan.read();
            if(a == 1){
                System.out.println("Vào phòng!");
                DataOutputStream output = new DataOutputStream(sB.getOutputStream());
                DataInputStream mInput = new DataInputStream(sB.getInputStream());
                BanCo bc = new BanCo(output);
                bc.setVisible(true);
                th = new Thread(){
                    public void run(){
                        try {
                            while(true){
                                String input = mInput.readUTF();
                                if(input.charAt(0)== 'g'){
                                    String[] go = input.substring(1).split(" ");
                                    int hang = Integer.parseInt(go[0]);
                                    int cot = Integer.parseInt(go[1]);
                                    bc.truyen(hang,cot,"O");
                      
                                    
                                }
                                else if(input.charAt(0)=='r'){
                                    String[] go = input.substring(1).split(" ");
                                    int hang = Integer.parseInt(go[0]);
                                    int cot = Integer.parseInt(go[1]);
                                    bc.truyen(hang,cot,"X");
                                }
                                if(input.charAt(0)=='w'){
                                    System.out.println("Winner");
                                    System.exit(0);
                                }
                                if(input.charAt(0)=='l'){
                                    System.out.println("Loser");
                                    System.exit(0);
                                }
                                if(input.charAt(0)=='o'){
                                    for(int i = 0 ; i < 3 ; i++){
                                        for(int j = 0; j <3 ; j++){
                                            bc.truyen(i,j,"");
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                };
                th.start();
            }
            else {
                System.out.println("Phòng đầy!");
            }
            
            
           
          }
           catch (Exception e) { // Xử lý ngoại lệ
             e.printStackTrace();                  
          }      
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

