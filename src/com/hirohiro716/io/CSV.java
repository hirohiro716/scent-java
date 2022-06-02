package com.hirohiro716.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.hirohiro716.StringObject;
import com.hirohiro716.filesystem.File;
import com.hirohiro716.filesystem.File.ProcessAfterReadingCharacter;
import com.hirohiro716.filesystem.File.WritingProcess;

/**
 * CSVファイル(RFC4180準拠)の解析と作成を行うクラス。
 * 
 * @author hiro
 *
 */
public class CSV {
    
    private List<String> headers = null;
    
    /**
     * このインスタンスのヘッダーの情報を取得する。
     * 
     * @return 結果。
     */
    public List<String> getHeaders() {
        return this.headers;
    }
    
    /**
     * このインスタンスにヘッダーの情報をセットする。
     * 
     * @param headers
     */
    public void setHeaders(String... headers) {
        this.headers = Arrays.asList(headers);
    }
    
    /**
     * このインスタンスにヘッダーの情報をセットする。
     * 
     * @param headers
     */
    public void setHeaders(Collection<String> headers) {
        this.setHeaders(headers.toArray(new String[] {}));
    }
    
    private static final String DEFAULT_DELIMITER = ",";
    
    private String delimiter = CSV.DEFAULT_DELIMITER;
    
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
     * このインスタンスに行を追加する。
     * 
     * @param values
     */
    public void addRow(String... values) {
        this.rows.add(Arrays.asList(values));
    }
    
    /**
     * このインスタンスに行を追加する。
     * 
     * @param values
     */
    public final void addRow(Collection<String> values) {
        this.addRow(values.toArray(new String[] {}));
    }
    
    /**
     * このインスタンス内のすべての行を置き換える。
     * 
     * @param rows
     */
    public final void setRows(List<String>[] rows) {
        this.clearRows();
        for (List<String> row : rows) {
            this.addRow(row);
        }
    }
    
    /**
     * このインスタンス内のすべての行を置き換える。
     * 
     * @param rows
     */
    @SuppressWarnings("unchecked")
    public final void setRows(Collection<List<String>> rows) {
        this.setRows(rows.toArray(new ArrayList[] {}));
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
        file.write(charsetName, new WritingProcess() {
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
        });
    }

    /**
     * このインスタンスの値をデフォルトのcharsetを使用してファイルにエクスポートする。
     * 
     * @param file
     * @throws IOException
     */
    public final void exportToFile(File file) throws IOException {
        this.exportToFile(file, null);
    }
    
    /**
     * CSVファイル一行を書き込む度に処理を行うコールバック。
     * 
     * @author hiro
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
        CSVParser parser = new CSVParser(this.delimiter, new ProcessAfterParsing() {
            
            @Override
            public Exception call(List<String> parsed) {
                CSV csv = CSV.this;
                csv.rows.add(parsed);
                return null;
            }
        });
        file.read(charsetName, parser);
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
    public final void importFromFile(File file, boolean firstRowIsHeader) throws IOException {
        this.importFromFile(file, null, firstRowIsHeader);
    }
    
    /**
     * CSVファイル、charset、値区切り文字、行情報を解析した後の処理を指定してCSVファイルを解析する。
     * 
     * @param file
     * @param charsetName
     * @param delimiter
     * @param processAfterParsing
     * @return 処理中に発生した例外を返す。発生しなかった場合はnullを返す。
     * @throws IOException
     */
    public static Exception parse(File file, String charsetName, String delimiter, ProcessAfterParsing processAfterParsing) throws IOException {
        CSVParser parser = new CSVParser(delimiter, processAfterParsing);
        try {
            file.read(charsetName, parser);
        } catch (IOException exception) {
            if (parser.getException() == null) {
                throw exception;
            }
            return parser.getException();
        }
        if (parser.getIncompleteValues().size() > 0) {
            Exception exception = processAfterParsing.call(parser.getIncompleteValues());
            if (exception != null) {
                return exception;
            }
        }
        return null;
    }
    
