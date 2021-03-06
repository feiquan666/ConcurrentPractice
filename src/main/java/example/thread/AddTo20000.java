package example.thread;

/**
 * @Description: 加到 20000
 * @Date: 2020-12-28 14:57:07
 * @Author: 飞拳
 */
public class AddTo20000 {

	private static long NUM = 0;

	private void add10k() {
		int idx = 0;
		while (idx++ < 10000) {
			NUM += 1;
		}
	}

	public static void main(String[] args) throws InterruptedException {
		System.out.println(cal());
	}

	private static long cal() throws InterruptedException {
		final AddTo20000 app = new AddTo20000();
		Thread t1 = new Thread(app::add10k);
		Thread t2 = new Thread(app::add10k);
		t1.start();
		t2.start();
		t1.join();
		t2.join();
		return NUM;
	}
}
