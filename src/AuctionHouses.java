
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * Created by alexschmidt-gonzales on 11/30/17.
 */
public class AuctionHouses extends Thread {

    private int CENTRAL_PORT = 8081;
    private int PORT_NUMBER = 4200;
    private String host = "127.0.0.1";
    private String list;
    private ObjectOutputStream toCentralServer;
    private ObjectInputStream fromCentralServer;
    private Socket centralSocket;

    private String[] items1 = {"Shit , $1.00 \n", "Andrews gay ass, $0.25\n", "MoreShit, $7.00\n"};
    private Map<String, Integer> items = new HashMap<>();

    public static void main(String[] args) {
        new AuctionHouses();
    }

    public AuctionHouses() {
        randomItems();
        start();
    }


    public void run() {
        try {


            System.out.println("Connecting to Agent " + host + " on port " + CENTRAL_PORT + ".");


            try {


                centralSocket = new Socket(host, CENTRAL_PORT);


                toCentralServer = new ObjectOutputStream(centralSocket.getOutputStream());
                fromCentralServer = new ObjectInputStream(centralSocket.getInputStream());


            } catch (UnknownHostException e) {
                System.err.println("Unknown host: " + host);
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Unable to get streams from server in Auction houses");
                System.exit(1);
            }


            Message request;

            Message myName = new Message();
            myName.username = "House";
            myName.newHouse = true;
            toCentralServer.writeObject(myName);
            toCentralServer.flush();

            while ((request = (Message) fromCentralServer.readObject()) != null) {

                System.out.println("In loop");

                System.out.println(request.message);
//
                if (request.getItems) {
                    System.out.println("sending out to agent as: " + myName);
                    Message response = new Message();
                    response.agentName = request.agentName;
                    response.fromHouse = true;
                    response.isList = true;
                    response.message = getItemList();
                    toCentralServer.writeObject(response);
                    toCentralServer.flush();
                } else if(request.placeBid){
                    System.out.println("incoming bid for: " + request.message);
                    Message response = new Message();
                    response.message = requestBid(request.message, request.bid);
                    response.fromHouse = true;
                    toCentralServer.writeObject(response);
                    toCentralServer.flush();
                }
            }
            Message kill = new Message();
            kill.KILL = true;
            kill.HOUSE_LEAVING = true;
            toCentralServer.writeObject(kill);
            toCentralServer.flush();

            System.out.println("HERE IN AUCTION HOUSE");

            fromCentralServer.close();
            toCentralServer.close();
            centralSocket.close();


        }


        catch(Exception e){
            e.printStackTrace();

        }
    }

    private String requestBid(String item, Integer bid){
        String s;
        if(hasItem(item)){
            Boolean won = placeBid(item,bid);
            if(won){
                s = "You successfully placed a bid of " + bid + " for " + item + "\n" + getItemList();
            } else {
                s = "Nahh, that bid ain't enough for " + item;
            }
        } else {
            s = "Woah, that item doesn't exist in this house.";

        }
        return s;
    }

    private Boolean hasItem(String item){
        return items.containsKey(item);
    }
    private Boolean placeBid(String item, Integer bid){
        Integer oldBid = items.get(item);
        if(bid > oldBid){
            // replace old bid with higher one
            items.replace(item, oldBid, bid);
            return true;
        } else{
            return false;
        }
    }

    private void randomItems(){
        Random random = new Random(); // or new Random(someSeed);
        String[] randItems = {"cat","dog","tree", "house", "rat", "bug", "alex" , "hat"};
        for(int i = 0; i < 5; i ++){
            int index = random.nextInt(7);
            int price = 0;
            items.put(randItems[index], price);
        }
    }


    private String getItemList(){
        String s = "";
        Iterator entries = items.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry thisEntry = (Map.Entry) entries.next();
            String key = (String)thisEntry.getKey();
            Integer price = (Integer)thisEntry.getValue();
            s += "~ " + key + " - " + price + "\n";
        }
        return s;
    }
}







