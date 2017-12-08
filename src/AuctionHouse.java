public class AuctionHouse {
    /*
    private ArrayList<String> items = new ArrayList<>();
    private HashMap<String, Integer> itemMap = new HashMap<String, Integer>();
    private String host;
    private int port;
    private String name = "";

    public static void main(String args[]) {
        String host = "127.0.0.1";
        int port = 8081;
        new AuctionHouse(port, host);
    }

    public AuctionHouse(int port, String host){
        System.out.println("auction house created");
        this.port = port;
        this.host = host;
        register();
    }

    private void register(){
        try {

            System.out.println("Connecting to host " + host + " on port " + host + ".");
            Socket centralSocket = null;
            BufferedReader in = null;

            ObjectOutputStream toCentralServer = null;
            ObjectInputStream fromCentralServer = null;

            try {
                centralSocket = new Socket(host, port);
                toCentralServer = new ObjectOutputStream(centralSocket.getOutputStream());
                fromCentralServer = new ObjectInputStream(centralSocket.getInputStream());

            } catch (UnknownHostException e) {
                System.err.println("Unknown host: " + host);
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Unable to get streams from server in Agent");
                System.exit(1);
            }

            System.out.println("Options : Register (r) / Add Item (a)");
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            Boolean registerInput = false;
            String request;

            while((request = stdin.readLine()) != null){
                //System.out.println("Message sent: " + request);
                // Waiting on register input from user
                if(registerInput){
                    System.out.println("Registering with id " + request);
                    name = request;
                    Message send = new Message();
                    send.register = true;
                    send.ip = host;
                    send.username = name;
                    send.items = itemMap;
                    toCentralServer.writeObject(send);
                    System.out.println(((Message)fromCentralServer.readObject()).getMessage());
                    System.out.println("\nOptions : Add Item (a)");
                    registerInput = false;
                }else if(request.equals("Register") || request.equals("r")){
                    if(name.equals("")){
                        System.out.println("Enter name/id you would like to register as: ");
                        registerInput = true;
                    } else {
                        System.out.println("You are already registered as: " + name);
                        Message send = new Message();
                        send.addItems = true;
                        send.username = name;
                        send.items = itemMap;
                        toCentralServer.writeObject(send);
                        System.out.println(((Message)fromCentralServer.readObject()).getMessage());
                        System.out.println("\n\nOptions : Add Item (a)");
                    }

                }else if(request.equals("Add Item") || request.equals("a")){
                    // Add list of items
                    inputItems(stdin);
                } else {
                    System.out.println("Sike. Not allowed.");
                    System.out.println("Options : Register/Add Item"); // make a print options function later
                }

                toCentralServer.flush();
                toCentralServer.reset();

            }

            toCentralServer.close();
            fromCentralServer.close();

            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void inputItems(BufferedReader stdin){
        System.out.println("Enter item you would like to auction off.");
        String item;
        try
        {
            while((item = stdin.readLine()) != null && !item.equals("q")){
                if(!items.contains(item)){
                    Integer price = getRandPrice();
                    items.add(item);
                    itemMap.put(item, price);
                    System.out.println("Added " + item + " to the auction");
                }
                System.out.println("would you like to add more? (q to quit)");
            }

        }catch (IOException e){
            System.out.println("caught it");
        }
        System.out.println("Auction list: \n" );
        printList(items);
        printMap();
        System.out.println("Options: Register (r) /Add Item (a)");
    }

    private void printList(ArrayList<String> list){
        for(int j = 0; j < list.size(); j ++){
            System.out.println("item " + (j +1) + ": " + list.get(j));
        }
    }

    private void printMap(){
        Iterator entries = itemMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry thisEntry = (Map.Entry) entries.next();
            String key = (String)thisEntry.getKey();
            Integer price = (Integer)thisEntry.getValue();
            System.out.println(" ~ " + key + " -> " + price);
        }
    }

    private Integer getRandPrice(){
        Random r = new Random();
        Integer price = r.nextInt(20);
        return 0;
    }



        */
}
