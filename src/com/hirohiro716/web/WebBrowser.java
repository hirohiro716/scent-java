package com.hirohiro716.web;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.w3c.dom.Node;

import com.hirohiro716.Array;
import com.hirohiro716.StringObject;
import com.hirohiro716.datetime.Datetime;
import com.hirohiro716.filesystem.Directory;
import com.hirohiro716.reflection.DynamicClass;
import com.hirohiro716.reflection.Method;

/**
 * HTML5、CSS3、JavaScriptをサポートするWEBブラウザのクラス。
 * 
 * @author hiro
 *
 */
public class WebBrowser extends DynamicClass {
    
    /**
     * コンストラクタ。<br>
     * パラメーターにHtmlUnitライブラリの各jarファイルが入ったディレクトリを指定する。
     * 
     * @param htmlUnitLibraryDirectory
     * @throws ClassNotFoundException
     * @throws Exception
     */
    public WebBrowser(Directory htmlUnitLibraryDirectory) throws ClassNotFoundException, Exception {
        super(htmlUnitLibraryDirectory);
        Constructor constructor = new Constructor("com.gargoylesoftware.htmlunit.WebClient");
        this.webClient = constructor.newInstance();
        Method getCurrentWindowMethod = new Method(this.webClient);
        this.webWindow = getCurrentWindowMethod.invoke("getCurrentWindow");
        // Hide warning
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
        // Setting that does not process as an exception if the JavaScript link destination does not exist
        Method getOptionMethod = new Method(this.webClient);
        Object webClientOptions = getOptionMethod.invoke("getOptions");
        Method setThrowExceptionOnFailingStatusCodeMethod = new Method(webClientOptions);
        setThrowExceptionOnFailingStatusCodeMethod.setParameterTypes(boolean.class);
        setThrowExceptionOnFailingStatusCodeMethod.invoke("setThrowExceptionOnFailingStatusCode", false);
        // Setting that does not handle JavaScript errors as exceptions
        Method setThrowExceptionOnScriptErrorMethod = new Method(webClientOptions);
        setThrowExceptionOnScriptErrorMethod.setParameterTypes(boolean.class);
        setThrowExceptionOnScriptErrorMethod.invoke("setThrowExceptionOnScriptError", false);
        // Setting to wait for the end of ajax communication
        Constructor ajaxControllerConstructor = new Constructor("com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController");
        Object ajaxController = ajaxControllerConstructor.newInstance();
        Method setAjaxControllerMethod = new Method(this.webClient);
        setAjaxControllerMethod.setParameterTypes(this.loadClass("com.gargoylesoftware.htmlunit.AjaxController"));
        setAjaxControllerMethod.invoke("setAjaxController", ajaxController);
    }
    
    private Object webClient;
    
    private Object webWindow;
    
    private Object webPage = null;

    private Map<Object, Element> mapElement = new HashMap<>();
    
