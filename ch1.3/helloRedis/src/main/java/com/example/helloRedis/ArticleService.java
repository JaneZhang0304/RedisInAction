package com.example.helloRedis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;

/*
 * 构建一个文章投票的网站，如果一篇文章获得了至少200票，那么网站就认为这篇文章是一篇有趣的文章；假如这个网站
 * 每天发布1000篇文章，而其中的50篇符合对有趣文章的要求，那么网站要把这50篇文章放到文章列表前100位至少一天
 * 评分随着时间流逝而不断减少，程序根据文章的发布时间和当前时间来计算评分：Unix时间秒数+票数*432
 *
 * */
@Service
public class ArticleService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;//除了article:表之外都用stringRedisTemplate

    @PostConstruct
    public void init() {
        Boolean exist = stringRedisTemplate.hasKey("article:");
        if (exist != null && !exist) {
            stringRedisTemplate.opsForValue().set("article:", "1");
        }
    }

    private static final int ONE_WEEK_IN_SECONDS = 7 * 86400;//一周的秒数
    private static final int VOTE_SCORE = 432;//86400/200（200票为有趣的文章
    private static final int ARTICLES_PER_PAGE = 25;//每页显示25篇

    /**
     * 1.3.1 1-6对文章进行投票
     *
     * @param user    样式为user:456
     * @param article 样式为article:123, user
     */
    public void articleVote(String user, String article) {
        double cutOff = System.currentTimeMillis() / 1000.0d - ONE_WEEK_IN_SECONDS;
        Double score = stringRedisTemplate.opsForZSet().score("time:", article);
        //发布超过7天不能投票
        if (score != null && cutOff > score) {
            return;
        }
        String[] split = article.split(":");
        String articleId = split[split.length - 1];
        Boolean contains = stringRedisTemplate.opsForSet().isMember("voted:" + articleId, user);
        if (contains == null || !contains) {
            //用户没有投过该文章
            stringRedisTemplate.opsForSet().add("voted:" + articleId, user);//已投该文章的用户的set
            stringRedisTemplate.opsForZSet().incrementScore("score:", article, VOTE_SCORE);//分数zSet
            redisTemplate.opsForHash().increment(article, "votes", 1);//article信息的hash
        }
    }

    /**
     * 1.3.2 1-7发布并获取文章
     *
     * @param user
     * @param title
     * @param link
     */
    public Long postArticle(String user, String title, String link) {
        Long articleId = stringRedisTemplate.opsForValue().increment("article:");
        String voted = "voted:" + articleId;//创建该文章的voted表格， 存放已投用户
        stringRedisTemplate.opsForSet().add(voted, user);
        stringRedisTemplate.expire(voted, ONE_WEEK_IN_SECONDS, TimeUnit.SECONDS);//一周以后过期，因为就不让投票了
        String article = "article:" + articleId;
        double now = System.currentTimeMillis() / 1000.0d;
        HashMap<String, Object> info = new HashMap<>();//文章的详细信息
        info.put("title", title);
        info.put("link", link);
        info.put("poster", user);
        info.put("time", now);
        info.put("votes", 1);
        redisTemplate.opsForHash().putAll(article, info);
        stringRedisTemplate.opsForZSet().add("score:", article, now + VOTE_SCORE);//score:保存所有文章的评分
        stringRedisTemplate.opsForZSet().add("time:", article, now);//time:保存所有文章的发布时间
        return articleId;
    }

    /**
     * 1.3.2 1-8 获取文章，通过分数或者发布时间
     *
     * @param page  页数
     * @param order score:或者 time:
     */
    public List<Map<Object, Object>> getArticles(int page, String order) {
        int start = (page - 1) * ARTICLES_PER_PAGE;
        int end = start + ARTICLES_PER_PAGE;
        //从order(order:或time:)表中获取article的列表
        Set<String> ids = stringRedisTemplate.opsForZSet().reverseRange(order, start, end);//从大到小排列
        List<Map<Object, Object>> articles = new ArrayList<>();
        if (ids != null) {
            for (String id : ids) {
                Map<Object, Object> articleData = redisTemplate.opsForHash().entries(id);
                articleData.put("id", id);
                articles.add(articleData);
            }
        }
        return articles;
    }
    public List<Map<Object,Object>> getArticles(int page){
        return getArticles(page,"score:");
    }
    /**
     * 1.3.3 1-9 对文章进行分组
     *
     * @param articleId 文章Id,不包含article:前缀
     * @param toAdd     要加入的分组Id的列表
     * @param toRemove  要移出的分组Id的列表
     */
    public void addRemoveGroups(String articleId, List<String> toAdd, List<String> toRemove) {
        String article = "article:" + articleId;
        for (String groupId : toAdd) {
            stringRedisTemplate.opsForSet().add("group:"+groupId,article);
        }
        for(String groupId: toRemove){
            stringRedisTemplate.opsForSet().remove("groups:"+groupId,article);
        }
    }

    /**
     * 1.3.3 1-10 从群组里面获取一整页文章
     * @param group groupName
     * @param page
     * @param order
     * @return
     */
    public List<Map<Object,Object>> getGroupArticles(String group, int page, String order){
        String key = order+group; //score:123,该组的分数或者其他
        Boolean hasKey = stringRedisTemplate.hasKey(key);
        if(hasKey==null || !hasKey){
            //获取group表和score或者time表的交集，即为该组文章的score或time表
            stringRedisTemplate.opsForZSet().intersectAndStore("group:"+group,Collections.singletonList(order),key, RedisZSetCommands.Aggregate.MAX);
            //60秒后删除
            stringRedisTemplate.expire(key,60,TimeUnit.SECONDS);
        }
        return getArticles(page,key);
    }
    public List<Map<Object,Object>> getGroupArticles(String group, int page){
        return getGroupArticles(group,page,"score:");
    }

}
