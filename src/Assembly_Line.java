import java.util.Collections;

public class Assembly_Line extends Thread{

    /**
     * id of the assembly line
     */
    private int id;

    /**
     * number of heads in the assembly line
     */
    private int heads = 0;

    /**
     * number of tails in the assembly line
     */
    private int tails = 0;

    /**
     * number of torsos in the assembly line
     */
    private int torsos = 0;

    /**
     * number of legs in the assembly line
     */
    private int legs = 0;

    /**
     * number of bunnies has been produced
     */
    private int bunnies = 0;

    /**
     * Class Constructor
     * @param id
     */
    public Assembly_Line(int id){
        this.id = id;
        setName("Assembly_Line-" + id);
    }


    /**
     * unloading the parts from the truck
     */
    public void unload() throws InterruptedException {

        //The assembly line waits for the truck to arrive
        Main.truck_arrival.acquire(4);

        //unloading the parts from the trucks
        this.heads += Main.trucks.get(0).getParts_in_the_truck();
        msg(this.heads + " heads has been delivered to the assembly line.");
        this.torsos += Main.trucks.get(1).getParts_in_the_truck();
        msg(this.torsos + " torsos has been delivered to the assembly line.");
        this.tails += Main.trucks.get(2).getParts_in_the_truck();
        msg(this.tails + " tails has been delivered to the assembly line.");
        this.legs += Main.trucks.get(3).getParts_in_the_truck();
        msg(this.legs + " legs has been delivered to the assembly line.");
        Main.truck_leave.release(4);

        //sleep for a random amount of time to simulate unloading process
        Thread.sleep((long) Math.random()*1000 + 1000);
        }

    /**
     * When there are enough parts, produce bunnies.
     */
    public void produce(){
        while (this.heads >= 1
                && this.torsos >= 1
                && this.tails >= 1
                && this.legs >= 4){
            this.heads -= 1;
            this.torsos -= 1;
            this.tails -= 1;
            this.legs -= 4;
            this.bunnies += 1;
        }
    }

    /**
     * Prints the invoice for the day.
     * Set the surplus for the trucks.
     * Sort the trucks, and use semaphore to let trucks leave in sequence.
     */
    public void printInvoice(){
        msg("Heads Surplus: " + this.heads);
        msg("Torsos Surplus: " + this.torsos);
        msg("Tails Surplus: " + this.tails);
        msg("Legs Surplus: " + this.legs);
        msg(bunnies + " bunnies has been produced today.");
        Main.trucks.get(0).setSurplus(this.heads);
        Main.trucks.get(1).setSurplus(this.torsos);
        Main.trucks.get(2).setSurplus(this.tails);
        Main.trucks.get(3).setSurplus(this.legs);
        Collections.sort(Main.trucks, Collections.reverseOrder());

        //signal the semaphore after setting the surplus and printing the invoice.
        Main.calculating_surplus.release(4);
    }

    @Override
    public void run() {
        while(Main.trucks.get(0).getNum_delivery() != 0
                || Main.trucks.get(1).getNum_delivery() != 0
                || Main.trucks.get(2).getNum_delivery() != 0
                || Main.trucks.get(3).getNum_delivery() != 0) {
            try {
                unload();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            produce();
        }
        printInvoice();
    }

    /**
     * msg method prints out messages with current thread name and timestamp
     * @param m the message to be printed out
     */
    public void msg(String m) {
        System.out.println("["+(System.currentTimeMillis()-Main.time)+"] "+getName()+": "+m);
    }


}
