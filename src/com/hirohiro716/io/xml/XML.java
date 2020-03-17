package com.hirohiro716.io.xml;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.hirohiro716.Array;
import com.hirohiro716.StringObject;
import com.hirohiro716.filesystem.File;

/**
 * XMLファイルの解析と作成を行うクラス。
 * 
 * @author hiro
 *
 */
public class XML {
    
    /**
     * コンストラクタ。
     */
    public XML() {
        try {
            this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
            this.documentBuilder = this.documentBuilderFactory.newDocumentBuilder();
            this.root = new XMLNode(this.documentBuilder.newDocument());
        } catch (ParserConfigurationException exception) {
            exception.printStackTrace();
        }
    }
    
    private DocumentBuilderFactory documentBuilderFactory;
    
    private DocumentBuilder documentBuilder;
    
    private XMLNode root;
    
    /**
     * このインスタンスのルートのXMLNodeを取得する。
     * 
     * @return 結果。
     */
    public XMLNode getRoot() {
        return this.root;
    }
    
    private boolean isEnableIndent = true;
    
    /**
     * このインスタンスからソースを作る際のインデントを有効にする場合はtrueをセットする。初期値はtrue。
     * 
     * @param isEnableIndent
     */
    public void setEnableIndent(boolean isEnableIndent) {
        this.isEnableIndent = isEnableIndent;
    }
    
    /**
     * 指定されたエンコーディングを使用して、このインスタンスと同じ内容のソースを作成する。
     * 
     * @param encoding "UTF-8"や"Shift_JIS"などのエンコーディング。
     * @return 結果。
     * @throws TransformerException
     */
    public String buildSource(String encoding) throws TransformerException {
        DOMSource domSource = new DOMSource(this.root.getInnerInstance());
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
        if (this.isEnableIndent) {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        } else {
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
        }
        transformer.transform(domSource, streamResult);
        return stringWriter.toString();
    }

    /**
     * UTF-8のエンコーディングを使用して、このインスタンスと同じ内容のソースを作成する。
     * 
     * @return 結果。
     * @throws TransformerException
     */
    public String buildSource() throws TransformerException {
        return buildSource("UTF-8");
    }
    
    /**
     * 指定されたエンコーディングを使用して、このインスタンスの内容でXMLファイルにエクスポートする。
     * 
     * @param file
     * @param encoding "UTF-8"や"Shift_JIS"などのエンコーディング。
     * @throws IOException
     * @throws TransformerException
     */
    public void exportToFile(File file, String encoding) throws IOException, TransformerException {
        try (FileOutputStream outputStream = new FileOutputStream(file.toJavaIoFile())) {
            DOMSource domSource = new DOMSource(this.root.getInnerInstance());
            StreamResult streamResult = new StreamResult(outputStream);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
            if (this.isEnableIndent) {
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            } else {
                transformer.setOutputProperty(OutputKeys.INDENT, "no");
            }
            transformer.transform(domSource, streamResult);
        }
    }

    /**
     * UTF-8のエンコーディングを使用して、このインスタンスの内容でXMLファイルにエクスポートする。
     * 
     * @param file
     * @throws IOException
     * @throws TransformerException
     */
    public void exportToFile(File file) throws IOException, TransformerException {
        this.exportToFile(file, "UTF-8");
    }
    
    /**
     * 指定されたエンコーディングを使用して、このインスタンスにソースの内容をインポートする。
     * 
     * @param source
     * @param encoding
     * @throws IOException
     * @throws SAXException
     */
    public void importFromSource(String source, String encoding) throws IOException, SAXException {
        this.root = new XMLNode(this.documentBuilder.parse(new ByteArrayInputStream(source.getBytes(encoding))));
    }
    
    /**
     * UTF-8のエンコーディングを使用して、このインスタンスにソースの内容をインポートする。
     * 
     * @param source
     * @throws IOException
     * @throws SAXException
     */
    public void importFromSource(String source) throws IOException, SAXException {
        this.importFromSource(source, "UTF-8");
    }
    
    /**
     * このインスタンスにファイルの内容をインポートする。
     * 
     * @param file
     * @throws IOException
     * @throws SAXException
     */
    public void importFromFile(File file) throws IOException, SAXException {
        this.root = new XMLNode(this.documentBuilder.parse(file.toJavaIoFile()));
    }
    
    /**
     * XML要素のクラス。
     * 
     * @author hiro
     *
     */
    public class XMLNode {
        
        /**
         * コンストラクタ。
         * 
         * @param name
         * @param textContent
         * @param parent
         */
        public XMLNode(String name, String textContent, XMLNode parent) {
            XML xml = XML.this;
            Document document = (Document) xml.getRoot().getInnerInstance();
            this.node = document.createElement(name);
            parent.getInnerInstance().appendChild(this.getInnerInstance());
        }

