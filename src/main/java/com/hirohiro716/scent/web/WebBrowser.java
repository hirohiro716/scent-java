package com.hirohiro716.scent.web;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hirohiro716.scent.Array;
import com.hirohiro716.scent.datetime.Datetime;
import com.hirohiro716.scent.filesystem.File;
import com.hirohiro716.scent.filesystem.FilesystemItem;
import com.hirohiro716.scent.reflection.DynamicClass;

/**
 * HTML5、CSS3、JavaScriptをサポートするWEBブラウザのインターフェース。
 * 
 * @author hiro
 * 
 * @param <E> 要素の型。
 */
public abstract class WebBrowser<E extends WebBrowser.Element> extends DynamicClass {

    /**
     * コンストラクタ。<br>
     * パラメーターに使用するjarファイル、またはjarファイルの親ディレクトリを指定する。
     * 
     * @param filesystemItems
     */
    public WebBrowser(FilesystemItem... filesystemItems) {
        super(filesystemItems);
    }
    
    /**
     * WEBブラウザを閉じる。
     */
    public abstract void close();
    
    /**
     * WEBブラウザが閉じられている場合はtrueを返す。
     * 
     * @return
     */
    public abstract boolean isClosed();
    
    /**
     * WEBページを読み込む。
     * 
     * @param url
     * @throws Exception 
     */
    public abstract void load(URL url) throws Exception;

    /**
     * WEBページを読み込む。
     * 
     * @param url
     * @throws Exception 
     */
    public final void load(String url) throws Exception {
        this.load(new URL(url));
    }

    /**
     * WEBページのタイトルを取得する。
     * 
     * @return
     */
    public abstract String getTitle();

    /**
     * WEBページのHTMLソースコードを取得する。
     * 
     * @return
     */
    public abstract String getSource();

    /**
     * 指定されたJavaScriptを実行する。
     * 
     * @param javascript
     */
    public abstract void executeJavaScript(String javascript);

    /**
     * WEBページの操作対象フレームを指定された名前に切り替える。
     * 
     * @param name
     */
    public abstract void switchFrame(String name);

    /**
     * 指定されたDOMオブジェクトから要素を作成する。
     * 
     * @param htmlObject
     * @return
     * @throws ClassNotFoundException 
     */
    protected abstract E createElement(Object htmlObject) throws ClassNotFoundException;
    
    /**
     * WEBページのBODY要素を取得する。
     * 
     * @return
     */
    public abstract E getBodyElement();    

    /**
     * 指定された親要素の子孫要素を再帰的にすべて取得する。
     * 
     * @param parent
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<E> createListOfAllChildElement(E parent) {
        List<E> elements = new ArrayList<>();
        try {
            for (Element element: parent.getChildElements()) {
                elements.add((E) element);
                elements.addAll(this.createListOfAllChildElement((E) element));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return elements;
    }

    /**
     * 指定された親要素の子孫要素を再帰的にすべて取得する。
     * 
     * @param parent
     * @return
     */
    public Array<E> getAllElements(E parent) {
        return new Array<>(this.createListOfAllChildElement(parent));
    }

