/**
 * Created by alexschmidt-gonzales on 11/28/17.
 */
import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    private static final long serialVersionUID = -5399605122490343339L;

    String username;
    String accountNum;
    String balance;
    String agentName;
    boolean newAccount;
    boolean viewAuctionHouses;
    volatile boolean KILL;
    boolean HOUSE_LEAVING;

    boolean askForList, getItems, houseList, isItems;
    boolean fromHouse, register, moveFunds;
    boolean selectHouse, notification;
    boolean placeBid, verify, isMember, fromBank;
    boolean newHouse, newUser, invalidBid, test;
    Integer biddingKey, bankKey, index, bidAmount;
    String message, selectedHouse;
    String[] items;
    String[] houses;
    double[] prices;
    int[] timeLeft;

    int counterD = 0;
    int counterS = 0;
    int counterT = 0;


    public Message(){}

    public void makeDoubleArray(double price){
        prices[counterD] = price;
        counterD++;
    }

    public void makeStringArray(String item){
        items[counterS] = item;
        counterS++;
    }

    public void makeTimerArray(int timer){
        timeLeft[counterT] = timer;
        counterT++;
    }

    public String getMessage(){
        return message;
    }



    public String printInfo(){

        return "User = " + username + '\n' +
                "Account Number = " + accountNum + '\n' +
                "Balance = " + balance;

    }



    public int getAccountNum(){
        return Integer.getInteger(accountNum);
    }

    public boolean hasNewAccountInfo(){
        return (username != null);
    }



}
