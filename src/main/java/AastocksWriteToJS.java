import com.eggyolk.crawler.AastocksBlueChipList;
import com.eggyolk.crawler.AastocksETFList;
import com.eggyolk.crawler.AastocksIndexList;
import com.eggyolk.crawler.AastocksIndustryList;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AastocksWriteToJS {

    public static void main(String[] args) throws Exception {

        // Write JS Test
        (new AastocksWriteToJS()).writeJS();
    }

    public void writeJS() throws IOException {

        final String SEP = "\\";

        Properties prop = new Properties();
        InputStream input;

        input = this.getClass().getResourceAsStream("main.properties");

        // load a properties file
        prop.load(input);
        String jsFilePath = prop.getProperty("jspath") + prop.getProperty("jsfile");
        try (FileWriter file = new FileWriter(jsFilePath)) {

            file.write(getJsListContent("indexData", new AastocksIndexList().getJson()));
            file.write(getJsListContent("etfData", new AastocksETFList().getJson()));
            file.write(getJsListContent("blueChipData", new AastocksBlueChipList().getJson()));
            file.write(getJsListContent("industryData", new AastocksIndustryList().getJson()));
            file.flush();

            System.out.println("JS File written to " + jsFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getJsListContent(String listName, String json) {
        String v = "var " + listName + " ={\n\tlist:" + json + "\n};\n\n";
        return v;
    }
}