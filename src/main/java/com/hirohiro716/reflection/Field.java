package com.hirohiro716.reflection;

import java.lang.reflect.InvocationTargetException;

/**
 * Reflectionでclasspathにないjarのフィールドを取得するクラス。
 * 
 * @author hiro
 *
 */
public class Field {
    
    /**
     * コンストラクタ。
     * 
     * @param targetClass メソッドを検索するクラス。
     * @param instance メソッドを実行するインスタンス。
     */
    public Field(Class<?> targetClass, Object instance) {
        this.targetClass = targetClass;
        this.instance = instance;
    }

    /**
     * コンストラクタ。
     * 
     * @param instance メソッドを実行するインスタンス。
     */
    public Field(Object instance) {
        this(instance.getClass(), instance);
    }
    
    /**
     * コンストラクタ。
     * 
     * @param targetClass メソッドを検索するクラス。
     */
    public Field(Class<?> targetClass) {
        this(targetClass, null);
    }
    
    private Class<?> targetClass;
    
    private Object instance;
    
    /**
     * 指定された名前のフィールドの値を取得する。<br>
     * このメソッドを (new Field(ins)).getValue(name) で呼び出すと次のコードと同じ結果を返す。<br>
     * Field field = ins.getClass().getDeclaredField(name);<br>
     * field.setAccessible(true);<br>
     * return field.get(ins);
     * 
     * @param <T> メソッドの戻り値の型。
     * @param name
     * @return 呼び出したメソッドの戻り値。
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(String name) throws NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        java.lang.reflect.Field field = this.targetClass.getDeclaredField(name);
        field.setAccessible(true);
        return (T) field.get(this.instance);
    }
}
