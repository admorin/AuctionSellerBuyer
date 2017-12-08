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
public class AuctionCentral extends Thread
{

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


    public AuctionCentral(Socket socket)
    {
        this.socket = socket;

        try
        {

            toClient = new ObjectOutputStream(socket.getOutputStream());
            fromClient = new ObjectInputStream(socket.getInputStream());

//            b = new Bank(bankSocket = new Socket(host, 8080));
            bankSocket = new Socket(host, 8080);
            toBank = new ObjectOutputStream(bankSocket.getOutputStream());
            fromBank = new ObjectInputStream(bankSocket.getInputStream());

            Object curObj = fromClient.readObject();
            if (curObj.getClass().getSimpleName().equals("House"))
            {

                House curHouse = (House) curObj;
                this.myName = curHouse.getName();
                this.newHouse = true;
                String test = "";
                for (Item i : curHouse.getItemList())
                    test += i.getName() + " : " + i.getMinBid() + "\n";
                System.out.println(test);
            } else
            {
                Message user = (Message) curObj;
                this.myName = user.username;
                this.newHouse = user.newHouse;
            }

        } catch (IOException e)
        {

        } catch (ClassNotFoundException e)
        {

        }
    }

    public void run()
    {


        try
        {

            while (!KILL)
            {
                Message request;
                Message response;


                nameClients();
                newHouseListener(newHouse);
                newHouse = false;
                while ((request = (Message) fromClient.readObject()) != null)
                {

                    sendlist = request.askForList;
                    selectHouse = request.selectHouse;


                    //getting list of items from house then sending them to the requesting agent
                    if (request.fromHouse)
                    {
                        for (AuctionCentral t : threads)
                        {
                            if (t.myName.equals(request.agentName))
                            {
                                t.agentBroadcast(request.message);
                            }
                        }
                    }

                    if (request.fromBank)
                    {
                        System.out.println(request.message);
                    }

                    if (request.register)
                    {
                        response = new Message();
                        this.clientBankKey = request.bankKey;
                        this.myName = request.agentName;
                        System.out.println("Agent Name = " + myName);
                        //registeredUsers.put(makeBiddingKey(), this);
                        response.bankKey = request.bankKey;
                        response.verify = true;
                        bankBroadcast(response);
                        readFromBank();


                    }

                    if (request.isMember)
                    {
                        System.out.println("HERE");
                        System.out.println(request.message);
                    }


                    if (sendlist)
                    {

                        sendHouseList();
                    }

                    if (selectHouse && housesAvailable)
                    {
                        if (request != null) formCommunication(this.myName, request.message);
                    } else if (selectHouse && !housesAvailable)
                    {
                        agentBroadcast("There are no houses available...");
                    }
                }
                houseLeavingListener(true);
            }


        } catch (IOException e)
        {


        } catch (ClassNotFoundException e)
        {


        } finally
        {
            try
            {

                System.out.println(myName + " is logging off...");
                threads.remove(this);
                fromClient.close();
                toClient.close();
                socket.close();


            } catch (IOException e)
            {
                e.printStackTrace();
            }

        }

    }

    private void nameClients()
    {
        for (AuctionCentral t : threads)
        {
            //System.out.println(t)
            if (t.myName.contains("House"))
            {
                t.myName = "House " + houseId++;
            } else if (t.myName.equals("Agent"))
            {
                t.myName = "Agent " + agentId++;

            }

        }

    }


    private void formCommunication(String agent, String house)
    {
        Message msg = new Message();
        for (AuctionCentral t : threads)
        {
            if (t.myName.equals(house))
            {
                msg.agentName = agent;
                msg.getItems = true;
                t.houseBroadcast(msg);
            }
        }
    }

    private void newHouseListener(boolean newHouse)
    {
        if (newHouse)
        {
            for (AuctionCentral t : threads)
            {
                if (t.myName.contains("Agent"))
                {
                    t.agentBroadcast("New AuctionHouse has entered! -> " + this.myName);
                    housesAvailable = true;
                }
            }
        }

    }

