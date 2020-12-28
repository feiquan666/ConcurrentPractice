package jike.A并发理论基础.A可见性和原子性和有序性;

import lombok.Data;

/**
 * @Description:
 * @Date: 2020-12-17 17:59:38
 * @Author: 飞拳
 */
@Data
public class Singleton {
	private static Singleton instance;

	public static Singleton getInstance(){
		// 实例未初始化（检查进入方法前是否被实例化）
		if (instance == null) {
			// 加锁
			synchronized(Singleton.class) {
				// 实例未初始化（检查加锁前是否被实例化）
				if (instance == null) {
					instance = new Singleton();
				}
			}
		}
		return instance;
	}
}
