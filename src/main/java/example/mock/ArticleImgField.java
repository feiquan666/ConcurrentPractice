package example.mock;

import lombok.Data;

/**
 * @Description: 文章含图片字段集
 * @Date: 2020-12-15 15:26:33
 * @Author: 飞拳
 */
@Data
public class ArticleImgField {

	/**
	 * 主键 ID
	 */
	private Long id;

	/**
	 * 对象 ID
	 */
	private String objectId;

	/**
	 * 文章内容（文章帖富文本）
	 */
	private String content;

	/**
	 * 文章帖图片集
	 */
	private String contentImages;

	/**
	 * 图文帖图片集
	 */
	private String images;

	/**
	 * 文章封面
	 */
	private String cover;

	/**
	 * 帖子类型： 1 图文 2 链接 3 提问 4 文章 5 投票帖
	 */
	private Integer type;
}