    private void houseLeavingListener(boolean leave)
    {
        if (leave)
        {
            System.out.println("Entered");
            for (AuctionCentral t : threads)
            {
                if (t.myName.contains("Agent"))
                {
                    t.agentBroadcast("Auction House " + this.myName + " is offline.");

                }
            }
        }
    }

    private void bankBroadcast(Message m) throws ClassNotFoundException
    {
        try
        {

            toBank.writeObject(m);
            toBank.flush();

        } catch (IOException e)
        {

        }

    }

    private boolean readFromBank() throws ClassNotFoundException
    {

        try
        {


            Message m = (Message) fromBank.readObject();
            boolean member = m.isMember;

            System.out.println(member);

            System.out.println(m.message);
            return m.isMember;
        } catch (IOException e)
        {

        }
        return false;


    }

    private void getMsgFromBank() throws ClassNotFoundException
    {
        try
        {

            Bank b = new Bank(new Socket(host, 8080));
            b.start();
            System.out.println(b.fromClient.readObject());

        } catch (IOException e)
        {

        }

    }


    private void agentBroadcast(String msg)
    {
        try
        {
            Message x = new Message();
            x.message = msg;
            toClient.writeObject(x);
            toClient.flush();
        } catch (IOException e)
        {
            e.printStackTrace();

        }
    }

    private void houseBroadcast(Object msg)
    {
        try
        {
            toClient.writeObject(msg);
            toClient.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void sendHouseList()
    {
        boolean hasHouse = false;
        if (!threads.isEmpty())
        {
            for (AuctionCentral t : threads)
            {
                System.out.println("USER = " + t.myName + " " + t);

                if (!t.equals(this) && t.myName.contains("House") && t.isAlive())
                {
                    agentBroadcast("All houses online  " + t.myName);
                    hasHouse = true;
                    housesAvailable = true;

                }
            }
            System.out.println("------------------");
            if (!hasHouse)
            {
                String msg = "No Auction Houses are online right now";
                housesAvailable = false;
                agentBroadcast(msg);
            }
        }

    }


    private void remove()
    {
        for (AuctionCentral t : threads)
        {
            if (!t.isAlive())
            {
                System.out.println("DEAD THREAD -> " + t.myName);
                threads.remove(t);
                System.out.println(t.myName);
                try
                {
                    t.toClient.close();
                    t.fromClient.close();
                    t.socket.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }


    private int makeBiddingKey()
    {
        Random rand = new Random();
        int key = rand.nextInt(50);
        if (registeredUsers.containsKey(key))
        {
            makeBiddingKey();
        }
        return key;
    }


    //start();


//    public void run() {
//
//
//
//        try {
//
//
//            toAgent = new ObjectOutputStream(agentSocket.getOutputStream());
//            fromAgent = new ObjectInputStream(agentSocket.getInputStream());
//
//            Message request;
//            Message reponse;
//
//
//            while(true) {
//                while ((request = (Message) fromAgent.readObject()) != null) {
//                    System.out.println("In Auction Central Server...");
//
//
//
//                    if (request.viewAuctionHouses) {
//                        Message response = new Message();
//                        response.askForList = true;
//
//
//                    }
////
////
////
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//
//            System.out.println("Unable to get streams from client in Server 2");
//        }
//
//        catch (ClassNotFoundException e) {
//        }
//
//        finally {
//            try {
//
//
//                threads.clear();
//                fromAgent.close();
//                toAgent.close();
//                agentSocket.close();
//
//
//            } catch (IOException ex) {
//
//                ex.printStackTrace();
//            }
//        }
//    }


    public static void main(String[] args)
    {
        System.out.println("Starting Auction Central...");
        ServerSocket fromAgent = null;
        ServerSocket fromHouse = null;
        try
        {
            fromAgent = new ServerSocket(CENTER_PORT);

            while (true)
            {


                AuctionCentral c = new AuctionCentral(fromAgent.accept());
                threads.add(c);
                c.start();

//                ClientThreads t = new ClientThreads(fromAgent.accept());
//                threads.add(t);
//                t.start();


            }


        } catch (IOException ex)
        {
            System.out.println("Unable to start Auction Central.");
        } finally
        {
            try
            {
                if (fromAgent != null) fromAgent.close();

            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }
}