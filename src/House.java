import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by BeauKujath on 02/12/2017.
 */
public class House implements Serializable
{
    private String houseName;
    private LinkedList<Item> itemInv = new LinkedList<>();

    String command;

    public House(String userName)
    {
        itemInv.add(new Item("Pizza", 50));
        itemInv.add(new Item("Bread", 55));
        this.houseName = userName;
    }

    public String getName() { return houseName; }

    public LinkedList<Item> getItemList() { return itemInv; }

}

class Item implements Serializable
{
    private String itemName;
    private int minBid;
    private int itemId;

    public Item(String nameIn, int bidIn)
    {
        itemName = nameIn;
        minBid = bidIn;
    }

    public String getName()
    {
        return itemName;
    }

    public int getMinBid()
    {
        return minBid;
    }
}
