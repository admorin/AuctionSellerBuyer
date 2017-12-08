import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Agent extends Thread {
    public String userName;
    private String initBid;
    private static int BANK_PORT = 8080;
    private static int CENTRAL_PORT = 8081;
    public static final String host = "127.0.0.1";
    public volatile boolean bidding = false;
    private volatile boolean isRunning = true;
    public int myKey;
    private ArrayList<ListenFromServer> servers = new ArrayList<>();
    private Encrypt encrypt;
    Socket bankSocket = null;
    Socket centralSocket = null;

    ObjectOutputStream toBankServer;
    ObjectOutputStream toCentralServer;

    ObjectInputStream fromBankServer;
    ObjectInputStream fromCentralServer;

    BufferedReader stdin;
    private String accountName = "";
    private String agentThread = "";
    private int accountNumber;
    private Boolean registered = false;
    private String housePicked = "";

    public static  volatile boolean changeServer;





    public static void main(String args[]) {

        new Agent("ALEX");

    }

    public Agent(String userName){

        this.userName = userName;

        start();



    }





    private void sendMsgToCentral(Message msg){
        try{
            toCentralServer.writeObject(msg);
            toCentralServer.flush();

        }
        catch(IOException e){

        }

    }

    private void sendMsgToBank(Message msg){
        try {
            toBankServer.writeObject(msg);
            toBankServer.flush();
        }
        catch(IOException e){

        }
    }

    private void connectToServer(){
        System.out.println("Connecting to host " + host + " on ports " + BANK_PORT + ", " + CENTRAL_PORT);
        try {
            bankSocket = new Socket(host, BANK_PORT);
            centralSocket = new Socket(host, CENTRAL_PORT);

            try {

                toBankServer = new ObjectOutputStream(bankSocket.getOutputStream());
                fromBankServer = new ObjectInputStream(bankSocket.getInputStream());

                toCentralServer = new ObjectOutputStream(centralSocket.getOutputStream());
                fromCentralServer = new ObjectInputStream(centralSocket.getInputStream());



            } catch (UnknownHostException e) {
                System.err.println("Unknown host: " + host);
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Unable to get streams from server in Agent");
                System.exit(1);
            }
        }
        catch (IOException e){

        }
    }



    public void run() {
//
        try {


            System.out.println("Connecting to host " + host + " on ports " + BANK_PORT + ", " + CENTRAL_PORT);

            bankSocket = new Socket(host, BANK_PORT);
            centralSocket = new Socket(host, CENTRAL_PORT);




            try {

                toBankServer = new ObjectOutputStream(bankSocket.getOutputStream());
                fromBankServer = new ObjectInputStream(bankSocket.getInputStream());

                toCentralServer = new ObjectOutputStream(centralSocket.getOutputStream());
                fromCentralServer = new ObjectInputStream(centralSocket.getInputStream());

                new ListenFromServer(fromBankServer, "Bank").start();
                new ListenFromServer(fromCentralServer, "Central").start();

                stdin = new BufferedReader(new InputStreamReader(System.in));



            } catch (UnknownHostException e) {
                System.err.println("Unknown host: " + host);
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Unable to get streams from server in Agent");
                System.exit(1);
            }

            System.out.println("Options : Make Account (m) / View Houses (v) / register (r)");
            System.out.println("To return to main menu typ HOME ");



            Message myName = new Message();
            myName.username = "Agent";
            sendMsgToCentral(myName);

            String ui;
            Message msg;
            Message request;




            while((ui = stdin.readLine()) != null){

                if(ui.equals("QUIT")){
                    msg = new Message();
                    msg.KILL = true;
                    sendMsgToBank(msg);
                    sendMsgToCentral(msg);
                    isRunning = false;
                    break;
                }

                else if (ui.equalsIgnoreCase("Make Account") || ui.equals("m")) {

                    System.out.println("Please Enter Name: ");
                    if ((ui = stdin.readLine()) != null) {
                        request = new Message();
                        request.newAccount = true;
                        request.username = ui;
                        accountName = ui;
                        sendMsgToBank(request);
                        printOptions();
                    }
                }

                else if (ui.equalsIgnoreCase("Register") || ui.equalsIgnoreCase("r")) {

                    System.out.println("Please Provide Bank Key: ");
                    if((ui = stdin.readLine()) != null){
                        request = new Message();
                        request.register = true;
                        Integer bankKey = Integer.parseInt(ui);
                        request.bankKey = bankKey;
                        accountNumber = bankKey;
                        request.agentName = "Agent";
                        sendMsgToCentral(request);
                    }
                }

                else if (ui.equals("View Houses")  || ui.equals("v")) {
                    System.out.println("viewing..");
                    if(registered){
                        request = new Message();
                        request.message = "View";
                        //central.username = "Agent";
                        request.askForList = true;
                        sendMsgToCentral(request);
                    } else {
                        System.out.println("You gotta register before you can see the houses");
                        printOptions();
                    }
                }

                else if(ui.equalsIgnoreCase("Place bid") || ui.equalsIgnoreCase("p")){
                    System.out.println("Enter item you would like to bid for...");
                    if ((ui = stdin.readLine()) != null) {
                        String item = ui;
                        System.out.println("Enter amount you would like to bid..");
                        request = new Message();
                        if ((ui = stdin.readLine()) != null) {
                            System.out.println("bid amount: "  + ui);
                            Integer bid = Integer.parseInt(ui);
                            request.message = item;
                            request.destination = housePicked;
                            request.agentName = agentThread;
                            request.bid = bid;
                            request.username = userName;
                            request.placeBid = true;
                            request.username = userName;
                            request.bankKey = accountNumber;
                            sendMsgToCentral(request);
                        }
                    }
                }
                else if (ui.equals("Select House") || ui.equals("s")) {
                    if(registered){
                        System.out.println("Please Enter House Name: ");
                        request = new Message();
                        if ((ui = stdin.readLine()) != null) {
                            System.out.println("you chose: "  + ui);
                            housePicked = ui;
                            request.message = ui;
                            request.username = userName;
                            request.selectHouse = true;
                            request.agentName = agentThread;
                            sendMsgToCentral(request);

                        }
                    } else {
                        System.out.println("Sorry you are not registered.");
                        printOptions();
                    }

                }
                else{
                    System.out.println("Please try again...");
                }


            }
            try {
                System.out.println("Logging out...");
                sleep(1000);
                toBankServer.close();
                fromBankServer.close();
                toCentralServer.close();
                fromCentralServer.close();
                bankSocket.close();
                centralSocket.close();

            }
            catch(InterruptedException e){

            }

        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }



    class ListenFromServer extends Thread {

        ObjectInputStream fromServer;
        String serverName;

        public ListenFromServer(ObjectInputStream fromServer, String serverName){
            this.fromServer = fromServer;
            this.serverName = serverName;
        }

        public void run() {

            while(isRunning) {

                try {

                    Message server = (Message) fromServer.readObject();

                    if (server != null) {
                        // sort(server);

                        if(server.register){
                            if(server.isMember){
                                registered = true;
                                agentThread = server.agentName;
                                System.out.println("my agent thread to talk to is: " + agentThread);
                                System.out.println("Successfully registered w/ auction central as: " + accountName);
                            } else {
                                System.out.println("Unable to register w/ auction central as: " + accountName);
                            }
                            printOptions();
                        } else if(server.houseList){
                            // print house options
                            System.out.println(server.message);
                            printOptions();
                        }else if(server.isList){
                            System.out.println("Items from house: \n\n" + server.message);
                            printOptions();
                        } else {
                            System.out.println(serverName + " > " + server.message);
                        }
                    }
                }

                catch(IOException e) {

                }
                catch(ClassNotFoundException ex){

                }

            }

        }

        private void sort(Message msg){
            if(msg.agentName != null){
                userName = msg.agentName;

            }
            else if(msg.bankKey != -1){
                myKey = msg.bankKey;
                System.out.println("MY KEY = " + myKey);
            }


        }

    }

    private void printOptions(){
        if(accountName.equals("")){
            System.out.println("Options : Make Account (m) / View Bidding Houses (v) / Select House (s)");
        }else if(!registered){
            System.out.println("Options :  View Bidding Houses (v) / Register w Auction Central (r)");
        } else {
            System.out.println("Options : View Bidding Houses (v) / Select House (s) / Place Bid (p)");
        }
    }

}





