package com.hirohiro716.scent.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import com.hirohiro716.scent.IdentifiableEnum;
import com.hirohiro716.scent.StringObject;

/**
 * WEBサイトへリクエストするクラス。
 */
public class WebsiteRequester {
    
    /**
     * コンストラクタ。<br>
     * リクエストを送信するURLを指定する。
     * 
     * @param url
     */
    public WebsiteRequester(String url) {
        this.url = url;
    }
    
    private String url;
    
    /**
     * リクエストを送信するURLを指定する。
     * 
     * @param url
     */
    public void setURL(String url) {
        this.url = url;
    }
    
    /**
     * リクエストを送信するURLを取得する。
     * 
     * @return
     */
    public String getURL() {
        return this.url;
    }
    
    private static CookieManager cookieManager = new CookieManager();

    private String charsetName = "UTF-8";
    
    /**
     * 使用する文字コードを指定する。
     * 
     * @param charsetName
     */
    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }
    
    /**
     * 使用する文字コードを取得する。
     * 
     * @return
     */
    public String getCharsetName() {
        return this.charsetName;
    }
    
    private int connectTimeoutMillisecond = 0;
    
    /**
     * サーバーとの通信の接続が確立するまでにかかる時間の上限を設定する。
     * 
     * @param millisecond
     */
    public void setConnectTimeoutMillisecond(int millisecond) {
        this.connectTimeoutMillisecond = millisecond;
    }
    
    private int requestTimeoutMillisecond = 0;
    
    /**
     * リクエストしてからレスポンスが返ってくるまでの時間の上限を設定する。
     * 
     * @param millisecond
     */
    public void setRequestTimeoutMillisecond(int millisecond) {
        this.requestTimeoutMillisecond = millisecond;
    }

    private Method method = Method.GET;

    /**
     * リクエストメソッドを取得する。
     * 
     * @return
     */
    public Method getMethod() {
        return this.method;
    }

    /**
     * リクエストメソッドを設定する。初期値はGET。
     * 
     * @param method
     */
    public void setMethod(Method method) {
        this.method = method;
    }

    private String contentType = null;

    /**
     * リクエストの"content-type"を取得する。
     * 
     * @return
     */
    public String getContentType() {
        return this.contentType;
    }

    /**
     * リクエストの"content-type"を設定する。初期値は"application/x-www-form-urlencoded"。<br>
     * 
     * @param contentType "application/json"や"application/x-www-form-urlencoded"など。
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    /**
     * 送信するURLに対して本文を指定してリクエストを送信し、その結果を取得する。
     * 
     * @param url
     * @param body
     * @return
     * @throws IOException
     */
    private Response request(URL url, String body) throws IOException {
        CookieHandler.setDefault(WebsiteRequester.cookieManager);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(this.connectTimeoutMillisecond);
        connection.setReadTimeout(this.requestTimeoutMillisecond);
        connection.setRequestMethod(this.method.getID().toUpperCase());
        StringObject contentType = new StringObject(this.contentType);
        if (contentType.clone().lower().toString().contains("charset") == false) {
            if (contentType.length() > 0) {
                contentType.append("; ");
            }
            contentType.append("charset=");
            contentType.append(this.charsetName);
        }
        connection.addRequestProperty("Content-Type", contentType.toString());
        connection.setDoInput(true);
        StringObject bodyStringObject = new StringObject(body);
        if (bodyStringObject.length() > 0) {
            connection.setDoOutput(true);
            try (OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream(), "UTF-8")) {
                streamWriter.write(bodyStringObject.toString());
            }
        } else {
            connection.setDoOutput(false);
        }
        int code = connection.getResponseCode();
        InputStream inputStream = null;
        if (code < 400) {
            inputStream = connection.getInputStream();
        } else {
            inputStream = connection.getErrorStream();
        }
        return new Response(code, inputStream, this.charsetName);
    }

    /**
     * 送信する本文を指定してリクエストを送信し、その結果を取得する。
     * 
     * @param body
     * @return
     * @throws IOException
     */
    public Response request(String body) throws IOException {
        URL url = new URL(this.url);
        return this.request(url, body);
    }

    /**
     * リクエストを送信して結果を取得する。
     * 
     * @return
     * @throws IOException
     */
    public Response request() throws IOException {
        return this.request("");
    }

    /**
     * 送信するパラメーターを指定してリクエストを送信、その結果を取得する。
     * 
     * @param requestParameters
     * @return
     * @throws IOException
     */
    public Response request(Map<String, String> requestParameters) throws IOException {
        StringObject parameters = new StringObject();
        for (String key: requestParameters.keySet()) {
            if (parameters.length() > 0) {
                parameters.append("&");
            }
            parameters.append(URLEncoder.encode(key, "UTF-8"));
            parameters.append("=");
            parameters.append(URLEncoder.encode(requestParameters.get(key), "UTF-8"));
        }
        switch (this.method) {
            case GET:
            case DELETE:
                return this.request(new URL(StringObject.join(this.url, "?", parameters).toString()), "");
            case POST:
            case PUT:
                break;
        }
        return this.request(parameters.toString());
    }

    /**
     * リクエストメソッドの列挙型。
     */
    public enum Method implements IdentifiableEnum<String> {
        /**
         * GETメソッドを使用して送信。
         */
        GET("get", "GETメソッドを使用して送信"),
        /**
         * POSTメソッドを使用して送信。。
         */
        POST("post", "POSTメソッドを使用して送信"),
        /**
         * PUTメソッドを使用して送信。
         */
        PUT("put", "PUTメソッドを使用して送信"),
        /**
         * DELETEメソッドを使用して送信。
         */
        DELETE("delete", "DELETEメソッドを使用して送信"),
        ;
        
        /**
         * コンストラクタ。<br>
         * ID、名前を指定する。
         * 
         * @param id
         * @param name 
         */
        private Method(String id, String name) {
            this.id = id;
            this.name = name;
        }
        
        private String id;
        
        @Override
        public String getID() {
            return this.id;
        }
        
        private String name;

        @Override
        public String getName() {
            return this.name;
        }
        
        /**
         * 指定されたIDに該当する列挙子を取得する。該当するものがない場合はnullを返す。
         * 
         * @param id 
         * @return
         */
        public static Method enumOf(String id) {
            return IdentifiableEnum.enumOf(id, Method.class);
        }
    }

    /**
     * レスポンスのクラス。
     */
    public static class Response {

        /**
         * コンストラクタ。
         * 
         * @param code レスポンスコード。
         * @param inputStream レスポンスのストリーム。
         * @param charsetName 使用する文字コード。
         */
        public Response(int code, InputStream inputStream, String charsetName) {
            this.code = code;
            this.inputStream = inputStream;
            this.charsetName = charsetName;
        }

        private int code;

        private InputStream inputStream;

        private String charsetName;

        /**
         * レスポンスコードを取得する。
         * 
         * @return
         */
        public int getCode() {
            return  this.code;
        }

        private String bodyText = null;

        /**
         * レスポンスのボディを文字列として取得する。
         * 
         * @return
         */
        public String getBodyAsString() {
            try (InputStreamReader streamReader = new InputStreamReader(inputStream, this.charsetName)) {
                try (BufferedReader bufferedReader = new BufferedReader(streamReader)) {
                    StringObject resultBody = new StringObject();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        resultBody.append(line);
                        resultBody.append("\n");
                    }
                    this.bodyText = resultBody.toString();
                    return resultBody.toString();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return null;
        }

        /**
         * レスポンスのボディをInputStreamとして取得する。
         * 
         * @return
         */
        public InputStream getBodyAsInputStream() {
            return this.inputStream;
        }

        @Override
        public String toString() {
            StringObject stringObject = new StringObject("code: ");
            stringObject.append(this.code);
            stringObject.append("\n");
            stringObject.append("body: ");
            if (this.bodyText != null) {
                stringObject.append(this.bodyText);
            } else {
                stringObject.append("[Stream Data]");
            }
            return stringObject.toString();
        }
    }
}
