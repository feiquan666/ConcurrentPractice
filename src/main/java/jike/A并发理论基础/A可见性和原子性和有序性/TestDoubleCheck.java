package jike.A并发理论基础.A可见性和原子性和有序性;

public class TestDoubleCheck {

	public static void main(String[] args) {

		Thread t1 = new Thread(() -> System.out.println(Singleton.getInstance()));
		Thread t2 = new Thread(() -> System.out.println(Singleton.getInstance()));
		Thread t3 = new Thread(() -> System.out.println(Singleton.getInstance()));
		Thread t4 = new Thread(() -> System.out.println(Singleton.getInstance()));
		Thread t5 = new Thread(() -> System.out.println(Singleton.getInstance()));
		Thread t6 = new Thread(() -> System.out.println(Singleton.getInstance()));
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
		t6.start();
	}
}
