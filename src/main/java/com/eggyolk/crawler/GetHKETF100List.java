package com.eggyolk.crawler;

import com.eggyolk.vo.Label;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class GetHKETF100List implements GenericLabelList {

    public String getJson() throws Exception {

        JSONArray list = new JSONArray();

        (new GetHKETF100List()).getList().stream().forEach((label) -> {
            //System.out.println(label);
            JSONObject cobj = new JSONObject();
            cobj.put("label", label.descZh);
            cobj.put("code", label.code);
            list.add(cobj);
        });

        return list.toJSONString();
    }

    public ArrayList<Label> getList() throws Exception {

        Document doc;
        ArrayList<Label> list  = new ArrayList();

        try {

            // need http protocol
            doc = Jsoup.connect("http://www.aastocks.com/tc/stocks/etf/default.aspx").get();

            // get page title
            String title = doc.title();
            //System.out.println("title : " + title);

            Element table = doc.select("table#tabETF1").get(0);
            Elements rows = table.select("tr");

            for (int i = 1; i < rows.size(); i++) { // first row is the col names so skip it.
                Element row = rows.get(i);
                Elements cols = row.select("td");

                // Check only if have Turnover
                if (!cols.get(6).text().equals("0.00") && !cols.get(6).text().equals("")) {
                    //System.out.println("Name: " + cols.get(0).text().split(" ")[0].replace(" ", ""));
                    //System.out.println("Code: " + cols.get(0).text().split(" ")[1].replace(".HK", ""));
                    //System.out.println("Ref Entity: " + cols.get(1).text());
                    //System.out.println("Turnover [" + cols.get(6).text() + "]\n");

                    Label lbl = new Label();
                    lbl.code = cols.get(0).text().split(" ")[1].replace(".HK", "");
                    lbl.descZh = cols.get(0).text().split(" ")[0].replace(" ", "");
                    lbl.addInfo = cols.get(1).text();
                    list.add(lbl);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        return list;
    }

    public static void main(String[] args) throws Exception {

        GenericLabelList list = new GetHKETF100List();
        System.out.println(list.getJson());
    }
}
