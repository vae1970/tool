package com.vae1970.tool.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author dongzhou.gu
 * @date 2019/10/24
 */
public class HttpUtil {
    /**
     * 两层map，第一层为domain，第二层为user
     */
    private static final ConcurrentObject<Map<String, Map<String, HttpClientContext>>> CONTEXT_MAP = new ConcurrentObject<>(new HashMap<>());


    private static CookieStore cookieStore = null;
    private static HttpClientContext context = null;
    private static final Charset ENCODING = StandardCharsets.UTF_8;
    private static final int CONNECT_TIMEOUT = 6000;
    private static final int SOCKET_TIMEOUT = 6000;

    public static ResponseEntity<String> doGet(String uri) {
        return doGet(null, null, uri, CONNECT_TIMEOUT, SOCKET_TIMEOUT, ENCODING);
    }

    /**
     * HTTP GET method
     *
     * @param params params
     * @param uri    uri
     * @return ResponseEntity<String>
     */
    public static ResponseEntity<String> doGet(Map<String, String> params, String uri) {
        return doGet(null, params, uri);
    }

    /**
     * HTTP GET method
     *
     * @param headers headers
     * @param params  params
     * @param uri     uri
     * @return ResponseEntity<String>
     */
    public static ResponseEntity<String> doGet(Map<String, String> headers, Map<String, String> params, String uri) {
        return doGet(headers, params, uri, CONNECT_TIMEOUT, SOCKET_TIMEOUT, ENCODING);
    }

    /**
     * HTTP GET method
     *
     * @param headers        headers
     * @param params         params
     * @param uri            uri
     * @param connectTimeout connectTimeout
     * @param socketTimeout  socketTimeout
     * @param charset        charset
     * @return ResponseEntity<String>
     */
    public static ResponseEntity<String> doGet(Map<String, String> headers, Map<String, String> params, String uri, int connectTimeout, int socketTimeout, Charset charset) {
        try {
            CloseableHttpClient httpClient = HttpClients.custom().build();
            URIBuilder uriBuilder = new URIBuilder(uri);
            if (params != null) {
                Set<Map.Entry<String, String>> entrySet = params.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    uriBuilder.setParameter(entry.getKey(), entry.getValue());
                }
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout)
                    .setSocketTimeout(socketTimeout).build();
            httpGet.setConfig(requestConfig);
            if (headers != null) {
                Set<Map.Entry<String, String>> entrySet = headers.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    httpGet.setHeader(entry.getKey(), entry.getValue());
                }
            }
            CloseableHttpResponse response = httpClient.execute(httpGet, new HttpClientContext());

            response.getEntity();

            HttpEntity entity = response.getEntity();
            HttpStatus httpStatus = Optional.ofNullable(response.getStatusLine()).map(StatusLine::getStatusCode)
                    .map(HttpStatus::resolve).orElse(null);


            Header firstHeader = response.getFirstHeader("Set-Cookie");
            System.out.println(JSONObject.toJSONString(firstHeader));
            setCookieStore(response, uri);
            // context
            setContext();
            return httpStatus == null ? ResponseEntity.badRequest().build()
                    : new ResponseEntity<>(EntityUtils.toString(entity, charset), httpStatus);
        } catch (IOException | URISyntaxException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public static void setContext() {
        context = HttpClientContext.create();
        Registry<CookieSpecProvider> registry = RegistryBuilder
                .<CookieSpecProvider>create()
                .register(CookieSpecs.DEFAULT, new BestMatchSpecFactory())
                .register(CookieSpecs.BROWSER_COMPATIBILITY,
                        new BrowserCompatSpecFactory()).build();
        context.setCookieSpecRegistry(registry);
        context.setCookieStore(cookieStore);
    }

    public static void setCookieStore(HttpResponse httpResponse, String host) {
        cookieStore = new BasicCookieStore();
        // JSESSIONID
        if (null == httpResponse.getFirstHeader("Set-Cookie")) {
            cookieStore = null;
        } else {
            String setCookie = httpResponse.getFirstHeader("Set-Cookie").getValue();
            String JSESSIONID = setCookie.substring("JSESSIONID=".length(), setCookie.indexOf(";"));
            // 新建一个Cookie
            BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", JSESSIONID);
            cookie.setVersion(0);
            cookie.setDomain(host);
            cookie.setPath("/");
            cookieStore.addCookie(cookie);
        }
    }

    public void updateContext(HttpResponse httpResponse, String domain, String userKey) {
        CONTEXT_MAP.set(map -> {
            Map<String, HttpClientContext> stringHttpClientContextMap = map.get(domain);

        });


    }


    public CookieStore getCookie(HttpResponse httpResponse, String domain) {
        CookieStore cookieStore = new BasicCookieStore();
        Header[] headers = httpResponse.getHeaders("Set-Cookie");
        if (headers != null) {
            for (Header header : headers) {
                Optional.ofNullable(header.getValue()).map(cookie -> cookie.split(";")).map(s -> s[0])
                        .map(cookie -> cookie.split("=")).ifPresent(kv -> {
                            if (kv.length == 2) {
                                BasicClientCookie cookie = new BasicClientCookie(kv[0], kv[1]);
                                cookie.setDomain(domain);
                                cookie.setPath("/");
                                cookieStore.addCookie(cookie);
                            }
                        }
                );
            }
        }
        return cookieStore;
    }


}
