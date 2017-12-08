import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by alexschmidt-gonzales on 11/28/17.
 */
public class AuctionCentral extends Thread {

    Socket agentSocket;
    Socket houseSocket;

    public static final int CENTER_PORT = 8081;


    Socket socket;
    public ObjectOutputStream toClient;
    public ObjectInputStream fromClient;
    boolean sendlist, newHouse, selectHouse;
    boolean housesAvailable, houseLeaving;
    volatile boolean KILL;
    volatile boolean KILL_HOUSE = false;
    String myName;
    Message init;
    int clientBankKey;
    public static int agentId = 1;
    public static int houseId = 1;

    public ObjectOutputStream toBank;
    public ObjectInputStream fromBank;

    private final String host = "127.0.0.1";





    public static ArrayList<AuctionCentral> threads = new ArrayList<>();
    public static HashMap<Integer, AuctionCentral> registeredUsers = new HashMap<>();
    public Bank b;
    public Socket bankSocket;



    public AuctionCentral(Socket socket) {
        this.socket = socket;

        try {

            toClient = new ObjectOutputStream(socket.getOutputStream());
            fromClient = new ObjectInputStream(socket.getInputStream());

//            b = new Bank(bankSocket = new Socket(host, 8080));
            bankSocket = new Socket(host, 8080);
            toBank = new ObjectOutputStream(bankSocket.getOutputStream());
            fromBank = new ObjectInputStream(bankSocket.getInputStream());


            Message user = (Message) fromClient.readObject();
            System.out.println("initial thread name: " + user.username);
            this.myName = user.username ;
            this.newHouse = user.newHouse;


        }
        catch (IOException e) {

        }
        catch (ClassNotFoundException e) {

        }
    }

