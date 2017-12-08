
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

public class Account extends HashMap{

    private String clientName;
    private double initialAmount;
    private static HashMap<Integer, String> ACCOUNT_MAP = new HashMap<>();
    //private HashMap<Integer, HashMap<String, Double>> accountNumbers = new HashMap<>();
    private static double FIXED_INITIAL_DEPOSIT = 5.00;
    private Encrypt encrypt;
    private BigInteger MY_PRIVATE_KEY;
    private BigInteger MY_PUBLIC_KEY;
    private BigInteger BANK_PUBLIC_KEY;
    private Integer clientNumber;
    private int balance;



    private String clientInfo = "";


    public Account(String clientName){
        this.clientName = clientName;
        this.clientNumber = makeAccountNumber();
        ACCOUNT_MAP.put(clientNumber, clientName);
        this.encrypt = new Encrypt(clientNumber.toString());
        this.MY_PRIVATE_KEY = encrypt.getPrivate();
        this.MY_PUBLIC_KEY = encrypt.getPublic();
        int initialDeposit = 60;
        this.balance = initialDeposit;

        System.out.println("Account ["+clientName+"] has been created...");

    }

    public Integer getAccountNumber(){
        return clientNumber;
    }


    public void loginAccount(Integer memberNumber){
        if(ACCOUNT_MAP.containsValue(memberNumber)){
            System.out.println("User = " + ACCOUNT_MAP.get(memberNumber));
        }
        else{
            System.out.println("Account Does not exist..");
        }
    }

    public String returnPackage(){

        String packet = "User = " + clientName + ", " + "Account Number = " + clientNumber + " , "
                + "Your Public Key = " + MY_PUBLIC_KEY + " , "
                + "Balance = " + balance + "Bank Key '\n'";

        return packet;

    }

    public void depositFunds(Integer amount){
        balance += amount;

    }

    private void encryptShit(){
        System.out.println("Public Key = " + encrypt.getPublic());

    }


    private int makeAccountNumber(){
        Random rand = new Random();
        int x = rand.nextInt(100000);
        if(ACCOUNT_MAP.containsKey(x)){
            makeAccountNumber();
        }

        return x;
    }

    public Boolean placeHold(Integer bid){
        System.out.println("bid is " + bid);
        System.out.println("placed hold on account of bid value: " + bid.intValue());
        if(bid > balance){
            System.out.println(clientName + " does not have enough money to bid " + bid);
            System.out.println("account balance is: " + balance);
            return false;
        } else {
            balance -= bid;
            System.out.println("account balance now is: " + balance);
            return true;
        }


    }

    public int makeKey(){
        Random rand = new Random();
        int temp = rand.nextInt(50);
        return temp;
    }



}