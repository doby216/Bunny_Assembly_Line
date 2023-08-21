/**
 * @author Yan Chen
 * @class CSCI-340
 * @lastModified May 12th, 2023
 */

import java.util.ArrayList;
import java.util.concurrent.*;

public class Main {
    /** Semaphore used to synchronize the shared variable legs*/
    static Semaphore mutex_leg = new Semaphore(1, Boolean.TRUE);

    /** a shared counter used to count the number of legs produced*/
    static int legs = 0;

    /** Semaphore used to synchronize the shared variable torsos*/
    static Semaphore mutex_torso = new Semaphore(1, Boolean.TRUE);

    /** a shared counter used to count the number of torsos produced*/
    static int torsos = 0;

    /** Semaphore used to synchronize the shared variable heads*/
    static Semaphore mutex_head = new Semaphore(1, Boolean.TRUE);

    /** a shared counter used to count the number of heads produced*/
    static int heads = 0;

    /** Semaphore used to synchronize the shared variable tails*/
    static Semaphore mutex_tail = new Semaphore(1, Boolean.TRUE);

    /** a shared counter used to count the number of tails produced*/
    static int tails = 0;

    /** record the starting time of the program */
    public static long time;

    /** number of manufacturers */
    static final int nManufacturer = 4;

    /** number of trucks */
    static final int nTruck  = 4;

    /** number of gas pumps, the default value is 2 */
    static final int numGasPumps = 2;

    /** the assembly line object */
    static Assembly_Line assembly_line = new Assembly_Line(0);

    /** an arraylist of manufacturer objects */
    static ArrayList<Thread> manufacturers = new ArrayList<>(nManufacturer);

    /** an arraylist of truck objects */
    static ArrayList<Truck> trucks = new ArrayList<>(nTruck);

    /**
     * semaphore that is used to control the leaving of the truck.
     * The Truck threads wait on this semaphore if the unloading process is not done.
     * The Assembly_Line signals this semaphore if the unloading process is done.
     * */
    static Semaphore truck_leave = new Semaphore(0,Boolean.TRUE);

    /**
     * Semaphore that is used to control the unloading process
     * The Assembly_Line waits on this semaphore if the truck has not yet arrived.
     * The truck signals the semaphore if the truck has arrived and ready to be unloaded.
     * */
    static Semaphore truck_arrival = new Semaphore(0,Boolean.TRUE);

    /**
     * Semaphore that is used to control the gas pumps.
     * The truck thread waits on this semaphore if there is no enough gas pump.
     * The truck thread signals the semaphore when it's done fueling up.
     */
    static Semaphore sem_gas_pumps = new Semaphore(numGasPumps, Boolean.TRUE);

    /**
     * Semaphore that is used to control the process of calculating surplus.
     * The truck thread waits on this semaphore before the Assembly_line calculates
     * the surplus of each truck. And the Assembly_Line signals the semaphore
     * after it prints the invoice
     */
    static Semaphore calculating_surplus = new Semaphore(0, Boolean.TRUE);

    /**
     * Semaphores which are used to control the leaving sequence of the trucks
     * The first truck to leave signals the leave_first.
     * The second truck to leave waits on the leave_first and signals the leave_second.
     * The third truck to leave waits on the leave_second and signals the leave_last.
     * And the last truck to leave waist on the leave_last semaphore.
     */
    static Semaphore leave_first = new Semaphore(0, Boolean.TRUE);
    static Semaphore leave_second = new Semaphore(0, Boolean.TRUE);
    static Semaphore leave_last = new Semaphore(0, Boolean.TRUE);

    /**
     * The main method which initializes and starts the threads.
     * @param args
     */
    public static void main(String[] args) {

        int num_delivery = 3;

        time = System.currentTimeMillis();

        for (int i = 0; i < nTruck; i++){
            trucks.add(new Truck(i, num_delivery));
            trucks.get(i).start();
        }

        for (int i = 0; i < nManufacturer; i++){
            manufacturers.add(new Manufacturer(i));
            manufacturers.get(i).start();
        }

        assembly_line.start();
    }
}