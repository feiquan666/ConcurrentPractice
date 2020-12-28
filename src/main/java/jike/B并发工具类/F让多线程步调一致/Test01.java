package jike.B并发工具类.F让多线程步调一致;

/**
 * @Description: 测试类
 * @Date: 2020-12-10 13:59:44
 * @Author: 飞拳
 */
public class Test01 {

	public static void main(String[] args) throws InterruptedException {
		Thread t1 = new Thread(() -> {
			for (int i = 0; i < 10; i++) {
				System.out.println(Thread.currentThread().getName() + "：" + i);
			}
		});
		t1.start();
		Thread t2 = new Thread(() -> {
			for (int i = 10; i > 0; i--) {
				System.out.println(Thread.currentThread().getName() + "：" + i);
			}
		});
		t2.start();
		// 线程 join 的用处就是当前主线程会阻塞，知道被 join 线程执行结束才会执行后续代码
		t1.join();
		System.out.println(123);
		t2.join();
	}

}
