package com.hirohiro716.filesystem;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.hirohiro716.StringObject;

/**
 * ファイルシステム上のファイルクラス。
 * 
 * @author hiro
 *
 */
public class File extends FilesystemItem {

    /**
     * コンストラクタ。<br>
     * 指定されたjava.io.Fileから新しいインスタンスを作成する。
     * 
     * @param file
     * @throws IllegalArgumentException 
     */
    public File(java.io.File file) throws IllegalArgumentException {
        super(file);
        if (this.toJavaIoFile().exists() && this.isFile() == false) {
            throw new IllegalArgumentException("Argument must be file:" + this.getPath());
        }
    }

    /**
     * コンストラクタ。<br>
     * 指定されたパスで新しいインスタンスを作成する。
     * 
     * @param location
     */
    public File(String location) {
        this(new java.io.File(location));
    }
    
    /**
     * コンストラクタ。<br>
     * 指定されたディレクトリ内、ファイル名で新しいインスタンスを作成する。
     * 
     * @param parentDirectory 
     * @param fileName 
     */
    public File(Directory parentDirectory, String fileName) {
        this(new java.io.File(parentDirectory.toJavaIoFile(), fileName));
    }
    
    /**
     * コンストラクタ。<br>
     * 指定されたファイルのURIで新しいインスタンスを作成する。
     * 
     * @param uri
     */
    public File(URI uri) {
        this(new java.io.File(uri));
    }
    
    @Override
    public boolean isExist() {
        return this.toJavaIoFile().exists() && this.isFile();
    }

    @Override
    public void create() throws IOException {
        Files.createFile(this.toJavaIoFile().toPath());
    }

    @Override
    public void delete() throws IOException {
        Files.delete(this.toJavaIoFile().toPath());
    }

    @Override
    public void move(String moveTo) throws IOException {
        Files.move(this.toJavaIoFile().toPath(), Paths.get(moveTo));
    }

    @Override
    public void copy(String copyTo) throws IOException {
        Files.copy(this.toJavaIoFile().toPath(), Paths.get(copyTo));
    }
    
