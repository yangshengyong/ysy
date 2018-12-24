package com.baizhi.controller;

import com.baizhi.entity.Poetries;
import com.baizhi.entity.Poets;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.wltea.analyzer.lucene.IKAnalyzer;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

@Controller
@RequestMapping("/ts")
public class TsSearchController {
    @RequestMapping("/search")
    public String search(String type, String name, HttpSession session, Integer nowPage) throws IOException, ParseException, InvalidTokenOffsetsException {

        FSDirectory fsDirectory = FSDirectory.open(Paths.get("E:\\TEST\\03"));
        IndexReader indexReader = DirectoryReader.open(fsDirectory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //查询解析对象 作用 解析查询表达式 域名:条件
        //参数一：域名(默认域)
        QueryParser queryParser = new QueryParser(type, new IKAnalyzer());
        Query query = null;

        try {
            query = queryParser.parse(name + "*");
        } catch (ParseException e) {
            e.printStackTrace();
            return "redirect:/wrong.jsp";
        }

        //#############################################
        //声明请求参数信息
        if (nowPage == null) {
            nowPage = 1;//当前页数
        }
        int pageSize = 10;//每页显示条数
        //分页数据
        //参数一：当前页的上一页的文档的SoureDoc对象
        //参数二：查询条件
        //参数三：保留多少条结果
        TopDocs topDocs = null;

        topDocs = null;
        if (nowPage == 1) {
            topDocs = indexSearcher.search(query, pageSize);
        } else if (nowPage > 1) {
            topDocs = indexSearcher.search(query, (nowPage - 1) * pageSize);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;

            ScoreDoc sd = null;
            try {
                sd = scoreDocs[scoreDocs.length - 1];
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/wrong.jsp";
            }

            topDocs = indexSearcher.searchAfter(sd, query, pageSize);
        }

        //总记录条数
        int count = topDocs.totalHits;
        session.setAttribute("count", count);
        //求出总页数
        Integer pages = (count % pageSize == 0) ? count / pageSize : count / pageSize + 1;
        session.setAttribute("pages", pages);
        //当前页数
        session.setAttribute("nowPage", nowPage);
        //查询内容
        session.setAttribute("tsname", name);
        //查询各式
        session.setAttribute("tstype", type);
        //##############################################
        //创建高亮器对象
        QueryScorer scorer = new QueryScorer(query);//分数器(对文档进行打分)
        // 默认高亮样式 加粗
        // 使用自定义的高亮样式
        Formatter formatter = new SimpleHTMLFormatter("<span style=\"color:red\">", "</span>");
        Highlighter highlighter = new Highlighter(formatter, scorer);


        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        ArrayList<Poetries> poetries = new ArrayList<>();

        try {
            for (ScoreDoc scoreDoc : scoreDocs) {

                Poetries poetries1 = new Poetries();
                Poets poets = new Poets();
                int docID = scoreDoc.doc;
                Document document = indexReader.document(docID);

                // String bestFragment = highlighter.getBestFragment(new StandardAnalyzer(), "title", document.get("title"));

                poetries1.setTitle(document.get("title"));

                poets.setName(document.get("author"));

                poetries1.setPoetId(poets);

                String highlighterBestFragment = highlighter.getBestFragment(new StandardAnalyzer(), "content", document.get("content"));
                poetries1.setContent(highlighterBestFragment);
                if (highlighterBestFragment == null) {
                    poetries1.setContent(document.get("content"));
                }

                poetries.add(poetries1);
                session.setAttribute("poetries", poetries);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidTokenOffsetsException e) {
            e.printStackTrace();
            return "redirect:/wrong.jsp";
        }
        indexReader.close();

        return "redirect:/emplist.jsp";
    }
}
