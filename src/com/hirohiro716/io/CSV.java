package com.hirohiro716.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.hirohiro716.StringObject;
import com.hirohiro716.filesystem.File;
import com.hirohiro716.filesystem.File.ReadCharacterCallback;
import com.hirohiro716.filesystem.File.WriteCallback;

/**
 * CSVファイル(RFC4180準拠)の解析と作成を行うクラス。
 * 
 * @author hiro
 *
 */
public class CSV {
    
    private List<String> headers = null;
    
    /**
     * このインスタンスにヘッダーの情報をセットする。
     * 
     * @param headers
     */
    public void setHeaders(String... headers) {
        this.headers = new ArrayList<>();
        for (String header : headers) {
            this.headers.add(header);
        }
    }
    
    /**
     * このインスタンスにヘッダーの情報をセットする。
     * 
     * @param headers
     */
    public void setHeaders(Collection<String> headers) {
        this.setHeaders(headers.toArray(new String[] {}));
    }
    
    private String delimiter = ",";
    
    /**
     * このインスタンスで使用する値の区切り文字を指定する。初期値はカンマ。
     * 
     * @param delimiter
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
    
    private String lineSeparator = "\r\n";
    
    /**
     * このインスタンスで使用する行の区切り文字を指定する。初期値はCRLF。
     * 
     * @param lineSeparator
     */
    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }
    
    private List<List<String>> rows = new ArrayList<>();
    
    /**
     * このインスタンス内のすべての行を取得する。
     * 
     * @return 結果。
     */
    public List<List<String>> getRows() {
        return this.rows;
    }
    
    /**
     * このインスタンス内のすべての行を置き換える。
     * 
     * @param rows
     */
    public void setRows(List<String>[] rows) {
        this.clearRows();
        for (List<String> row : rows) {
            this.addRows(row);
        }
    }
    
    /**
     * このインスタンス内のすべての行を置き換える。
     * 
     * @param rows
     */
    @SuppressWarnings("unchecked")
    public void setRows(Collection<List<String>> rows) {
        this.setRows(rows.toArray(new ArrayList[] {}));
    }
    
    /**
     * このインスタンスに行を追加する。
     * 
     * @param values
     */
    public void addRows(String... values) {
        List<String> list = new ArrayList<>();
        for (String value : values) {
            list.add(value);
        }
        this.rows.add(list);
    }
    
    /**
     * このインスタンスに行に追加する。
     * 
     * @param values
     */
    public void addRows(Collection<String> values) {
        this.addRows(values.toArray(new String[] {}));
    }
    
    /**
     * このインスタンス内のすべての行をクリアする。
     */
    public void clearRows() {
        this.rows.clear();
    }
    
    /**
     * このインスタンスの値を指定されたcharsetを使用してファイルにエクスポートする。
     * 
     * @param file
     * @param charsetName
     * @throws IOException
     */
    public void exportToFile(File file, String charsetName) throws IOException {
        List<List<String>> writing = new ArrayList<>();
        if (this.headers != null) {
            writing.add(this.headers);
        }
        writing.addAll(this.rows);
        
        if (writing.size() == 0) {
            return;
        }
        
        file.write(new WriteCallback() {
            @Override
            public void call(OutputStreamWriter writer) throws IOException {
                CSV csv = CSV.this;
                int lastIndex = writing.size() - 1;
                for (int index = 0; index <= lastIndex; index++) {
                    List<String> values = writing.get(index);
                    StringObject line = new StringObject();
                    for (String value : values) {
                        if (line.length() > 0) {
                            line.append(csv.delimiter);
                        }
                        StringObject valueObject = new StringObject(value);
                        line.append("\"");
                        line.append(valueObject.replace("\"", "\"\""));
                        line.append("\"");
                    }
                    line.append(csv.lineSeparator);
                    writer.write(line.toString());
                }
            }
        }, charsetName);
    }

    /**
     * このインスタンスの値をデフォルトのcharsetを使用してファイルにエクスポートする。
     * 
     * @param file
     * @throws IOException
     */
    public void exportToFile(File file) throws IOException {
        this.exportToFile(file, null);
    }
    
    /**
     * CSVファイル一行を書き込む度に処理を行うコールバック。
     * 
     * @author hiro
     *
     */
    public interface WriteLineCallback {
        
        /**
         * CSVファイルに一行を書き込んだ後に呼び出される。
         * 
         * @param currentIndex
         * @param lastIndex
         */
        public abstract void call(int currentIndex, int lastIndex);
    }
    
    /**
     * CSVファイルを指定されたcharsetを使用して解析しインポートする。
     * 
     * @param file
     * @param charsetName
     * @param firstRowIsHeader 一行目をヘッダーとして取り込む場合はtrueを指定。
     * @throws IOException
     */
    public void importFromFile(File file, String charsetName, boolean firstRowIsHeader) throws IOException {
        this.rows.clear();
        if (file.isExist() == false) {
            return;
        }
        CSVParser parser = new CSVParser();
        file.read(parser, charsetName);
        if (parser.getIncompleteValues().size() > 0) {
            this.rows.add(parser.getIncompleteValues());
        }
        if (firstRowIsHeader && this.rows.size() > 0) {
            this.headers = this.rows.get(0);
            this.rows.remove(this.headers);
        }
    }

    /**
     * CSVファイルをデフォルトのcharsetを使用して解析する。
     * 
     * @param file
     * @param firstRowIsHeader 一行目をヘッダーとして取り込む場合はtrueを指定。
     * @throws IOException
     */
    public void importFromFile(File file, boolean firstRowIsHeader) throws IOException {
        this.importFromFile(file, null, firstRowIsHeader);
    }
    
    /**
     * CSVファイルの解析を行うReadCharacterCallback。
     * 
     * @author hiro
     *
     */
    private class CSVParser implements ReadCharacterCallback {
        
        private List<String> values = new ArrayList<>();
        
        /**
         * 取り込みが未完了の値を取得する。
         * 
         * @return 結果。
         */
        public List<String> getIncompleteValues() {
            return this.values;
        }
        
        @Override
        public void call(char character, BufferedReader bufferedReader) throws IOException {
            String first = String.valueOf(character);
            switch (first) {
            case "\"":
                this.values.add(this.parseValueOfString(bufferedReader));
                break;
            case "\r":
            case "\n":
                break;
            default:
                this.values.add(this.parseValue(character, bufferedReader));
                break;
            }
        }
        
        /**
         * 取り込み中の行情報を取得する。
         */
        private void changeToNewRow() {
            CSV csv = CSV.this;
            if (this.values.size() > 0) {
                csv.rows.add(this.values);
                this.values = new ArrayList<>();
            }
        }
        
        /**
         * ダブルクォートで囲われていない値を解析する。
         * 
         * @param firstCharacter ダブルクォートで囲われていないと判断した最初のcharacter。
         * @param bufferedReader
         * @return 結果。
         * @throws IOException
         */
        private String parseValue(int firstCharacter, BufferedReader bufferedReader) throws IOException {
            CSV csv = CSV.this;
            StringObject result = new StringObject();
            int character = firstCharacter;
            while (character > -1) {
                String first = String.valueOf((char) character);
                if (first.equals("\n") || first.equals("\r")) {
                    this.changeToNewRow();
                    break;
                }
                if (first.equals(csv.delimiter)) {
                    break;
                }
                result.append(first);
                character = bufferedReader.read();
            }
            return result.toString();
        }
        
        /**
         * ダブルクォートで囲われている値を解析する。
         * 
         * @param bufferedReader
         * @return 結果。
         * @throws IOException
         */
        private String parseValueOfString(BufferedReader bufferedReader) throws IOException {
            StringObject result = new StringObject();
            char character = (char) bufferedReader.read();
            while (character > -1) {
                String first = String.valueOf(character);
                if (first.equals("\"")) {
                    character = (char) bufferedReader.read();
                    String second = String.valueOf(character);
                    if (second.equals("\"")) {
                        result.append("\"");
                    } else {
                        if (second.equals("\n") || second.equals("\r")) {
                            this.changeToNewRow();
                        }
                        break;
                    }
                } else {
                    result.append(first);
                }
                character = (char) bufferedReader.read();
            }
            return result.toString();
        }
    }
}
