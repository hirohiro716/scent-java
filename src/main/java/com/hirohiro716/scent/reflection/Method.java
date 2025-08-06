package com.hirohiro716.scent.reflection;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reflectionでclasspathにないjarのメソッドを利用するクラス。
 */
public class Method {
    
    /**
     * コンストラクタ。
     * 
     * @param targetClass メソッドを検索するクラス。
     * @param instance メソッドを実行するインスタンス。
     */
    public Method(Class<?> targetClass, Object instance) {
        this.targetClass = targetClass;
        this.instance = instance;
    }

    /**
     * コンストラクタ。
     * 
     * @param instance メソッドを実行するインスタンス。
     */
    public Method(Object instance) {
        this(instance.getClass(), instance);
    }
    
    /**
     * コンストラクタ。
     * 
     * @param targetClass メソッドを検索するクラス。
     */
    public Method(Class<?> targetClass) {
        this(targetClass, null);
    }
    
    private Class<?> targetClass;
    
    private Object instance;
    
    private Class<?>[] parameterTypes = null;
    
    /**
     * メソッドを検索するためのパラメータータイプをセットする。
     * 
     * @param parameterTypes
     */
    public void setParameterTypes(Class<?>... parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
    
    /**
     * 指定されたメソッド名、パラメーターを使用してメソッドを呼び出す。<br>
     * このメソッドを (new Method(ins)).invoke(name, param)
     * で呼び出すと次のコードと同じ結果を返す。<br>
     * Method method = ins.getClass().getDeclaredMethod(name, new Class&lt;?&gt;[]
     * {param.getClass()});<br>
     * method.setAccessible(true);<br>
     * return method.invoke(ins, param);
     * 
     * @param <T> メソッドの戻り値の型。
     * @param name
     * @param parameters
     * @return 呼び出したメソッドの戻り値。
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @SuppressWarnings("unchecked")
    public <T> T invoke(String name, Object... parameters) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?>[] parameterTypes = this.parameterTypes;
        if (parameterTypes == null) {
            List<Class<?>> parameterTypeList = new ArrayList<>();
            for (Object parameter: parameters) {
                parameterTypeList.add(parameter.getClass());
            }
            parameterTypes = parameterTypeList.toArray(new Class<?>[] {});
        }
        java.lang.reflect.Method method = this.targetClass.getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return (T) method.invoke(this.instance, parameters);
    }
}
