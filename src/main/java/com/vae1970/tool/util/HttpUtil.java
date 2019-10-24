package com.vae1970.tool.util;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author dongzhou.gu
 * @date 2019/10/24
 */
//@SuppressWarnings("WeakerAccess")
public class HttpUtil {
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
            CloseableHttpClient httpClient = HttpClients.createDefault();
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
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            HttpStatus httpStatus = Optional.ofNullable(response.getStatusLine()).map(StatusLine::getStatusCode)
                    .map(HttpStatus::resolve).orElse(null);
            return httpStatus == null ? ResponseEntity.badRequest().build()
                    : new ResponseEntity<>(EntityUtils.toString(entity, charset), httpStatus);
        } catch (IOException | URISyntaxException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
