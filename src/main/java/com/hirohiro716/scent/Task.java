package com.hirohiro716.scent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 別のスレッドで時間のかかる処理を実行するクラス。
 * 
 * @author hiro
 *
 * @param <R> 処理結果の型。
 */
public abstract class Task<R> {
    
    /**
     * コンストラクタ。
     */
    public Task() {
        this.futureTask = new FutureTask<>(this.createCallable());
        this.thread = new Thread(this.futureTask);
    }
    
    private FutureTask<R> futureTask;
    
    private Thread thread;
    
    /**
     * このインスタンスで処理する内容を作成する。このメソッドはコンストラクタで自動的に呼び出される。
     * 
     * @return 結果。
     */
    protected abstract Callable createCallable();
    
    /**
     * タスクを開始する。
     */
    public void start() {
        this.thread.start();
    }
    
    /**
     * タスクが終了している場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isDone() {
        return this.futureTask.isDone();
    }
    
    /**
     * タスクがすでにキャンセルされている場合はtrueを返す。
     * 
     * @return 結果。
     */
    public boolean isCancelled() {
        return this.futureTask.isCancelled();
    }
    
    /**
     * タスクをキャンセルする。
     */
    public void cancel() {
        this.futureTask.cancel(false);
    }
    
    /**
     * このタスクの処理結果を取得する。<br>
     * このメソットを呼び出したThreadは、タスクの結果が取得できるまで待機する。
     * 
     * @return 結果。
     * @throws Exception タスク内の処理中に例外が発生した場合。
     */
    public R getResult() throws Exception {
        try {
            return this.futureTask.get();
        } catch (ExecutionException exception) {
            throw (Exception) exception.getCause();
        }
    }
    
    /**
     * タスクが完了するまで、このメソッドを呼び出したThreadを待機させる。
     * 
     * @throws Exception タスク内の処理中に例外が発生した場合。
     */
    public void waitForFinish() throws Exception {
        try {
            this.futureTask.get();
        } catch (ExecutionException exception) {
            throw (Exception) exception.getCause();
        }
    }
    
    /**
     * タスク内で実行する処理内容のクラス。
     * 
     * @author hiro
     */
    public abstract class Callable implements java.util.concurrent.Callable<R> {
        
        /**
         * 時間のかかる処理を行い結果を取得する。
         * 
         * @return 処理結果。
         * @throws Exception タスクがキャンセルされた場合、または処理に失敗した場合。
         */
        @Override
        public abstract R call() throws Exception;

        /**
         * タスクがすでにキャンセルされている場合はtrueを返す。
         * 
         * @return 結果。
         */
        protected boolean isCancelled() {
            return Task.this.isCancelled();
        }
    }
}
