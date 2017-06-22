/*
 * Copyright (c) 2012-2016, b3log.org & hacpai.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tramp.wechat4j.wechat.bot;

import com.google.common.collect.Maps;
import com.tramp.wechat4j.wechat.utils.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;


public class TuringQueryService {

	String botName="小明";
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TuringQueryService.class.getName());

    /**
     * Turing Robot API.
     */
    private static final String TURING_API = "http://www.tuling123.com/openapi/api";

    /**
     * Turing Robot Key.
     */
    private static final String TURING_KEY = "4788f04fd33045cb9ae3c25622c0e598";


    /**
     * Chat with Turing Robot.
     *
     * @param userName the specified user name
     * @param msg the specified message
     * @return robot returned message, return {@code null} if not found
     */
    public String chat( String userName, String msg) {
        if (StringUtils.isBlank(msg)) {
            return null;
        }

        if (msg.startsWith(botName + " ")) {
            msg = msg.replace(botName + " ", "");
        }
        if (msg.startsWith(botName + "，")) {
            msg = msg.replace(botName + "，", "");
        }
        if (msg.startsWith(botName + ",")) {
            msg = msg.replace(botName + ",", "");
        }
        if (msg.startsWith(botName)) {
            msg = msg.replace(botName, "");
        }

        if (StringUtils.isBlank(userName) || StringUtils.isBlank(msg)) {
            return null;
        }

        

        try {
        	Map<String, String> params = Maps.newHashMap();
        	params.put("key", TURING_KEY);
        	params.put("info", msg);
        	params.put("userid", userName);
            String result = HttpUtil.postByHttpClient(TURING_API, params, null);
            JSONObject resultObject = new JSONObject(result); 
            int code = resultObject.getInt("code");

            switch (code) {
                case 40001:
                case 40002:
                case 40007:
                    LOGGER.log(Level.ERROR, resultObject.optString("text"));

                    return null;
                case 40004:
                    return "聊累了，明天请早吧~";
                case 100000:
                    return resultObject.optString("text");
                case 200000:
                    return resultObject.optString("text") + " " + resultObject.optString("url");
                case 302000:
                    String ret302000 = resultObject.optString("text") + " ";
                    final JSONArray list302000 = resultObject.optJSONArray("list");
                    final StringBuilder builder302000 = new StringBuilder();
                    for (int i = 0; i < list302000.length(); i++) {
                        final JSONObject news = list302000.optJSONObject(i);
                        builder302000.append(news.optString("article")).append(news.optString("detailurl"))
                                .append("\n\n");
                    }

                    return ret302000 + " " + builder302000.toString();
                case 308000:
                    String ret308000 = resultObject.optString("text") + " ";
                    final JSONArray list308000 = resultObject.optJSONArray("list");
                    final StringBuilder builder308000 = new StringBuilder();
                    for (int i = 0; i < list308000.length(); i++) {
                        final JSONObject news = list308000.optJSONObject(i);
                        builder308000.append(news.optString("name")).append(news.optString("detailurl"))
                                .append("\n\n");
                    }

                    return ret308000 + " " + builder308000.toString();
                default:
                    LOGGER.log(Level.WARN, "Turing Robot default return [" + resultObject.toString(4) + "]");
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Chat with Turing Robot failed", e);
        }

        return null;
    }
}
