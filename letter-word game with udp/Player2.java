import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;

public class Player2 {
    public static void main(String args[]) throws Exception {
        String str, message;
        ArrayList<String > listofWords = new ArrayList<>();
        DatagramPacket sendPacket;
        DatagramSocket serverSocket = new DatagramSocket(5566);
        Scanner sc = new Scanner(System.in);
        System.out.println("Wait for offer other player, \nyou have 10 seconds to response after received a word");
        byte[] receiveOffer = new byte[1024];
        byte[] sendRequest = new byte[1024];
        DatagramPacket offerPacket = new DatagramPacket(receiveOffer, receiveOffer.length);
        serverSocket.receive(offerPacket);
        String offerMessage = new String(offerPacket.getData());
        String trimOfferMessage = offerMessage.trim();
        InetAddress IP2 = offerPacket.getAddress();
        int portNo2 = offerPacket.getPort();
        if (trimOfferMessage.equals("offer")){
            Scanner in = new Scanner(System.in);
            System.out.println("Player1 wants to play a game with you, \nEnter 0 for accept");
            if (in.nextInt()==0){
                System.out.println("Game will start soon, please wait for Player1's word");
                System.out.println("");
                String handshake = "player2 accept your offer";
                sendRequest = handshake.getBytes();
                DatagramPacket sendRequestPacket = new DatagramPacket(sendRequest, sendRequest.length,IP2, portNo2);
                serverSocket.send(sendRequestPacket);
            }
        }
        while (true) {
            byte[] receiveBuffer = new byte[50];
            byte[] sendBuffer = new byte[50];
            DatagramPacket recvdpkt = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            serverSocket.receive(recvdpkt);
            InetAddress IP = recvdpkt.getAddress();
            int portNo = recvdpkt.getPort();
            String strcon = new String(recvdpkt.getData());
            do {
                message = new String(recvdpkt.getData());
                str=  message.trim();
                listofWords.add(str);
                if (listofWords.contains(str)){
                    byte[] echo = new byte[50];
                    String echoString = "your word received to player2";
                    echo = echoString.getBytes();
                    DatagramPacket echoPacket = new DatagramPacket(echo, echo.length,IP2, portNo2);
                    serverSocket.send(echoPacket);
                }

                if (str.equals("you win!")){
                    System.out.println("Other player can not write a word within 10 seconds You win");
                    serverSocket.close();
                    System.exit(0);
                }
                long start = System.currentTimeMillis();
                System.out.println("Player1 : "+str);
                System.out.println("Player2! your turn: ");

                int k = listofWords.size();
                String control = listofWords.get(listofWords.size()-1);
                int m = control.length();
                do{
                    String str1 = sc.nextLine();
                    sendBuffer = str1.getBytes();
                    sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length,IP, portNo);
                    if(listofWords.contains(str1)){
                        System.out.println("Try another word");
                    }
                    else if((str1.charAt(1)!= control.charAt(m-1))||(str1.charAt(0)!= control.charAt(m-2))){
                        System.out.println("Try another word");
                    }
                    else
                        listofWords.add(str1);
                } while (listofWords.size()==k);
                long end = System.currentTimeMillis();
                if(end-start>10000){
                    String win = "you win!";
                    System.out.println("time out! You lose");
                    sendBuffer = win.getBytes();
                    sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length,IP, portNo);
                    serverSocket.send(sendPacket);
                    serverSocket.close();
                    System.exit(0);
                }
                serverSocket.send(sendPacket);
                byte[] echos = new byte[50];
                String mEcho;
                do {DatagramPacket EchoReqPacket = new DatagramPacket(echos,echos.length);
                    serverSocket.receive(EchoReqPacket);
                    String messageEcho = new String(EchoReqPacket.getData());
                    mEcho = messageEcho.trim();
                    System.out.println(mEcho);
                    EchoReqPacket.setData(receiveBuffer);}
                while(!mEcho.equals("your word received to player1"));
            }while (!message.equals(strcon));

        }
    }
}
