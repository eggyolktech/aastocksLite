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

public class GetHKETFList implements GenericLabelList {

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
                iobj.put("addInfo", label.addInfo);
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
            doc = Jsoup.connect("http://www.aastocks.com/tc/stocks/etf/search.aspx").get();

            // get page title
            String title = doc.title();
            //System.out.println("title : " + title);

            Element select = doc.select("select#cp_ddlETFCategory").get(0);
            Elements options = select.select("option");

            for (int i = 1; i < options.size(); i++) { // first row is the col names so skip it.
                Element option = options.get(i);

                //System.out.println(option.text() + "[" + option.attr("value") + "]");
                Label lbl = new Label();
                lbl.code = option.attr("value");
                lbl.descZh = option.text();
                lbl.addInfo ="";
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
        ArrayList<Label> list = this.getList();
/*
<select name="ctl00$cp$ddlETFCategory" id="cp_ddlETFCategory" style="width:200px;"> <option selected="selected" value="">所有</option>
 <option value="1;5">股票 - 香港</option> <option value="1;13">股票 - 中國 (香港上市公司) </option>
 <option value="1;14">股票 - 中國 (中國上市公司)</option> <option value="1;8">股票 - 日本</option> <option value="1;9">股票 - 亞太區 (日本除外)</option>
 <option value="1;10">股票 - 新興市場</option> <option value="1;11">股票 - 環球</option> <option value="1;12">股票 - 大中華地區</option>
 <option value="1;15">股票 - 中國（中國概念股）</option> <option value="1;17">股票 - 泛亞區（日本及澳紐除外）</option> <option value="1;18">股票 - 亞洲</option>
  <option value="1;16">股票 - 中國以外地區</option> <option value="1;19">股票 - 歐洲</option> <option value="1;20">股票 - 亞洲(日本除外)</option>
  <option value="1;21">股票 - 中國及美國</option> <option value="22;23">期貨合約 - 恒指</option> <option value="1;24">股票 - 韓國</option>
   <option value=";3">商品</option> <option value=";1">股票</option> <option value=";2">固定收益</option> <option value=";4">貨幣市場</option>
    <option value=";22">期貨合約</option> </select>

 */
        list.stream().forEach((label) -> {
            //System.out.println(label);

            try {

                Document doc = Jsoup.connect("http://www.aastocks.com/tc/stocks/etf/search.aspx?t=1&s=8&o=0&cat=" + label.code).get();
                ArrayList<Label> sublist = new ArrayList();
                Element table = doc.select("table#tabETF1").get(0);
                Elements rows = table.select("tr");

                for (int j = 1; j < rows.size(); j++) { // first row is the col names so skip it.
                    Element row = rows.get(j);
                    Elements cols = row.select("td");

                    Element col = cols.get(0);
                    Element a = col.select("a").get(0);

                    Label lbl = new Label();
                    lbl.code = a.attr("href").split("=")[1];
                    lbl.descZh = a.text();
                    lbl.addInfo = "Tracker: " + cols.get(1).text();
                    sublist.add(lbl);
                    //System.out.println(lbl);
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

        GenericLabelList list = new GetHKETFList();
        System.out.println(list.getJson());
    }
}
