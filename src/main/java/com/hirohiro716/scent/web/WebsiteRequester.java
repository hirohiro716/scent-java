package com.hirohiro716.scent.web;

import java.io.BufferedReader;
import java.io.IOException;
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
 * 
 * @author hiro
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

    private String contentType = "application/x-www-form-urlencoded";

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
     * 送信する本文を指定してリクエストを送信し、その結果を取得する。
     * 
     * @param body
     * @return
     * @throws IOException
     */
    public Response request(String body) throws IOException {
        CookieHandler.setDefault(WebsiteRequester.cookieManager);
        URL url = new URL(this.url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(this.connectTimeoutMillisecond);
        connection.setReadTimeout(this.requestTimeoutMillisecond);
        connection.setRequestMethod(this.method.getID().toUpperCase());
        connection.setDoInput(true);
        switch (this.method) {
            case GET:
                connection.setDoOutput(false);
                break;
            case POST:
                connection.setDoOutput(true);
                try (OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream(), this.charsetName)) {
                    streamWriter.write(StringObject.newInstance(body).toString());
                }
                break;
        }
        try (InputStreamReader streamReader = new InputStreamReader(connection.getInputStream(), this.charsetName)) {
            try (BufferedReader bufferedReader = new BufferedReader(streamReader)) {
                StringObject resultBody = new StringObject();
                int code = connection.getResponseCode();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    resultBody.append(line);
                    resultBody.append("\n");
                }
                return new Response(code, resultBody.toString());
            }
        }
    }

    /**
     * POST送信するパラメーターを指定してリクエストを送信し、その結果を取得する。
     * 
     * @param postRequestParameters
     * @return
     * @throws IOException
     */
    public Response request(Map<String, String> postRequestParameters) throws IOException {
        StringObject body = new StringObject();
        for (String key : postRequestParameters.keySet()) {
            if (body.length() > 0) {
                body.append("&");
            }
            body.append(URLEncoder.encode(key, this.charsetName));
            body.append("=");
            body.append(URLEncoder.encode(postRequestParameters.get(key), this.charsetName));
        }
        return this.request(body.toString());
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
     * リクエストメソッドの列挙型。
     * 
     * @author hiro
     */
    public enum Method implements IdentifiableEnum<String> {
        /**
         * GETメソッドを使用して送信。
         */
        GET("get", "GETメソッドを使用して送信"),
        /**
         * POST。
         */
        POST("post", "POSTメソッドを使用して送信"),
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
         * @param body レスポンスのボディ。
         */
        public Response(int code, String body) {
            this.code = code;
            this.body = body;
        }

        private int code;

        private String body;

        /**
         * レスポンスコードを取得する。
         * 
         * @return
         */
        public int getCode() {
            return  this.code;
        }

        /**
         * レスポンスのボディを取得する。
         * 
         * @return
         */
        public String getBody() {
            return this.body;
        }

        @Override
        public String toString() {
            StringObject stringObject = new StringObject("code: ");
            stringObject.append(this.code);
            stringObject.append("\n");
            stringObject.append("body: ");
            stringObject.append(this.body);
            return stringObject.toString();
        }
    }
}
