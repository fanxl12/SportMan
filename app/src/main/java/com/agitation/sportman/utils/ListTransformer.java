package com.agitation.sportman.utils;

import android.util.Log;

import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.Transformer;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * JSON转Map
 * Created by fanxl on 2015/8/3.
 */
public class ListTransformer implements Transformer {

    public <T> T transform(String url, Class<T> type, String encoding, byte[] data, AjaxStatus status) {
        try {

            Log.i("info",new String(data, "utf-8"));
            return new ObjectMapper().readValue(data, new TypeReference<List<Map<String, Object>>>(){});
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
