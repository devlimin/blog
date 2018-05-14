package com.limin.blog.service;

import com.github.pagehelper.PageInfo;
import com.limin.blog.model.Article;
import com.limin.blog.model.ForumTopic;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class SearchService {
    @Autowired
    private SolrClient solrClient;

    private static final String ARTICLE_ID_FIELD = "article_id";
    private static final String ARTICLE_TITLE_FIELD = "article_title";
    private static final String ARTICLE_CONTENT_FIELD = "article_content";
    private static final String ARTICLE_RELEASE_DATE_FIELD = "article_release_date";

    private static final String TOPIC_ID_FIELD = "topic_id";
    private static final String TOPIC_TITLE_FIELD = "topic_title";
    private static final String TOPIC_CONTENT_FIELD = "topic_content";
    private static final String TOPIC_RELEASE_DATE_FIELD = "topic_release_date";
    private static final String TOPIC_USER_ID_FIELD ="topic_user_id";
    private static final String TOPIC_USER_NAME_FIELD ="topic_user_name";
    private static final String TOPIC_READ_NUM_FIELD = "topic_read_num";
    private static final String TOPIC_COMMENT_NUM_FIELD = "topic_comment_num";

    public PageInfo<Article> searchArticle(String keyword,Integer pageNum,Integer pageSize) {
        try {
            SolrQuery query = new SolrQuery(ARTICLE_TITLE_FIELD+":"+keyword+" and "+ARTICLE_CONTENT_FIELD+":"+keyword);
            query.addSort(ARTICLE_RELEASE_DATE_FIELD,SolrQuery.ORDER.desc);
            query.setStart((pageNum-1)*pageSize);
            query.setRows(pageSize-1);

            query.setHighlight(true);
            query.addHighlightField(ARTICLE_TITLE_FIELD);// 高亮字段
            query.addHighlightField(ARTICLE_CONTENT_FIELD);// 高亮字段
            query.setHighlightSimplePre("<span style='color:red'>");
            query.setHighlightSimplePost("</span>");
            query.setHighlight(true).setHighlightSnippets(1);
            query.setHighlightFragsize(200);

            List<Article> articles = new ArrayList<>();
            QueryResponse response = solrClient.query(query);
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
            SolrDocumentList docs = response.getResults();
            Iterator<SolrDocument> iterator = docs.iterator();
            while (iterator.hasNext()){
                SolrDocument doc = iterator.next();
                String idStr = doc.getFieldValue("id").toString();
                Integer id = Integer.parseInt(doc.getFieldValue(ARTICLE_ID_FIELD).toString());
                String title = doc.getFieldValue(ARTICLE_TITLE_FIELD).toString();
                String content = doc.getFieldValue(ARTICLE_CONTENT_FIELD).toString();
                Date releaseDate = (Date)doc.getFieldValue(ARTICLE_RELEASE_DATE_FIELD);
                Article article = new Article();
                article.setId(id);
                article.setTitle(title);
                article.setContent(content);
                article.setReleaseDate(releaseDate);
                List<String> titles = highlighting.get(idStr).get(ARTICLE_TITLE_FIELD);
                List<String> contents = highlighting.get(idStr).get(ARTICLE_CONTENT_FIELD);
                if (titles!=null && titles.size()> 0) {
                    article.setTitle(titles.get(0));
                }
                if (contents!=null && contents.size()> 0) {
                    article.setContent(contents.get(0));
                }
                String html = Jsoup.clean(article.getContent(), new Whitelist().addAttributes("span","style"));
                article.setContent(StringUtils.substring(html, 0, 200));

                articles.add(article);
            }
            long numFound = docs.getNumFound();
            PageInfo pageInfo = new PageInfo();
            pageInfo.setList(articles);
            pageInfo.setTotal(numFound);
            return pageInfo;
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public PageInfo<ForumTopic> searchTopic(String keyword,Integer pageNum,Integer pageSize) {
        try {
            SolrQuery query = new SolrQuery(TOPIC_TITLE_FIELD+":"+keyword+" and "+TOPIC_CONTENT_FIELD+":"+keyword);
            query.addSort(TOPIC_RELEASE_DATE_FIELD,SolrQuery.ORDER.desc);
            query.setStart((pageNum-1)*pageSize);
            query.setRows(pageSize-1);

            query.setHighlight(true);
            query.addHighlightField(TOPIC_TITLE_FIELD);// 高亮字段
            query.addHighlightField(TOPIC_CONTENT_FIELD);// 高亮字段
            query.setHighlightSimplePre("<span style='color:red'>");
            query.setHighlightSimplePost("</span>");
            query.setHighlight(true).setHighlightSnippets(1);
            query.setHighlightFragsize(200);

            List<ForumTopic> topics = new ArrayList<>();
            QueryResponse response = solrClient.query(query);
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
            SolrDocumentList docs = response.getResults();
            Iterator<SolrDocument> iterator = docs.iterator();
            while (iterator.hasNext()){
                SolrDocument doc = iterator.next();
                String idStr = doc.getFieldValue("id").toString();
                Integer id = Integer.parseInt(doc.getFieldValue(TOPIC_ID_FIELD).toString());
                String title = doc.getFieldValue(TOPIC_TITLE_FIELD).toString();
                String content = doc.getFieldValue(TOPIC_CONTENT_FIELD).toString();
                Date releaseDate = (Date)doc.getFieldValue(TOPIC_RELEASE_DATE_FIELD);
                Integer userId = Integer.parseInt(doc.getFieldValue(TOPIC_USER_ID_FIELD).toString());
                String userName = doc.getFieldValue(TOPIC_USER_NAME_FIELD).toString();
                Integer readNum = Integer.parseInt(doc.getFieldValue(TOPIC_READ_NUM_FIELD).toString());
                Integer commentNum = Integer.parseInt(doc.getFieldValue(TOPIC_COMMENT_NUM_FIELD).toString());
                ForumTopic topic = new ForumTopic();
                topic.setId(id);
                topic.setTitle(title);
                topic.setContent(content);
                topic.setReleaseDate(releaseDate);
                topic.setUserId(userId);
                topic.setUserName(userName);
                topic.setReadNum(readNum);
                topic.setCommentNum(commentNum);
                List<String> titles = highlighting.get(idStr).get(TOPIC_TITLE_FIELD);
                List<String> contents = highlighting.get(idStr).get(TOPIC_CONTENT_FIELD);
                if (titles!=null && titles.size()> 0) {
                    topic.setTitle(titles.get(0));
                }
                if (contents!=null && contents.size()> 0) {
                    topic.setContent(contents.get(0));
                }
                String html = Jsoup.clean(topic.getContent(), new Whitelist().addAttributes("span","style"));
                topic.setContent(StringUtils.substring(html, 0, 200));

                topics.add(topic);
            }
            long numFound = docs.getNumFound();
            PageInfo pageInfo = new PageInfo();
            pageInfo.setList(topics);
            pageInfo.setTotal(numFound);
            return pageInfo;
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean indexArticle(Article article) {
        try{
            SolrInputDocument doc =  new SolrInputDocument();
            doc.setField(ARTICLE_ID_FIELD, article.getId());
            doc.setField(ARTICLE_TITLE_FIELD, article.getTitle());
            doc.setField(ARTICLE_CONTENT_FIELD, article.getContent());
            doc.setField(ARTICLE_RELEASE_DATE_FIELD,article.getReleaseDate());
            UpdateResponse response = solrClient.add(doc, 1000);
            return response != null && response.getStatus() == 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean indexTopic(ForumTopic topic) {
        try{
            SolrInputDocument doc =  new SolrInputDocument();
            doc.setField(TOPIC_ID_FIELD, topic.getId());
            doc.setField(TOPIC_TITLE_FIELD, topic.getTitle());
            doc.setField(TOPIC_CONTENT_FIELD, topic.getContent());
            doc.setField(TOPIC_RELEASE_DATE_FIELD,topic.getReleaseDate());
            doc.setField(TOPIC_USER_ID_FIELD,topic.getUserId());
            doc.setField(TOPIC_USER_NAME_FIELD,topic.getUserName());
            doc.setField(TOPIC_READ_NUM_FIELD,topic.getReadNum());
            doc.setField(TOPIC_COMMENT_NUM_FIELD,topic.getCommentNum());
            UpdateResponse response = solrClient.add(doc, 1000);
            return response != null && response.getStatus() == 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
