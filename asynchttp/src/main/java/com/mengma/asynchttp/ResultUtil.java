package com.mengma.asynchttp;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * Created by szhua 2016/3/11
 */
public class ResultUtil {
    public static JsonNode handleResult(String result) throws IOException {
        JsonNode node = JsonUtil.json2node(result);
        return node;
    }
}
