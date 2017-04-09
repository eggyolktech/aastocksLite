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

public class GetHKIndexList implements GenericLabelList {

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
            doc = Jsoup.connect("http://www.etnet.com.hk/www/tc/stocks/indexes_main.php").get();

            // get page title
            String title = doc.title();
            //System.out.println("title : " + title);

            Element select = doc.select("div#SectionMenu").get(0);
            Elements links = select.select("a");

            for (int i = 0; i < links.size(); i++) { // first row is the col names so skip it.
                Element a = links.get(i);
                //System.out.println(a.text() + " - " + (a.attr("href")));

                if (a.attr("href").contains("indexes_detail.php?subtype")) {
                    Label lbl = new Label();
                    lbl.code = a.attr("href").split("=")[1];
                    lbl.descZh = a.text();
                    lbl.addInfo ="";

                    // exclude mainland indexes
                    if (!lbl.code.equalsIgnoreCase("cesa80"))
                        list.add(lbl);
                    //System.out.println(lbl);
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

                Document doc = Jsoup.connect("http://www.etnet.com.hk/www/tc/stocks/indexes_detail.php?subtype=" + label.code).get();
                ArrayList<Label> sublist = new ArrayList();

                Element table = doc.select("table[class=figureTable]").get(0);
                Elements rows = table.select("tr");

                for (int i = 1; i < rows.size(); i++) { // first row is the col names so skip it.
                    Element row = rows.get(i);
                    Elements cols = row.select("td");

                    Label lbl = new Label();
                    lbl.code = cols.get(0).text();
                    lbl.descZh = cols.get(1).text();
                    sublist.add(lbl);
                }

                System.out.println(label.descZh + "/" + label.code + ": [" + sublist.size() + " record(s)]");

                lhm.put(label, sublist);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return lhm;
    }

    public static void main(String[] args) throws Exception {

        GenericLabelList list = new GetHKIndexList();
        System.out.println(list.getJson());
    }
}
