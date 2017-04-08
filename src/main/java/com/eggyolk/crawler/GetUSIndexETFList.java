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

public class GetUSIndexETFList implements GenericLabelList {

    public String getJson() throws Exception {

        JSONArray list = new JSONArray();
        LinkedHashMap map = this.getMap();

        // Get a set of the entries
        Set set = map.entrySet();

        // Get an iterator
        Iterator i = set.iterator();
        int id = 1;
        // Display elements
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            Label lbl = (Label) me.getKey();
            ArrayList<Label> indlist = (ArrayList<Label>) me.getValue();

            JSONObject cobj = new JSONObject();
            cobj.put("label", lbl.descEn);
            cobj.put("code", "US" + id);

            JSONArray jsonList = new JSONArray();

            indlist.stream().forEach((label) -> {

                JSONObject iobj = new JSONObject();
                iobj.put("label", label.descEn);
                iobj.put("code", label.code);
                jsonList.add(iobj);
            });

            cobj.put("list", jsonList);
            list.add(cobj);

            id++;
        }

        return list.toJSONString();
    }

    public ArrayList<Label> getList() throws Exception {

        Document doc;
        ArrayList<Label> list  = new ArrayList();

        try {

            // Add Dow 30 & Nasdaq first
            Label lbl = new Label();
            lbl.code = "The Dow 30";
            lbl.descEn = "The Dow 30";
            lbl.addInfo = "http://investsnips.com/dow-30/";
            list.add(lbl);

            lbl = new Label();
            lbl.code = "NASDAQ 100";
            lbl.descEn = "NASDAQ 100";
            lbl.addInfo = "http://investsnips.com/the-nasdaq-100/";
            list.add(lbl);

            lbl = new Label();
            lbl.code = "ETF 100";
            lbl.descEn = "ETF 100";
            lbl.addInfo = "http://etfdb.com/compare/volume/";
            list.add(lbl);

            // need http protocol
            doc = Jsoup.connect("http://investsnips.com/list-of-sp-500-companies/").get();

            Element div = doc.select("div.et_pb_column_1").get(0);
            Elements ps = div.select("p");
            boolean start_to_add = false;

            for (int i = 1; i < ps.size(); i++) { // first row is the col names so skip it.
                Element p = ps.get(i);

                if (start_to_add) {
                    Element a = p.select("a").get(0);
                    lbl = new Label();

                    lbl.code = a.text();
                    lbl.descEn = "S&P500 - " + a.text();
                    lbl.addInfo = a.attr("href");
                    //System.out.println(lbl);
                    list.add(lbl);
                }

                if (p.text().contains("Industry Links"))
                    start_to_add = true;
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

        this.getList().stream().forEach((label) -> {

            try {

                Document doc = Jsoup.connect(label.addInfo).get();
                ArrayList<Label> sublist = new ArrayList();

                if (label.code.startsWith("ETF")) {

                    Element tbody = doc.select("tbody").get(0);
                    Elements rows = tbody.select("tr");

                    for (int i = 0; i < rows.size(); i++) {
                        Element row = rows.get(i);
                        Elements cols = row.select("td");

                        Label lbl = new Label();
                        lbl.code = cols.get(0).text()  + ".US";
                        lbl.descEn = cols.get(1).text();
                        sublist.add(lbl);
                    }
                } else {
                    Element div = doc.select("div.et_pb_text_0").get(0);
                    Elements links = div.select("a");

                    for (int i = 0; i < links.size(); i++) {
                        Element a = links.get(i);

                        //<p><a href="http://investsnips.com/boeing-company-the-ba/">Boeing Company (The) (BA)</a> (World’s largest aerospace company; commercial jetliners; defense, drones, space and security systems)</p>
                        //System.out.println(a.text() + " - " + a.text().contains("("));
                        if (a.text().contains("(") && a.text().contains(")")) {
                            Label lbl = new Label();
                            lbl.code = a.text().split("\\(")[1].split("\\)")[0] + ".US";
                            lbl.descEn = a.text();
                            lbl.addInfo = a.text();
                            //System.out.println(lbl);
                            sublist.add(lbl);
                        }
                    }
                }

                System.out.println(label.descEn + " / " + label.addInfo + ": [" + sublist.size() + " record(s)]");

                lhm.put(label, sublist);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return lhm;
    }

    public static void main(String[] args) throws Exception {

        GenericLabelList list = new GetUSIndexETFList();
        System.out.println(list.getJson());
    }
}
