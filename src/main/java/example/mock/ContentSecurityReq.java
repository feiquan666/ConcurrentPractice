package example.mock;

import lombok.Data;

@Data
public class ContentSecurityReq {

	/**
	 * 起始帖子 ID
	 */
	private Long startId = 0L;

	/**
	 * 截止帖子 ID
	 */
	private Long endId = 0L;

	/**
	 * 每次查询的最大条数
	 */
	private Long selSize = 5000L;
}
