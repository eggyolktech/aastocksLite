import com.eggyolk.crawler.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

public class AastocksWriteToJS {

    public static void main(String[] args) throws Exception {

        // Write JS Test
        (new AastocksWriteToJS()).writeJS();
    }

    public void writeJS() throws Exception {

        final String SEP = "\\";

        Properties prop = new Properties();
        InputStream input;

        input = this.getClass().getResourceAsStream("main.properties");

        // load a properties file
        prop.load(input);
        String jsFilePath = prop.getProperty("jspath") + prop.getProperty("jsfile");
        try (FileWriter file = new FileWriter(jsFilePath)) {

            file.write(getJsListContent("indexData", new AastocksHKIndexList().getJson()));
            file.write(getJsListContent("etfData", new AastocksHKETFList().getJson()));
            file.write(getJsListContent("blueChipData", new AastocksHKBlueChipList().getJson()));
            file.write(getJsListContent("industryData", new AastocksHKIndustryList().getJson()));
            file.flush();

            System.out.println("JS File written to " + jsFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.writeJsonListFile("ETF", new AastocksHKETFList());
        this.writeJsonListFile("BlueChip", new AastocksHKBlueChipList());
        this.writeJsonListFile("Industry", new AastocksHKIndustryList());
    }

    public void writeJsonListFile(String listName, AastocksList list) throws Exception {

        Properties prop = new Properties();
        InputStream input;
        input = this.getClass().getResourceAsStream("main.properties");

        // load a properties file
        prop.load(input);

        String jsonFilePath = prop.getProperty("jsonpath") + MessageFormat.format((String) prop.get("jsonfile"), listName);

        try (FileWriter file = new FileWriter(jsonFilePath)) {

            file.write(list.getJson());
            file.flush();

            System.out.println("JSON File written to " + jsonFilePath);

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