    /**
     * このファイルの内容を指定されたcharsetを使用して読み込む。
     * 
     * @param charsetName 
     * @param processAfterReadingCharacter 読み込んだファイルの一文字を処理するコールバック。
     * @throws IOException
     */
    public void read(String charsetName, ProcessAfterReadingCharacter processAfterReadingCharacter) throws IOException {
        Charset charset = Charset.defaultCharset();
        try {
            if (charsetName != null && charsetName.length() > 0) {
                charset = Charset.forName(charsetName);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        try (FileInputStream stream = new FileInputStream(this.toJavaIoFile())) {
            try (InputStreamReader streamReader = new InputStreamReader(stream, charset)) {
                try (BufferedReader bufferedReader = new BufferedReader(streamReader)) {
                    int character = bufferedReader.read();
                    while (character > -1) {
                        processAfterReadingCharacter.call((char) character, bufferedReader);
                        character = bufferedReader.read();
                    }
                }
            }
        }
    }
    
    /**
     * このファイルの内容をデフォルトのcharsetを使用して読み込む。
     * 
     * @param processAfterReadingCharacter 読み込んだファイルの一文字を処理するコールバック。
     * @throws IOException
     */
    public void read(ProcessAfterReadingCharacter processAfterReadingCharacter) throws IOException {
        this.read(null, processAfterReadingCharacter);
    }
    
    /**
     * ファイルの一文字を読み込んだ後の処理インターフェース。
     * 
     * @author hiro
     *
     */
    public interface ProcessAfterReadingCharacter {
        
        /**
         * ファイルの一文字を読み込んだ際に呼び出される。
         * 
         * @param character
         * @param bufferedReader 読み込みに使用しているBufferedReaderインスタンス。
         * @throws IOException 
         */
        public abstract void call(char character, BufferedReader bufferedReader) throws IOException;
    }
    
    /**
     * このファイルの内容を指定されたcharsetを使用して読み込む。
     * 
     * @param charsetName 
     * @param processAfterReadingLine 読み込んだ一行を処理するコールバック。
     * @throws IOException
     */
    public void read(String charsetName, ProcessAfterReadingLine processAfterReadingLine) throws IOException {
        Charset charset = Charset.defaultCharset();
        try {
            if (charsetName != null && charsetName.length() > 0) {
                charset = Charset.forName(charsetName);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        try (FileInputStream stream = new FileInputStream(this.toJavaIoFile())) {
            try (InputStreamReader streamReader = new InputStreamReader(stream, charset)) {
                try (BufferedReader bufferedReader = new BufferedReader(streamReader)) {
                    String line = bufferedReader.readLine();
                    while (line != null) {
                        processAfterReadingLine.call(line, bufferedReader);
                        line = bufferedReader.readLine();
                    }
                }
            }
        }
    }
    
    /**
     * このファイルの内容をデフォルトのcharsetを使用して読み込む。
     * 
     * @param processAfterReadingLine 読み込んだ一行を処理するコールバック。
     * @throws IOException
     */
    public void read(ProcessAfterReadingLine processAfterReadingLine) throws IOException {
        this.read(null, processAfterReadingLine);
    }
    
    /**
     * ファイルの一行を読み込んだ後の処理インターフェース。
     * 
     * @author hiro
     *
     */
    public interface ProcessAfterReadingLine {
        
        /**
         * ファイルの一行を読み込んだ際に呼び出される。
         * 
         * @param line
         * @param bufferedReader 読み込みに使用しているBufferedReaderインスタンス。
         * @throws IOException 
         */
        public abstract void call(String line, BufferedReader bufferedReader) throws IOException;
    }

    /**
     * ファイルの行数をカウントするクラス。
     * 
     * @author hiro
     *
     */
    public static class LineCounter implements ProcessAfterReadingLine {
        
        private int numberOfLines = 0;
        
        /**
         * カウントされたCSVファイルの行数を返す。
         * 
         * @return 結果。
         */
        public int getNumberOfLines() {
            return this.numberOfLines;
        }
        
        @Override
        public void call(String line, BufferedReader bufferedReader) throws IOException {
            this.numberOfLines++;
        }
    }

    /**
     * このファイルに指定されたcharsetを使用して文字列を書き込む。既存の内容は上書きされる。
     * 
     * @param charsetName 
     * @param writingProcess 書き込み処理するコールバック。
     * @throws IOException
     */
    public void write(String charsetName, WritingProcess writingProcess) throws IOException {
        Charset charset = Charset.defaultCharset();
        try {
            if (charsetName != null && charsetName.length() > 0) {
                charset = Charset.forName(charsetName);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        try (FileOutputStream stream = new FileOutputStream(this.toJavaIoFile())) {
            try (OutputStreamWriter writer = new OutputStreamWriter(stream, charset)) {
                writingProcess.call(writer);
            }
        }
    }

    /**
     * このファイルに指定されたcharsetを使用して文字列を書き込む。既存の内容は上書きされる。
     * 
     * @param writingProcess 書き込み処理するコールバック。
     * @throws IOException
     */
    public void write(WritingProcess writingProcess) throws IOException {
        this.write(null, writingProcess);
    }
    
    /**
     * 書き込みの処理インターフェース。
     * 
     * @author hiro
     *
     */
    public static interface WritingProcess {
        
        /**
         * ファイルに文字列を書き込む際に呼び出される。
         * 
         * @param writer 書き込み対象のOutputStreamWriter。
         * @throws IOException
         */
        public abstract void call(OutputStreamWriter writer) throws IOException;
    }
    
    /**
     * 指定されたクラスのクラスファイルを取得する。<br>
     * もしクラスファイルがjarファイルに含まれる場合はjarファイルを取得する。
     * 
     * @param clazz
     * @return クラスファイル(*.class *.jar)
     * @throws URISyntaxException
     */
    public static File findClassFile(Class<?> clazz) throws URISyntaxException {
        StringObject path = new StringObject();
        StringObject classURL = new StringObject(clazz.getResource(clazz.getSimpleName() + ".class").toExternalForm());
        classURL.replace("jar:", "");
        for (String part: classURL.split(FilesystemItem.getFileSeparator())) {
            if (path.length() > 0) {
                path.append(FilesystemItem.getFileSeparator());
            }
            if (path.length() > 0 || part.equals("file:")) {
                if (part.endsWith("!")) {
                    path.append(part.substring(0, part.length() - 1));
                    break;
                }
                path.append(part);
            }
        }
        return new File(new URI(path.toString()));
    }
}
