public class Truck extends Thread implements Comparable<Truck>{
    /**
     * id of the truck
     */
    private int id;

    /**
     * the surplus of the truck
     */
    private int surplus;

    /**
     * numbers of delivery jobs of the truck
     */
    private int num_delivery;

    /**
     * number of parts currently in the truck
     */
    private int parts_in_the_truck;

    /**
     * Class constructor
     * @param id the id of the class, Truck-0 delivers heads,
     *           Truck-1 delivers torsos, Truck-2 delivers tails,
     *           Truck-3 delivers legs
     * @param num_delivery number of delivery jobs of the truck
     */
    public Truck(int id, int num_delivery) {
        this.id = id;
        this.num_delivery = num_delivery;
        this.parts_in_the_truck = 0;
        this.surplus = -1;
        setName("Truck-" + id);
    }

    /**
     * set the surplus of the truck
     * @param surplus surplus of truck
     */
    public void setSurplus(int surplus) {
        this.surplus = surplus;
    }

    /**
     * returns number of deliveries left for the day
     * @return num_delivery
     */
    public int getNum_delivery() {
        return num_delivery;
    }

    /**
     * returns number of parts currently in the truck
     * @return parts_in_the_truck
     */
    public int getParts_in_the_truck() {
        return parts_in_the_truck;
    }

    /**
     * loading method simulates the loading of the parts to the truck.
     * The loading time is simulated by sleeping for a random time.
     * it will copy the value of corresponding part from shared variables
     * in main method to the class variables and reset the counters in main method to 0.
     * And then it prints out message indicating how many parts has been loaded.
     * Semaphores are used to implement the mutual exclusion for changing the shared variables.
     */
    public void loading() throws InterruptedException {

        Thread.sleep((long) Math.random()*1000 + 1000);

        if (id == 0) {
            Main.mutex_head.acquire();
            this.parts_in_the_truck += Main.heads;
            Main.heads = 0;
            Main.mutex_head.release();
            msg(parts_in_the_truck + " heads has been loaded to " + getName());
        } else if (id == 1) {
            Main.mutex_torso.acquire();
            this.parts_in_the_truck += Main.torsos;
            Main.torsos = 0;
            Main.mutex_torso.release();
            msg(parts_in_the_truck + " torsos has been loaded to " + getName());
        } else if (id == 2) {
            Main.mutex_tail.acquire();
            this.parts_in_the_truck += Main.tails;
            Main.tails = 0;
            Main.mutex_tail.release();
            msg(parts_in_the_truck + " tails has been loaded to " + getName());
        } else {
            Main.mutex_leg.acquire();
            this.parts_in_the_truck += Main.legs;
            Main.legs = 0;
            Main.mutex_leg.release();
            msg(parts_in_the_truck + " legs has been loaded to " + getName());
        }
    }

    /**
     * delivering method simulates the delivery of parts to the assembly line.
     * On its way to delivery, the truck speeds up, simulated by setPriority(),
     * set thread's priority to higher priority.
     * After truck sets the priority, it will sleep for a random time.
     * Then reset its priority back to normal.
     */
    public void delivering() {
        this.setPriority(MAX_PRIORITY);
        msg("Truck is speeding up!");
        try {
            Thread.sleep((long) Math.random()*1000 + 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.setPriority(NORM_PRIORITY);
    }

    /**
     * Once arrived at the assembly line,
     * the trucks will signal to let the Assembly_Line knows that
     * it can proceed to unload the parts.
     * the truck will wait (simulated by semaphore) on the line
     * until the assembly line finish unloading.
     * Then it will decrease its num_delivery and sets part_in_the_truck to 0.
     * @throws InterruptedException
     */
    public void arrival() throws InterruptedException {
        msg("Arrived at the assembly line.");
        Main.truck_arrival.release();
        Main.truck_leave.acquire();
        this.parts_in_the_truck = 0;
        this.num_delivery -= 1;

    }

    /**
     * Once the delivery is accepted,
     * the truck decides to take a break and get gas.
     * (simulated by a yield() followed by a sleep of random time).
     * There are only two gas pumps available for trucks,
     * which are simulated by semaphore sem_gas_pumps.
     */
    public void take_break_and_get_gas(){
        Thread.yield();
        try {
            Main.sem_gas_pumps.acquire();
            Thread.sleep((long)Math.random()*1000);
            Main.sem_gas_pumps.release();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Before terminating, the truck waits for the Assembly_Line to calculate the surplus.
     * And then the trucks will terminate in sequence.
     * The order is enforced by semaphores.
     * @throws InterruptedException
     */
    public void terminating() throws InterruptedException {
        Main.calculating_surplus.acquire();

        if (Main.trucks.indexOf(this) == 0){
            leaving_message();
            Main.leave_first.release();
        }
        else if (Main.trucks.indexOf(this) == 1){
            Main.leave_first.acquire();
            leaving_message();
            Main.leave_second.release();
        }
        else if (Main.trucks.indexOf(this) == 2){
            Main.leave_second.acquire();
            leaving_message();
            Main.leave_last.release();
        }
        else {
            Main.leave_last.acquire();
            leaving_message();
        }
    }

    /**
     * Prints the leaving message of trucks by truck ID
     */
    public void leaving_message(){
        if (id == 0) {
            msg("Leaving with the surplus of " + this.surplus + " heads");
        }
        else if (id == 1){
            msg("Leaving with the surplus of " + this.surplus + " torsos");
        }
        else if (id == 2){
            msg("Leaving with the surplus of " + this.surplus + " tails");
        }
        else {
            msg("Leaving with the surplus of " + this.surplus + " legs");
        }
    }

    @Override
    public int compareTo(Truck o) {
        return this.surplus - o.surplus;
    }

    @Override
    public void run() {
        while (this.num_delivery > 0) {
            try {
                loading();
                delivering();
                arrival();
                take_break_and_get_gas();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            terminating();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
