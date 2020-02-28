package com.leoero.intelligentlamp.util;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonParser {

    /**
     * @brief 解析识别传输过来的语音Json数据
     * @param json 未解析的语音数据
     * @return 解析后的语音数据
     */
    public static String parse(String json) {
        StringBuffer buffer = new StringBuffer();
        Log.i("jsonData", json);
        try{
            JSONTokener token = new JSONTokener(json);
            JSONObject object = new JSONObject(token);
            JSONArray array = object.getJSONArray("ws");

            //Json的数据:[{"bg":1,"cw":[{"w":"你好","sc":0}]},{"bg":1,"cw":[{"w":"你好","sc":0}]},{"bg":1,"cw":[{"w":"你好","sc":0}]}]
            //解析的语音存在"ws"的key里面，因此当ws不为null时，将其加入string buffer
            for(int i = 0; i < array.length(); i++) {
                JSONArray items = array.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                buffer.append(obj.getString("w"));
            }
            Log.d("jsonData", buffer.toString());
            Log.d("jsonData", array.toString());
        } catch(JSONException e) {
            Log.e("jsonError", "Parse the json data failed!");
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
