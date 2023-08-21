public class Manufacturer extends Thread{

    /**
     * id of the manufacturer
     */
    private int id;

    /**
     * Class constructor
     * @param id the id of the class, Manufacturer-0 produces heads,
     *           Manufacturer-1 produces torsos, Manufacturer-2 produces tails,
     *           Manufacturer-3 produces legs
     */
    public Manufacturer(int id){
        this.id = id;
        setName("Manufacturer-" + id);
    }

    /**
     * the void method produce modifies corresponding shared variables by thread's id
     * and print the message indicating the number of parts has been produced,
     * after producing the part, the thread sleeps for a random time to simulate the production.
     */
    public void produce() throws InterruptedException {
        if (id == 0) {
            int parts = (int) (Math.random() * 31 + 70);
            Main.mutex_head.acquire();
            Main.heads += parts;
            Main.mutex_head.release();;
            msg(parts + " heads has been produced by " + getName());
        } else if (id == 1) {
            int parts = (int) (Math.random() * 31 + 70);
            Main.mutex_torso.acquire();
            Main.torsos += parts;
            Main.mutex_torso.release();
            msg(parts + " torsos has been produced by " + getName());
        } else if (id == 2) {
            int parts = (int) (Math.random() * 31 + 70);
            Main.mutex_tail.acquire();
            Main.tails += parts;
            Main.mutex_tail.release();
            msg(parts + " tails has been produced by " + getName());
        } else {
            int parts = (int) (Math.random() * 251 + 250);
            Main.mutex_leg.acquire();
            Main.legs += parts;
            Main.mutex_leg.release();
            msg(parts + " legs has been produced by " + getName());
        }

        Thread.sleep((long)Math.random()*1000+1000);

    }

    /**
     * override run method
     * The manufacturer keeps producing parts
     * if its corresponding truck has not done with today's delivery job(default is 3 deliveries/day)
     */
    @Override
    public void run() {
        while(Main.trucks.get(id).getNum_delivery() > 0){
            try {
                produce();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * msg method prints out messages with current thread name and timestamp
     * @param m the message to be printed out
     */
    public void msg(String m) {
        System.out.println("["+(System.currentTimeMillis()-Main.time)+"] "+getName()+": "+m);
    }
}
