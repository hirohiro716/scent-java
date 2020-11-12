package com.hirohiro716;

/**
 * 例外から詳細メッセージを作成するクラス。
 * 
 * @author hiro
 *
 */
public class ExceptionMessenger {
    
    /**
     * コンストラクタ。
     * 
     * @param exception
     */
    public ExceptionMessenger(Exception exception) {
        this.exception = exception;
    }
    
    private Exception exception;
    
    /**
     * 新しい主要メッセージと、コンストラクタで指定した例外からメッセージを作成する。
     * 
     * @param mainMessage
     * @return 結果。
     */
    public String make(String mainMessage) {
        this.exception.printStackTrace();
        StringObject message = new StringObject(mainMessage);
        if (message.length() > 0) {
            message.append(OS.thisOS().getLineSeparator());
            message.append(OS.thisOS().getLineSeparator());
        }
        message.append("An exception occurred in ");
        StackTraceElement stackTraceElement = this.exception.getStackTrace()[this.exception.getStackTrace().length - 1];
        message.append(stackTraceElement.getClassName());
        message.append(":");
        message.append(stackTraceElement.getLineNumber());
        message.append(OS.thisOS().getLineSeparator());
        if (this.exception.getMessage() != null && this.exception.getMessage().length() > 0) {
            message.append(this.exception.getClass().getName());
            message.append(": ");
            message.append(this.exception.getMessage());
        } else {
            message.append(this.exception.getClass().getName());
        }
        return message.toString();
    }
    
    /**
     * コンストラクタで指定した例外からメッセージを作成する。
     * 
     * @return 結果。
     */
    public String make() {
        return this.make(null);
    }
    
    /**
     * このメソッドはコンストラクタの呼び出しと同じで、新しいインスタンスを作成する。
     * 
     * @param exception 
     * @return 新しいインスタンス。
     */
    public static ExceptionMessenger newInstance(Exception exception) {
        return new ExceptionMessenger(exception);
    }
}