        /**
         * コンストラクタ。
         * 
         * @param node
         */
        protected XMLNode(Node node) {
            this.node = node;
        }
        
        private Node node;
        
        /**
         * このXML要素にラップされているインスタンスを取得する。
         * 
         * @return 結果。
         */
        public Node getInnerInstance() {
            return this.node;
        }
        
        /**
         * このXML要素の名前を取得する。
         * 
         * @return 結果。
         */
        public String getName() {
            return this.getInnerInstance().getNodeName();
        }
        
        /**
         * このXML要素のテキストを取得する。
         * 
         * @return 結果。
         */
        public String getTextContent() {
            return this.getInnerInstance().getTextContent();
        }

        /**
         * このXML要素にテキストをセットする。
         * 
         * @param textContent
         */
        public void setTextContent(String textContent) {
            this.getInnerInstance().setTextContent(textContent);
        }
        
        /**
         * このXML要素の属性値を取得する。
         * 
         * @param name
         * @return 結果。
         */
        public String getAttributeValue(String name) {
            return this.getInnerInstance().getAttributes().getNamedItem(name).getNodeValue();
        }
        
        /**
         * このXML要素の属性値をセットする。
         * 
         * @param name
         * @param value
         */
        public void setAttributeValue(String name, String value) {
            this.getInnerInstance().getAttributes().getNamedItem(name).setNodeValue(value);
        }
        
        /**
         * このXML要素内に新しい要素を作成して文字列をセットする。
         * 
         * @param name
         * @param textContent
         * @return 作成した要素。
         */
        public XMLNode createNode(String name, String textContent) {
            XMLNode node = new XMLNode(name, textContent, this);
            if (textContent != null) {
                node.setTextContent(textContent);
            }
            return node;
        }
        
        /**
         * このXML要素内に新しい要素を作成する。
         * 
         * @param name
         * @return 作成した要素。
         */
        public XMLNode createNode(String name) {
            return this.createNode(name, null);
        }
        
        /**
         * このXML要素内の子要素を取得する。
         * 
         * @return 結果。
         */
        public Array<XMLNode> getChildren() {
            List<XMLNode> nodes = new ArrayList<>();
            NodeList nodeList = this.getInnerInstance().getChildNodes();
            for (int index = 0; index < nodeList.getLength(); index++) {
                nodes.add(new XMLNode(nodeList.item(index)));
            }
            return new Array<>(nodes);
        }
        
        /**
         * このXML要素内の子要素を属性名と属性値で検索する。
         * 
         * @param name
         * @param value 
         * @return 見つかった要素。
         */
        public Array<XMLNode> findXMLNodesByAttribute(String name, String value) {
            List<XMLNode> nodes = new ArrayList<>();
            for (XMLNode node : this.getChildren()) {
                if (node.getAttributeValue(name).equals(value)) {
                    nodes.add(node);
                }
            }
            return new Array<>(nodes);
        }

        /**
         * このXML要素内の子要素を属性名と属性値で検索する。該当するものがない場合はnullを返す。
         * 
         * @param name
         * @param value 
         * @return 見つかった要素。
         */
        public XMLNode findXMLNodeByAttribute(String name, String value) {
            Array<XMLNode> nodes = this.findXMLNodesByAttribute(name, value);
            if (nodes.length() == 0) {
                return null;
            }
            return nodes.get(0);
        }

        /**
         * このXML要素内の子要素を名前で検索する。
         * 
         * @param name
         * @return 見つかった要素。
         */
        public Array<XMLNode> findXMLNodesByName(String name) {
            List<XMLNode> nodes = new ArrayList<>();
            for (XMLNode node : this.getChildren()) {
                if (node.getName().equals(name)) {
                    nodes.add(node);
                }
            }
            return new Array<>(nodes);
        }
        
        /**
         * このXML要素内の子要素を名前で検索する。該当するものがない場合はnullを返す。
         * 
         * @param name
         * @return 見つかった要素。
         */
        public XMLNode findXMLNodeByName(String name) {
            Array<XMLNode> nodeList = findXMLNodesByName(name);
            if (nodeList.length() == 0) {
                return null;
            }
            return nodeList.get(0);
        }
        
        /**
         * このXML要素を親要素から取り除く。
         */
        public void remove() {
            Node parent = this.getInnerInstance().getParentNode();
            parent.removeChild(this.getInnerInstance());
        }

        @Override
        public String toString() {
            return StringObject.join(this.getName(), ":", this.getTextContent()).toString();
        }
    }
}
