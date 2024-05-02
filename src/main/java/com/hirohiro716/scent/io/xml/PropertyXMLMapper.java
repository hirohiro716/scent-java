package com.hirohiro716.scent.io.xml;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import com.hirohiro716.scent.DynamicArray;
import com.hirohiro716.scent.property.PropertyInterface;
import com.hirohiro716.scent.property.ValidationException;

/**
 * XMLとオブジェクトをマップするための抽象クラス。
 * 
 * @author hiro
 * 
 * @param <P> プロパティの型。
 */
public abstract class PropertyXMLMapper<P extends PropertyInterface> {
    
    /**
     * コンストラクタ。
     * 
     * @throws IOException 
     */
    public PropertyXMLMapper() throws IOException {
        this.propertyXML = this.createPropertyXML();
        this.propertyValues = new DynamicArray<>();
        for (P property : this.getProperties()) {
            String value = this.propertyXML.get(property);
            if (this.propertyXML.exists(property)) {
                this.propertyValues.put(property, value);
            } else {
                this.propertyValues.put(property, property.getDefaultValue());
            }
        }
    }
    
    /**
     * このインスタンスにマップするXMLを作成する。
     * 
     * @return 結果。
     * @throws IOException 
     */
    protected abstract PropertyXML createPropertyXML() throws IOException;
    
    private PropertyXML propertyXML;
    
    /**
     * このインスタンスにマップされているXMLを取得する。
     * 
     * @return 結果。
     */
    public PropertyXML getPropertyXML() {
        return this.propertyXML;
    }
    
    private DynamicArray<P> propertyValues;
    
    /**
     * XMLに含まれるすべてのプロパティを取得する。
     * 
     * @return 結果。
     */
    public abstract P[] getProperties();
    
    /**
     * このインスタンスにマップされている、すべての値を取得する。
     * 
     * @return 結果。
     */
    public DynamicArray<P> getValues() {
        return this.propertyValues;
    }
    
    /**
     * このインスタンスにマップされているすべての値を、指定された値に置き換える。
     * 
     * @param values
     */
    public void setValues(DynamicArray<P> values) {
        this.propertyValues = values;
    }
    
    /**
     * このインスタンスにマップされているすべての値が有効か検証する。
     * 
     * @throws ValidationException
     * @throws Exception
     */
    public abstract void validate() throws ValidationException, Exception;
    
    /**
     * このインスタンスにマップされているすべての値を標準化する。
     * 
     * @throws Exception
     */
    public abstract void normalize() throws Exception;
    
    /**
     * このインスタンスにマップされている、すべての値をXMLファイルに書き込む。
     * 
     * @param encoding "UTF-8"や"Shift_JIS"などのエンコーディング。
     * @throws IOException
     * @throws TransformerException 
     */
    public void write(String encoding) throws IOException, TransformerException  {
        for (P property : this.getValues().getKeys()) {
            Object value = this.getValues().get(property);
            if (value == null) {
                value = "";
            }
            this.propertyXML.entry(property, value.toString());
        }
        this.propertyXML.write(encoding);
    }

    /**
     * UTF-8のエンコーディングを使用して、このインスタンスにマップされている、すべての値をXMLファイルに書き込む。
     * 
     * @throws IOException
     * @throws TransformerException 
     */
    public final void write() throws IOException, TransformerException  {
        this.write("UTF-8");
    }
}
