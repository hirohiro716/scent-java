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
     * @return 結果。
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
     * @return 結果。
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
     * @return 結果。
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
     * @return 結果。
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
     * @param requestBody
     * @return 結果。
     * @throws IOException
     */
    public String sendAndGetResult(String requestBody) throws IOException {
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
                    StringObject body = new StringObject(requestBody);
                    streamWriter.write(body.toString());
                }
                break;
        }
        StringObject resultBody = new StringObject();
        try (InputStreamReader streamReader = new InputStreamReader(connection.getInputStream(), this.charsetName)) {
            try (BufferedReader bufferedReader = new BufferedReader(streamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    resultBody.append(line);
                    resultBody.append("\n");
                }
            }
        }
        return resultBody.toString();
    }

    /**
     * POST送信するパラメーターを指定してリクエストを送信し、その結果を取得する。
     * 
     * @param postRequestParameters
     * @return 結果。
     * @throws IOException
     */
    public String sendAndGetResult(Map<String, String> postRequestParameters) throws IOException {
        StringObject body = new StringObject();
        for (String key : postRequestParameters.keySet()) {
            if (body.length() > 0) {
                body.append("&");
            }
            body.append(URLEncoder.encode(key, this.charsetName));
            body.append("=");
            body.append(URLEncoder.encode(postRequestParameters.get(key), this.charsetName));
        }
        return this.sendAndGetResult(body.toString());
    }

    /**
     * リクエストを送信して結果を取得する。
     * 
     * @return 結果。
     * @throws IOException
     */
    public String sendAndGetResult() throws IOException {
        return this.sendAndGetResult("");
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
         * @return 結果。
         */
        public static Method enumOf(String id) {
            return IdentifiableEnum.enumOf(id, Method.class);
        }
    }
}
