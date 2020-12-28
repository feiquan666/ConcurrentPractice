package example;

import com.alibaba.fastjson.JSON;
import example.mock.ArticleImgField;
import example.mock.ArticleMapper;
import example.mock.ContentSecurityFeign;
import example.mock.ContentSecurityReq;
import example.mock.ImageStruct;
import example.mock.MyLog;
import example.mock.RedisUtil;
import example.mock.StringUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

/**
 * @Description: 内容安全 job
 * @Date: 2020-12-15 14:52:00
 * @Author: 飞拳
 */
public class ContentSecurityJob {

	private final ArticleMapper articleMapper = new ArticleMapper();
	private final ContentSecurityFeign contentSecurityFeign = new ContentSecurityFeign();
	private final RedisUtil redisUtil = new RedisUtil();
	private final MyLog log = new MyLog();

	/**
	 * 起始 ID
	 */
	private volatile static Long START_ID = 0L;

	/**
	 * 截止 ID
	 */
	private volatile static Long END_ID = 0L;
	/**
	 * 历史帖子图片检测完成的帖子 IDS
	 */
	private final String HISTORY_POST_IMAGE_CHECK_SUCCESS_IDS = "HISTORY_POST_IMAGE_CHECK_SUCCESS_IDS";

	/**
	 * 历史帖子图片检测失败的帖子 IDS（feign 调用失败）
	 */
	private final String HISTORY_POST_IMAGE_CHECK_FAILED_IDS = "HISTORY_POST_IMAGE_CHECK_FAILED_IDS";


	/**
	 * 历史帖子图片检测更新数据库失败的帖子 IDS
	 */
	private final String HISTORY_POST_IMAGE_CHECK_UPDATED_FAILED_IDS = "HISTORY_POST_IMAGE_CHECK_UPDATED_FAILED_IDS";

	/**
	 * 历史帖子图片检测全部通过（不需要更新数据库）的帖子 IDS
	 */
	private final String HISTORY_POST_IMAGE_CHECK_ALL_PASS_IDS = "HISTORY_POST_IMAGE_CHECK_ALL_PASS_IDS";

	public String execute(ContentSecurityReq req) {
		log.info("运行参数：{}", req.toString());
		START_ID = req.getStartId();
		END_ID = req.getEndId();
		if (START_ID == null || START_ID < 0) {
			return "定时任务运行时失败！原因: START_ID 未指定或小于 0。参数值为：" + START_ID;
		}
		long sta = System.currentTimeMillis();
		checkImages(req);
		long end = System.currentTimeMillis();
		return "定时任务运行完成，耗时：" + (end - sta) / 1000 + " 秒";
	}

	/**
	 * 开始循环检测图片
	 */
	private void checkImages(ContentSecurityReq req) {
		long batch = req.getSelSize();
		while (START_ID >= END_ID) {
			// 起始ID
			long sId = Math.min(START_ID - batch, START_ID);
			List<ArticleImgField> articles = articleMapper.selectArticleByIdRange(sId, START_ID);
			if (CollectionUtils.isNotEmpty(articles)) {
				checkByAlYun(articles);
			}
			// 下一次循环的查询范围 截止ID
			START_ID = sId - 1;
		}
	}

	/**
	 * 筛出帖子中的图片并调用内容安全接口
	 */
	private void checkByAlYun(List<ArticleImgField> articles) {
		for (ArticleImgField article : articles) {
			List<String> urls = new ArrayList<>();
			// 封面
			String strCover = article.getCover();
			if (!StringUtil.isTrimEmpty(strCover)) {
				ImageStruct imageStruct = new ImageStruct(strCover);
				String coverUrl = imageStruct.getUrl();
				if (!StringUtil.isTrimEmpty(coverUrl)) {
					urls.add(coverUrl);
				}
			}
			// 文章帖图集
			String strConImages = article.getContentImages(), strImages = article.getImages();
			String finalStrImages = StringUtil.isTrimEmpty(strConImages)
					? StringUtil.isTrimEmpty(strImages) ? null : strImages : strConImages;
			if (!StringUtil.isTrimEmpty(finalStrImages)) {
				List<ImageStruct> imageStructs = JSON.parseArray(finalStrImages, ImageStruct.class);
				if (CollectionUtils.isNotEmpty(imageStructs)) {
					List<String> imgUrls = imageStructs.stream().map(ImageStruct::getUrl)
							.collect(Collectors.toList());
					urls.addAll(imgUrls);
				}
			}
			if (CollectionUtils.isNotEmpty(urls)) {
				callAlYun(urls, article);
			} else {
				log.info("该帖子没有一张图片，无需检测：{}", article.getId());
			}
		}
	}

	/**
	 * 构造阿里云请求参数，根据检测结果更新帖子
	 */
	private void callAlYun(List<String> urls, ArticleImgField article) {
		Long id = article.getId();
		Boolean allPass = contentSecurityFeign.historyImagesCheck(urls);
		if (Objects.nonNull(allPass)) {
			if (allPass) {
				log.info("调用 feign 检测全部通过：{}", article.toString());
				redisUtil.sadd(HISTORY_POST_IMAGE_CHECK_ALL_PASS_IDS, String.valueOf(id));
				return;
			}
			updatePost(article);
			return;
		}
		log.info("调用 feign 检测失败：{}", article.toString());
		redisUtil.sadd(HISTORY_POST_IMAGE_CHECK_FAILED_IDS, String.valueOf(id));
	}

	/**
	 * 更新帖子表
	 */
	private void updatePost(ArticleImgField article) {
		String strCover = article.getCover(),
				content = article.getContent(),
				strConImages = article.getContentImages(),
				strImages = article.getImages();
		strCover = StringUtil.isTrimEmpty(strCover) ? strCover : replaceHost(strCover);
		strConImages = StringUtil.isTrimEmpty(strConImages) ? strConImages : replaceHost(strConImages);
		strImages = StringUtil.isTrimEmpty(strImages) ? strImages : replaceHost(strImages);
		Integer art = 4;
		if (art.equals(article.getType())) {
			content = StringUtil.isTrimEmpty(content) ? content : replaceHost(content);
			article.setContent(content);
		}
		article.setCover(strCover);
		article.setContentImages(strConImages);
		article.setImages(strImages);
		int res = articleMapper.updateArticleImgFields(article);
		if (res > 0) {
			log.info("调用 update 更新数据库成功：{}", article.toString());
			redisUtil.sadd(HISTORY_POST_IMAGE_CHECK_SUCCESS_IDS, String.valueOf(article.getId()));
			return;
		}
		log.info("调用 update 更新数据库失败：{}", article.toString());
		redisUtil.sadd(HISTORY_POST_IMAGE_CHECK_UPDATED_FAILED_IDS, String.valueOf(article.getId()));
	}

	/**
	 * 替换为 OSS 域名
	 */
	private String replaceHost(String strCover) {
		return strCover.replace("img.visvachina.com", "pic.visvachina.com");
	}
}
