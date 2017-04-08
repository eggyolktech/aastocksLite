package com.eggyolk.crawler;

import com.eggyolk.vo.Label;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Hin on 4/3/2017.
 */
public interface GenericLabelList {

    public String getJson() throws Exception;
    public ArrayList<Label> getList() throws Exception;
}
