
package caro;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            String name = JOptionPane.showInputDialog(null, "Nhập tên!!"); 
             new ClientCaro(); 
              sB = new Socket("127.0.0.1",9000);  
            BufferedReader nhan = new BufferedReader(
                  new InputStreamReader(sB.getInputStream())); 
            
            int a = nhan.read();
            if(a == 1){
                System.out.println("Vào phòng!");
                DataOutputStream output = new DataOutputStream(sB.getOutputStream());
                DataInputStream mInput = new DataInputStream(sB.getInputStream());
                BanCo bc = new BanCo(output);
                bc.setVisible(true);
                bc.setTitle(name);
                for(int i = 0 ; i < 10 ; i++){
                    for(int j = 0; j <10 ; j++){
                        bc.truyen(i,j,Color.white);
                    }
                }
                output.writeUTF("n"+name);
                th = new Thread(){
                    public void run(){
                        try {
                            while(true){
                                String input = mInput.readUTF();
                                if(input.charAt(0)== 'g'){
                                    String[] go = input.substring(1).split(" ");
                                    int hang = Integer.parseInt(go[0]);
                                    int cot = Integer.parseInt(go[1]);
                                    bc.truyen(hang,cot,Color.green);
                      
                                    
                                }
                                else if(input.charAt(0)=='r'){
                                    String[] go = input.substring(1).split(" ");
                                    int hang = Integer.parseInt(go[0]);
                                    int cot = Integer.parseInt(go[1]);
                                    bc.truyen(hang,cot,Color.red);
                                }
                                if(input.charAt(0)=='w'){
                                    JOptionPane.showMessageDialog(null,"Thắng");
                                    for(int i = 0 ; i < 10 ; i++){
                                        for(int j = 0; j <10 ; j++){
                                            bc.truyen(i,j,Color.white);
                                        }
                                    }
                                }
                                if(input.charAt(0)=='l'){
                                    JOptionPane.showMessageDialog(null,"Thua");
                                    for(int i = 0 ; i < 10 ; i++){
                                        for(int j = 0; j <10 ; j++){
                                            bc.truyen(i,j,Color.white);
                                        }
                                    }
                                }
                                if(input.charAt(0)=='o'){
                                    JOptionPane.showMessageDialog(null, "Đối thủ đã out");
                                    for(int i = 0 ; i < 10 ; i++){
                                        for(int j = 0; j <10 ; j++){
                                            bc.truyen(i,j,Color.white);
                                        }
                                    }
                                    bc.choi(true);
                                }
                                if(input.charAt(0)=='d'){
                                    JOptionPane.showMessageDialog(null, "Hòa!Ván mới!!");
                                    for(int i = 0 ; i < 10 ; i++){
                                        for(int j = 0; j <10 ; j++){
                                            bc.truyen(i,j,Color.white);
                                        }
                                    }
                                }
                                if(input.charAt(0)=='m'){
                                    String s = input.substring(1);
                                    bc.addtext(s);
                                }
                                if(input.charAt(0)=='n'){
                                    bc.setTitle(name+" vs "+input.substring(1));
                                    bc.choi(false);
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

