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

public class GetCNIndexList implements GenericLabelList {

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

            Label lbl = new Label();
            lbl.code = "CSI.000010";
            lbl.descZh = "上證180指數";
            lbl.addInfo = "http://www.etnet.com.hk/www/tc/ashares/indexes_detail.php?subtype=CSI.000010&page=";
            list.add(lbl);

            lbl = new Label();
            lbl.code = "CSI.000009";
            lbl.descZh = "上證380指數";
            lbl.addInfo = "http://www.etnet.com.hk/www/tc/ashares/indexes_detail.php?subtype=CSI.000009&page=";
            list.add(lbl);

            lbl = new Label();
            lbl.code = "SZSE.399004";
            lbl.descZh = "深證100指數";
            lbl.addInfo = "http://www.etnet.com.hk/www/tc/ashares/indexes_detail.php?subtype=SZSE.399004&page=";
            list.add(lbl);

        } catch (Exception e) {
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

                ArrayList<Label> sublist = new ArrayList();

                for (int i=1 ; i<=20; i++) {

                    Document doc = Jsoup.connect(label.addInfo + i).get();
                   // System.out.println("url: " + label.addInfo + i);
                    Element table = doc.select("table[class=figureTable]").get(0);
                    Elements rows = table.select("tr");

                    //System.out.println(rows.size());
                    for (int j = 1; j < rows.size(); j++) { // first row is the col names so skip it.
                        Element row = rows.get(j);
                        Elements cols = row.select("td");

                        if (cols.size() == 10 && !cols.get(0).text().equalsIgnoreCase("上頁")) {
                            Label lbl = new Label();
                            lbl.code = cols.get(0).text();
                            lbl.descZh = cols.get(1).text();

                            if (lbl.code.startsWith("6"))
                                lbl.code += ".SH";
                            else
                                lbl.code += ".SZ";

                            sublist.add(lbl);
                        }
                    }
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

        GenericLabelList list = new GetCNIndexList();
        System.out.println(list.getJson());
    }
}
