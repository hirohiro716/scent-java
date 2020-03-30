package com.hirohiro716.email;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.hirohiro716.StringObject;
import com.hirohiro716.filesystem.File;
import com.hirohiro716.reflection.DynamicClass;
import com.hirohiro716.reflection.Method;

/**
 * JavaMailを使用してE-mailを送信するクラス。 
 * 
 * @author hiro
 *
 */
public class EmailTransmitter extends DynamicClass {
    
    /**
     * コンストラクタ。<br>
     * 下記のjarファイルを指定する。<br>
     * ・JavaMail - <a href="https://javaee.github.io/javamail/">https://javaee.github.io/javamail/</a><br>
     * ・JavaBeans Activation Framework - <a href="https://github.com/javaee/activation/">https://github.com/javaee/activation/</a>
     * 
     * @param javamailLibraryJar
     * @param activationLibraryJar
     */
    public EmailTransmitter(File javamailLibraryJar, File activationLibraryJar) {
        super(javamailLibraryJar, activationLibraryJar);
    }
    
    /**
     * E-mail本文に使用する改行コード。
     */
    public static final String LINE_SEPARATOR = "\r\n";
    
    private String myAddress;
    
    /**
     * 送信元のE-mailアドレスをセットする。
     * 
     * @param myAddress
     */
    public void setMyAddress(String myAddress) {
        this.myAddress = myAddress;
    }
    
    private String host;
    
    /**
     * E-mail送信を行うホストをセットする。
     * 
     * @param host
     */
    public void setHost(String host) {
        this.host = host;
    }
    
    private String user;
    
    /**
     * ホストに認証を行うユーザーをセットする。
     * 
     * @param user
     */
    public void setUser(String user) {
        this.user = user;
    }
    
    private String password;
    
    /**
     * ホストに認証を行うパスワードをセットする。
     * 
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    private String portNumber = "25";
    
    /**
     * E-mail送信を行うポート番号をセットする。初期値は25番ポート。
     * 
     * @param portNumber
     */
    public void setPortNumber(int portNumber) {
        this.portNumber = String.valueOf(portNumber);
    }
    
    private boolean isEnableTLS = false;
    
    /**
     * ホストとの通信にTLSを使用する場合はtrueをセットする。初期値はfalse。
     * 
     * @param isEnableTLS
     */
    public void setEnableTLS(boolean isEnableTLS) {
        this.isEnableTLS = isEnableTLS;
    }
    
    private Map<ReceiverType, List<String>> toAddresses = new HashMap<>();
    
    /**
     * 送信先のE-mailアドレスを追加する。
     * 
     * @param toAddress
     * @param receiverType
     */
    public void addToAddress(String toAddress, ReceiverType receiverType) {
        if (this.toAddresses.containsKey(receiverType) == false) {
            this.toAddresses.put(receiverType, new ArrayList<>());
        }
        this.toAddresses.get(receiverType).add(toAddress);
    }
    
    private String charset = "UTF-8";
    
    /**
     * 使用する文字セットをセットする。"UTF-8"が初期値。
     * 
     * @param charset
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }
    
    private boolean isEnableDebug = false;
    
    /**
     * デバッグを有効にする場合はtrueをセットする。
     * 
     * @param isEnableDebug
     */
    public void setEnableDebug(boolean isEnableDebug) {
        this.isEnableDebug = isEnableDebug;
    }
    
    /**
     * 複数のE-mailアドレスの文字列をInternetAddressのインスタンスにパースする。
     * 
     * @param emailAddresses
     * @return 結果。
     * @throws Exception
     */
    private Object stringToInternetAddress(String... emailAddresses) throws Exception {
        Method method = new Method(this.loadClass("javax.mail.internet.InternetAddress"));
        Object addresses = method.invoke("parse", (Object[]) emailAddresses);
        return addresses;
    }
    
