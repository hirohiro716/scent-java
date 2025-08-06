package com.hirohiro716.scent.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hirohiro716.scent.StringObject;
import com.hirohiro716.scent.filesystem.Directory;
import com.hirohiro716.scent.filesystem.FilesystemItem;

/**
 * Reflectionでclasspathにないjarを利用する抽象クラス。
 */
public abstract class DynamicClass {
    
    /**
     * コンストラクタ。<br>
     * パラメーターに使用するjarファイル、またはjarファイルの親ディレクトリを指定する。
     * 
     * @param filesystemItems
     */
    public DynamicClass(FilesystemItem... filesystemItems) {
        List<URL> urls = new ArrayList<>();
        for (FilesystemItem item: filesystemItems) {
            try {
                if (item.isFile() && item.toString().endsWith(".jar")) {
                    urls.add(item.toURI().toURL());
                }
                if (item.isDirectory()) {
                    Directory directory = (Directory) item;
                    for (FilesystemItem childFile: directory.searchItems(null, null)) {
                        if (childFile.isFile() && childFile.toString().endsWith(".jar")) {
                            urls.add(childFile.toURI().toURL());
                        }
                    }
                }
            } catch (MalformedURLException exception) {
                exception.printStackTrace();
            }
        }
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URLClassLoader urlClassLoader = new URLClassLoader(urls.toArray(new URL[] {}), classLoader);
        Thread.currentThread().setContextClassLoader(urlClassLoader);
    }
    
    /**
     * 指定されたバイナリ名のclassを読み込んで取得する。
     * 
     * @param name Java言語仕様で定義されたバイナリ名。
     * @return class
     * @throws ClassNotFoundException
     */
    protected Class<?> loadClass(String name) throws ClassNotFoundException {
        return Class.forName(name, true, Thread.currentThread().getContextClassLoader());
    }
    
    /**
     * 指定された親クラスの中に定義されている、指定されたバイナリ名のclassを読み込んで取得する。
     * 
     * @param simpleName バイナリ名ではないクラスの単純な名前。
     * @param parentClass 取得したいクラスが存在する親クラス。
     * @return class
     * @throws ClassNotFoundException
     */
    protected Class<?> loadClass(String simpleName, Class<?> parentClass) throws ClassNotFoundException {
        for (Class<?> innerClass: parentClass.getDeclaredClasses()) {
            if (innerClass.getSimpleName().equals(simpleName)) {
                return innerClass;
            }
        }
        throw new ClassNotFoundException(StringObject.join(parentClass.getName(), "$", simpleName).toString());
    }
    
    /**
     * 指定されたバイナリ名の列挙型に定義されている定数をすべて返す。
     * 
     * @param name Java言語仕様で定義されたバイナリ名。
     * @return 列挙型の要素、バイナリ名が列挙型ではない場合はnull。
     * @throws ClassNotFoundException
     */
    public Map<String, Object> getEnumConstants(String name) throws ClassNotFoundException {
        Object[] objects = this.loadClass(name).getEnumConstants();
        Map<String, Object> hashMap = new HashMap<>();
        for (Object object: objects) {
            hashMap.put(object.toString(), object);
        }
        return hashMap;
    }
    
    /**
     * 指定された親クラスの中に定義されている、指定されたバイナリ名の列挙型に定義されている定数をすべて返す。
     * 
     * @param simpleName バイナリ名ではないクラスの単純な名前。
     * @param parentClass 取得したい列挙型が存在する親クラス。
     * @return 列挙型の要素、バイナリ名が列挙型ではない場合はnull。
     * @throws ClassNotFoundException
     */
    public Map<String, Object> getEnumConstants(String simpleName, Class<?> parentClass) throws ClassNotFoundException {
        String fullName = StringObject.join(parentClass.getName(), "$", simpleName).toString();
        for (Class<?> innerClass: parentClass.getDeclaredClasses()) {
            if (innerClass.getSimpleName().equals(simpleName)) {
                Map<String, Object> hashMap = new HashMap<>();
                for (Field field: innerClass.getDeclaredFields()) {
                    if (field.getGenericType().getTypeName().equals(fullName)) {
                        try {
                            Object object = field.get(null);
                            hashMap.put(object.toString(), object);
                        } catch (Exception exception) {
                        }
                    }
                }
                return hashMap;
            }
        }
        throw new ClassNotFoundException(fullName);
    }
    
    /**
     * Reflectionでclasspathにないjarのクラスのコンストラクタを利用するクラス。
     */
    protected class Constructor {
        
        /**
         * コンストラクタ。
         * 
         * @param name Java言語仕様で定義されたバイナリ名。
         */
        public Constructor(String name) {
            this.name = name;
        }
        
        private String name;
        
        private Class<?>[] parameterTypes = null;
        
        /**
         * コンストラクタを検索するためのパラメータータイプをセットする。
         * 
         * @param parameterTypes
         */
        public void setParameterTypes(Class<?>... parameterTypes) {
            this.parameterTypes = parameterTypes;
        }
        
        /**
         * 指定されたバイナリ名のインスタンスを作成する。
         * 
         * @param parameters
         * @return 作成したインスタンス。
         * @throws NoSuchMethodException
         * @throws SecurityException
         * @throws ClassNotFoundException
         * @throws InstantiationException
         * @throws IllegalAccessException
         * @throws IllegalArgumentException
         * @throws InvocationTargetException
         */
        public Object newInstance(Object... parameters) throws NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            Class<?>[] parameterTypes = this.parameterTypes;
            if (parameterTypes == null) {
                List<Class<?>> parameterTypeList = new ArrayList<>();
                for (Object parameter: parameters) {
                    parameterTypeList.add(parameter.getClass());
                }
                parameterTypes = parameterTypeList.toArray(new Class<?>[] {});
            }
            java.lang.reflect.Constructor<?> constructor = DynamicClass.this.loadClass(this.name).getDeclaredConstructor(parameterTypes);
            return constructor.newInstance(parameters);
        }
    }
}
