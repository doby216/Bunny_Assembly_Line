# Bunny_Assembly_Line
## Description
This is a school project for Operating System Principles.

This project is the simulation of a bunny assembly factory using Java Multithreading.

A bunny assembly line assembles bunnies’ parts that are supplied by part-manufacturers. The
possible parts are legs, torsos, heads and tails. A bunny is completed when it has 4 legs, 1
torso, 1 head and 1 tail. Each manufacturer produces only one type of part. There are four
manufacturers.

Use sleep of a random interval of time to simulate production. During this time a random
number of parts have been produced (for heads, tails, torsos consider a random number
between 70 and 100, for legs consider a random number between 250 and 500).

Next, the manufacturer uses a truck to deliver the parts to the assembly line. Loading the truck
will take a brief time (simulated by sleep of random time). The truck waits during the loading
time. Once loaded the truck heads to the assembly line.
Once it has arrived at the assembly line, the truck (commute should be simulated by a sleep of
random time) will wait until the assembly line will be ready to process its delivery.
The assembly line will signal the delivery trucks.

**Note: FCFS does NOT NEED to be enforced.**

Once the delivery is accepted (trucks must wait for the delivery to be accepted), the truck will
take a break and get gas. There are numGasPumps so the truck will wait until a pump is
available. Next it goes back to its manufacturer (simulated by sleep of random time) for a new
load.

Each truck will do num_deliveries. After each truck does all its deliveries, for the day, it will
wait for a report from the assembly line. Once all the deliveries are done, the assembly line will
issue an invoice with the total of complete bunnies assembled in that day and also the surplus
of parts.

The truck that delivered the largest surplus will be signaled to leave first. The other trucks will
wait and terminate in sequence. The order of sequence is, from the truck with the highest
surplus to the one with the lowest surplus. (use operations on semaphores to achieve a
proper sequence).

After loading the last production, the manufacturer will wait and terminate as soon as its truck is
done for the day.

After all manufacturers are closed for the day, the assembly line closes (terminates) too. (last
manufacturer to close will signal the assemblyLine).

### Default values: 
1. numDeliveries = 3
2. numGasPumps = 2
