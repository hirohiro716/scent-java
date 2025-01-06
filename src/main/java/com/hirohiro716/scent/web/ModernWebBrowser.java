package com.hirohiro716.scent.web;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hirohiro716.scent.Array;
import com.hirohiro716.scent.IdentifiableEnum;
import com.hirohiro716.scent.StringObject;
import com.hirohiro716.scent.datetime.Datetime;
import com.hirohiro716.scent.filesystem.Directory;
import com.hirohiro716.scent.filesystem.File;
import com.hirohiro716.scent.reflection.Method;

/**
 * HTML5、CSS3、JavaScriptをサポートするモダンWEBブラウザのクラス。<br>
 * ・Selenium - <a href="https://www.selenium.dev/downloads/">https://www.selenium.dev/downloads/</a><br>
 * ・WebDriver - <a href="https://www.selenium.dev/documentation/en/webdriver/driver_requirements/">https://www.selenium.dev/documentation/en/webdriver/driver_requirements/</a>
 * 
 * @author hiro
*/
public class ModernWebBrowser extends WebBrowser<ModernWebBrowser.Element> {
    
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
    public ModernWebBrowser(Directory seleniumLibraryDirectory, Type type, File seleniumWebDriver) throws ClassNotFoundException, Exception {
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
    public ModernWebBrowser(Directory seleniumLibraryDirectory, File seleniumWebDriver) throws ClassNotFoundException, Exception {
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
    public ModernWebBrowser(Directory seleniumLibraryDirectory, Type type) throws ClassNotFoundException, Exception {
        this(seleniumLibraryDirectory, type, null);
    }

    private Class<?> classWebDriver = this.loadClass("org.openqa.selenium.WebDriver");
    
    private Object webDriver;
    
    /**
     * 指定されたタイプのWEBドライバーを作成する。
     * 
     * @param type
     * @return
     * @throws Exception
     */
    public Object createWebDriver(Type type) throws Exception {
        Object webDriver = null;
        Constructor webDriverConstructor;
        Constructor optionConstructor;
        Object options;
        switch (type) {
        case CHROME:
            optionConstructor = new Constructor("org.openqa.selenium.chrome.ChromeOptions");
            options = optionConstructor.newInstance();
            Method chromeSetExperimentalOptionMethod = new Method(this.loadClass("org.openqa.selenium.chrome.ChromeOptions"), options);
            Map<String, Object> chromePrefs = new HashMap<>();
            chromePrefs.put("profile.content_settings.exceptions.automatic_downloads.*.setting", 1);
            chromeSetExperimentalOptionMethod.setParameterTypes(String.class, Object.class);
            chromeSetExperimentalOptionMethod.invoke("setExperimentalOption", "prefs", chromePrefs);
            webDriverConstructor = new Constructor("org.openqa.selenium.chrome.ChromeDriver");
            webDriver = webDriverConstructor.newInstance(options);
            break;
        case FIREFOX:
            optionConstructor = new Constructor("org.openqa.selenium.firefox.FirefoxOptions");
            options = optionConstructor.newInstance();
            Method firefoxAddPreferenceMethod = new Method(this.loadClass("org.openqa.selenium.firefox.FirefoxOptions"), options);
            firefoxAddPreferenceMethod.setParameterTypes(String.class, boolean.class);
            firefoxAddPreferenceMethod.invoke("addPreference", "pdfjs.disabled", true);
            webDriverConstructor = new Constructor("org.openqa.selenium.firefox.FirefoxDriver");
            webDriver = webDriverConstructor.newInstance(options);
            break;
        case EDGE:
            webDriverConstructor = new Constructor("org.openqa.selenium.edge.EdgeDriver");
            webDriver = webDriverConstructor.newInstance();
            break;
        case OPERA:
            webDriverConstructor = new Constructor("org.openqa.selenium.opera.OperaDriver");
            webDriver = webDriverConstructor.newInstance();
            break;
        case SAFARI:
            webDriverConstructor = new Constructor("org.openqa.selenium.safari.SafariDriver");
            webDriver = webDriverConstructor.newInstance();
            break;
        }
        return webDriver;
    }
    
    @Override
    public void close() {
        try {
            Method method = new Method(this.classWebDriver, this.webDriver);
            method.invoke("quit");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public boolean isClosed() {
        try {
            Method method = new Method(this.classWebDriver, this.webDriver);
            method.invoke("getTitle");
        } catch (Exception exception) {
            switch (exception.getCause().getClass().getSimpleName()) {
                case "NoSuchSessionException":
                case "NoSuchWindowException":
                    return true;
                case "WebDriverException":
                    if (exception.getCause().getMessage().toLowerCase().contains("window was closed")) {
                        return true;
                    }
                    if (exception.getCause().getMessage().toLowerCase().contains("failed to connect to localhost")) {
                        return true;
                    }
                    break;
                default:
                    break;
            }
            exception.printStackTrace();
        }
        return false;
    }

    /**
     * WEBブラウザに表示されているダイアログを承認する。
     */
    public void acceptDialog() {
    	try {
            Method method = new Method(this.classWebDriver, this.webDriver);
            Object switchTo = method.invoke("switchTo");
            Method alertMethod = new Method(switchTo);
            Object alert = alertMethod.invoke("alert");
            Method acceptMethod = new Method(alert);
            acceptMethod.invoke("accept");
            Thread.sleep(500);
            this.acceptDialog();
    	} catch (Exception exception) {
    	}
    }
    
    private Class<?> classRemoteWebElement = this.loadClass("org.openqa.selenium.remote.RemoteWebElement");

    private Class<?> classWebElement = this.loadClass("org.openqa.selenium.WebElement");

    private Class<?> classBy = this.loadClass("org.openqa.selenium.By");
    
    private Class<?> classJavascriptExecutor = this.loadClass("org.openqa.selenium.JavascriptExecutor");
    
    /**
     * WEBページの読み込み開始と読み込み完了を待つ。
     * 
     * @throws Exception
     */
    public void waitForLoadingAndComplete() throws Exception {
        Datetime limit = new Datetime();
        limit.addSecond(1);
        String result = "";
        while (result.equals("loading") == false) {
            Method method = new Method(this.classJavascriptExecutor, this.webDriver);
            result = method.invoke("executeScript", "return document.readyState;", new Object[] {});
            if (limit.getAllMilliSecond() < Datetime.newInstance().getAllMilliSecond()) {
                break;
            }
            Thread.sleep(200);
        }
        while (result.equals("complete") == false) {
            Method method = new Method(this.classJavascriptExecutor, this.webDriver);
            result = method.invoke("executeScript", "return document.readyState;", new Object[] {});
        }
    }

    @Override
    public void load(URL url) throws Exception {
    	this.acceptDialog();
        Method method = new Method(this.classWebDriver, this.webDriver);
        method.invoke("get", url.toExternalForm());
        this.getMapOfElementAndHtmlObject().clear();
        this.clearSelectedElements();
    }
    
    @Override
    public String getTitle() {
        try {
            Method method = new Method(this.classWebDriver, this.webDriver);
            return method.invoke("getTitle");
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
    
    @Override
    public String getSource() {
        try {
            Method method = new Method(this.classWebDriver, this.webDriver);
            return method.invoke("getPageSource");
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void executeJavaScript(String javascript) {
        try {
            this.acceptDialog();
            Method method = new Method(this.classJavascriptExecutor, this.webDriver);
            method.invoke("executeScript", javascript, new Object[] {});
            this.getMapOfElementAndHtmlObject().clear();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void switchFrame(String name) {
        try {
            Method method = new Method(this.classWebDriver, this.webDriver);
            Object switchTo = method.invoke("switchTo");
            Method frameMethod = new Method(switchTo);
            frameMethod.invoke("frame", name);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected Element createElement(Object htmlObject) throws ClassNotFoundException {
        return new Element(htmlObject);
    }
    
    @Override
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

    @Override
    protected List<Element> createElementListOfFoundByAttribute(Element parent, String attributeName, String attributeValue) throws Exception {
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

    @Override
    protected List<Element> createElementListOfFoundByTagName(Element parent, String tagName, String textContent) throws Exception {
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

    @Override
    protected List<Element> createElementListOfFoundByCssSelector(Element parent, String cssSelector) throws Exception {
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

    @Override
    protected List<Element> createElementListOfFoundByXPath(Element parent, String xPath) throws Exception {
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
     * WebBrowserにおけるHTML文書内の要素を表す。
     * 
     * @author hiro
     */
    public class Element implements WebBrowser.Element {
        
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
        
        @Override
        public String getTagName() throws Exception {
            ModernWebBrowser browser = ModernWebBrowser.this;
            Method method = new Method(browser.classRemoteWebElement, this.element);
            return method.invoke("getTagName");
        }
        
        @Override
        public String getSource() throws Exception {
            ModernWebBrowser browser = ModernWebBrowser.this;
            Method method = new Method(browser.classRemoteWebElement, this.element);
            return method.invoke("getAttribute", "outerHTML");
        }
        
        @Override
        public String getTextContent() throws Exception {
            ModernWebBrowser browser = ModernWebBrowser.this;
            Method method = new Method(browser.classRemoteWebElement, this.element);
            return method.invoke("getAttribute", "innerText");
        }
        
        @Override
        public void setTextContent(String textContent) throws Exception {
            ModernWebBrowser browser = ModernWebBrowser.this;
            Method method = new Method(browser.classJavascriptExecutor, browser.webDriver);
            method.invoke("executeScript", "arguments[0].innerText = arguments[1];", new Object[] {this.element, textContent});
        }
        
        @Override
        public String getAttribute(String name) throws Exception {
            ModernWebBrowser browser = ModernWebBrowser.this;
            Method method = new Method(browser.classRemoteWebElement, this.element);
            return method.invoke("getAttribute", name);
        }
        
        @Override
        public void setAttribute(String name, String value) throws Exception {
            ModernWebBrowser browser = ModernWebBrowser.this;
            Method method = new Method(browser.classJavascriptExecutor, browser.webDriver);
            method.invoke("executeScript", "arguments[0].setAttribute(arguments[1], arguments[2]);", new Object[] {this.element, name, value});
        }

        @Override
        public void removeAttribute(String name) throws Exception {
            ModernWebBrowser browser = ModernWebBrowser.this;
            Method method = new Method(browser.classJavascriptExecutor, browser.webDriver);
            method.invoke("executeScript", "arguments[0].removeAttribute(arguments[1]);", new Object[] {this.element, name});
        }
        
        @Override
        public void focus() throws Exception {
            ModernWebBrowser browser = ModernWebBrowser.this;
            browser.acceptDialog();
            Method method = new Method(browser.classJavascriptExecutor, browser.webDriver);
            method.invoke("executeScript", "arguments[0].focus();", new Object[] {this.element});
            browser.getMapOfElementAndHtmlObject().clear();
        }

        @Override
        public void click() throws Exception {
            ModernWebBrowser browser = ModernWebBrowser.this;
            browser.acceptDialog();
            Method method = new Method(browser.classJavascriptExecutor, browser.webDriver);
            method.invoke("executeScript", "arguments[0].click();", new Object[] {this.element});
            browser.getMapOfElementAndHtmlObject().clear();
        }

        @Override
        public Element[] getSelectedOptions() throws Exception {
            ModernWebBrowser browser = ModernWebBrowser.this;
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
        
        @Override
        public void addSelectedOption(String value) throws Exception {
            ModernWebBrowser browser = ModernWebBrowser.this;
            if (this.getTagName().equalsIgnoreCase("select")) {
                Constructor constructor = new Constructor("org.openqa.selenium.support.ui.Select");
                constructor.setParameterTypes(browser.classWebElement);
                Object selectElement = constructor.newInstance(this.element);
                Method method = new Method(selectElement);
                method.invoke("selectByValue", value);
            }
        }
        
        @Override
        public void clearSelectedOptions() throws Exception {
            for (Element element : this.getSelectedOptions()) {
            	element.removeAttribute("selected");
            }
        }
        
        @Override
        public void setFile(File file) throws Exception {
            ModernWebBrowser browser = ModernWebBrowser.this;
            if (this.getTagName().equalsIgnoreCase("input") && this.getAttribute("type").equalsIgnoreCase("file")) {
                Method method = new Method(browser.classRemoteWebElement, this.element);
                method.setParameterTypes(CharSequence[].class);
                CharSequence[] parameters = new CharSequence[] {file.getAbsolutePath()};
                method.invoke("sendKeys", new Object[] {parameters});
            }
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public Array<Element> getChildElements() throws Exception {
            ModernWebBrowser browser = ModernWebBrowser.this;
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
        
        @Override
        public Element getParentElement() throws Exception {
            ModernWebBrowser browser = ModernWebBrowser.this;
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
         * @return
         */
        public String getSystemPropertyName() {
            return this.systemPropertyName;
        }
        
        /**
         * 指定されたWEBドライバーのファイルから、該当する列挙子を取得する。該当するものがない場合はnullを返す。
         * 
         * @param seleniumWebDriver 
         * @return
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
