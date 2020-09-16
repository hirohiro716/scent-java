package com.hirohiro716.web;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.hirohiro716.Array;
import com.hirohiro716.IdentifiableEnum;
import com.hirohiro716.StringObject;
import com.hirohiro716.datetime.Datetime;
import com.hirohiro716.filesystem.Directory;
import com.hirohiro716.filesystem.File;
import com.hirohiro716.reflection.DynamicClass;
import com.hirohiro716.reflection.Method;

/**
 * HTML5、CSS3、JavaScriptをサポートするWEBブラウザのクラス。<br>
 * Selenium - <a href="https://www.selenium.dev/downloads/">https://www.selenium.dev/downloads/</a><br>
 * WebDriver - <a href="https://www.selenium.dev/documentation/en/webdriver/driver_requirements/">https://www.selenium.dev/documentation/en/webdriver/driver_requirements/</a>
 * 
 * @author hiro
 *
 */
public class WebBrowser extends DynamicClass {
    
    /**
     * コンストラクタ。<br>
     * パラメーターにseleniumライブラリの各jarファイルが入ったディレクトリ、WEBブラウザの種類、seleniumのWEBドライバー実行ファイルを指定する。
     * 
     * @param seleniumLibraryDirectory
     * @param type 
     * @param seleniumWebDriver 
     * @throws ClassNotFoundException
     * @throws Exception
     */
    public WebBrowser(Directory seleniumLibraryDirectory, Type type, File seleniumWebDriver) throws ClassNotFoundException, Exception {
        super(seleniumLibraryDirectory);
        if (type == null && seleniumWebDriver == null) {
            throw new IOException("WebDriver is not specified.");
        }
        Type usingType = type;
        if (usingType == null) {
            usingType = Type.enumOf(seleniumWebDriver);
        }
        if (usingType == null) {
            throw new IOException("The type of WebDriver could not be determined.");
        }
        if (seleniumWebDriver != null) {
            System.setProperty(usingType.getSystemPropertyName(), seleniumWebDriver.getAbsolutePath());
        }
        this.webDriver = this.createWebDriver(usingType);
    }
    
    /**
     * コンストラクタ。<br>
     * パラメーターにseleniumライブラリの各jarファイルが入ったディレクトリ、seleniumのWEBドライバー実行ファイルを指定する。
     * 
     * @param seleniumLibraryDirectory
     * @param seleniumWebDriver 
     * @throws ClassNotFoundException
     * @throws Exception
     */
    public WebBrowser(Directory seleniumLibraryDirectory, File seleniumWebDriver) throws ClassNotFoundException, Exception {
        this(seleniumLibraryDirectory, null, seleniumWebDriver);
    }

    /**
     * コンストラクタ。<br>
     * パラメーターにseleniumライブラリの各jarファイルが入ったディレクトリ、WEBブラウザの種類を指定する。
     * 
     * @param seleniumLibraryDirectory
     * @param type 
     * @throws ClassNotFoundException
     * @throws Exception
     */
    public WebBrowser(Directory seleniumLibraryDirectory, Type type) throws ClassNotFoundException, Exception {
        this(seleniumLibraryDirectory, type, null);
    }

    private Class<?> classWebDriver = this.loadClass("org.openqa.selenium.WebDriver");
    
    private Object webDriver;
    
    /**
     * 指定されたタイプのWEBドライバーを作成する。
     * 
     * @param type
     * @return 結果。
     * @throws Exception
     */
    public Object createWebDriver(Type type) throws Exception {
        Object webDriver = null;
        Constructor constructor;
        switch (type) {
        case CHROME:
            constructor = new Constructor("org.openqa.selenium.chrome.ChromeDriver");
            webDriver = constructor.newInstance();
            break;
        case FIREFOX:
            constructor = new Constructor("org.openqa.selenium.firefox.FirefoxDriver");
            webDriver = constructor.newInstance();
            break;
        case OPERA:
            constructor = new Constructor("org.openqa.selenium.opera.OperaDriver");
            webDriver = constructor.newInstance();
            break;
        case EDGE:
            constructor = new Constructor("org.openqa.selenium.edge.EdgeDriver");
            webDriver = constructor.newInstance();
            break;
        case SAFARI:
            constructor = new Constructor("org.openqa.selenium.safari.SafariDriver");
            webDriver = constructor.newInstance();
            break;
        }
        return webDriver;
    }
    
    private Map<Object, Element> mapElement = new HashMap<>();
    
