import java.util.Random;

public class DiningPhilosopher implements Runnable {
	static int[] chopsticks;

	public static int[] getChopsticks() {
		return chopsticks;
	}

	public static void setChopsticks(int[] chopsticks) {
		DiningPhilosopher.chopsticks = chopsticks;
	}

	private static Object gateKeeper = new Object();
	private static Random rand = new Random();

	private int cL;
	private int cR;
	private int philNum;

	private DiningPhilosopher(int philosopherNum, int chopL, int chopR) {
		philNum = philosopherNum;
		cL = chopL;
		cR = chopR;
	}

	public static void main(String[] args) {
		if (args.length != 1){
			System.out.println("Usage: java DiningPhilosopher [int]");
			System.exit(1);
		}
		int numOfPhil = 0;
		try {
	        numOfPhil = Integer.parseInt(args[0]);
	    } catch (NumberFormatException e) {
	        System.err.println("Argument " + args[0] + " must be an integer.");
	        System.exit(1);
	    }
		for (String s: args) {
            System.out.println("creating " + s + " philosophers and chopsticks");
        }
		if (numOfPhil == 0){
			System.out.println("There are no philosophers or chopsticks!\nexiting...");
			System.exit(0);
		}
		final int[] chopsticks = new int[numOfPhil];
		System.out.print("chopsticks: [");
		for (int i = 0; i < numOfPhil; i++){
			chopsticks[i] = 0;
			System.out.print(chopsticks[i] + " ");
		}
		System.out.println("]");
		setChopsticks(chopsticks);
		for (int i = 0; i < numOfPhil; i++){
			if (i == numOfPhil - 1){
				(new Thread(new DiningPhilosopher(i + 1, i, 0))).start();
			} else {
			(new Thread(new DiningPhilosopher(i + 1, i, i + 1))).start();
			}
		}
		
	}

	public void run() {
		while (true) {
			int toDo = rand.nextInt(2);
			if (toDo == 0) {
				synchronized (gateKeeper) {
					while (pickupChopsticks() == 0) {
						try {
							gateKeeper.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				eat();
			} else {
				think();
			}
		}
	}

	private void eat() {
		System.out.println(this.philNum + " is eating");
		try {
			Thread.sleep(rand.nextInt(4000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(this.philNum + " ate");

		dumpChopsticks();
	}

	private int pickupChopsticks() {
		//System.out.println(this.philNum + " is picking up chopsticks");
		if ((chopsticks[this.cL] == 0) && (chopsticks[this.cR] == 0)) {
			chopsticks[this.cL] = 1;
			chopsticks[this.cR] = 1;
			System.out.println(this.philNum + " got the chopsticks");
			return 1;
		}
		//System.out.println(this.philNum + " did not pick up chopsticks");
		return 0;
	}

	private void think() {
		System.out.println(this.philNum + " is thinking");
		try {
			Thread.sleep(rand.nextInt(2000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void dumpChopsticks() {
		synchronized (gateKeeper) {
			chopsticks[this.cL] = 0;
			chopsticks[this.cR] = 0;
			gateKeeper.notifyAll();
		}
	}
}
