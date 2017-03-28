import com.eggyolk.crawler.AastocksGetIndexList;
import com.eggyolk.vo.AastocksLabel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class AastocksJsonWrite {

    public static void main(String[] args) throws Exception {


        AastocksGetIndexList al = new AastocksGetIndexList();
        ArrayList<AastocksLabel> indexList = al.getAastocksIndexList();

        JSONObject obj = new JSONObject();
        JSONArray list = new JSONArray();

        for (int i=0; i< indexList.size(); i++) {
            AastocksLabel lbl = indexList.get(i);
            System.out.println("Code: " + lbl.code + ", Desc: " + lbl.descZh);
            JSONObject cobj = new JSONObject();
            cobj.put(lbl.descZh, lbl.code);
            list.add(cobj);
        }

        obj.put("主要指數", list);

        try (FileWriter file = new FileWriter("c:\\Test\\test.json")) {
            file.write(obj.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print(obj);

    }
}