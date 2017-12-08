import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Created by BeauKujath on 30/11/2017.
 */
public class AgentList
{
    //private HashMap<String, House> houses = new HashMap<String, House>(); // Maps registered id of house to object
    private static AgentList single = null;
    private int count = 0;

    private AgentList()
    {
        System.out.println("creating agent list");
    }

    // static method to create instance of Singleton class
    public static AgentList getInstance()
    {
        if (single== null)
            single = new AgentList();

        return single;
    }
    
    public String getNextAgent(){
        count++;
        return "Agent " + count;
    }
    
    /*

    public String addAgent(String name, HashMap<String, Integer> items){
        String result = "";
        if(hasHouse(name)){
            getHouse(name).addItems(items);
            result += "Successfully added items to " + name;
        } else {
            result += "Sorry that house does not exist.";
        }
        return result;
    }

    public String getItems(String name){
        String s = "";
        if(hasHouse(name)){
            s = getHouse(name).getItemString();
        } else {
            s = "Sorry that house does not exist.";
        }
        return s;
    }


    public void addHouse(String userName, House h){
        houses.put(userName, h);
    }


    private Boolean hasHouse(String user){
        if(houses.containsKey(user)){
            return true;
        }
        return false;
    }

    public House getHouse(String user){
        return houses.get(user);
    }

    public Boolean placeBid(String house, String item, Integer bid){
        House h = houses.get(house);
        return h.placeBid(item, bid);
    }


    public String getAgentString(){
        String s = "The Houses: \n";
        Iterator entries = houses.entrySet().iterator();
        while (entries.hasNext()) {
            Entry thisEntry = (Entry) entries.next();
            String key = (String)thisEntry.getKey();
            //House value = (House)thisEntry.getValue();
            s += "- " + key + "\n";
        }
        if(houses.size() == 0 ){
            s = "No houses have been registered with Auction Central yet.\n";
        }
        return s;
    }

*/

}
