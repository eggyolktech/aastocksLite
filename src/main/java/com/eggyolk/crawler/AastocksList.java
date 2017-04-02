package com.eggyolk.crawler;

import com.eggyolk.vo.AastocksLabel;
import java.util.ArrayList;

/**
 * Created by Hin on 4/3/2017.
 */
public interface AastocksList {

    public String getJson() throws Exception;
    public ArrayList<AastocksLabel> getList() throws Exception;
}
