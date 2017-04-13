package com.eggyolk.crawler;

import com.eggyolk.vo.Label;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class GetCNIndustryList implements GenericLabelList {

    public String getJson() throws Exception {

        JSONArray list = new JSONArray();
        LinkedHashMap map = this.getMap();

        // Get a set of the entries
        Set set = map.entrySet();

        // Get an iterator
        Iterator i = set.iterator();

        // Display elements
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            Label lbl = (Label) me.getKey();
            ArrayList<Label> indlist = (ArrayList<Label>) me.getValue();

            JSONObject cobj = new JSONObject();
            cobj.put("label", lbl.descZh);
            cobj.put("code", lbl.code);

            JSONArray jsonList = new JSONArray();

            indlist.stream().forEach((label) -> {

                JSONObject iobj = new JSONObject();
                iobj.put("label", label.descZh);
                iobj.put("code", label.code);
                jsonList.add(iobj);
            });

            cobj.put("list", jsonList);
            list.add(cobj);


            //System.out.print(me.getKey() + ": ");
            //System.out.println(me.getValue());
        }

        return list.toJSONString();
    }

    public ArrayList<Label> getList() throws Exception {

        Document doc;
        ArrayList<Label> list  = new ArrayList();

        try {

            // need http protocol
            doc = Jsoup.connect("http://www.aastocks.com/tc/cnhk/market/industry/industry-performance.aspx?market_id=CN").get();

            // get page title
            String title = doc.title();
            //System.out.println("title : " + title);
            Element div = doc.select("div[class=etfContainer]").get(0);
            Element table = div.select("table").get(0);
            Elements rows = table.select("tr");

            for (int i = 1; i < rows.size(); i++) { // first row is the col names so skip it.
                Element row = rows.get(i);
                Elements cols = row.select("td");

                Element col = cols.get(0);

                if (!col.select("a").toString().equalsIgnoreCase("")) {
                    Element a = col.select("a").get(0);
                    Label lbl = new Label();
                    lbl.code = a.attr("href").split("=")[2];
                    lbl.descZh = a.text();
                    lbl.addInfo = a.attr("href");
                    list.add(lbl);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        return list;
    }

    public LinkedHashMap getMap() throws Exception {

        // Create a hash map
        LinkedHashMap lhm = new LinkedHashMap();
        ArrayList<Label> list = this.getList();

        list.stream().forEach((label) -> {

            try {

                Document doc = Jsoup.connect("http://www.aastocks.com/tc/cnhk/market/industry/sector-industry-details.aspx?market_id=CN&industrysymbol=" + label.code).get();
                ArrayList<Label> sublist = new ArrayList();
                Element table = doc.select("table#tbTS").get(0);
                Elements rows = table.select("tr");

                for (int j = 1; j < rows.size(); j++) { // first row is the col names so skip it.
                    Element row = rows.get(j);
                    Elements cols = row.select("td");
                    Element col = cols.get(0);

                    if (!col.select("a").toString().equalsIgnoreCase("")) {

                        Element a = col.select("a").get(0);
                        Element span = col.select("span[class=float_l]").get(0);

                        Label lbl = new Label();
                        lbl.code = a.text();
                        lbl.descZh = span.text();
                        sublist.add(lbl);
                        //System.out.println(lbl);
                    }
                }

                System.out.println(label.descZh + "/" + label.code + ": [" + sublist.size() + " record(s)]");

                if (sublist.size() > 0) {
                    lhm.put(label, sublist);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return lhm;
    }

    public static void main(String[] args) throws Exception {

        GenericLabelList list = new GetCNIndustryList();
        System.out.println(list.getJson());
    }
}
