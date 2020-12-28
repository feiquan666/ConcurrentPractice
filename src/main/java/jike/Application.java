package jike;

import java.util.concurrent.locks.StampedLock;

public class Application {

	private  static long NUM = 0;

	private void add10k() {
		int idx = 0;
		while (idx++ < 10000) {
			NUM++;
		}
	}

	public static void main(String[] args) throws InterruptedException {
		System.out.println(cal());
	}

	private static long cal() throws InterruptedException {
		StampedLock stampedLock = new StampedLock();
		stampedLock.readLockInterruptibly();
		final Application app = new Application();
		Thread t1 = new Thread(app::add10k);
		Thread t2 = new Thread(app::add10k);
		t1.start();
		t2.start();
		t1.join();
		t2.join();
		return NUM;
	}
}