    /**
     * WEBページを読み込む。
     * 
     * @param url
     * @throws Exception 
     */
    public void load(URL url) throws Exception {
        Method method = new Method(this.webClient);
        this.webPage = method.invoke("getPage", url);
        this.mapElement.clear();
        this.clearSelectedElements();
    }
    
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
     * @return 結果。
     */
    public String getTitle() {
        try {
            Method method = new Method(this.webPage);
            return method.invoke("getTitleText");
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
    
    /**
     * WEBページのHTMLソースコードを取得する。
     * 
     * @return 結果。
     */
    public String getSource() {
        try {
            Method method = new Method(this.loadClass("com.gargoylesoftware.htmlunit.SgmlPage"), this.webPage);
            return method.invoke("asXml");
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * JavaScriptなどから変更されたWEBページを再読み込みする。
     * @throws Exception 
     */
    private void updateWebPage() throws Exception {
        Method method = new Method(this.loadClass("com.gargoylesoftware.htmlunit.WebWindow"), this.webWindow);
        this.webPage = method.invoke("getEnclosedPage");
        this.mapElement.clear();
    }
    
    /**
     * 指定されたJavaScriptを実行する。
     * 
     * @param javascript
     */
    public void executeJavaScript(String javascript) {
        try {
            Method method = new Method(this.webPage);
            method.invoke("executeJavaScript", javascript);
            this.updateWebPage();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    /**
     * WEBページのBODY要素を取得する。
     * 
     * @return 結果。
     */
    public Element getBodyElement() {
        try {
            Method method = new Method(this.webPage);
            Object bodyHtmlElement = method.invoke("getBody");
            return this.getElement(bodyHtmlElement);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * 親要素の中から子要素を再帰的にすべて取得する。
     * 
     * @param parent
     * @return 結果。
     */
    private List<Element> getChildElements(Element parent) {
        List<Element> elements = new ArrayList<>();
        try {
            for (Element element: parent.getChildElements()) {
                elements.add(element);
                elements.addAll(this.getChildElements(element));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return elements;
    }
    
    /**
     * WEBページのすべての要素を取得する。
     * 
     * @return 結果。
     */
    public Element[] getAllElements() {
        List<Element> elements = new ArrayList<>();
        if (this.webPage != null) {
            try {
                elements.add(this.getBodyElement());
                for (Element selectedElement: this.getBodyElement().getChildElements()) {
                    elements.addAll(this.getChildElements(selectedElement));
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return elements.toArray(new Element[] {});
    }
    
    private List<Element> selectedElements = new ArrayList<>();
    
    /**
     * 選択状態にある要素をすべて取得する。
     * 
     * @return 結果。
     */
    public Element[] getSelectedElements() {
        return this.selectedElements.toArray(new Element[] {});
    }
    
    /**
     * 選択状態にある1つめの要素を取得する。選択状態の要素がない場合はnullを返す。
     * 
     * @return 結果。
     */
    public Element getSelectedElement() {
        if (this.selectedElements.size() > 0) {
            return this.selectedElements.get(0);
        }
        return null;
    }
    
    /**
     * 指定されたDOMオブジェクトに対する要素を取得する。
     * 
     * @param htmlObject
     * @return 結果。
     * @throws ClassNotFoundException 
     */
    private Element getElement(Object htmlObject) throws ClassNotFoundException {
        if (this.mapElement.containsKey(htmlObject)) {
            return this.mapElement.get(htmlObject);
        }
        Element element = new Element(htmlObject);
        this.mapElement.put(htmlObject, element);
        return element;
    }
    
    /**
     * 選択状態をすべて解除してBODYだけを選択した状態にする。
     */
    public void clearSelectedElements() {
        this.selectedElements.clear();
        try {
            Method method = new Method(this.webPage);
            Object bodyHtmlElement = method.invoke("getBody");
            this.selectedElements.add(this.getElement(bodyHtmlElement));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 親要素の中から、正規表現に一致する属性値を持つ子要素を、再帰的にすべて取得する。
     * 
     * @param parent
     * @param attributeName
     * @param regexForAttributeValue
     * @return 結果。
     */
    private List<Element> findElementsByAttribute(Element parent, String attributeName, String regexForAttributeValue) {
        List<Element> elements = new ArrayList<>();
        try {
            for (Element element: parent.getChildElements()) {
                String value = StringObject.newInstance(element.getAttribute(attributeName)).replaceCR("").replaceLF("").replaceCRLF("").toString();
                if (value.matches(regexForAttributeValue)) {
                    elements.add(element);
                }
                elements.addAll(this.findElementsByAttribute(element, attributeName, regexForAttributeValue));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return elements;
    }
    
    /**
     * すでに選択状態にある要素の子要素から、更に正規表現に一致する属性値を持つ要素を選択状態にする。
     * 
     * @param attributeName
     * @param regexForAttributeValue
     */
    public void moreSelectElementsByAttribute(String attributeName, String regexForAttributeValue) {
        if (this.webPage == null) {
            return;
        }
        try {
            List<Element> newSelectedElements = new ArrayList<>();
            for (Element selectedElement: this.selectedElements) {
                newSelectedElements.addAll(this.findElementsByAttribute(selectedElement, attributeName, regexForAttributeValue));
            }
            this.selectedElements = newSelectedElements;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * BODY要素の子要素から、正規表現に一致する属性値を持つ要素を選択状態にする。
     * 
     * @param attributeName
     * @param regexForAttributeValue
     */
    public void selectElementsByAttribute(String attributeName, String regexForAttributeValue) {
        this.clearSelectedElements();
        this.moreSelectElementsByAttribute(attributeName, regexForAttributeValue);
    }
    
    /**
     * すでに選択状態にある要素の子要素から、更に正規表現に一致する属性値を持つ要素が見つかるのを待機する。
     * 
     * @param attributeName
     * @param regexForAttributeValue
     * @param timeoutSeconds タイムアウトまでの秒数。
     */
    public void waitForAttributeFound(String attributeName, String regexForAttributeValue, int timeoutSeconds) {
        Datetime limit = new Datetime();
        limit.addSecond(timeoutSeconds);
        try {
            while (limit.getDate().getTime() > new Date().getTime()) {
                for (Element parent: this.selectedElements) {
                    if (this.findElementsByAttribute(parent, attributeName, regexForAttributeValue).size() > 0) {
                        return;
                    }
                }
                Thread.sleep(1000);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    /**
     * すでに選択状態にある要素の子要素から、更に正規表現に一致する属性値を持つ要素が失われるのを待機する。
     * 
     * @param attributeName
     * @param regexForAttributeValue
     * @param timeoutSeconds タイムアウトまでの秒数。
     */
    public void waitForAttributeLost(String attributeName, String regexForAttributeValue, int timeoutSeconds) {
        Datetime limit = new Datetime();
        limit.addSecond(timeoutSeconds);
        try {
            while (limit.getDate().getTime() > new Date().getTime()) {
                List<Element> elements = new ArrayList<>();
                for (Element parent: this.selectedElements) {
                    elements.addAll(this.findElementsByAttribute(parent, attributeName, regexForAttributeValue));
                }
                if (elements.size() == 0) {
                    return;
                }
                Thread.sleep(1000);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 親要素の中から、タグ名が一致していて、内包するテキストが正規表現にも一致する子要素を、再帰的にすべて取得する。
     * 
     * @param parent
     * @param tagName
     * @param regexForTextContent 
     * @return 結果。
     */
    private List<Element> findElementsByTagName(Element parent, String tagName, String regexForTextContent) {
        List<Element> elements = new ArrayList<>();
        try {
            for (Element element: parent.getChildElements()) {
                String comparison = StringObject.newInstance(element.getTextContent()).replaceCR("").replaceLF("").replaceCRLF("").toString();
                if (element.getTagName().toUpperCase().equals(tagName.toUpperCase()) && comparison.matches(regexForTextContent)) {
                    elements.add(element);
                }
                elements.addAll(this.findElementsByTagName(element, tagName, regexForTextContent));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return elements;
    }

    /**
     * すでに選択状態にある要素の子要素から、タグ名が一致していて、内包するテキストが正規表現にも一致する要素を選択状態にする。
     * 
     * @param tagName
     * @param regexForTextContent 
     */
    public void moreSelectElementsByTagName(String tagName, String regexForTextContent) {
        if (this.webPage == null) {
            return;
        }
        try {
            List<Element> newSelectedElements = new ArrayList<>();
            for (Element selectedElement: this.selectedElements) {
                newSelectedElements.addAll(this.findElementsByTagName(selectedElement, tagName, regexForTextContent));
            }
            this.selectedElements = newSelectedElements;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * すでに選択状態にある要素の子要素から、タグ名が一致している要素を選択状態にする。
     * 
     * @param tagName
     */
    public void moreSelectElementsByTagName(String tagName) {
        this.moreSelectElementsByTagName(tagName, ".{0,}");
    }

    /**
     * BODY要素の子要素から、タグ名が一致していて、内包するテキストが正規表現にも一致する要素を選択状態にする。
     * 
     * @param tagName
     * @param regexForTextContent 
     */
    public void selectElementsByTagName(String tagName, String regexForTextContent) {
        this.clearSelectedElements();
        this.moreSelectElementsByTagName(tagName, regexForTextContent);
    }

    /**
     * BODY要素の子要素から、タグ名が一致している要素を選択状態にする。
     * 
     * @param tagName
     */
    public void selectElementsByTagName(String tagName) {
        this.selectElementsByTagName(tagName, ".{0,}");
    }

    /**
     * すでに選択状態にある要素の子要素から、タグ名が一致していて、内包するテキストが正規表現にも一致する要素が見つかるのを待機する。
     * 
     * @param tagName
     * @param regexForTextContent
     * @param timeoutSeconds タイムアウトまでの秒数。
     */
    public void waitForTagNameFound(String tagName, String regexForTextContent, int timeoutSeconds) {
        Datetime limit = new Datetime();
        limit.addSecond(timeoutSeconds);
        try {
            while (limit.getDate().getTime() > new Date().getTime()) {
                for (Element parent: this.selectedElements) {
                    if (this.findElementsByTagName(parent, tagName, regexForTextContent).size() > 0) {
                        return;
                    }
                }
                Thread.sleep(1000);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * すでに選択状態にある要素の子要素から、タグ名が一致している要素が見つかるのを待機する。
     * 
     * @param tagName
     * @param timeoutSeconds タイムアウトまでの秒数。
     */
    public void waitForTagNameFound(String tagName, int timeoutSeconds) {
        this.waitForTagNameFound(tagName, ".{0,}", timeoutSeconds);
    }
    
    /**
     * すでに選択状態にある要素の子要素から、タグ名が一致していて、内包するテキストが正規表現にも一致する要素が失われるのを待機する。
     * 
     * @param tagName
     * @param regexForTextContent
     * @param timeoutSeconds タイムアウトまでの秒数。
     */
    public void waitForTagNameLost(String tagName, String regexForTextContent, int timeoutSeconds) {
        Datetime limit = new Datetime();
        limit.addSecond(timeoutSeconds);
        try {
            while (limit.getDate().getTime() > new Date().getTime()) {
                List<Element> elements = new ArrayList<>();
                for (Element parent: this.selectedElements) {
                    elements.addAll(this.findElementsByAttribute(parent, tagName, regexForTextContent));
                }
                if (elements.size() == 0) {
                    return;
                }
                Thread.sleep(1000);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * すでに選択状態にある要素の子要素から、タグ名が一致している要素が失われるのを待機する。
     * 
     * @param tagName
     * @param timeoutSeconds タイムアウトまでの秒数。
     */
    public void waitForTagNameLost(String tagName, int timeoutSeconds) {
        this.waitForTagNameLost(tagName, ".{0,}", timeoutSeconds);
    }
    
    /**
     * WebBrowserにおけるHTML文書内の要素を表す。
     * 
     * @author hiro
     *
     */
    public class Element {
        
        /**
         * コンストラクタ。
         * 
         * @param element
         * @throws ClassNotFoundException
         */
        public Element(Object element) throws ClassNotFoundException {
            this.element = element;
        }
        
        private Class<?> classDomNode = WebBrowser.this.loadClass("com.gargoylesoftware.htmlunit.html.DomNode");
        
        private Class<?> classDomElement = WebBrowser.this.loadClass("com.gargoylesoftware.htmlunit.html.DomElement");
        
        private Class<?> classHtmlSelect = WebBrowser.this.loadClass("com.gargoylesoftware.htmlunit.html.HtmlSelect");

        private Object element;
        
        /**
         * この要素のタグ名を取得する。
         * 
         * @return 結果。
         * @throws Exception
         */
        public String getTagName() throws Exception {
            Method method = new Method(this.classDomElement, this.element);
            return method.invoke("getTagName");
        }
        
        /**
         * この要素のHTMLソースコードを取得する。
         * 
         * @return 結果。
         * @throws Exception
         */
        public String getSource() throws Exception {
            Method method = new Method(this.classDomNode, this.element);
            return method.invoke("asXml");
        }
        
        /**
         * この要素が内包する文字列を取得する。
         * 
         * @return 結果。
         * @throws Exception
         */
        public String getTextContent() throws Exception {
            Method method = new Method(Node.class, this.element);
            return method.invoke("getTextContent");
        }
        
        /**
         * この要素の属性値を取得する。属性が存在しない場合は空文字列を返す。
         * 
         * @param name
         * @return 結果。
         * @throws Exception
         */
        public String getAttribute(String name) throws Exception {
            Method method = new Method(this.classDomElement, this.element);
            return method.invoke("getAttribute", name);
        }
        
        /**
         * この要素に属性値をセットする。
         * 
         * @param name
         * @param value
         * @throws Exception
         */
        public void setAttribute(String name, String value) throws Exception {
            Method method = new Method(this.classDomElement, this.element);
            method.invoke("setAttribute", name, value);
        }
        
        /**
         * この要素をクリックする。
         * 
         * @throws Exception
         */
        public void click() throws Exception {
            WebBrowser browser = WebBrowser.this;
            Method method = new Method(this.classDomElement, this.element);
            browser.webPage = method.invoke("click");
            browser.clearSelectedElements();
        }
        
        /**
         * select要素の中で、選択されているoption要素を取得する。
         * 
         * @return 結果。
         * @throws Exception
         */
        public Element[] getSelectedOptions() throws Exception {
            WebBrowser browser = WebBrowser.this;
            List<Element> result = new ArrayList<>();
            if (this.getTagName().equalsIgnoreCase("select")) {
                Method method = new Method(this.classHtmlSelect, this.element);
                List<?> options = method.invoke("getSelectedOptions");
                for (Object option: options) {
                    result.add(browser.getElement(option));
                }
            }
            return result.toArray(new Element[] {});
        }
        
        /**
         * select要素の中で、指定された値を持つoption要素を選択する。
         * 
         * @param value
         * @throws Exception
         */
        public void addSelectedOption(String value) throws Exception {
            if (this.getTagName().equalsIgnoreCase("select")) {
                Method method = new Method(this.classHtmlSelect, this.element);
                method.setParameterTypes(String.class, boolean.class);
                method.invoke("setSelectedAttribute", value, true);
            }
        }
        
        /**
         * select要素内のすべてのoption要素を未選択にする。
         * 
         * @throws Exception
         */
        public void clearSelectedOptions() throws Exception {
            for (Element element: this.getSelectedOptions()) {
                Method method = new Method(this.classHtmlSelect, this.element);
                method.setParameterTypes(String.class, boolean.class);
                method.invoke("setSelectedAttribute", element.getAttribute("value"), false);
            }
        }
        
        /**
         * この要素の子要素を取得する。
         * 
         * @return 結果。
         * @throws Exception
         */
        public Array<Element> getChildElements() throws Exception {
            WebBrowser browser = WebBrowser.this;
            Method method = new Method(this.classDomElement, this.element);
            Iterable<?> elements = method.invoke("getChildElements");
            List<Element> result = new ArrayList<>();
            for (Object element: elements) {
                result.add(browser.getElement(element));
            }
            return new Array<>(result);
        }
    }
}
