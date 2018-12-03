package io.bcaas.api;

import io.bcaas.constants.Constants;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpClient {

    /**
     * http get请求
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return
     */
    public static String get(String url, Map<String, Object> params) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = null;
        try {
            if (params != null && !params.isEmpty()) {
                url = url + "?";
                for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext(); ) {
                    String key = iterator.next();
                    String temp = key + "=" + params.get(key) + "&";
                    url = url + temp;
                }
                url = url.substring(0, url.length() - 1);
            }
            HttpGet httpGet = new HttpGet(url);
            httpClient = HttpClients.createDefault();
            RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(10000).setConnectTimeout(10000)
                                                .setSocketTimeout(10000).build();
            httpGet.setConfig(config);
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, Constants.CHARSET_UTF8);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                response.close();
                httpClient.close();
            } catch (IOException e) {
            }
        }
        return "";
    }

    /**
     * http post请求
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return
     */
    public static String post(String url, Map<String, Object> params) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = null;
        try {
            HttpPost httpPost = new HttpPost(url);

            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext(); ) {
                String key = iterator.next();
                parameters.add(new BasicNameValuePair(key, params.get(key).toString()));
            }
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(parameters, Constants.CHARSET_UTF8);

            httpPost.setEntity(uefEntity);
            RequestConfig config = RequestConfig.custom()
                                                .setConnectionRequestTimeout(10000)
                                                .setConnectTimeout(10000)
                                                .setSocketTimeout(10000).build();
            httpPost.setConfig(config);

            httpClient = HttpClients.createDefault();
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String content = EntityUtils.toString(entity, Constants.CHARSET_UTF8);

                Constants.LOGGER_INFO.info("HTTP POST URL:{} ,Response content:{}", url,content);

                return content;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                response.close();
                httpClient.close();
            } catch (IOException e) {
            }

        }
        return "";
    }

    /**
     * http post请求
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return
     */
    public static String post(String url, String params) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = null;
        try {
            HttpPost httpPost = new HttpPost(url);

            StringEntity sEntity = new StringEntity(params, Constants.CHARSET_UTF8);
            httpPost.setEntity(sEntity);
            RequestConfig config = RequestConfig.custom()
                                                .setConnectionRequestTimeout(10000)
                                                .setConnectTimeout(10000)
                                                .setSocketTimeout(10000).build();
            httpPost.setConfig(config);
            httpClient = HttpClients.createDefault();
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, Constants.CHARSET_UTF8);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                response.close();
                httpClient.close();
            } catch (IOException e) {
            }

        }
        return "";
    }


    /**
     * 处理get请求.
     *
     * @param url 请求路径
     * @return json
     */
    public String get(String url) {
        Constants.LOGGER_INFO.info("HTTP Resquest url:{}", url);

        //实例化httpclient
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //实例化get方法
        HttpGet httpget = new HttpGet(url);
        //请求结果
        CloseableHttpResponse response = null;
        String content = "";
        try {
            //执行get方法
            response = httpclient.execute(httpget);
            if (response.getStatusLine().getStatusCode() == 200) {
                content = EntityUtils.toString(response.getEntity(), "utf-8");

                //去掉空格和换行
                content = replaceSpecialStr(content);
                Constants.LOGGER_INFO.info("HTTP Response content:{}", content);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 去除字符串中的空格、回车、换行符、制表符等
     *
     * @param str
     * @return
     */
    public static String replaceSpecialStr(String str) {
        String repl = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            repl = m.replaceAll("");
        }
        return repl;
    }
}
