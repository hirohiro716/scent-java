package com.hirohiro716.scent.web;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.w3c.dom.Node;

import com.hirohiro716.scent.Array;
import com.hirohiro716.scent.StringObject;
import com.hirohiro716.scent.filesystem.Directory;
import com.hirohiro716.scent.filesystem.File;
import com.hirohiro716.scent.reflection.Method;

/**
 * HTML5、CSS3、JavaScriptをサポートするヘッドレスWEBブラウザのクラス。<br>
 * ・HtmlUnit - <a href="https://htmlunit.sourceforge.io/">https://htmlunit.sourceforge.io/</a>
 * 
 * @author hiro
*/
public class HeadlessWebBrowser extends WebBrowser<HeadlessWebBrowser.Element> {

    /**
     * コンストラクタ。<br>
     * パラメーターにHtmlUnitライブラリの各jarファイルが入ったディレクトリを指定する。
     * 
     * @param htmlUnitLibraryDirectory
     * @throws ClassNotFoundException
     * @throws Exception
     */
    public HeadlessWebBrowser(Directory htmlUnitLibraryDirectory) throws ClassNotFoundException, Exception {
        super(htmlUnitLibraryDirectory);
        Constructor constructor = new Constructor("org.htmlunit.WebClient");
        this.webClient = constructor.newInstance();
        Method getCurrentWindowMethod = new Method(this.webClient);
        this.webWindow = getCurrentWindowMethod.invoke("getCurrentWindow");
        // Hide warning
        java.util.logging.Logger.getLogger("org.htmlunit").setLevel(Level.OFF); 
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
        Constructor ajaxControllerConstructor = new Constructor("org.htmlunit.NicelyResynchronizingAjaxController");
        Object ajaxController = ajaxControllerConstructor.newInstance();
        Method setAjaxControllerMethod = new Method(this.webClient);
        setAjaxControllerMethod.setParameterTypes(this.loadClass("org.htmlunit.AjaxController"));
        setAjaxControllerMethod.invoke("setAjaxController", ajaxController);
    }

    private Object webClient;

    private Object webWindow;
    
    private Object webPage = null;

