package com.eggyolk.crawler;

import com.eggyolk.vo.AastocksLabel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class AastocksIndustryList implements AastocksList {

    public String getJson() throws Exception {

        JSONArray list = new JSONArray();

        (new AastocksIndustryList()).getList().stream().forEach((label) -> {
            System.out.println(label);
            JSONObject cobj = new JSONObject();
            cobj.put("label", label.descZh);
            cobj.put("code", label.code);
            list.add(cobj);
        });

        return list.toJSONString();
    }

    public ArrayList<AastocksLabel> getList() throws Exception {

        Document doc, docdtl;

        // Create a hash map
        LinkedHashMap lhm = new LinkedHashMap();

        try {

            // need http protocol
            doc = Jsoup.connect("http://services1.aastocks.com/web/cjsh/IndustrySection.aspx?CJSHLanguage=Chi").get();

            // get page title
            String title = doc.title();
            System.out.println("title : " + title);

            Element select = doc.select("select[class=DefaultAAIndustryConstituentDdl]").get(0);
            Elements options = select.select("option");

            for (int i = 1; i < options.size(); i++) { // first row is the col names so skip it.
                Element option = options.get(i);

                System.out.println(option.text() + "[" + option.attr("value") + "]");
                docdtl = Jsoup.connect("http://services1.aastocks.com/web/cjsh/IndustrySection.aspx?CJSHLanguage=Chi&symbol=&industry=" +  option.attr("value")).get();

                Element table = docdtl.select("table[class=DefaultAAIndustryConstituentDataTable]").get(0);
                Elements rows = table.select("tr");
                ArrayList<AastocksLabel> list  = new ArrayList();

                for (int j = 2; j < rows.size(); j++) { // first row is the col names so skip it.
                    Element row = rows.get(j);
                    Elements cols = row.select("td");

                    AastocksLabel lbl = new AastocksLabel();
                    lbl.code = cols.get(0).text();
                    lbl.descZh = cols.get(1).text();
                    lbl.addInfo = cols.get(6).text();
                    list.add(lbl);
                    System.out.println(lbl);
                }

                lhm.put(option.text(), list);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        ArrayList<AastocksLabel> list  = new ArrayList();
        return list;
    }

    public static void main(String[] args) throws Exception {

        AastocksList list = new AastocksIndustryList();
        System.out.println(list.getJson());
    }
}
