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

    /**
     * WEBブラウザに表示されているダイアログを承認する。
     */
    private void acceptDialog() {
    	try {
            Method method = new Method(this.classWebDriver, this.webDriver);
            Object switchTo = method.invoke("switchTo");
            Method alertMethod = new Method(switchTo);
            Object alert = alertMethod.invoke("alert");
            Method acceptMethod = new Method(alert);
            acceptMethod.invoke("accept");
    	} catch (Exception exception) {
    	}
    }
    
    private Class<?> classRemoteWebElement = this.loadClass("org.openqa.selenium.remote.RemoteWebElement");

    private Class<?> classWebElement = this.loadClass("org.openqa.selenium.WebElement");

    private Class<?> classBy = this.loadClass("org.openqa.selenium.By");
    
    private Map<Object, Element> mapElement = new HashMap<>();
    
    /**
     * WEBページを読み込む。
     * 
     * @param url
     * @throws Exception 
     */
    public void load(URL url) throws Exception {
    	this.acceptDialog();
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
        	this.acceptDialog();
            Method method = new Method(this.classJavascriptExecutor, this.webDriver);
            method.invoke("executeScript", javascript, new Object[] {});
            this.mapElement.clear();
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
     * 指定された親要素の中から、指定値に一致する属性値を持つ要素を、再帰的にすべて取得する。
     * 
     * @param parent
     * @param attributeName
     * @param attributeValue
     * @return 結果。
     * @throws Exception
     */
    private List<Element> findElementsByAttribute(Element parent, String attributeName, String attributeValue) throws Exception {
        List<Element> elements = new ArrayList<>();
        // Search at XPath
        StringObject xpath = new StringObject(".//*[@");
        xpath.append(attributeName);
        xpath.append("='");
        xpath.append(attributeValue);
        xpath.append("']");
        Method byMethod = new Method(this.classBy);
        byMethod.setParameterTypes(String.class);
        Object by = byMethod.invoke("xpath", xpath.toString());
        // Search elements
        Method findElementsMethod = new Method(this.classRemoteWebElement, parent.element);
        findElementsMethod.setParameterTypes(this.classBy);
        List<?> elementObjects = findElementsMethod.invoke("findElements", by);
        for (Object elementObject : elementObjects) {
            elements.add(this.getElement(elementObject));
        }
        return elements;
    }
    
    /**
     * すでに選択状態にある要素の子要素から、指定値に一致する属性値を持つ要素を選択状態にする。
     * 
     * @param attributeName
     * @param attributeValue
     * @throws Exception 
     */
    public void moreSelectElementsByAttribute(String attributeName, String attributeValue) throws Exception {
        List<Element> newSelectedElements = new ArrayList<>();
        for (Element selectedElement: this.selectedElements) {
            newSelectedElements.addAll(this.findElementsByAttribute(selectedElement, attributeName, attributeValue));
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
        this.moreSelectElementsByAttribute(attributeName, attributeValue);
    }
    
    /**
     * すべての要素から指定値に一致する属性値を持つ要素が見つかるのを待機する。
     * 
     * @param attributeName
     * @param attributeValue
     * @param timeoutSeconds タイムアウトまでの秒数。
     */
    public void waitForAttributeFound(String attributeName, String attributeValue, int timeoutSeconds) {
        Datetime limit = new Datetime();
        limit.addSecond(timeoutSeconds);
        while (limit.getDate().getTime() > new Date().getTime()) {
        	try {
		        if (this.findElementsByAttribute(this.getBodyElement(), attributeName, attributeValue).size() > 0) {
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
    public void waitForAttributeLost(String attributeName, String attributeValue, int timeoutSeconds) {
        Datetime limit = new Datetime();
        limit.addSecond(timeoutSeconds);
        while (limit.getDate().getTime() > new Date().getTime()) {
        	try {
	            if (this.findElementsByAttribute(this.getBodyElement(), attributeName, attributeValue).size() == 0) {
	                return;
	            }
	            Thread.sleep(1000);
        	} catch (Exception exception) {
        	}
        }
    }

    /**
     * 親要素の中から、タグ名が一致していて、内包するテキストが指定値を含む要素を、再帰的にすべて取得する。
     * 
     * @param parent
     * @param tagName
     * @param textContent 
     * @return 結果。
     * @throws Exception 
     */
    private List<Element> findElementsByTagName(Element parent, String tagName, String textContent) throws Exception {
        List<Element> elements = new ArrayList<>();
        // Search at XPath
        StringObject xpath = new StringObject(".//");
        xpath.append(tagName);
        xpath.append("[contains(text(), '");
        xpath.append(textContent);
        xpath.append("')]");
        Method byMethod = new Method(this.classBy);
        byMethod.setParameterTypes(String.class);
        Object by = byMethod.invoke("xpath", xpath.toString());
        // Search elements
        Method findElementsMethod = new Method(this.classRemoteWebElement, parent.element);
        findElementsMethod.setParameterTypes(this.classBy);
        List<?> elementObjects = findElementsMethod.invoke("findElements", by);
        for (Object elementObject : elementObjects) {
            elements.add(this.getElement(elementObject));
        }
        return elements;
    }

    /**
     * すでに選択状態にある要素の子要素から、タグ名が一致していて、内包するテキストが指定値を含む要素を選択状態にする。
     * 
     * @param tagName
     * @param textContent 
     * @throws Exception 
     */
    public void moreSelectElementsByTagName(String tagName, String textContent) throws Exception {
        List<Element> newSelectedElements = new ArrayList<>();
        for (Element selectedElement: this.selectedElements) {
            newSelectedElements.addAll(this.findElementsByTagName(selectedElement, tagName, textContent));
        }
        this.selectedElements = newSelectedElements;
    }

    /**
     * すでに選択状態にある要素の子要素から、タグ名が一致している要素を選択状態にする。
     * 
     * @param tagName
     * @throws Exception 
     */
    public void moreSelectElementsByTagName(String tagName) throws Exception {
        this.moreSelectElementsByTagName(tagName, "");
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
        this.moreSelectElementsByTagName(tagName, textContent);
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
    public void waitForTagNameFound(String tagName, String textContent, int timeoutSeconds) {
        Datetime limit = new Datetime();
        limit.addSecond(timeoutSeconds);
        while (limit.getDate().getTime() > new Date().getTime()) {
        	try {
		        if (this.findElementsByTagName(this.getBodyElement(), tagName, textContent).size() > 0) {
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
    public void waitForTagNameFound(String tagName, int timeoutSeconds) {
        this.waitForTagNameFound(tagName, "", timeoutSeconds);
    }
    
    /**
     * すべての要素から、タグ名が一致していて、内包するテキストが指定値を含む要素が失われるのを待機する。
     * 
     * @param tagName
     * @param textContent
     * @param timeoutSeconds タイムアウトまでの秒数。
     */
    public void waitForTagNameLost(String tagName, String textContent, int timeoutSeconds) {
        Datetime limit = new Datetime();
        limit.addSecond(timeoutSeconds);
        while (limit.getDate().getTime() > new Date().getTime()) {
        	try {
	            if (this.findElementsByTagName(this.getBodyElement(), tagName, textContent).size() == 0) {
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
    public void waitForTagNameLost(String tagName, int timeoutSeconds) {
        this.waitForTagNameLost(tagName, "", timeoutSeconds);
    }
    
    /**
     * 指定された親要素の中から、CSSセレクタに一致する要素を再帰的にすべて取得する。
     * 
     * @param parent
     * @param cssSelector
     * @return 結果。
     * @throws Exception
     */
    private List<Element> findElementsByCssSelector(Element parent, String cssSelector) throws Exception {
        List<Element> elements = new ArrayList<>();
        // Search at CSS selector
        Method byMethod = new Method(this.classBy);
        byMethod.setParameterTypes(String.class);
        Object by = byMethod.invoke("cssSelector", cssSelector);
        // Search elements
        Method findElementsMethod = new Method(this.classRemoteWebElement, parent.element);
        findElementsMethod.setParameterTypes(this.classBy);
        List<?> elementObjects = findElementsMethod.invoke("findElements", by);
        for (Object elementObject : elementObjects) {
            elements.add(this.getElement(elementObject));
        }
        return elements;
    }
    
    /**
     * すでに選択状態にある要素の子要素から、CSSセレクタに一致する要素を選択状態にする。
     * 
     * @param cssSelector
     * @throws Exception 
     */
    public void moreSelectElementsByCssSelector(String cssSelector) throws Exception {
        List<Element> newSelectedElements = new ArrayList<>();
        for (Element selectedElement: this.selectedElements) {
            newSelectedElements.addAll(this.findElementsByCssSelector(selectedElement, cssSelector));
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
        this.moreSelectElementsByCssSelector(cssSelector);
    }
    
    /**
     * すべての要素からCSSセレクタに一致する要素が見つかるのを待機する。
     * 
     * @param cssSelector
     * @param timeoutSeconds タイムアウトまでの秒数。
     */
    public void waitForCssSelectionFound(String cssSelector, int timeoutSeconds) {
        Datetime limit = new Datetime();
        limit.addSecond(timeoutSeconds);
        while (limit.getDate().getTime() > new Date().getTime()) {
        	try {
	            if (this.findElementsByCssSelector(this.getBodyElement(), cssSelector).size() > 0) {
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
    public void waitForCssSelectionLost(String cssSelector, int timeoutSeconds) {
        Datetime limit = new Datetime();
        limit.addSecond(timeoutSeconds);
        while (limit.getDate().getTime() > new Date().getTime()) {
        	try {
	            if (this.findElementsByCssSelector(this.getBodyElement(), cssSelector).size() == 0) {
	                return;
	            }
	            Thread.sleep(1000);
        	} catch (Exception exception) {
        	}
        }
    }

    /**
     * 指定された親要素の中から、XPathに一致する要素を再帰的にすべて取得する。
     * 
     * @param parent
     * @param xPath
     * @return 結果。
     * @throws Exception
     */
    private List<Element> findElementsByXPath(Element parent, String xPath) throws Exception {
        List<Element> elements = new ArrayList<>();
        // Search at XPath
        Method byMethod = new Method(this.classBy);
        byMethod.setParameterTypes(String.class);
        Object by = byMethod.invoke("xpath", xPath);
        // Search elements
        Method findElementsMethod = new Method(this.classRemoteWebElement, parent.element);
        findElementsMethod.setParameterTypes(this.classBy);
        List<?> elementObjects = findElementsMethod.invoke("findElements", by);
        for (Object elementObject : elementObjects) {
            elements.add(this.getElement(elementObject));
        }
        return elements;
    }
    
    /**
     * すでに選択状態にある要素の子要素から、XPathに一致する要素を選択状態にする。
     * 
     * @param xPath
     * @throws Exception 
     */
    public void moreSelectElementsByXPath(String xPath) throws Exception {
        List<Element> newSelectedElements = new ArrayList<>();
        for (Element selectedElement: this.selectedElements) {
            newSelectedElements.addAll(this.findElementsByXPath(selectedElement, xPath));
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
        this.moreSelectElementsByXPath(xPath);
    }
    
    /**
     * すべての要素からXPathに一致する要素が見つかるのを待機する。
     * 
     * @param xPath
     * @param timeoutSeconds タイムアウトまでの秒数。
     */
    public void waitForXPathFound(String xPath, int timeoutSeconds) {
        Datetime limit = new Datetime();
        limit.addSecond(timeoutSeconds);
        while (limit.getDate().getTime() > new Date().getTime()) {
        	try {
	            if (this.findElementsByXPath(this.getBodyElement(), xPath).size() > 0) {
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
    public void waitForXPathLost(String xPath, int timeoutSeconds) {
        Datetime limit = new Datetime();
        limit.addSecond(timeoutSeconds);
        while (limit.getDate().getTime() > new Date().getTime()) {
        	try {
	            if (this.findElementsByXPath(this.getBodyElement(), xPath).size() == 0) {
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
        
        private Object element;
        
        /**
         * この要素のタグ名を取得する。
         * 
         * @return 結果。
         * @throws Exception
         */
        public String getTagName() throws Exception {
            WebBrowser browser = WebBrowser.this;
            Method method = new Method(browser.classRemoteWebElement, this.element);
            return method.invoke("getTagName");
        }
        
        /**
         * この要素のHTMLソースコードを取得する。
         * 
         * @return 結果。
         * @throws Exception
         */
        public String getSource() throws Exception {
            WebBrowser browser = WebBrowser.this;
            Method method = new Method(browser.classRemoteWebElement, this.element);
            return method.invoke("getAttribute", "outerHTML");
        }
        
        /**
         * この要素が内包する文字列を取得する。
         * 
         * @return 結果。
         * @throws Exception
         */
        public String getTextContent() throws Exception {
            WebBrowser browser = WebBrowser.this;
            Method method = new Method(browser.classRemoteWebElement, this.element);
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
            WebBrowser browser = WebBrowser.this;
            Method method = new Method(browser.classRemoteWebElement, this.element);
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
         * この要素の属性を削除する。
         * 
         * @param name
         * @throws Exception
         */
        public void removeAttribute(String name) throws Exception {
            WebBrowser browser = WebBrowser.this;
            Method method = new Method(browser.classJavascriptExecutor, browser.webDriver);
            method.invoke("executeScript", "arguments[0].removeAttribute(arguments[1]);", new Object[] {this.element, name});
        }
        
        /**
         * この要素をクリックする。
         * 
         * @throws Exception
         */
        public void click() throws Exception {
            WebBrowser browser = WebBrowser.this;
        	browser.acceptDialog();
            Method method = new Method(browser.classJavascriptExecutor, browser.webDriver);
            method.invoke("executeScript", "arguments[0].click();", new Object[] {this.element});
            browser.mapElement.clear();
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
                Constructor constructor = new Constructor("org.openqa.selenium.support.ui.Select");
                constructor.setParameterTypes(browser.classWebElement);
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
            WebBrowser browser = WebBrowser.this;
            if (this.getTagName().equalsIgnoreCase("select")) {
                Constructor constructor = new Constructor("org.openqa.selenium.support.ui.Select");
                constructor.setParameterTypes(browser.classWebElement);
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
            for (Element element : this.getSelectedOptions()) {
            	element.removeAttribute("selected");
            }
        }
        
        /**
         * タイプ属性がfileのinput要素にファイルパスをセットする。
         * 
         * @param file
         * @throws Exception
         */
        public void setFile(File file) throws Exception {
            WebBrowser browser = WebBrowser.this;
            if (this.getTagName().equalsIgnoreCase("input") && this.getAttribute("type").equalsIgnoreCase("file")) {
                Method method = new Method(browser.classRemoteWebElement, this.element);
                method.setParameterTypes(CharSequence[].class);
                CharSequence[] parameters = new CharSequence[] {file.getAbsolutePath()};
                method.invoke("sendKeys", new Object[] {parameters});
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
            // By.xpath method
            Method byMethod = new Method(browser.classBy);
            byMethod.setParameterTypes(String.class);
            Object by = byMethod.invoke("xpath", "./*");
            // findElements method
            Method findElementsMethod = new Method(browser.classRemoteWebElement, this.element);
            findElementsMethod.setParameterTypes(browser.classBy);
            List<?> elements = findElementsMethod.invoke("findElements", by);
            List<Element> result = new ArrayList<>();
            for (Object element: elements) {
                result.add(browser.getElement(element));
            }
            return new Array<>(result);
        }
        
        /**
         * この要素の親要素を取得する。
         * 
         * @return 結果。
         * @throws Exception
         */
        public Element getParentElement() throws Exception {
            WebBrowser browser = WebBrowser.this;
            // By.xpath method
            Method byMethod = new Method(browser.classBy);
            byMethod.setParameterTypes(String.class);
            Object by = byMethod.invoke("xpath", "./..");
            // findElement method
            Method findElementsMethod = new Method(browser.classRemoteWebElement, this.element);
            findElementsMethod.setParameterTypes(browser.classBy);
            Object element = findElementsMethod.invoke("findElement", by);
            return browser.getElement(element);
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
