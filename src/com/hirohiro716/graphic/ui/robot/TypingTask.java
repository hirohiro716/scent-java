package com.hirohiro716.graphic.ui.robot;

import java.awt.AWTException;
import java.util.ArrayList;
import java.util.List;

import com.hirohiro716.StringObject;
import com.hirohiro716.graphic.ui.KeyCode;

/**
 * キーボードの入力を、タスクを定義した順に自動的に行うクラス。
 * 
 * @author hiro
 *
 */
public class TypingTask {
    
    /**
     * コンストラクタ。
     * 
     * @throws AWTException
     */
    public TypingTask() throws AWTException {
        this.robot = new Robot();
    }
    
    private Robot robot;
    
    private List<Task> tasks = new ArrayList<>();
    
    /**
     * 定義されているタスクの数を取得する。
     * 
     * @return 結果。
     */
    public int getNumberOfTasks() {
        return this.tasks.size();
    }

    /**
     * タスク定義文字列の各タスクの区切り文字。
     */
    public static final String DEFINITION_STRING_TASK_DELIMITER = " ";

    /**
     * タスク定義文字列のタイプと値の区切り文字。
     */
    public static final String DEFINITION_STRING_TYPE_AND_VALUE_DELIMITER = ":";

    /**
     * タスク定義文字列の複数値の区切り文字。
     */
    public static final String DEFINITION_STRING_VALUES_DELIMITER = ",";
    
    /**
     * 定義されているすべてのタスクから、タスク定義文字列を作成する。
     * 
     * @return 結果。
     */
    public String makeDefinitionString() {
        StringObject result = new StringObject();
        for (Task task: this.tasks) {
            if (result.length() > 0) {
                result.append(TypingTask.DEFINITION_STRING_TASK_DELIMITER);
            }
            switch (task.getTaskType()) {
            case KEY:
                result.append(task.getTaskType().toString());
                result.append(TypingTask.DEFINITION_STRING_TYPE_AND_VALUE_DELIMITER);
                boolean isFirst = true;
                for (KeyCode keyCode: task.getKeyCodes()) {
                    if (isFirst == false) {
                        result.append(TypingTask.DEFINITION_STRING_VALUES_DELIMITER);
                    }
                    result.append(keyCode.toString());
                    isFirst = false;
                }
                break;
            case SLEEP:
                result.append(task.getTaskType().toString());
                result.append(TypingTask.DEFINITION_STRING_TYPE_AND_VALUE_DELIMITER);
                result.append(task.getMilliseconds());
                break;
            }
        }
        return result.toString();
    }
    
    /**
     * このクラスのmakeDefinitionStringメソッドで作成したタスク定義文字列から、すべてのタスクを復元する。
     * 
     * @param definitionString
     */
    public void importFromDefinitionString(String definitionString) {
        this.tasks.clear();
        try {
            String[] taskStrings = StringObject.newInstance(definitionString).split(TypingTask.DEFINITION_STRING_TASK_DELIMITER);
            for (String taskString: taskStrings) {
                String[] typeAndValue = StringObject.newInstance(taskString).split(TypingTask.DEFINITION_STRING_TYPE_AND_VALUE_DELIMITER);
                TaskType taskType = TaskType.find(typeAndValue[0]);
                switch (taskType) {
                case KEY:
                    String[] keyCodeStrings = typeAndValue[1].split(TypingTask.DEFINITION_STRING_VALUES_DELIMITER);
                    List<KeyCode> keyCodes = new ArrayList<>();
                    for (String keyCodeString: keyCodeStrings) {
                        KeyCode keyCode = KeyCode.valueOf(keyCodeString);
                        keyCodes.add(keyCode);
                    }
                    this.addKeyTypeTask(keyCodes.toArray(new KeyCode[] {}));
                    break;
                case SLEEP:
                    long milliseconds = StringObject.newInstance(typeAndValue[1]).toLong();
                    this.addSleepTask(milliseconds);
                    break;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    /**
     * キーの入力を行うタスクを追加する。同時にキー入力を行う場合は複数のKeyCodeを指定する。
     * 
     * @param keyCodes
     */
    public void addKeyTypeTask(KeyCode... keyCodes) {
        this.tasks.add(new Task(keyCodes));
    }
    
    /**
     * 指定時間待機するタスクを追加する。
     * 
     * @param milliseconds
     */
    public void addSleepTask(long milliseconds) {
        this.tasks.add(new Task(milliseconds));
    }
    
    /**
     * 定義されているすべてのタスクを順番に実行する。
     */
    public void execute() {
        for (Task task: this.tasks) {
            switch (task.getTaskType()) {
            case KEY:
                this.robot.KeyType(task.keyCodes);
                break;
            case SLEEP:
                try {
                    Thread.sleep(task.getMilliseconds());
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * キーボードの入力を自動的に行うタスクのクラス。
     * 
     * @author hiro
     * 
     */
    private class Task {

        /**
         * コンストラクタ。<br>
         * キーの入力を行うタスクを追加する。同時にキー入力を行う場合は複数のKeyCodeを指定する。
         * 
         * @param keyCodes
         */
        public Task(KeyCode[] keyCodes) {
            this.taskType = TaskType.KEY;
            this.keyCodes = keyCodes;
        }
        
        /**
         * コンストラクタ。<br>
         * 指定時間待機するTaskを作成する。
         * 
         * @param milliseconds
         */
        public Task(long milliseconds) {
            this.taskType = TaskType.SLEEP;
            this.milliseconds = milliseconds;
        }
        
        private TaskType taskType;
        
        /**
         * このタスクの種類を取得する。
         * 
         * @return 結果。
         */
        public TaskType getTaskType() {
            return this.taskType;
        }

        private KeyCode[] keyCodes;
        
        /**
         * このタスクのKeyCodeを取得する。
         * 
         * @return 結果。
         */
        public KeyCode[] getKeyCodes() {
            return this.keyCodes;
        }
        
        private long milliseconds;
        
        /**
         * このタスクの待機時間をミリ秒で取得する。
         * 
         * @return 結果。
         */
        public long getMilliseconds() {
            return this.milliseconds;
        }
    }
    
    /**
     * タスク種類の列挙型。
     * 
     * @author hiro
     * 
     */
    private enum TaskType {
        /**
         * キー入力。
         */
        KEY,
        /**
         * 待機。
         */
        SLEEP,
        ;
        
        /**
         * 文字列からTaskTypeを取得する。
         * 
         * @param string
         * @return 結果。
         */
        public static TaskType find(String string) {
            for (TaskType taskType: TaskType.values()) {
                if (string != null) {
                    if (taskType.toString().toLowerCase().equals(string.toLowerCase()) || taskType.toString().toUpperCase().equals(string.toUpperCase())) {
                        return taskType;
                    }
                }
            }
            return null;
        }
    }
}
