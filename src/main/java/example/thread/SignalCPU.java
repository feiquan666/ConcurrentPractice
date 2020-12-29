package example.thread;

public class SignalCPU {

	synchronized public static void printString(){
		System.out.println("begin");
		while (true) {
			System.out.println("a线程永远suspend了！");
			Thread.currentThread().suspend();
		}
	}

	public static void main(String[] args) {
		printString();
	}
}
