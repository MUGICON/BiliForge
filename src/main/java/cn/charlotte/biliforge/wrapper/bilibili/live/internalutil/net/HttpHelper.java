package cn.charlotte.biliforge.wrapper.bilibili.live.internalutil.net;

import cn.charlotte.biliforge.wrapper.bilibili.live.Globals;
import cn.charlotte.biliforge.wrapper.bilibili.live.I18n;
import cn.charlotte.biliforge.wrapper.bilibili.live.exceptions.BiliLiveException;
import cn.charlotte.biliforge.wrapper.bilibili.live.exceptions.NetworkException;
import cn.charlotte.biliforge.wrapper.bilibili.live.exceptions.NotLoggedInException;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 用于方便Http Client访问API。仅内部使用，可能发生修改。
 * 注意，本类中的大部分'get'意义为Http Get，而非Getter/Setter中的Get!
 *
 * @author Charlie Jiang
 * @since rv1
 */
public class HttpHelper {
    private static final int STATUS_NOT_LOGGED_IN = -101;
    private final SSLContext bilibiliSSLContext = Globals.get().getBilibiliSSLContext();
    private final HttpHost biliLiveRoot = Globals.get().getBiliLiveRoot();
    @Getter
    private HttpClient httpClient;

    public static int getStatusCode(HttpResponse response) {
        return response.getStatusLine().getStatusCode();
    }

    public static InputStream responseToInputStream(HttpResponse response) throws IOException {
        return response.getEntity().getContent();
    }

    public static String responseToString(HttpResponse response) throws IOException {
        String str = EntityUtils.toString(response.getEntity());
        EntityUtils.consume(response.getEntity());
        return str;
    }

    public HttpHelper init(String userAgent) {
        HttpClientBuilder builder = HttpClientBuilder.create()
                .setUserAgent(userAgent)
                .setSslcontext(bilibiliSSLContext);
        init(builder.build());
        return this;
    }

    public void init(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpResponse createPostBiliLiveHost(String url, PostArguments args) throws IOException {
        return createPostResponse(biliLiveRoot, url, args);
    }

    public HttpResponse createPostResponse(HttpHost host, String url, PostArguments args) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(args.toEntity());

        return httpClient.execute(host, httpPost);
    }

    public HttpResponse createGetResponse(HttpHost host, String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);

        return httpClient.execute(host, httpGet);
    }

    public HttpResponse createGetBiliLiveHost(String url) throws IOException {
        return createGetResponse(biliLiveRoot, url);
    }

    public HttpResponse createGetResponse(URL url) throws IOException {
        HttpGet httpGet;
        try {
            httpGet = new HttpGet(url.toURI());
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }

        return httpClient.execute(httpGet);
    }

    public <T> T getJSON(HttpHost httpHost, String url, Class<T> clazz, String exceptionKey) throws BiliLiveException {
        try {
            HttpResponse response = this.createGetResponse(httpHost, url);
            return responseToObject(response, clazz, exceptionKey);
        } catch (IOException e) {
            throw new NetworkException(I18n.getString(exceptionKey), e);
        }
    }

    public <T> T getBiliLiveJSON(String url, Class<T> clazz, String exceptionKey) throws BiliLiveException {
        return getJSON(biliLiveRoot, url, clazz, exceptionKey);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBiliLiveJSONAndCheckLogin(String url, Class<T> clazz, String exceptionKey)
            throws BiliLiveException {
        JsonObject rootObject = getJSON(biliLiveRoot, url, JsonObject.class, exceptionKey);
        if (isNotLoggedIn(rootObject)) throw new NotLoggedInException();
        if (clazz == JsonObject.class) return (T) rootObject;
        return Globals.get().gson().fromJson(rootObject, clazz);
    }

    private boolean isNotLoggedIn(JsonObject rootObject) {
        return rootObject.get("code").getAsInt() == STATUS_NOT_LOGGED_IN;
    }

    public <T> T postJSON(HttpHost httpHost, String url, PostArguments arguments,
                          Class<T> clazz, String exceptionKey) throws BiliLiveException {
        try {
            HttpResponse response = this.createPostResponse(httpHost, url, arguments);
            return responseToObject(response, clazz, exceptionKey);
        } catch (IOException e) {
            throw new NetworkException(I18n.getString(exceptionKey), e);
        }
    }

    public <T> T postBiliLiveJSON(String url, PostArguments arguments,
                                  Class<T> clazz, String exceptionKey) throws BiliLiveException {
        return postJSON(biliLiveRoot, url, arguments, clazz, exceptionKey);
    }

    public <T> T responseToObject(HttpResponse response, Class<T> clazz, String exceptionKey) throws IOException {
        String jsonString = HttpHelper.responseToString(response);

        return Globals.get().gson().fromJson(jsonString, clazz);
    }

    public void executeGet(HttpHost httpHost, String url) throws IOException {
        EntityUtils.consume(this.createGetResponse(httpHost, url).getEntity());
    }

    public void executeBiliLiveGet(String url) throws IOException {
        executeGet(biliLiveRoot, url);
    }
}
