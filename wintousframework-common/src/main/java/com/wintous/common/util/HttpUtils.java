package com.ctg.itrdc.event.utils;

import com.ctg.itrdc.event.exceptions.EventNetException;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Http工具
 *
 * @author kenny
 *
 */
public class HttpUtils {
    
    private static final int                          DEFAULT_TIMEOUT = 30000;
    
    private static MultiThreadedHttpConnectionManager manager         = new MultiThreadedHttpConnectionManager();
    
    private static HttpClient                         httpClient      = new HttpClient(manager);
    
    static {
        //每主机最大连接数和总共最大连接数，通过hosfConfiguration设置host来区分每个主机
        /*DefaultMaxConnectionsPerHost参数定义每台主机允许的最大连接数，默认为2。这个参数只能用于一些特定的httpConnectionManager，比如MultiThreadedHttpConnectionManager。*/
        httpClient.getHttpConnectionManager().getParams()
            .setDefaultMaxConnectionsPerHost(Integer.MAX_VALUE);
        /*MaxTotalConnections参数表示httpConnectionManager管理的最大连接数，默认为20。同上个参数，这个参数也只是在某些特定的httpConnectionManager中有用。*/
        httpClient.getHttpConnectionManager().getParams().setMaxTotalConnections(Integer.MAX_VALUE);
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(60000);
        /*setTcpNoDelay(true)设置是否启用Nagle算法，设置true后禁用Nagle算法，默认为false（即默认启用Nagle算法）。Nagle算法试图通过减少分片的数量来节省带宽。当应用程序希望降低网络延迟并提高性能时，它们可以关闭Nagle算法，这样数据将会更早地发送，但是增加了网络消耗。*/
        httpClient.getHttpConnectionManager().getParams().setTcpNoDelay(true);
        /*setLinger(1000)设置socket延迟关闭时间，值为0表示这个选项是关闭的，值为-1表示使用JRE的默认设置。*/
        httpClient.getHttpConnectionManager().getParams().setLinger(3000);
        /*setStaleCheckingEnabled(true)参数设置是否启用旧连接检查，默认是开启的。关闭这个旧连接检查可以提高一点点性能，但是增加了I/O错误的风险（当服务端关闭连接时）。开启这个选项则在每次使用老的连接之前都会检查连接是否可用，这个耗时大概在15-30ms之间[3]。*/
        //失败的情况下会进行3次尝试,成功之后不会再尝试
        httpClient.getHttpConnectionManager().getParams()
            .setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
    }
    
    private static class SingletonHolder {
        private static HttpUtils INSTANCE = new HttpUtils();
    }
    
    public static HttpUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }
    
    /**
     * .
     *
     * @param url
     *            String
     * @param params
     *            Map<String, String>
     * @return String
     */
    public String httpPost(String url, Map<String, String> params) {
        return httpPost(url, params, true);
    }
    
    /**
     * .
     *
     * @param url
     *            String
     * @param params
     *            Map<String, String>
     * @return String
     */
    public String httpPost(String url, Map<String, String> params, boolean isCookies) {

        //        HttpClient httpClient = new HttpClient();
        //        httpClient.getHttpConnectionManager().getParams()
        //            .setConnectionTimeout(DEFAULT_TIMEOUT);
        //        httpClient.getHttpConnectionManager().getParams()
        //            .setSoTimeout(DEFAULT_TIMEOUT);
        
        PostMethod postMethod = new PostMethod(url);
        if (isCookies) {
            postMethod.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
            postMethod.setRequestHeader("Cookie", "special-cookie=value");
            postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        }
        
        //        postMethod.setRequestHeader("Connection", "close");
        // 填入各个表单域的值
        if (params != null && params.size() > 0) {
            NameValuePair[] datas = new NameValuePair[params.size()];
            int i = 0;
            for (Entry<String, String> entry : params.entrySet()) {
                NameValuePair paramPair = new NameValuePair(entry.getKey(), entry.getValue());
                datas[i] = paramPair;
                i++;
            }
            // 将表单的值放入postMethod中
            postMethod.setRequestBody(datas);
        }
        
        // 执行postMethod
        int statusCode = -1;
        try {
            statusCode = httpClient.executeMethod(postMethod);
            // HttpClient对于要求接受后继服务的请求，象POST和PUT等不能自动处理转发
            // 301或者302,现在只处理200 ok的情况
            // 和linzq了解，200状态码表示调用成功，无网络异常；通过报文对象中的result属性来判断调用是否成功
            if (statusCode == HttpStatus.SC_OK) {
                String strRet = postMethod.getResponseBodyAsString();
                return strRet;
            } else {
                throw new EventNetException("调用http服务错误，返回码" + String.valueOf(statusCode) + "返回内容："
                    + postMethod.getResponseBodyAsString());
            }
        } catch (HttpException e) {
            e.printStackTrace();
            throw new EventNetException("调用失败", e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new EventNetException("调用失败", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EventNetException("调用失败", e);
        } finally {
            postMethod.releaseConnection();
        }
    }
    
    public String jdkHttpPost(String urlStr, Map<String, String> params) throws IOException {
        HttpURLConnection connection = null;
        String str2 = "";
        BufferedReader reader = null;
        URL postUrl = null;
        try {
            postUrl = new URL(urlStr);
            // 打开连接
            connection = (HttpURLConnection) postUrl.openConnection();
            
            // 设置是否向connection输出，因为这个是post请求，参数要放在
            // http正文内，因此需要设为true
            connection.setDoOutput(true);
            // Read from the connection. Default is true.
            connection.setDoInput(true);
            // 默认是 GET方式
            connection.setRequestMethod("POST");
            
            // Post 请求不能使用缓存
            connection.setUseCaches(false);
            
            connection.setInstanceFollowRedirects(true);
            
            // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
            // 意思是正文是urlencoded编码过的form参数，下面我们可以看到我们对正文内容使用URLEncoder.encode
            // 进行编码
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
            // 要注意的是connection.getOutputStream会隐含的进行connect。
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            // The URL-encoded contend
            // 正文，正文内容其实跟get的URL中 '? '后的参数字符串一致
            String content = "";
            if (params != null && params.size() > 0) {
                int i = 0;
                for (Entry<String, String> entry : params.entrySet()) {
                    if (i != 0) {
                        content += "&";
                    }
                    content += entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8");
                    i++;
                }
            }
            // DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写到流里面
            out.writeBytes(content);
            
            out.flush();
            out.close();
            
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            
            String line;
            
            while ((line = reader.readLine()) != null) {
                str2 += line;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RtManagerException("调用失败", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str2;
    }
}