    /**
     * CSVファイル、charset、行情報を解析した後の処理を指定して、値区切り文字にカンマを使用しているCSVファイルを解析する。
     * 
     * @param file
     * @param charsetName
     * @param processAfterParsing
     * @return 処理中に発生した例外を返す。発生しなかった場合はnullを返す。
     * @throws IOException
     */
    public static Exception parse(File file, String charsetName, ProcessAfterParsing processAfterParsing) throws IOException{
        return CSV.parse(file, charsetName, CSV.DEFAULT_DELIMITER, processAfterParsing);
    }
    
    /**
     * CSVファイル、行情報を解析した後の処理を指定して、デフォルトのcharsetで値区切り文字にカンマを使用しているCSVファイルを解析する。
     * 
     * @param file
     * @param processAfterParsing
     * @return 処理中に発生した例外を返す。発生しなかった場合はnullを返す。
     * @throws IOException
     */
    public static Exception parse(File file, ProcessAfterParsing processAfterParsing) throws IOException {
        return CSV.parse(file, null, CSV.DEFAULT_DELIMITER, processAfterParsing);
    }
    
    /**
     * CSVファイルの行情報を解析した後の処理インターフェース。
     * 
     * @author hiro
     */
    public interface ProcessAfterParsing {
        
        /**
         * CSVファイルの行情報を解析した後に呼び出される処理。
         * 
         * @param parsed
         * @return 処理中に発生した例外を返す。発生しなかった場合はnullを返す。
         */
        public abstract Exception call(List<String> parsed);
    }
    
    /**
     * CSVファイルの解析を行うクラス。
     * 
     * @author hiro
     */
    private static class CSVParser implements ProcessAfterReadingCharacter {
        
        /**
         * コンストラクタ。<br>
         * 値の区切り文字、CSVファイルの行情報を解析した後の処理を指定する。
         * 
         * @param delimiter 
         * @param processAfterParsing
         */
        private CSVParser(String delimiter, ProcessAfterParsing processAfterParsing) {
            this.delimiter = delimiter;
            this.processAfterParsing = processAfterParsing;
        }
        
        private String delimiter;
        
        private ProcessAfterParsing processAfterParsing;
        
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
                this.parseValueOfString(bufferedReader);
                break;
            case "\r":
            case "\n":
                break;
            default:
                this.parseValue(character, bufferedReader);
                break;
            }
            if (this.exception != null) {
                throw new IOException(this.exception);
            }
        }
        
        private Exception exception;
        
        /**
         * 処理中に発生した例外を取得する。発生していない場合はnullを返す。
         * 
         * @return 結果。
         */
        public Exception getException() {
            return this.exception;
        }
        
        /**
         * 行末に達した場合に行情報の配列を新しくする。
         */
        private void changeToNewRow() {
            if (this.values.size() > 0) {
                this.exception = this.processAfterParsing.call(this.values);
                this.values = new ArrayList<>();
            }
        }
        
        /**
         * ダブルクォートで囲われていない値を解析する。
         * 
         * @param firstCharacter ダブルクォートで囲われていないと判断した最初のcharacter。
         * @param bufferedReader
         * @throws IOException
         */
        private void parseValue(int firstCharacter, BufferedReader bufferedReader) throws IOException {
            StringObject result = new StringObject();
            int character = firstCharacter;
            while (character > -1) {
                String one = String.valueOf((char) character);
                if (one.equals("\n") || one.equals("\r")) {
                    this.values.add(result.toString());
                    this.changeToNewRow();
                    return;
                }
                if (one.equals(this.delimiter)) {
                    break;
                }
                result.append(one);
                character = bufferedReader.read();
            }
            this.values.add(result.toString());
        }
        
        /**
         * ダブルクォートで囲われている値を解析する。
         * 
         * @param bufferedReader
         * @throws IOException
         */
        private void parseValueOfString(BufferedReader bufferedReader) throws IOException {
            boolean isChangeToNewRow = false;
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
                            isChangeToNewRow = true;
                        }
                        break;
                    }
                } else {
                    result.append(first);
                }
                character = (char) bufferedReader.read();
            }
            this.values.add(result.toString());
            if (isChangeToNewRow) {
                this.changeToNewRow();
            }
        }
    }
}
