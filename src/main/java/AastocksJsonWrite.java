import com.eggyolk.crawler.AastocksGetIndexList;
import com.eggyolk.vo.AastocksLabel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class AastocksJsonWrite {

    public static void main(String[] args) throws Exception {

        JSONObject obj = new JSONObject();
        JSONArray list = new JSONArray();

        (new AastocksGetIndexList()).getAastocksIndexList().stream().forEach((label) -> {
            System.out.println(label);
            JSONObject cobj = new JSONObject();
            cobj.put("label", label.descZh);
            cobj.put("code", label.code);
            list.add(cobj);

        });

        obj.put("MajorIdx", list);

        try (FileWriter file = new FileWriter("c:\\Test\\test.json")) {
            file.write(list.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print(list);
    }
}