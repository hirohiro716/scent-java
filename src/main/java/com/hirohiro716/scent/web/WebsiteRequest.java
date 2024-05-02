package com.hirohiro716.scent.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.hirohiro716.scent.StringObject;

/**
 * WEBサイトへリクエストするクラス。
 * 
 * @author hiro
 *
 */
public class WebsiteRequest {
    
    /**
     * コンストラクタ。<br>
     * リクエストを送信するURLを指定する。
     * 
     * @param url
     */
    public WebsiteRequest(String url) {
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
    
    /**
     * リクエストを送信して結果を取得する。
     * 
     * @return 結果。
     * @throws IOException
     */
    public String sendAndGetResult() throws IOException {
        URL url = new URL(this.url);
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(this.connectTimeoutMillisecond);
        connection.setReadTimeout(this.requestTimeoutMillisecond);
        StringObject body = new StringObject();
        try (InputStreamReader streamReader = new InputStreamReader(connection.getInputStream(), this.charsetName)) {
            try (BufferedReader bufferedReader = new BufferedReader(streamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    body.append(line);
                    body.append("\n");
                }
            }
        }
        return body.toString();
    }
}
