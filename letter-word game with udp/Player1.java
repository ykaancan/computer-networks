import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;

public class Player1 {
    static int m;
    public static void main(String args[]) throws Exception {
        Scanner sc = new Scanner(System.in);
        ArrayList<String> listofWords = new ArrayList<>();
        InetAddress IP = InetAddress.getByName("localhost");
        DatagramSocket s = new DatagramSocket();
        System.out.println("Welcome Player1 to make a game offer please wait" );
        DatagramPacket sendPacket;
        byte[] sendoffer = new byte[1024];
        byte[] receiveoffer = new byte[1024];
        String offerMessage = "offer";
        sendoffer = offerMessage.getBytes();
        DatagramPacket offerPacket = new DatagramPacket(sendoffer, sendoffer.length,IP,5566);
        s.send(offerPacket);
        DatagramPacket receiveOfferPacket = new DatagramPacket(receiveoffer,receiveoffer.length);
        s.receive(receiveOfferPacket);
        String message2 = new String(receiveOfferPacket.getData());
        String receiveMessage = message2.trim();
        if(!receiveMessage.equals("player2 accept your offer")){
            s.close();
            System.exit(0);
        }
        System.out.println(receiveMessage);
        System.out.println("You have 10 seconds to write a word, Please enter to 'basla' to play");

        while (true) {
            byte[] sendBuffer = new byte[50];
            byte[] receiveBuffer = new byte[50];
            System.out.println("Player1! your turn: ");
            long start = System.currentTimeMillis();
            int k = listofWords.size();
            String control = "baba";
            if(k!=0){
                control = listofWords.get(k-1);}
            m = control.length();
            do{
                String str = sc.nextLine();
                sendBuffer = str.getBytes();
                sendPacket = new DatagramPacket(sendBuffer,sendBuffer.length,IP,5566 );
                if(listofWords.contains(str)){
                    System.out.println("Try another word");
                }
                else if((str.charAt(1)!= control.charAt(m-1))||(str.charAt(0)!= control.charAt(m-2))){
                    System.out.println("Try another word");
                }
                else{
                    listofWords.add(str);
                    long end = System.currentTimeMillis();
                    if(end-start>10000){
                        String win = "you win!";
                        sendBuffer = win.getBytes();
                        sendPacket = new DatagramPacket(sendBuffer,sendBuffer.length,IP,5566 );
                        System.out.println("time out! You lose");
                        s.send(sendPacket);
                        s.close();
                        System.exit(0);
                    }
                }
            } while (listofWords.size()==k);
            s.send(sendPacket);
            byte[] echos = new byte[50];
            String mEcho;
            do {DatagramPacket EchoReqPacket = new DatagramPacket(echos,echos.length);
            s.receive(EchoReqPacket);
            String messageEcho = new String(EchoReqPacket.getData());
            mEcho = messageEcho.trim();
            System.out.println(mEcho);
            EchoReqPacket.setData(receiveBuffer);}
            while(!mEcho.equals("your word received to player2"));


            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer,receiveBuffer.length);
            s.receive(receivePacket);
            String message = new String(receivePacket.getData());
            String str1 = message.trim();
            listofWords.add(str1);
            InetAddress IP2 = receivePacket.getAddress();
            int portNo2 = receivePacket.getPort();
            if (listofWords.contains(str1)){
                byte[] echo = new byte[50];
                String echoString = "your word received to player1";
                echo = echoString.getBytes();
                DatagramPacket echoPacket = new DatagramPacket(echo, echo.length,IP2, portNo2);
                s.send(echoPacket);
            }
            if (str1.equals("you win!")){
                System.out.println("Other player can not write a word within 30 seconds \nYou win");
                s.close();
                System.exit(0);
            }

            System.out.println("Player2: " + str1);

        }

    }
}
