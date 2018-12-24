package com.baizhi;

import com.baizhi.entity.Poetries;
import com.baizhi.service.PoetriesService;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {
@Autowired
private PoetriesService poetriesService;
    @Test
    public void test1() {
        List<Poetries> findall = poetriesService.findall();
        for (Poetries poetries : findall) {
            System.out.println(poetries);
        }
    }
    @Test
    public void test2() throws IOException {

        FSDirectory fsDirectory = FSDirectory.open(Paths.get("E:\\TEST\\03"));
        IndexWriter indexWriter = new IndexWriter(fsDirectory, new IndexWriterConfig(new IKAnalyzer()));
        List<Poetries> findall = poetriesService.findall();
        Document document=null;
        for (Poetries poetries : findall) {
            document=new Document();
            document.add(new IntField("id",poetries.getId(), Field.Store.YES));
            document.add(new StringField("author",poetries.getPoetId().getName(), Field.Store.YES));
            document.add(new TextField("title",poetries.getTitle(), Field.Store.YES));
            document.add(new TextField("content",poetries.getContent(), Field.Store.YES));
            indexWriter.addDocument(document);
        }
        indexWriter.commit();
        indexWriter.close();
    }
    @Test
    public void test3() throws IOException, ParseException, InvalidTokenOffsetsException {
        FSDirectory fsDirectory = FSDirectory.open(Paths.get("E:\\TEST\\03"));
        IndexReader indexReader = DirectoryReader.open(fsDirectory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //查询解析对象 作用 解析查询表达式 域名:条件
        //参数一：域名(默认域)
        QueryParser queryParser = new QueryParser("", new IKAnalyzer());
        Query query=null;
        query = queryParser.parse("content:床");
        query = queryParser.parse("author:李白");
       // query = queryParser.parse("author:武则天");
      //  TopDocs topDocs = indexSearcher.search(query, 10);
        //#############################################
        //声明请求参数信息
        int nowPage=9;//当前页数
        int pageSize=2;//每页显示条数
        //分页数据
        //参数一：当前页的上一页的文档的SoureDoc对象
        //参数二：查询条件
        //参数三：保留多少条结果
        TopDocs topDocs=null;
        if(nowPage==1){
            topDocs = indexSearcher.search(query, pageSize);
        }else if(nowPage > 1){
            topDocs = indexSearcher.search(query, (nowPage - 1) * pageSize);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            ScoreDoc sd = scoreDocs[scoreDocs.length - 1];
            topDocs = indexSearcher.searchAfter(sd, query, pageSize);
        }
        //总记录条数
        int count = topDocs.totalHits;
        //##############################################
        //创建高亮器对象
//        QueryScorer scorer = new QueryScorer(query);//分数器(对文档进行打分)
//        Highlighter highlighter = new Highlighter(scorer);

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;


        for (ScoreDoc scoreDoc : scoreDocs) {
            int docID = scoreDoc.doc;
            Document document = indexReader.document(docID);

            System.out.println(
                    scoreDoc.score + " |" +
                            document.get("id") + " |" +
                            document.get("title") + " |" +
                            //highlighter.getBestFragment(new StandardAnalyzer(), "content", document.get("content")) + " |" +
                            document.get("content")+ " | " +
                            document.get("author")
            );
            //获取高亮的最佳片段
//            String bestFragment = highlighter.getBestFragment(new StandardAnalyzer(), "content", document.get("content"));
//            System.out.println(bestFragment);
        }
        indexReader.close();
    }

}