    /**
     * WEBページのすべての要素を取得する。
     * 
     * @return
     */
    public Array<E> getAllElements() {
        List<E> elements = new ArrayList<>();
        try {
            elements.add(this.getBodyElement());
            elements.addAll(this.createListOfAllChildElement(this.getBodyElement()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new Array<>(elements);
    }
    
    private List<E> selectedElements = new ArrayList<>();

    /**
     * 選択状態にある要素をすべて取得する。
     * 
     * @return
     */
    public Array<E> getSelectedElements() {
        return new Array<>(this.selectedElements);
    }

    /**
     * 選択状態にある1つめの要素を取得する。選択状態の要素がない場合はnullを返す。
     * 
     * @return
     */
    public E getSelectedElement() {
        if (this.selectedElements.size() > 0) {
            return this.selectedElements.get(0);
        }
        return null;
    }
    
    private Map<Object, E> mapOfElementAndHtmlObject = new HashMap<>();
    
    /**
     * DOMオブジェクトと要素の関連付けが定義されている連想配列を取得する。
     * 
     * @return
     */
    protected Map<Object, E> getMapOfElementAndHtmlObject() {
        return this.mapOfElementAndHtmlObject;
    }

    /**
     * 指定されたDOMオブジェクトに対する要素を取得する。
     * 
     * @param htmlObject
     * @return
     * @throws ClassNotFoundException 
     */
    protected E getElement(Object htmlObject) throws ClassNotFoundException {
        if (this.mapOfElementAndHtmlObject.containsKey(htmlObject)) {
            return this.mapOfElementAndHtmlObject.get(htmlObject);
        }
        E element = this.createElement(htmlObject);
        this.mapOfElementAndHtmlObject.put(htmlObject, element);
        return element;
    }

    /**
     * 選択状態をすべて解除してBODYだけを選択した状態にする。
     */
    public void clearSelectedElements() {
        this.selectedElements.clear();
        try {
            this.selectedElements.add(this.getBodyElement());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 指定された親要素の中から、指定値に一致する属性値を持つ要素を再帰的に検索してリストを作成する。
     * 
     * @param parent
     * @param attributeName
     * @param attributeValue
     * @return
     * @throws Exception
     */
    protected abstract List<E> createElementListOfFoundByAttribute(E parent, String attributeName, String attributeValue) throws Exception;

    /**
     * 指定された親要素の中から、指定値に一致する属性値を持つ要素を再帰的にすべて検索する。
     * 
     * @param parent
     * @param attributeName
     * @param attributeValue
     * @return
     * @throws Exception
     */
    public Array<E> findElementsByAttribute(E parent, String attributeName, String attributeValue) throws Exception {
        return new Array<>(this.createElementListOfFoundByAttribute(parent, attributeName, attributeValue));
    }

    /**
     * すでに選択状態にある要素の子要素から、指定値に一致する属性値を持つ要素を選択状態にする。
     * 
     * @param attributeName
     * @param attributeValue
     * @throws Exception 
     */
    public void selectMoreElementsByAttribute(String attributeName, String attributeValue) throws Exception {
        List<E> newSelectedElements = new ArrayList<>();
        for (E selectedElement: this.selectedElements) {
            newSelectedElements.addAll(this.createElementListOfFoundByAttribute(selectedElement, attributeName, attributeValue));
        }
        this.selectedElements = newSelectedElements;
    }

    /**
     * BODY要素の子要素から、指定値に一致する属性値を持つ要素を選択状態にする。
     * 
     * @param attributeName
     * @param attributeValue
     * @throws Exception 
     */
    public void selectElementsByAttribute(String attributeName, String attributeValue) throws Exception {
        this.clearSelectedElements();
        this.selectMoreElementsByAttribute(attributeName, attributeValue);
    }

    /**
     * すべての要素から指定値に一致する属性値を持つ要素が見つかるのを待機する。
     * 
     * @param attributeName
     * @param attributeValue
     * @param timeoutSeconds タイムアウトまでの秒数。
     */
    public void waitForFoundByAttribute(String attributeName, String attributeValue, int timeoutSeconds) {
        Datetime limit = new Datetime();
        limit.addSecond(timeoutSeconds);
        while (limit.getDate().getTime() > new Date().getTime()) {
            try {
                if (this.isClosed() || this.createElementListOfFoundByAttribute(this.getBodyElement(), attributeName, attributeValue).size() > 0) {
                    return;
                }
                Thread.sleep(1000);
            } catch (Exception exception) {
            }
        }
    }

    /**
     * すべての要素から指定値に一致する属性値を持つ要素が失われるのを待機する。
     * 
     * @param attributeName
     * @param attributeValue
     * @param timeoutSeconds タイムアウトまでの秒数。
     */
    public void waitForLostByAttribute(String attributeName, String attributeValue, int timeoutSeconds) {
        Datetime limit = new Datetime();
        limit.addSecond(timeoutSeconds);
        while (limit.getDate().getTime() > new Date().getTime()) {
            try {
                if (this.isClosed() || this.createElementListOfFoundByAttribute(this.getBodyElement(), attributeName, attributeValue).size() == 0) {
                    return;
                }
                Thread.sleep(1000);
            } catch (Exception exception) {
            }
        }
    }

    /**
     * 親要素の中から、タグ名が一致していて、内包するテキストが指定値を含む要素を再帰的に検索してリストを作成する。
     * 
     * @param parent
     * @param tagName
     * @param textContent 
     * @return
     * @throws Exception 
     */
    protected abstract List<E> createElementListOfFoundByTagName(E parent, String tagName, String textContent) throws Exception;

    /**
     * 親要素の中から、タグ名が一致していて、内包するテキストが指定値を含む要素を再帰的にすべて検索する。
     * 
     * @param parent
     * @param tagName
     * @param textContent 
     * @return
     * @throws Exception 
     */
    public Array<E> findElementsByTagName(E parent, String tagName, String textContent) throws Exception {
        return new Array<>(this.createElementListOfFoundByTagName(parent, tagName, textContent));
    }

    /**
     * すでに選択状態にある要素の子要素から、タグ名が一致していて、内包するテキストが指定値を含む要素を選択状態にする。
     * 
     * @param tagName
     * @param textContent 
     * @throws Exception 
     */
    public void selectMoreElementsByTagName(String tagName, String textContent) throws Exception {
        List<E> newSelectedElements = new ArrayList<>();
        for (E selectedElement: this.selectedElements) {
            newSelectedElements.addAll(this.createElementListOfFoundByTagName(selectedElement, tagName, textContent));
        }
        this.selectedElements = newSelectedElements;
    }

    /**
     * すでに選択状態にある要素の子要素から、タグ名が一致している要素を選択状態にする。
     * 
     * @param tagName
     * @throws Exception 
     */
    public void selectMoreElementsByTagName(String tagName) throws Exception {
        this.selectMoreElementsByTagName(tagName, "");
    }

    /**
     * BODY要素の子要素から、タグ名が一致していて、内包するテキストが指定値を含む要素を選択状態にする。
     * 
     * @param tagName
     * @param textContent 
     * @throws Exception 
     */
    public void selectElementsByTagName(String tagName, String textContent) throws Exception {
        this.clearSelectedElements();
        this.selectMoreElementsByTagName(tagName, textContent);
    }

    /**
     * BODY要素の子要素から、タグ名が一致している要素を選択状態にする。
     * 
     * @param tagName
     * @throws Exception 
     */
    public void selectElementsByTagName(String tagName) throws Exception {
        this.selectElementsByTagName(tagName, "");
    }

    /**
     * すべての要素から、タグ名が一致していて、内包するテキストが指定値を含む要素が見つかるのを待機する。
     * 
     * @param tagName
     * @param textContent
     * @param timeoutSeconds タイムアウトまでの秒数。
     */
    public void waitForFoundByTagName(String tagName, String textContent, int timeoutSeconds) {
        Datetime limit = new Datetime();
        limit.addSecond(timeoutSeconds);
        while (limit.getDate().getTime() > new Date().getTime()) {
            try {
                if (this.isClosed() || this.createElementListOfFoundByTagName(this.getBodyElement(), tagName, textContent).size() > 0) {
                    return;
                }
                Thread.sleep(1000);
            } catch (Exception exception) {
            }
        }
    }

    /**
     * すべての要素からタグ名が一致している要素が見つかるのを待機する。
     * 
     * @param tagName
     * @param timeoutSeconds タイムアウトまでの秒数。
     */
    public void waitForFoundByTagName(String tagName, int timeoutSeconds) {
        this.waitForFoundByTagName(tagName, "", timeoutSeconds);
    }

    /**
     * すべての要素から、タグ名が一致していて、内包するテキストが指定値を含む要素が失われるのを待機する。
     * 
     * @param tagName
     * @param textContent
     * @param timeoutSeconds タイムアウトまでの秒数。
     */
    public void waitForLostByTagName(String tagName, String textContent, int timeoutSeconds) {
        Datetime limit = new Datetime();
        limit.addSecond(timeoutSeconds);
        while (limit.getDate().getTime() > new Date().getTime()) {
            try {
                if (this.isClosed() || this.createElementListOfFoundByTagName(this.getBodyElement(), tagName, textContent).size() == 0) {
                    return;
                }
                Thread.sleep(1000);
            } catch (Exception exception) {
            }
        }
    }

    /**
     * すべての要素からタグ名が一致している要素が失われるのを待機する。
     * 
     * @param tagName
     * @param timeoutSeconds タイムアウトまでの秒数。
     */
    public void waitForLostByTagName(String tagName, int timeoutSeconds) {
        this.waitForLostByTagName(tagName, "", timeoutSeconds);
    }

    /**
     * 指定された親要素の中から、CSSセレクタに一致する要素を再帰的に検索してリストを作成する。
     * 
     * @param parent
     * @param cssSelector
     * @return
     * @throws Exception
     */
    protected abstract List<E> createElementListOfFoundByCssSelector(E parent, String cssSelector) throws Exception;

    /**
     * 指定された親要素の中から、CSSセレクタに一致する要素を再帰的にすべて検索する。
     * 
     * @param parent
     * @param cssSelector
     * @return
     * @throws Exception
     */
    public Array<E> findElementsByCssSelector(E parent, String cssSelector) throws Exception {
        return new Array<>(this.createElementListOfFoundByCssSelector(parent, cssSelector));
    }

    /**
     * すでに選択状態にある要素の子要素から、CSSセレクタに一致する要素を選択状態にする。
     * 
     * @param cssSelector
     * @throws Exception 
     */
    public void selectMoreElementsByCssSelector(String cssSelector) throws Exception {
        List<E> newSelectedElements = new ArrayList<>();
        for (E selectedElement: this.selectedElements) {
            newSelectedElements.addAll(this.createElementListOfFoundByCssSelector(selectedElement, cssSelector));
        }
        this.selectedElements = newSelectedElements;
    }

    /**
     * BODY要素の子要素から、CSSセレクタに一致する要素を選択状態にする。
     * 
     * @param cssSelector
     * @throws Exception 
     */
    public void selectElementsByCssSelector(String cssSelector) throws Exception {
        this.clearSelectedElements();
        this.selectMoreElementsByCssSelector(cssSelector);
    }

    /**
     * すべての要素からCSSセレクタに一致する要素が見つかるのを待機する。
     * 
     * @param cssSelector
     * @param timeoutSeconds タイムアウトまでの秒数。
     */
    public void waitForFoundByCssSelector(String cssSelector, int timeoutSeconds) {
        Datetime limit = new Datetime();
        limit.addSecond(timeoutSeconds);
        while (limit.getDate().getTime() > new Date().getTime()) {
            try {
                if (this.isClosed() || this.createElementListOfFoundByCssSelector(this.getBodyElement(), cssSelector).size() > 0) {
                    return;
                }
                Thread.sleep(1000);
            } catch (Exception exception) {
            }
        }
    }

    /**
     * すべての要素からCSSセレクタに一致する要素が失われるのを待機する。
     * 
     * @param cssSelector
     * @param timeoutSeconds タイムアウトまでの秒数。
     */
    public void waitForLostByCssSelector(String cssSelector, int timeoutSeconds) {
        Datetime limit = new Datetime();
        limit.addSecond(timeoutSeconds);
        while (limit.getDate().getTime() > new Date().getTime()) {
            try {
                if (this.isClosed() || this.createElementListOfFoundByCssSelector(this.getBodyElement(), cssSelector).size() == 0) {
                    return;
                }
                Thread.sleep(1000);
            } catch (Exception exception) {
            }
        }
    }

    /**
     * 指定された親要素の中から、XPathに一致する要素を再帰的に検索してリストを作成する。
     * 
     * @param parent
     * @param xPath
     * @return
     * @throws Exception
     */
    protected abstract List<E> createElementListOfFoundByXPath(E parent, String xPath) throws Exception;

    /**
     * 指定された親要素の中から、XPathに一致する要素を再帰的にすべて検索する。
     * 
     * @param parent
     * @param xPath
     * @return
     * @throws Exception
     */
    public Array<E> findElementsByXPath(E parent, String xPath) throws Exception {
        return new Array<>(this.createElementListOfFoundByXPath(parent, xPath));
    }

    /**
     * すでに選択状態にある要素の子要素から、XPathに一致する要素を選択状態にする。
     * 
     * @param xPath
     * @throws Exception 
     */
    public void selectMoreElementsByXPath(String xPath) throws Exception {
        List<E> newSelectedElements = new ArrayList<>();
        for (E selectedElement: this.selectedElements) {
            newSelectedElements.addAll(this.createElementListOfFoundByXPath(selectedElement, xPath));
        }
        this.selectedElements = newSelectedElements;
    }

    /**
     * BODY要素の子要素から、XPathに一致する要素を選択状態にする。
     * 
     * @param xPath
     * @throws Exception 
     */
    public void selectElementsByXPath(String xPath) throws Exception {
        this.clearSelectedElements();
        this.selectMoreElementsByXPath(xPath);
    }

    /**
     * すべての要素からXPathに一致する要素が見つかるのを待機する。
     * 
     * @param xPath
     * @param timeoutSeconds タイムアウトまでの秒数。
     */
    public void waitForFoundByXPath(String xPath, int timeoutSeconds) {
        Datetime limit = new Datetime();
        limit.addSecond(timeoutSeconds);
        while (limit.getDate().getTime() > new Date().getTime()) {
            try {
                if (this.isClosed() || this.createElementListOfFoundByXPath(this.getBodyElement(), xPath).size() > 0) {
                    return;
                }
                Thread.sleep(1000);
            } catch (Exception exception) {
            }
        }
    }

    /**
     * すべての要素からXPathに一致する要素が失われるのを待機する。
     * 
     * @param xPath
     * @param timeoutSeconds タイムアウトまでの秒数。
     */
    public void waitForLostByXPath(String xPath, int timeoutSeconds) {
        Datetime limit = new Datetime();
        limit.addSecond(timeoutSeconds);
        while (limit.getDate().getTime() > new Date().getTime()) {
            try {
                if (this.isClosed() || this.createElementListOfFoundByXPath(this.getBodyElement(), xPath).size() == 0) {
                    return;
                }
                Thread.sleep(1000);
            } catch (Exception exception) {
            }
        }
    }
    
    /**
     * WebBrowserにおけるHTML文書内の要素を表す。
     * 
     * @author hiro
     */
    public interface Element {

        /**
         * この要素のタグ名を取得する。
         * 
         * @return
         * @throws Exception
         */
        public abstract String getTagName() throws Exception;

        /**
         * この要素のHTMLソースコードを取得する。
         * 
         * @return
         * @throws Exception
         */
        public abstract String getSource() throws Exception;

        /**
         * この要素が内包する文字列を取得する。
         * 
         * @return
         * @throws Exception
         */
        public abstract String getTextContent() throws Exception;

        /**
         * この要素が内包する文字列をセットする。
         * 
         * @param textContent 
         * @throws Exception
         */
        public abstract void setTextContent(String textContent) throws Exception;

        /**
         * この要素の属性値を取得する。属性が存在しない場合は空文字列を返す。
         * 
         * @param name
         * @return
         * @throws Exception
         */
        public abstract String getAttribute(String name) throws Exception;

        /**
         * この要素に属性値をセットする。
         * 
         * @param name
         * @param value
         * @throws Exception
         */
        public abstract void setAttribute(String name, String value) throws Exception;
        
        /**
         * この要素の属性を削除する。
         * 
         * @param name
         * @throws Exception
         */
        public abstract void removeAttribute(String name) throws Exception;

        /**
         * この要素にフォーカスさせる。
         * 
         * @throws Exception
         */
        public abstract void focus() throws Exception;

        /**
         * この要素をクリックする。
         * 
         * @throws Exception
         */
        public abstract void click() throws Exception;

        /**
         * select要素の中で、選択されているoption要素を取得する。
         * 
         * @return
         * @throws Exception
         */
        public abstract Element[] getSelectedOptions() throws Exception;

        /**
         * select要素の中で、指定された値を持つoption要素を選択する。
         * 
         * @param value
         * @throws Exception
         */
        public abstract void addSelectedOption(String value) throws Exception;

        /**
         * select要素内のすべてのoption要素を未選択にする。
         * 
         * @throws Exception
         */
        public abstract void clearSelectedOptions() throws Exception;

        /**
         * タイプ属性がfileのinput要素にファイルパスをセットする。
         * 
         * @param file
         * @throws Exception
         */
        public abstract void setFile(File file) throws Exception;

        /**
         * この要素の子要素を取得する。
         * 
         * @param <E> 要素の型。
         * @return
         * @throws Exception
         */
        public abstract <E extends Element> Array<E> getChildElements() throws Exception;

        /**
         * この要素の親要素を取得する。
         * 
         * @return
         * @throws Exception
         */
        public abstract Element getParentElement() throws Exception;
    }
}