    @Override
    public void close() {
        try {
            Method method = new Method(this.webClient);
            method.invoke("close");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private Class<?> classWebWindow = this.loadClass("org.htmlunit.WebWindow");

    @Override
    public boolean isClosed() {
        try {
            Method method = new Method(classWebWindow, this.webWindow);
            return method.invoke("isClosed");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return true;
    }

    private Object otherPage = null;

    @Override
    public void load(URL url) throws Exception {
        Method method = new Method(this.webClient);
        Object maybeWebPage = method.invoke("getPage", url);
        if (maybeWebPage.getClass().getName().toLowerCase().indexOf("htmlpage") > -1) {
            this.webPage = maybeWebPage;
        }
        this.getMapOfElementAndHtmlObject().clear();
        this.clearSelectedElements();
    }
    
    @Override
    public String getTitle() {
        try {
            Method method = new Method(this.webPage);
            return method.invoke("getTitleText");
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public String getSource() {
        try {
            Method method = new Method(this.loadClass("org.htmlunit.SgmlPage"), this.webPage);
            return method.invoke("asXml");
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
    
    /**
     * JavaScriptなどから変更されたWEBページを再読み込みする。
     * 
     * @throws Exception 
     */
    public void updateWebPage() throws Exception {
        Method method = new Method(this.loadClass("org.htmlunit.WebWindow"), this.webWindow);
        Object maybeWebPage = method.invoke("getEnclosedPage");
        if (maybeWebPage.getClass().getName().toLowerCase().indexOf("htmlpage") > -1) {
            this.webPage = maybeWebPage;
        } else {
            this.otherPage = maybeWebPage;
            method.setParameterTypes(this.loadClass("org.htmlunit.Page"));
            method.invoke("setEnclosedPage", this.webPage);
        }
        this.getMapOfElementAndHtmlObject().clear();
        this.clearSelectedElements();
    }

    /**
     * WEBブラウザによって自動的にダウンロードされたHTML以外のファイルのInputStreamを取得する。存在しない場合はnullを返す。
     * 
     * @return
     */
    public InputStream getInputStream() {
        try {
            Method getInputStreamMethod = new Method(this.loadClass("org.htmlunit.UnexpectedPage"), this.otherPage);
            return getInputStreamMethod.invoke("getInputStream");
        } catch (Exception exception) {
            return null;
        }
    }
    
    @Override
    public void executeJavaScript(String javascript) {
        try {
            Method method = new Method(this.webPage);
            method.invoke("executeJavaScript", javascript);
            this.updateWebPage();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void switchFrame(String name) {
        try {
            Method getFrameByNameMethod = new Method(this.webPage);
            this.webWindow = getFrameByNameMethod.invoke("getFrameByName", name);
            Method getEnclosedPageMethod = new Method(this.loadClass("org.htmlunit.WebWindowImpl"), this.webWindow);
            this.webPage = getEnclosedPageMethod.invoke("getEnclosedPage");
            this.getMapOfElementAndHtmlObject().clear();
            this.clearSelectedElements();
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
            Method method = new Method(this.webPage);
            Object bodyHtmlElement = method.invoke("getBody");
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
        StringObject xpath = new StringObject("//*[@");
        xpath.append(attributeName);
        xpath.append("='");
        xpath.append(attributeValue);
        xpath.append("']");
        Method method = new Method(this.loadClass("org.htmlunit.html.DomNode"), parent.element);
        method.setParameterTypes(String.class);
        List<Object> elementObjects = method.invoke("getByXPath", xpath.toString());
        for (Object elementObject: elementObjects) {
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
        Method method = new Method(this.loadClass("org.htmlunit.html.DomNode"), parent.element);
        method.setParameterTypes(String.class);
        List<Object> elementObjects = method.invoke("getByXPath", xpath.toString());
        for (Object elementObject: elementObjects) {
            elements.add(this.getElement(elementObject));
        }
        return elements;
    }

    @Override
    protected List<Element> createElementListOfFoundByCssSelector(Element parent, String cssSelector) throws Exception {
        List<Element> elements = new ArrayList<>();
        // Search at CSS selector
        Method method = new Method(this.loadClass("org.htmlunit.html.DomNode"), parent.element);
        method.setParameterTypes(String.class);
        List<Object> elementObjects = method.invoke("querySelectorAll", cssSelector);
        for (Object elementObject: elementObjects) {
            elements.add(this.getElement(elementObject));
        }
        return elements;
    }

    @Override
    protected List<Element> createElementListOfFoundByXPath(Element parent, String xPath) throws Exception {
        List<Element> elements = new ArrayList<>();
        // Search at XPath
        Method method = new Method(this.loadClass("org.htmlunit.html.DomNode"), parent.element);
        method.setParameterTypes(String.class);
        List<Object> elementObjects = method.invoke("getByXPath", xPath);
        for (Object elementObject: elementObjects) {
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
        
        private Class<?> classDomNode = HeadlessWebBrowser.this.loadClass("org.htmlunit.html.DomNode");
        
        private Class<?> classDomElement = HeadlessWebBrowser.this.loadClass("org.htmlunit.html.DomElement");
        
        private Class<?> classHtmlSelect = HeadlessWebBrowser.this.loadClass("org.htmlunit.html.HtmlSelect");
        
        private Class<?> classHtmlFileInput = HeadlessWebBrowser.this.loadClass("org.htmlunit.html.HtmlFileInput");
        
        private Object element;
        
        @Override
        public String getTagName() throws Exception {
            Method method = new Method(this.classDomElement, this.element);
            return method.invoke("getTagName");
        }

        @Override
        public String getSource() throws Exception {
            Method method = new Method(this.classDomNode, this.element);
            return method.invoke("asXml");
        }

        @Override
        public String getTextContent() throws Exception {
            Method method = new Method(Node.class, this.element);
            return method.invoke("getTextContent");
        }

        @Override
        public void setTextContent(String textContent) throws Exception {
            Method method = new Method(this.classDomNode, this.element);
            method.invoke("setTextContent", textContent);
        }

        @Override
        public String getAttribute(String name) throws Exception {
            Method method = new Method(this.classDomElement, this.element);
            return method.invoke("getAttribute", name);
        }

        @Override
        public void setAttribute(String name, String value) throws Exception {
            Method method = new Method(this.classDomElement, this.element);
            method.invoke("setAttribute", name, value);
        }

        @Override
        public void removeAttribute(String name) throws Exception {
            Method method = new Method(this.classDomElement, this.element);
            method.invoke("removeAttribute", name);
        }

        @Override
        public void focus() throws Exception {
            HeadlessWebBrowser browser = HeadlessWebBrowser.this;
            try {
                Method method = new Method(browser.webPage);
                String backupClass = this.getAttribute("class");
                String flagClass = this.getClass().getName().replaceAll("\\.", "_") + "_FLAG_FOR_FOCUS";
                String newClass = StringObject.join(backupClass, " ", flagClass).toString();
                this.setAttribute("class", newClass);
                StringObject script = new StringObject("document.getElementsByClassName('");
                script.append(flagClass);
                script.append("')[0].focus();");
                method.invoke("executeJavaScript", script.toString());
                this.setAttribute("class", backupClass);
                browser.updateWebPage();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        @Override
        public void click() throws Exception {
            HeadlessWebBrowser browser = HeadlessWebBrowser.this;
            try {
                Method method = new Method(browser.webPage);
                String backupClass = this.getAttribute("class");
                String flagClass = this.getClass().getName().replaceAll("\\.", "_") + "_FLAG_FOR_CLICK";
                String newClass = StringObject.join(backupClass, " ", flagClass).toString();
                this.setAttribute("class", newClass);
                StringObject script = new StringObject("document.getElementsByClassName('");
                script.append(flagClass);
                script.append("')[0].click();");
                method.invoke("executeJavaScript", script.toString());
                this.setAttribute("class", backupClass);
                browser.updateWebPage();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        @Override
        public com.hirohiro716.scent.web.WebBrowser.Element[] getSelectedOptions() throws Exception {
            HeadlessWebBrowser browser = HeadlessWebBrowser.this;
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

        @Override
        public void addSelectedOption(String value) throws Exception {
            if (this.getTagName().equalsIgnoreCase("select")) {
                Method method = new Method(this.classHtmlSelect, this.element);
                method.setParameterTypes(String.class, boolean.class);
                method.invoke("setSelectedAttribute", value, true);
            }
        }

        @Override
        public void clearSelectedOptions() throws Exception {
            for (com.hirohiro716.scent.web.WebBrowser.Element element: this.getSelectedOptions()) {
                Method method = new Method(this.classHtmlSelect, this.element);
                method.setParameterTypes(String.class, boolean.class);
                method.invoke("setSelectedAttribute", element.getAttribute("value"), false);
            }
        }

        @Override
        public void setFile(File file) throws Exception {
            Method method = new Method(this.classHtmlFileInput, this.element);
            method.setParameterTypes(java.io.File[].class);
            method.invoke("setFiles", (Object) new java.io.File[] {file.toJavaIoFile()});
        }

        @Override
        @SuppressWarnings("unchecked")
        public Array<Element> getChildElements() throws Exception {
            HeadlessWebBrowser browser = HeadlessWebBrowser.this;
            Method method = new Method(this.classDomNode, this.element);
            method.setParameterTypes(String.class);
            List<Object> elementObjects = method.invoke("getByXPath", "./*");
            List<Element> result = new ArrayList<>();
            for (Object elementObject: elementObjects) {
                result.add(browser.getElement(elementObject));
            }
            return new Array<>(result);
        }

        @Override
        public com.hirohiro716.scent.web.WebBrowser.Element getParentElement() throws Exception {
            HeadlessWebBrowser browser = HeadlessWebBrowser.this;
            Method method = new Method(this.classDomNode, this.element);
            method.setParameterTypes(String.class);
            List<Object> elementObjects = method.invoke("getByXPath", "./..");
            for (Object elementObject: elementObjects) {
                return browser.getElement(elementObject);
            }
            return null;
        }
    }
}
