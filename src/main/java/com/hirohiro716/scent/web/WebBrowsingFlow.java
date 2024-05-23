package com.hirohiro716.scent.web;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.hirohiro716.scent.StringObject;
import com.hirohiro716.scent.datetime.Datetime;

/**
 * WEBブラウザへのタスクを順に処理するクラス。
 * 
 * @author hiro
 * 
 * @param <W> WEBブラウザの型。
 * @param <E> WEBブラウザの要素の型。
 */
public class WebBrowsingFlow<W extends WebBrowser<E>, E extends WebBrowser.Element> {
    
    /**
     * コンストラクタ。<br>
     * 使用するWEBブラウザを指定する。
     * 
     * @param webBrowser 
     */
    public WebBrowsingFlow(W webBrowser) {
        this.webBrowser = webBrowser;
    }
    
    private W webBrowser;
    
    private List<Task<W>> tasks = new ArrayList<>();
    
    /**
     * WEBブラウザへのタスクを追加する。
     * 
     * @param task
     */
    public void addTask(Task<W> task) {
        this.tasks.add(task);
    }
    
    private int intervalSecond = 1;
    
    /**
     * 処理する間隔の秒数を指定する。初期値は1秒。
     * 
     * @param second
     */
    public void setInterval(int second) {
        this.intervalSecond = second;
    }

    private int timeoutSecond = 10;
    
    /**
     * タイムアウトの秒数を指定する。初期値は10秒。
     * 
     * @param second
     */
    public void setTimeout(int second) {
        this.timeoutSecond = second;
    }
    
    private ProcessAfterFinishing processAfterFinishing = null;
    
    /**
     * WEBブラウザへのすべてのタスクが終了した際の処理をセットする。
     * 
     * @param processAfterFinishing
     */
    public void setProcessAfterFinishing(ProcessAfterFinishing processAfterFinishing) {
        this.processAfterFinishing = processAfterFinishing;
    }

    private boolean isCanceled = false;
    
    /**
     * WEBブラウザへのタスク実行をキャンセルする。
     */
    public void cancel() {
        this.isCanceled = true;
    }
    
    private int taskIndex = 0;
    
    private boolean isTimedout = false;
    
    private boolean isExceptionOccurred = false;
    
    /**
     * 次のタスクを実行する。
     */
    private void executeNextTask() {
        WebBrowsingFlow<W, E> flow = this;
        if (this.tasks.size() <= this.taskIndex || this.isTimedout || this.isCanceled || this.isExceptionOccurred) {
            if (this.processAfterFinishing != null) {
                this.processAfterFinishing.execute();
            }
            return;
        }
        Task<W> task = this.tasks.get(this.taskIndex);
        try {
            // Execute current task
            Thread thread = new Thread(new Runnable() {
                
                @Override
                public void run() {
                    try {
                        task.execute(flow.webBrowser);
                    } catch (Exception exception) {
                        if (flow.isTimedout == false) {
                            task.handleException(exception);
                        }
                        flow.isExceptionOccurred = true;
                    }
                }
            });
            thread.start();
            // Interval
            Thread.sleep(this.intervalSecond * 1000);
            // Timeout
            Datetime timeoutDatetime = new Datetime();
            timeoutDatetime.addSecond(this.timeoutSecond);
            while (thread.isAlive() && timeoutDatetime.getDate().getTime() > Datetime.newInstance().getDate().getTime()) {
                Thread.sleep(200);
            }
            if (thread.isAlive()) {
                this.isTimedout = true;
                StringObject message = new StringObject("Web browsing flow timed out on task number ");
                message.append(this.taskIndex + 1);
                message.append(".");
                task.handleException(new TimeoutException(message.toString()));
            }
            // Next
            this.taskIndex++;
            this.executeNextTask();
        } catch (Exception exception) {
            task.handleException(exception);
        }
    }
    
    /**
     * すべてのタスクを順に実行する。
     */
    public void execute() {
        WebBrowsingFlow<W, E> flow = this;
        Thread thread = new Thread(new Runnable() {
            
            @Override
            public void run() {
                flow.executeNextTask();
            }
        });
        thread.start();
    }
    
    /**
     * WEBブラウザへのタスクのインターフェース。
     * 
     * @author hiro
     * 
     * @param <W> WEBブラウザの型。
     */
    public interface Task<W extends WebBrowser<?>> {
        
        /**
         * 指定されたWEBブラウザに対してタスクを実行する。
         * 
         * @param webBrowser
         * @throws Exception
         */
        public abstract void execute(W webBrowser) throws Exception;
        
        /**
         * タスク処理中に発生した例外を処理する。
         * 
         * @param exception
         */
        public abstract void handleException(Exception exception);
    }
    
    /**
     * WEBブラウザへのすべてのタスクが終了した後の処理インターフェース。
     * 
     * @author hiro
     */
    public interface ProcessAfterFinishing {

        /**
         * 処理を実行する。
         */
        public abstract void execute();
    }
}
