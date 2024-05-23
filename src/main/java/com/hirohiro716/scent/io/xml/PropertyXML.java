package com.hirohiro716.scent.io.xml;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.hirohiro716.scent.StringObject;
import com.hirohiro716.scent.filesystem.File;
import com.hirohiro716.scent.io.xml.XML.XMLNode;
import com.hirohiro716.scent.property.PropertyInterface;

/**
 * XMLファイルのプロパティを読み書きするクラス。
 * 
 * @author hiro
*/
public class PropertyXML {
    
    /**
     * コンストラクタ。<br>
     * 指定されたXMLファイルから情報を読み込む。
     * 
     * @param file
     * @throws IOException 
     */
    public PropertyXML(File file) throws IOException {
        if (file.exists() == false) {
            file.create();
        }
        this.file = file;
        try {
            this.xml = new XML();
            this.xml.importFromFile(file);
        } catch (SAXException exception) {
        }
        this.xmlNode = this.xml.getRoot().findXMLNodeByName("properties");
        if (this.xmlNode == null) {
            this.xmlNode = this.xml.getRoot().createNode("properties");
        }
    }
    
    private File file;
    
    private XML xml;
    
    private XMLNode xmlNode;
    
    /**
     * プロパティが存在する場合はtrueを返す。
     * 
     * @param property
     * @return 結果。
     */
    public boolean exists(PropertyInterface property) {
        XMLNode node = this.xmlNode.findXMLNodeByName(property.getPhysicalName());
        if (node == null) {
            return false;
        }
        return true;
    }
    
    /**
     * プロパティの値を読み込む。該当がない場合はnullを返す。
     * 
     * @param property
     * @return 結果。
     */
    public String get(PropertyInterface property) {
        XMLNode node = this.xmlNode.findXMLNodeByName(property.getPhysicalName());
        if (node == null) {
            return null;
        }
        return node.getTextContent();
    }
    
    /**
     * プロパティの値をエントリする。ファイルへの書き込みは行わない。
     * 
     * @param property
     * @param value
     */
    public void entry(PropertyInterface property, String value) {
        XMLNode node = this.xmlNode.findXMLNodeByName(property.getPhysicalName());
        if (node == null) {
            node = this.xmlNode.createNode(property.getPhysicalName());
        }
        node.setTextContent(StringObject.newInstance(value).toString());
    }
    
    /**
     * プロパティのエントリを削除する。ファイルへの書き込みは行わない。
     * 
     * @param property
     */
    public void removeEntry(PropertyInterface property) {
        XMLNode node = this.xmlNode.findXMLNodeByName(property.getPhysicalName());
        if (node != null) {
            node.remove();
        }
    }
    
    /**
     * プロパティのエントリをすべて削除する。ファイルへの書き込みは行わない。
     */
    public void clearEntry() {
        for (XMLNode node : this.xmlNode.getChildren()) {
            node.remove();
        }
    }
    
    /**
     * コンストラクタで指定されたファイルに現在のプロパティを書き込む。
     * 
     * @param encoding "UTF-8"や"Shift_JIS"などのエンコーディング。
     * @throws IOException
     * @throws TransformerException
     */
    public void write(String encoding) throws IOException, TransformerException {
        this.xml.exportToFile(this.file, encoding);
    }

    /**
     * UTF-8のエンコーディングを使用して、コンストラクタで指定されたファイルに現在のプロパティを書き込む。
     * 
     * @throws IOException
     * @throws TransformerException
     */
    public void write() throws IOException, TransformerException {
        this.xml.exportToFile(this.file);
    }
}
