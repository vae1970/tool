package com.vae1970.tool.util;

import org.apache.http.Header;
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
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * @author dongzhou.gu
 * @date 2019/10/24
 */
public class HttpUtil {
    /**
     * 两层map，第一层为domain，第二层为user
     */
    private static final ConcurrentObject<Map<String, Map<String, HttpClientContext>>> CONTEXT_MAP = new ConcurrentObject<>(new HashMap<>());

    private static final Charset ENCODING = StandardCharsets.UTF_8;
    private static final int CONNECT_TIMEOUT = 6000;
    private static final int SOCKET_TIMEOUT = 6000;

    static {
        CONTEXT_MAP.set(map -> {
            String domain = "http://127.0.0.1:3000/s";

            Map<String, HttpClientContext> stringHttpClientContextMap = null;
            try {
                stringHttpClientContextMap = map.computeIfAbsent(new URIBuilder(domain).getHost(), k -> new HashMap<>(16));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            CookieStore cookieStore = new BasicCookieStore();
            BasicClientCookie cookie1 = new BasicClientCookie("MUSIC_U", "4fcdf12fbb765d3fb6915760a74f0fecc6b337a95149f5f960b4db66c2b3604cabbae36d280a3d122d3c3f94f9a73db07955a739ab43dce1");
            cookie1.setDomain(domain);
            cookie1.setPath("/");
            cookieStore.addCookie(cookie1);

            BasicClientCookie cookie2 = new BasicClientCookie("__csrf", "ba5864537e99876ccf93f5f4f7b74cdb");
            cookie2.setDomain(domain);
            cookie2.setPath("/");
            cookieStore.addCookie(cookie2);

            BasicClientCookie cookie3 = new BasicClientCookie("__remember_me", "true");
            cookie3.setDomain(domain);
            cookie3.setPath("/");
            cookieStore.addCookie(cookie3);

            HttpClientContext context = HttpClientContext.create();
            Registry<CookieSpecProvider> registry = RegistryBuilder
                    .<CookieSpecProvider>create()
                    .register(CookieSpecs.DEFAULT, new DefaultCookieSpecProvider())
                    .build();
            context.setCookieSpecRegistry(registry);
            context.setCookieStore(cookieStore);
            stringHttpClientContextMap.put("65656416", context);
            return "";
        });
    }

    /**
     * HTTP GET method
     *
     * @param params params
     * @param uri    uri
     * @return ResponseEntity<String>
     */
    public static ResponseEntity<String> doGet(Map<String, String> params, String uri, String userKey
            , Function<String, String> userKeyFunction) {
        return doGet(null, params, uri, userKey, userKeyFunction);
    }

    /**
     * HTTP GET method
     *
     * @param headers headers
     * @param params  params
     * @param uri     uri
     * @return ResponseEntity<String>
     */
    public static ResponseEntity<String> doGet(Map<String, String> headers, Map<String, String> params, String uri
            , String userKey, Function<String, String> userKeyFunction) {
        return doGet(headers, params, uri, CONNECT_TIMEOUT, SOCKET_TIMEOUT, ENCODING, userKey, userKeyFunction);
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
    public static ResponseEntity<String> doGet(Map<String, String> headers, Map<String, String> params, String uri
            , int connectTimeout, int socketTimeout, Charset charset, String userKey
            , Function<String, String> userKeyFunction) {
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
            HttpClientContext context = getContext(uriBuilder, userKey);
            CloseableHttpResponse response = httpClient.execute(httpGet, context);
            HttpStatus httpStatus = Optional.ofNullable(response.getStatusLine()).map(StatusLine::getStatusCode)
                    .map(HttpStatus::resolve).orElse(null);
            String responseString;
            if (userKeyFunction != null) {
                responseString = updateContext(response, uriBuilder, charset, userKeyFunction);
            } else {
                responseString = EntityUtils.toString(response.getEntity(), charset);
            }
            return httpStatus == null ? ResponseEntity.badRequest().build() : new ResponseEntity<>(responseString, httpStatus);
        } catch (IOException | URISyntaxException e) {
//            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    private static String updateContext(HttpResponse httpResponse, URIBuilder uriBuilder, Charset charset, Function<String, String> userKeyFunction) {
        return CONTEXT_MAP.set(map -> {
            String domain = uriBuilder.getHost();
            Map<String, HttpClientContext> stringHttpClientContextMap = map.computeIfAbsent(domain, k -> new HashMap<>(16));
            CookieStore cookieStore = getCookie(httpResponse, uriBuilder);
            HttpClientContext context = HttpClientContext.create();
            Registry<CookieSpecProvider> registry = RegistryBuilder
                    .<CookieSpecProvider>create()
                    .register(CookieSpecs.DEFAULT, new DefaultCookieSpecProvider())
                    .build();
            context.setCookieSpecRegistry(registry);
            context.setCookieStore(cookieStore);
            try {
                String s = EntityUtils.toString(httpResponse.getEntity(), charset);
                stringHttpClientContextMap.put(userKeyFunction.apply(s), context);
                return s;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    private static HttpClientContext getContext(URIBuilder uriBuilder, String userKey) {
        return CONTEXT_MAP.get(map -> Optional.of(map).map(i -> i.get(uriBuilder.getHost())).map(i -> i.get(userKey)).orElse(null));
    }

    private static CookieStore getCookie(HttpResponse httpResponse, URIBuilder uriBuilder) {
        CookieStore cookieStore = new BasicCookieStore();
        Header[] headers = httpResponse.getHeaders("Set-Cookie");
        if (headers != null) {
            for (Header header : headers) {
                Optional.ofNullable(header.getValue()).map(cookie -> cookie.split(";")).map(s -> s[0])
                        .map(cookie -> cookie.split("=")).ifPresent(kv -> {
                            if (kv.length == 2) {
                                BasicClientCookie cookie = new BasicClientCookie(kv[0], kv[1]);
                                System.out.println(kv[0] + "   " + kv[1]);
                                cookie.setDomain(uriBuilder.getHost());
                                cookie.setPath(uriBuilder.getPath());
                                cookieStore.addCookie(cookie);
                            }
                        }
                );
            }
        }
        return cookieStore;
    }

}
