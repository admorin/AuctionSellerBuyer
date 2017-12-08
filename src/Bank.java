
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by alexschmidt-gonzales on 11/19/17.
 */
public class Bank extends Thread {


    private static HashMap<Integer, Account> bankMap = new HashMap<>();
    private static LinkedList<Account> accounts = new LinkedList<>();
    private Socket bankSocket;
    private final int MAX_ACCOUNTS = 10000;
    private static HashMap<Integer, Account> listOfAccountNums = new HashMap<>();
    private Account account;


    public static ArrayList<Bank> threads = new ArrayList<>();

    private Socket auctionSocket;
    public static final int PORT_NUMBER = 8080;
    public static final int AUCTION_CENTRAL_PORT = 8081;
    public String clientName;

    public Message forCentral;

    private volatile boolean KILL;
    Socket centralSocket;
    ObjectInputStream fromClient;
    ObjectOutputStream toClient;

    ObjectInputStream fromCentral;
    ObjectOutputStream toCentral;

    String host = "127.0.0.1";

    public Bank(Socket socket) {
        this.bankSocket = socket;


        try {


            toClient = new ObjectOutputStream(bankSocket.getOutputStream());
            fromClient = new ObjectInputStream(bankSocket.getInputStream());



        } catch (IOException e) {

        }

    }

    public void run() {

        try {


            Message request;

            while (!KILL) {




                try {

                    while ((request = (Message) fromClient.readObject()) != null) {



                        printAllClients();


                        if (request.newAccount) {
                            makeAccount(request);


                        }


                        if (request.verify) {
                            Message response = new Message();
                            System.out.println("Entered If statement");
                            this.clientName = "CENTRAL";


                            response.isMember = bankMap.containsKey(request.bankKey);



                            response.message = "USER IS MEMBER";
                            response.fromBank = true;
                            for(Integer i : bankMap.keySet()){
                                System.out.println("KEYS = " + i);
                            }
                            sendMessage(response);
                          //  }

                        }

                        if (request.KILL) {
                            Message response = new Message();
                            response.message = "Goodbye...";
                            sendMessage(response);
                            KILL = true;
                            break;
                        }


                    }
                } catch (ClassNotFoundException ex) {

                } catch (IOException ex) {

                    //System.out.println("Unable to get streams from client in bank server");

                }
            }
        }

            finally{
                try {
                    System.out.println("Agent exiting...");
                    threads.remove(this);
                    toClient.close();
                    fromClient.close();
                    bankSocket.close();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }





    private void sendToCentral(){
        for(int i = 0; i < threads.size(); i++){
            Bank b = threads.get(i);

            if(b.clientName != null && b.clientName.equals("Bank")){
                System.out.println("Entered");
                Message m = new Message();
                m.message = "From bank";
                m.fromBank = true;
                b.sendMessage(m);
            }
        }

    }
    private void makeAccount(Message request){
        Message response = new Message();
        clientName = request.username;
        Account account = new Account(request.username);
        int key = makeBankKey();
        bankMap.put(key, account);
        //response.message = account.returnPackage();
        response.message = "Bank key = " + key;
        response.bankKey = key;
        sendMessage(response);
    }

    private void sendMessage(Message msg) {
        try {

            toClient.writeObject(msg);
            toClient.flush();

        } catch (IOException e) {

        }
    }

    private void talkToCentral() {
        try {

            Socket s = new Socket(host, 8081);
            AuctionCentral c = new AuctionCentral(s);
            c.start();

        }
        catch(IOException e){

        }


    }

    private void printAllClients(){
        for(Bank t : threads){
            System.out.println("User " + t.clientName);

        }
    }
    private void establishCentralConnection(Message m){
        for(Bank t : threads){
            if(t.clientName.equals("CENTRAL")){
                t.sendMessage(m);
            }
        }
    }

    private void makeAccount(String str){

    }

    private void sendMsgToCentral(Message msg){
        try {
            AuctionCentral c = new AuctionCentral(new Socket(host, AUCTION_CENTRAL_PORT));
            c.start();
            c.toClient.writeObject(msg);
            c.toClient.flush();
        }
        catch(IOException e){

        }
    }

    private void getMsgFromCentral()throws ClassNotFoundException
    {
        try {
            AuctionCentral c = new AuctionCentral(new Socket(host, AUCTION_CENTRAL_PORT));
            c.start();
            Message m = (Message)c.fromClient.readObject();
            System.out.println("INT BANK = " + m);

        }
        catch (IOException e)
        {

        }
    }


    private String createAccountNum(Account account) {
        Random rand = new Random();
        int accountNum = rand.nextInt(MAX_ACCOUNTS);
        if (listOfAccountNums.containsKey(accountNum)) {
            createAccountNum(account);
        }

        listOfAccountNums.put(accountNum, account);
        return Integer.toString(accountNum);

    }

    private int makeBankKey() {
        Random rand = new Random();
        int key = rand.nextInt(50);
        if(bankMap.containsKey(key)){
            makeBankKey();
        }

        bankMap.put(key, account);
        return key;

    }


    public static void main(String[] args) {
        System.out.println("Banking Server connected...");
        System.out.println("On port " + PORT_NUMBER);
        ServerSocket server = null;
        try {
            server = new ServerSocket(PORT_NUMBER);

            while (true) {

//                ClientThreads t = new ClientThreads(server.accept());
//                threads.add(t);
//                t.start();
                Bank b = new Bank(server.accept());
                threads.add(b);
                b.start();
                //new Bank(server.accept());

            }

        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Unable to start Banking server.");
        } finally {
            try {
                if (server != null)
                    server.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
