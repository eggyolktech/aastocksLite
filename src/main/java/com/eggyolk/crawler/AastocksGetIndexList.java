package com.eggyolk.crawler;

import com.eggyolk.vo.AastocksLabel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class AastocksGetIndexList {

    private static ArrayList<AastocksLabel> indexList;

    public static void main(String[] args) {

        Document doc;
        try {

            // need http protocol
            doc = Jsoup.connect("http://www.aastocks.com/tc/stocks/market/index/hk-index.aspx").get();

            // get page title
            String title = doc.title();
            System.out.println("title : " + title);

            //<a href="http://www.aastocks.com/tc/stock/DetailChart.aspx?symbol=110000" class="a15"><div class="float_l icon-box icon-chart"></div></a>
            Elements links = doc.select("a[href*=DetailChart.aspx?symbol]");
            for (Element link : links) {
                // get the value from href attribute
                System.out.println("code : " + link.attr("href").split("=")[1]);
            }

            // <a href="/tc/stocks/market/index/hk-index-con.aspx?index=HSI" class="a15"><div class="float_">恆生指數</div></a>
            links = doc.select("a[href*=hk-index-con.aspx?index]");
            for (Element link : links) {
                // get the value from href attribute
                System.out.println("text : " + link.text());
            }

            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            doc = Jsoup.connect("http://www.aastocks.com/tc/stocks/market/index/world-index.aspx").get();

            // get page title
            title = doc.title();
            System.out.println("\ntitle : " + title);

            //<a href="http://www.aastocks.com/tc/stock/DetailChart.aspx?symbol=111000"><div class="float_l icon-box icon-chart"></div></a>
            links = doc.select("a[href*=DetailChart.aspx?symbol]");
            for (Element link : links) {
                // get the value from href attribute
                System.out.println("code : " + link.attr("href").split("=")[1]);
            }

            //<span class="float_l" style="line-height:20px;">道瓊斯</span>
            Elements spans = doc.select("span.float_l");

            int currentPos = 0;
            for (Element span : spans) {
                // get the value from href attribute
                System.out.println("text : " + span.text());
                currentPos++;

                if(currentPos >= links.size())
                    break;
            }

            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            doc = Jsoup.connect("http://www.aastocks.com/tc/stocks/market/index/china-index.aspx").get();

            // get page title
            title = doc.title();
            System.out.println("\ntitle : " + title);

            //<a href="http://www.aastocks.com/tc/stock/DetailChart.aspx?symbol=111000"><div class="float_l icon-box icon-chart"></div></a>
            links = doc.select("a[href*=DetailChart.aspx?symbol]");
            for (Element link : links) {
                // get the value from href attribute
                System.out.println("code : " + link.attr("href").split("=")[1]);
            }

            //<span class="float_l" style="line-height:20px;">道瓊斯</span>
            spans = doc.select("span.float_l");

            currentPos = 0;
            for (Element span : spans) {
                // get the value from href attribute
                System.out.println("text : " + span.text());
                currentPos++;

                if(currentPos >= links.size())
                    break;
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