    /**
     * 指定された表題と本文のE-mailを送信する。
     * 
     * @param subject
     * @param body
     * @throws Exception
     */
    public void send(String subject, String body) throws Exception {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", this.host);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", this.portNumber);
        if (this.isEnableTLS) {
            properties.put("mail.smtp.starttls.required", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.socketFactory.fallback", "true");
        }
        // Session
        Method methodGetInstance = new Method(this.loadClass("javax.mail.Session"));
        Object session = methodGetInstance.invoke("getInstance", properties);
        // Debug
        Method methodSetDebug = new Method(session);
        methodSetDebug.setParameterTypes(boolean.class);
        methodSetDebug.invoke("setDebug", this.isEnableDebug);
        // MimeMessage
        Constructor constructorMimeMessage = new Constructor("javax.mail.internet.MimeMessage");
        Object mimeMessage = constructorMimeMessage.newInstance(session);
        // From
        Method methodMessage = new Method(this.loadClass("javax.mail.Message"), mimeMessage);
        if (StringObject.newInstance(this.myAddress).length() > 0) {
            methodMessage.setParameterTypes(this.loadClass("[Ljavax.mail.Address;"));
            methodMessage.invoke("addFrom", this.stringToInternetAddress(this.myAddress));
        }
        // RecipientType
        Class<?> classMessage = this.loadClass("javax.mail.Message");
        Class<?> classRecipientType = this.loadClass("RecipientType", classMessage);
        Map<String, Object> recipientTypes = this.getEnumConstants("RecipientType", classMessage);
        Object to = recipientTypes.get("To");
        Object cc = recipientTypes.get("Cc");
        Object bcc = recipientTypes.get("Bcc");
        // To
        List<String> emailAddresses = this.toAddresses.get(ReceiverType.TO);
        if (emailAddresses != null && emailAddresses.size() > 0) {
            methodMessage.setParameterTypes(classRecipientType, this.loadClass("[Ljavax.mail.Address;"));
            methodMessage.invoke("addRecipients", to, this.stringToInternetAddress(StringObject.joinWithSeparator(emailAddresses.toArray(new Object[] {}), ",").toString()));
        }
        // CC
        emailAddresses = this.toAddresses.get(ReceiverType.CC);
        if (emailAddresses != null && emailAddresses.size() > 0) {
            methodMessage.setParameterTypes(classRecipientType, this.loadClass("[Ljavax.mail.Address;"));
            methodMessage.invoke("addRecipients", cc, this.stringToInternetAddress(StringObject.joinWithSeparator(emailAddresses.toArray(new Object[] {}), ",").toString()));
        }
        // BCC
        emailAddresses = this.toAddresses.get(ReceiverType.BCC);
        if (emailAddresses != null && emailAddresses.size() > 0) {
            methodMessage.setParameterTypes(classRecipientType, this.loadClass("[Ljavax.mail.Address;"));
            methodMessage.invoke("addRecipients", bcc, this.stringToInternetAddress(StringObject.joinWithSeparator(emailAddresses.toArray(new Object[] {}), ",").toString()));
        }
        // Subject
        Method methodMimeMmessage = new Method(mimeMessage);
        methodMimeMmessage.invoke("setSubject", subject, this.charset);
        // Body
        StringObject bodyObject = new StringObject(body);
        bodyObject.replaceCR(LINE_SEPARATOR);
        bodyObject.replaceLF(LINE_SEPARATOR);
        methodMimeMmessage.invoke("setText", body.toString(), this.charset);
        // Send
        Method methodTransport = new Method(this.loadClass("javax.mail.Transport"));
        methodTransport.setParameterTypes(classMessage, String.class, String.class);
        methodTransport.invoke("send", mimeMessage, this.user, this.password);
    }
    
    /**
     * 受信者タイプの列挙型。
     * 
     * @author hiro
     */
    public enum ReceiverType {
        /**
         * 宛先。
         */
        TO,
        /**
         * CC。
         */
        CC,
        /**
         * BCC。
         */
        BCC,
    }
}
