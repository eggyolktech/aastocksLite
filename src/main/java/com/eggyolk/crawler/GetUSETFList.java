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

public class GetUSETFList implements GenericLabelList {

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
            cobj.put("code", "USETF" + id);

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

            Label lbl;

            // need http protocol
            doc = Jsoup.connect("http://etfdb.com/etfdb-categories/").get();

            Elements lis = doc.select("li.pull-left");

            //System.out.println(lis.size());

            for (int i = 0; i < lis.size(); i++) {

                Element li = lis.get(i);
                Elements links = li.select("a");

                //System.out.println(links.size());

                for (int j = 0; j < links.size(); j++) {
                    Element a = links.get(j);
                    lbl = new Label();

                    lbl.code = a.text();
                    lbl.descEn = a.text();
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

        this.getList().stream().forEach((label) -> {

            try {

                Document doc = Jsoup.connect("http://etfdb.com" + label.addInfo).get();
                ArrayList<Label> sublist = new ArrayList();

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

                System.out.println(label.descEn + " / " + label.addInfo + ": [" + sublist.size() + " record(s)]");

                lhm.put(label, sublist);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return lhm;
    }

    public static void main(String[] args) throws Exception {

        GenericLabelList list = new GetUSETFList();
        System.out.println(list.getJson());
    }
}