    /**
     * WEBページを読み込む。
     * 
     * @param url
     * @throws Exception 
     */
    public void load(URL url) throws Exception {
        Method method = new Method(this.classWebDriver, this.webDriver);
        method.invoke("get", url.toExternalForm());
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
            Method method = new Method(this.classWebDriver, this.webDriver);
            return method.invoke("getTitle");
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
            Method method = new Method(this.classWebDriver, this.webDriver);
            return method.invoke("getPageSource");
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
    
    private Class<?> classJavascriptExecutor = this.loadClass("org.openqa.selenium.JavascriptExecutor");
    
    /**
     * 指定されたJavaScriptを実行する。
     * 
     * @param javascript
     */
    public void executeJavaScript(String javascript) {
        try {
            Method method = new Method(this.classJavascriptExecutor, this.webDriver);
            method.invoke("executeScript", javascript, new Object[] {});
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
            Method method = new Method(this.classJavascriptExecutor, this.webDriver);
            Object bodyHtmlElement = method.invoke("executeScript", "return document.body;", new Object[] {});
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
        try {
            elements.add(this.getBodyElement());
            for (Element selectedElement: this.getBodyElement().getChildElements()) {
                elements.addAll(this.getChildElements(selectedElement));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
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
            this.selectedElements.add(this.getBodyElement());
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
     * すべての要素から正規表現に一致する属性値を持つ要素が見つかるのを待機する。
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
                for (Element parent: this.getAllElements()) {
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
     * すべての要素から正規表現に一致する属性値を持つ要素が失われるのを待機する。
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
                for (Element parent: this.getAllElements()) {
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
     * すべての要素から、タグ名が一致していて、内包するテキストが正規表現にも一致する要素が見つかるのを待機する。
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
                for (Element parent: this.getAllElements()) {
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
     * すべての要素からタグ名が一致している要素が見つかるのを待機する。
     * 
     * @param tagName
     * @param timeoutSeconds タイムアウトまでの秒数。
     */
    public void waitForTagNameFound(String tagName, int timeoutSeconds) {
        this.waitForTagNameFound(tagName, ".{0,}", timeoutSeconds);
    }
    
    /**
     * すべての要素から、タグ名が一致していて、内包するテキストが正規表現にも一致する要素が失われるのを待機する。
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
                for (Element parent: this.getAllElements()) {
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
     * すべての要素からタグ名が一致している要素が失われるのを待機する。
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
        
        private Class<?> classRemoteWebElement = WebBrowser.this.loadClass("org.openqa.selenium.remote.RemoteWebElement");
        
        private Object element;
        
        /**
         * この要素のタグ名を取得する。
         * 
         * @return 結果。
         * @throws Exception
         */
        public String getTagName() throws Exception {
            Method method = new Method(this.classRemoteWebElement, this.element);
            return method.invoke("getTagName");
        }
        
        /**
         * この要素のHTMLソースコードを取得する。
         * 
         * @return 結果。
         * @throws Exception
         */
        public String getSource() throws Exception {
            Method method = new Method(this.classRemoteWebElement, this.element);
            return method.invoke("getAttribute", "outerHTML");
        }
        
        /**
         * この要素が内包する文字列を取得する。
         * 
         * @return 結果。
         * @throws Exception
         */
        public String getTextContent() throws Exception {
            Method method = new Method(this.classRemoteWebElement, this.element);
            return method.invoke("getAttribute", "outerText");
        }
        
        /**
         * この要素の属性値を取得する。属性が存在しない場合は空文字列を返す。
         * 
         * @param name
         * @return 結果。
         * @throws Exception
         */
        public String getAttribute(String name) throws Exception {
            Method method = new Method(this.classRemoteWebElement, this.element);
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
            WebBrowser browser = WebBrowser.this;
            Method method = new Method(browser.classJavascriptExecutor, browser.webDriver);
            method.invoke("executeScript", "arguments[0].setAttribute(arguments[1], arguments[2]);", new Object[] {this.element, name, value});
        }
        
        /**
         * この要素をクリックする。
         * 
         * @throws Exception
         */
        public void click() throws Exception {
            WebBrowser browser = WebBrowser.this;
            Method method = new Method(browser.classJavascriptExecutor, browser.webDriver);
            method.invoke("executeScript", "arguments[0].click();", new Object[] {this.element});
        }

        private Class<?> classWebElement = WebBrowser.this.loadClass("org.openqa.selenium.WebElement");
        
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
                Constructor constructor = new Constructor("org.openqa.selenium.support.ui.Select");
                constructor.setParameterTypes(this.classWebElement);
                Object selectElement = constructor.newInstance(this.element);
                Method method = new Method(selectElement);
                List<?> options = method.invoke("getAllSelectedOptions");
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
                Constructor constructor = new Constructor("org.openqa.selenium.support.ui.Select");
                constructor.setParameterTypes(this.classWebElement);
                Object selectElement = constructor.newInstance(this.element);
                Method method = new Method(selectElement);
                method.invoke("selectByValue", value);
            }
        }
        
        /**
         * select要素内のすべてのoption要素を未選択にする。
         * 
         * @throws Exception
         */
        public void clearSelectedOptions() throws Exception {
            if (this.getTagName().equalsIgnoreCase("select")) {
                Constructor constructor = new Constructor("org.openqa.selenium.support.ui.Select");
                constructor.setParameterTypes(this.classWebElement);
                Object selectElement = constructor.newInstance(this.element);
                Method method = new Method(selectElement);
                method.invoke("deselectAll");
            }
        }
        
        private Class<?> classBy = WebBrowser.this.loadClass("org.openqa.selenium.By");
        
        /**
         * この要素の子要素を取得する。
         * 
         * @return 結果。
         * @throws Exception
         */
        public Array<Element> getChildElements() throws Exception {
            WebBrowser browser = WebBrowser.this;
            // By.xpath method
            Method byMethod = new Method(this.classBy);
            byMethod.setParameterTypes(String.class);
            Object by = byMethod.invoke("xpath", "*");
            // findElements method
            Method findElementsMethod = new Method(this.classRemoteWebElement, this.element);
            findElementsMethod.setParameterTypes(this.classBy);
            List<?> elements = findElementsMethod.invoke("findElements", by);
            List<Element> result = new ArrayList<>();
            for (Object element: elements) {
                result.add(browser.getElement(element));
            }
            return new Array<>(result);
        }
    }

    /**
     * WEBブラウザ種類の列挙型。
     * 
     * @author hiro
     *
     */
    public enum Type implements IdentifiableEnum<String> {
        /**
         * Google Chrome。
         */
        CHROME("chrome", "Google Chrome", "webdriver.chrome.driver"),
        /**
         * Mozilla Firefox。
         */
        FIREFOX("gecko", "Mozilla Firefox", "webdriver.gecko.driver"),
        /**
         * Opera。
         */
        OPERA("opera", "Opera", "webdriver.opera.driver"),
        /**
         * Microsoft Edge。
         */
        EDGE("edge", "Microsoft Edge", "webdriver.edge.driver"),
        /**
         * Safari。
         */
        SAFARI("safari", "Safari", "webdriver.safari.driver"),
        ;
        
        /**
         * コンストラクタ。<br>
         * ID、名前、システムにセットするプロパティ名を指定する。
         * 
         * @param id
         * @param name 
         * @param systemPropertyName
         */
        private Type(String id, String name, String systemPropertyName) {
            this.id = id;
            this.name = name;
            this.systemPropertyName = systemPropertyName;
        }
        
        private String id;
        
        @Override
        public String getID() {
            return this.id;
        }
        
        private String name;

        @Override
        public String getName() {
            return this.name;
        }
        
        private String systemPropertyName;
        
        /**
         * システムにセットするプロパティ名を取得する。
         * 
         * @return 結果。
         */
        public String getSystemPropertyName() {
            return this.systemPropertyName;
        }
        
        /**
         * 指定されたWEBドライバーのファイルから、該当する列挙子を取得する。該当するものがない場合はnullを返す。
         * 
         * @param seleniumWebDriver 
         * @return 結果。
         */
        public static Type enumOf(File seleniumWebDriver) {
            StringObject absolutePath = new StringObject();
            if (seleniumWebDriver != null) {
                absolutePath.append(seleniumWebDriver.getAbsolutePath());
            }
            if (absolutePath.toString().toLowerCase().indexOf("chrome") > -1) {
                return Type.CHROME;
            }
            if (absolutePath.toString().toLowerCase().indexOf("gecko") > -1) {
                return Type.FIREFOX;
            }
            if (absolutePath.toString().toLowerCase().indexOf("opera") > -1) {
                return Type.OPERA;
            }
            if (absolutePath.toString().toLowerCase().indexOf("edge") > -1) {
                return Type.EDGE;
            }
            if (absolutePath.toString().toLowerCase().indexOf("safari") > -1) {
                return Type.SAFARI;
            }
            return null;
        }
    }

}
