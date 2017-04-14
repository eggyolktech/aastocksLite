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

public class GetUSIndustryList implements GenericLabelList {

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
            cobj.put("code", "USIND" + id);

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

            // need http protocol
            doc = Jsoup.connect("http://investsnips.com/sectors-and-industries-u-s-exchanges/").get();

            Element div = doc.select("div.et_pb_column_2").get(0);
            Elements links = div.select("a");

            for (int i = 0; i < links.size(); i++) { // first row is the col names so skip it.
                Element a = links.get(i);
                Label lbl = new Label();

                lbl.code = a.text();
                lbl.descEn = a.text();
                if( lbl.descEn.equalsIgnoreCase("Biotechnology"))
                    lbl.addInfo = "http://investsnips.com/list-of-publicly-traded-large-cap-diversified-biotechnology-and-pharmaceutical-companies/";
                else
                    lbl.addInfo = a.attr("href");
                //System.out.println(lbl);
                list.add(lbl);
            }

            div = doc.select("div.et_pb_column_3").get(0);
            links = div.select("a");

            for (int i = 0; i < links.size(); i++) { // first row is the col names so skip it.
                Element a = links.get(i);
                Label lbl = new Label();

                lbl.code = a.text();
                lbl.descEn = a.text();

                if (lbl.descEn.contains("Medical Device"))
                    lbl.addInfo = "http://investsnips.com/list-of-publicly-traded-large-cap-diversified-medical-equipment-and-device-companies/";
                else if (lbl.descEn.contains("Pharmaceuticals"))
                    lbl.addInfo = "http://investsnips.com/publicly-traded-large-cap-pharmaceutical-companies/";
                else if (lbl.descEn.contains("Software"))
                    lbl.addInfo = "http://investsnips.com/list-of-publicly-traded-large-cap-software-and-technology-service-companies/";
                else if (lbl.descEn.contains("Technology"))
                    lbl.addInfo = "http://investsnips.com/list-of-publicly-traded-large-cap-diversified-medical-equipment-and-device-companies/";
                else
                    lbl.addInfo = a.attr("href");
                //System.out.println(lbl);
                list.add(lbl);
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

                Element div = doc.select("div.et_pb_section").get(1);
                Elements links = div.select("a");

                for (int i = 0; i < links.size(); i++) {
                    Element a = links.get(i);

                    if (a.text().contains("(") && a.text().contains(")")) {
                        Label lbl = new Label();
                        String[] str = a.text().split("\\(");

                        lbl.code = str[str.length-1].split("\\)")[0] + ".US";
                        lbl.descEn = a.text();
                        lbl.addInfo = a.text();

                        if (!lbl.code.contains(" ")) {
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

        GenericLabelList list = new GetUSIndustryList();
        System.out.println(list.getJson());
    }
}
