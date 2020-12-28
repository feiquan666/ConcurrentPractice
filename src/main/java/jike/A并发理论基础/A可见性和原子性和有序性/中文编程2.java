package jike.A并发理论基础.A可见性和原子性和有序性;

public class 中文编程2 {
	private String 不可变量;

	public 中文编程2(String 不可变量) {
		this.不可变量 = 不可变量;
	}

	public static void main(String[] args) {
		for (int 循环的次序 = 0, 循环的总次数 = 10; 循环的次序 <= 循环的总次数; 循环的次序++) {
			打印测试方法的返回值(测试方法(new 中文编程2("中文编程对象——> 不可变量：" + 循环的次序)));
		}
	}

	private static String 测试方法(中文编程2 中文编程) {
		return "输出内容：" + 中文编程.不可变量;
	}

	private static void 打印测试方法的返回值 (String 测试方法的返回值) {
		System.out.println(测试方法的返回值);
	}
}