    public void run() {


        try {

            while (!KILL) {
                Message request;
                Message response;


                nameClients();
                newHouseListener(newHouse);
                newHouse = false;
                while ((request = (Message) fromClient.readObject()) != null) {

                    sendlist = request.askForList;
                    selectHouse = request.selectHouse;

                    //getting list of items from house then sending them to the requesting agent
                    if (request.fromHouse) {
                        System.out.println("request from house: " + request.message);
                        response = new Message();
                        response.isList = true;
                        response.message = request.message;
                        /*
                         TODO this should tell which agent sent the initial request to the house
                         then send to that specific agent thread here not just the first one at index 0
                         */
                        threads.get(0).agentSend(response);
                    }

                    if(request.fromBank){
                        System.out.println(request.message);
                    }

                    if (request.register) {
                        printThreads();
                        response = new Message();
                        this.clientBankKey = request.bankKey;
                        this.myName = request.agentName;
                        System.out.println("Agent Name = " + myName);
                        response.bankKey = request.bankKey;
                        response.agentName = request.agentName;
                        response.verify = true;
                        bankBroadcast(response);
                        Boolean result = readFromBank();
                        Message m = new Message();
                        m.isMember = result;
                        m.register = true;
                        toClient.writeObject(m);
                        toClient.flush();
                        toClient.reset();
                    }

                    if(request.placeBid){
                        System.out.println("placing a bid on item: " );
                        // Try: can we just pass it request instead of this...
                        Message m = new Message();
                        m.message = request.message;
                        m.bid = request.bid;
                        m.username = request.username;
                        m.placeBid = true;
                        m.bankKey = request.bankKey;
                        bankBroadcast(m);

                        // TODO fix hardcoded here to only use house 1 when a place bid is requested.
                        houseSend(request.username,request.destination, m); // the house agent wants to send to
                    }

                    if (request.isMember) {
                        System.out.println(request.message);
                    }


                    if (sendlist) {

                        sendHouseList();
                    }

                    if (selectHouse && housesAvailable) {
                        if (request != null){
                            Message m = new Message();
                            m.getItems = true;
                            houseSend(request.username, request.message, m);
                        }
                    } else if (selectHouse && !housesAvailable) {
                        agentBroadcast("There are no houses available...");
                    }
                }
                houseLeavingListener(true);
            }


        } catch (IOException e) {


        } catch (ClassNotFoundException e) {


        } finally {
            try {

                System.out.println(myName + " is logging off...");
                threads.remove(this);
                fromClient.close();
                toClient.close();
                socket.close();


            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void printThreads(){
        System.out.println("The threads: \n");
        for(AuctionCentral t : threads){
            if(t.myName != null){
                System.out.println("thread name: "  + t.myName);
            }
        }
        System.out.println();
    }

    private void nameClients() {
        for (AuctionCentral t : threads) {
            if (t.myName.equals("House")) {
                t.myName = "House " + houseId++;
            }
            else if (t.myName.equals("Agent")) {
                t.myName = "Agent " + agentId++;

            }

        }

    }



    private void houseSend(String agent, String house, Message m) {
        for (AuctionCentral t : threads) {
            if(t.myName != null){
                if (t.myName.equals(house)) {
                    m.agentName = agent;
                    t.houseBroadcast(m);
                }
            }
        }
    }

    private void newHouseListener(boolean newHouse) {
        if (newHouse) {
            for (AuctionCentral t : threads) {
                if (t.myName.contains("Agent")) {
                    t.agentBroadcast("New AuctionHouse has entered! -> " + this.myName);
                    housesAvailable = true;
                }
            }
        }

    }

    private void houseLeavingListener(boolean leave) {
        if (leave) {
            System.out.println("Entered");
            for (AuctionCentral t : threads) {
                if (t.myName.contains("Agent")) {
                    t.agentBroadcast("Auction House " + this.myName + " is offline.");

                }
            }
        }
    }

    private void bankBroadcast(Message m) throws ClassNotFoundException {
        try {

            toBank.writeObject(m);
            toBank.flush();

        } catch (IOException e) {

        }

    }

    private boolean readFromBank() throws ClassNotFoundException{

        try {



            Message m = (Message)fromBank.readObject();
            boolean member = m.isMember;

            System.out.println(member);

            System.out.println(m.message);
            return m.isMember;
        }
        catch(IOException e){

        }
        return false;


    }
    private void getMsgFromBank() throws ClassNotFoundException {
        try {

            Bank b = new Bank(new Socket(host, 8080));
            b.start();
            System.out.println(b.fromClient.readObject());

        } catch (IOException e) {

        }

    }




    private void agentSend(Message m){
        try {
            toClient.writeObject(m);
            toClient.flush();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }


    private void agentBroadcast(String msg) {
        try {
            Message x = new Message();
            x.message = msg;
            toClient.writeObject(x);
            toClient.flush();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private void houseBroadcast(Message msg) {
        try {
            toClient.writeObject(msg);
            toClient.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendHouseList() {
        boolean hasHouse = false;
        String s = "";
        if (!threads.isEmpty()) {
            for (AuctionCentral t : threads) {
                System.out.println("USER = " + t.myName + " " + t);

                if (!t.equals(this) && t.myName.contains("House") && t.isAlive()) {
                    s += "~ " +  t.myName + "\n";
                }
            }
            Message m = new Message();
            m.message = "All houses online  \n" + s;
            m.houseList = true;
            hasHouse = true;
            housesAvailable = true;
            agentSend(m);
            System.out.println("------------------");
            if (!hasHouse) {
                String msg = "No Auction Houses are online right now";
                housesAvailable = false;
                agentBroadcast(msg);
            }
        }

    }


    private void remove() {
        for (AuctionCentral t : threads) {
            if (!t.isAlive()) {
                System.out.println("DEAD THREAD -> " + t.myName);
                threads.remove(t);
                System.out.println(t.myName);
                try {
                    t.toClient.close();
                    t.fromClient.close();
                    t.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private int makeBiddingKey() {
        Random rand = new Random();
        int key = rand.nextInt(50);
        if (registeredUsers.containsKey(key)) {
            makeBiddingKey();
        }
        return key;
    }




    public static void main(String[] args) {
        System.out.println("Starting Auction Central...");
        ServerSocket fromAgent = null;
        ServerSocket fromHouse = null;
        try {
            fromAgent = new ServerSocket(CENTER_PORT);

            while (true) {


                AuctionCentral c = new AuctionCentral(fromAgent.accept());
                threads.add(c);
                c.start();

            }


        } catch (IOException ex) {
            System.out.println("Unable to start Auction Central.");
        } finally {
            try {
                if (fromAgent != null) fromAgent.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
