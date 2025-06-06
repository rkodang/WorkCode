package com.gumdom.boot.infrastructure;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstServiceStep implements IBasicServiceController, IBasicExtension, IBridgeAcrossProxyService {

    @Autowired
    public BridgeAcrossService bridgeAcrossService;

    private ICaching cached;

    @Override
    public BridgeAcrossService getBridgeService() {
        return this.bridgeAcrossService;
    }

    public void setBridgeAcrossService(BridgeAcrossService bridgeAcrossService) {
        this.bridgeAcrossService = bridgeAcrossService;
    }

    public AbstServiceStep() {
        this.cached = MapCached.getInstance();
    }

    public AbstServiceStep(BridgeAcrossService bridgeAcrossService) {
        this();
        this.bridgeAcrossService = bridgeAcrossService;
    }

    @Override
    public ICaching getCached() {
        return cached;
    }

    public void setCached(ICaching cached) {
        this.cached = cached;
    }

    /**
     * 创建httpclient
     */
    public final CloseableHttpClient createHttpClient(boolean ignoreSSL) throws Exception {
        SSLConnectionSocketFactory sslConnectionSocketFactory = null;
        if (ignoreSSL) {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) {
                    return true;
                }
            }).build();
            HostnameVerifier instance = NoopHostnameVerifier.INSTANCE;
            sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, instance);
        }
        return sslConnectionSocketFactory == null ? HttpClients.createDefault() : HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
    }

    public String httpPost(String url, String json) {
        return this.httpPost(url, json, false);
    }

    private String httpPost(String url, String json, boolean ignoreSSL) {
        try (CloseableHttpClient httpClient = this.createHttpClient(ignoreSSL)) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Accept", "*/*");
            httpPost.addHeader("Accept-Encoding", "gzip,deflate");
            httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
            httpPost.addHeader("Cache-Control", "no-cache");
            httpPost.addHeader("Connection", "keep-alive");
            httpPost.addHeader("Pragma", "no-cache");
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("charset", "utf-8");
            httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64 X64)");
            if (ignoreSSL) {
                httpPost.addHeader("Accept", "application/json");
                HttpEntity se = new ByteArrayEntity(json.getBytes("utf-8"));
                httpPost.setEntity(se);
                CloseableHttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String body = entity != null ? EntityUtils.toString(entity, "utf-8") : "";
                response.close();
                return body;
            }
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }
        return "";
    }

    public String httpPost(String url, Map<String, String> form) {
        return this.httpPost(url, form, false);
    }

    public String httpPost(String url, Map<String, String> form, boolean ignoreSSL) {
        try (CloseableHttpClient httpClient = this.createHttpClient(ignoreSSL)) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Accept", "*/*");
            httpPost.addHeader("Accept-Encoding", "gzip,deflate");
            httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
            httpPost.addHeader("Cache-Control", "no-cache");
            httpPost.addHeader("Connection", "keep-alive");
            httpPost.addHeader("Pragma", "no-cache");
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("charset", "utf-8");
            httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64 X64)");

            List<NameValuePair> nameValuePairList = new ArrayList<>(form.size());
            for (Map.Entry<String, String> kv : form.entrySet()) {
                nameValuePairList.add(new BasicNameValuePair(kv.getKey(), kv.getValue()));
            }

            HttpEntity formEntity = new UrlEncodedFormEntity(nameValuePairList);
            httpPost.setEntity(formEntity);
            HttpResponse response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }
        return "";
    }


    public String httpGet(String url) {
        return this.httpGet(url, false);
    }

    public String httpGet(String url, boolean ignoreSSL) {
        try (CloseableHttpClient httpClient = this.createHttpClient(ignoreSSL)) {
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Accept", "*/*");
            httpGet.addHeader("Accept-Encoding", "gzip,deflate");
            httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
            httpGet.addHeader("Cache-Control", "no-cache");
            httpGet.addHeader("Connection", "keep-alive");
            httpGet.addHeader("Pragma", "no-cache");
            httpGet.addHeader("Content-Type", "application/json");
            httpGet.addHeader("charset", "utf-8");
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64 X64)");
            if (ignoreSSL) {
                httpGet.addHeader("Accept", "application/json");
                CloseableHttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                String body = entity != null ? EntityUtils.toString(entity, "utf-8") : "";
                response.close();
                return body;
            }
            HttpResponse response = httpClient.execute(httpGet);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }
        return "";
    }


    /**
     * 二分查找
     */
    public static int binarySearchBasic(int[] array, int target) {
        int i = 0;
        int j = array.length - 1;
        while (i <= j) {
            int m = (i + j) >>> 1;
            if (target < array[m]) {
                j = m - 1;
            } else if (target > array[m]) {
                i = m + 1;
            }else {
                return m;
            }
        }
        return -1;
    }

}
