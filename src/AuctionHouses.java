import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexschmidt-gonzales on 11/30/17.
 */
public class AuctionHouses extends Thread
{

    private int CENTRAL_PORT = 8081;
    private int PORT_NUMBER = 4200;
    private String host = "127.0.0.1";
    private Map<String, Double> items = new HashMap<>();
    private String list;
    private ObjectOutputStream toCentralServer;
    private ObjectInputStream fromCentralServer;
    private Socket centralSocket;

    private String[] items1 = {"Shit , $1.00 \n", "Andrews gay ass, $0.25\n", "MoreShit, $7.00\n"};

    public static void main(String[] args)
    {
        new AuctionHouses();
    }

    public AuctionHouses()
    {

        start();
    }


    public void run()
    {
        try
        {


            System.out.println("Connecting to Agent " + host + " on port " + CENTRAL_PORT + ".");


            try
            {


                centralSocket = new Socket(host, CENTRAL_PORT);


                toCentralServer = new ObjectOutputStream(centralSocket.getOutputStream());
                fromCentralServer = new ObjectInputStream(centralSocket.getInputStream());


            } catch (UnknownHostException e)
            {
                System.err.println("Unknown host: " + host);
                System.exit(1);
            } catch (IOException e)
            {
                System.err.println("Unable to get streams from server in Auction houses");
                System.exit(1);
            }


            Message request;
            House test = new House("House");
            toCentralServer.writeObject(test);
            toCentralServer.flush();

            while ((request = (Message) fromCentralServer.readObject()) != null)
            {

                System.out.println("In loop");

                if (request.getItems)
                {

                    Message response = new Message();
                    response.agentName = request.agentName;
                    response.fromHouse = true;
                    response.message = "LIST : ITEM A, ITEM B, ITEM C";
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


        } catch (Exception e)
        {
            e.printStackTrace();

        }
    }
}








