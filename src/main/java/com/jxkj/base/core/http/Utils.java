package com.jxkj.base.core.http;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2018/8/28.
 */

public class Utils {

    /**
     * 方法用途: 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序），并且生成url参数串<br>
     * 实现步骤: <br>
     *
     * @param paraMap 要排序的Map对象
     * @return
     */
    public static String formatParaMap(Map<String, Object> paraMap) {
        String buff;
        Map<String, Object> tmpMap = paraMap;
        try {
            List<Map.Entry<String, Object>> infoIds = new ArrayList<>(tmpMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, (o1, o2) -> (o1.getKey()).compareTo(o2.getKey()));

            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, Object> item : infoIds) {
                if (!TextUtils.isEmpty(item.getKey())) {
                    String key = item.getKey();
                    String val = item.getValue().toString();
                    buf.append(key + "=" + val);
                    buf.append("&");
                }

            }
            buff = buf.toString();
            if (buff.isEmpty() == false) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            throw new SecurityException(e.getMessage());
        }
        return buff;
    }


    public static String getContent(Map<String,Object> mapParams){
     //   String sign;
    //    String data;
        String content = "";
//        Date day = new Date();
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
//        data = df.format(day);
        //mapParams.put("requesttime", data);
//        sign = Tools.setMd5(formatParaMap(mapParams));
//        mapParams.put("sign", sign.toUpperCase());
//        Log.v("heng_md5",formatParaMap(mapParams));
//        Log.v("heng_signStr",sign.toUpperCase());
        Log.v("heng_content", JSON.toJSONString(mapParams));
        try {
            content =JSON.toJSONString(mapParams);
         //   Log.v("heng_content", AESUtil.encrypt(JSON.toJSONString(mapParams)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return content;

    }


}
