package com.hirohiro716.scent.filesystem;

import java.util.LinkedHashMap;

import com.hirohiro716.scent.IdentifiableEnum;

/**
 * ファイル拡張子の列挙型。
 */
public enum FileExtension implements IdentifiableEnum<String> {
    /**
     * PDFファイル。
     */
    PDF("pdf", "PDFファイル", "application/pdf"),
    /**
     * JPEGファイル。
     */
    JPG("jpg", "JPEGファイル", "image/jpeg"),
    /**
     * PNGファイル。
     */
    PNG("png", "PNGファイル", "image/png"),
    /**
     * TIFFファイル。
     */
    TIFF("tiff", "TIFFファイル", "image/tiff"),
    /**
     * マイクロソフトWordファイル。
     */
    DOCX("docx", "マイクロソフトWordファイル", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    /**
     * マイクロソフトExcelファイル。
     */
    XLSX("xlsx", "マイクロソフトExcelファイル", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    /**
     * マイクロソフトPowerPointファイル。
     */
    PPTX("pptx", "マイクロソフトPowerPointファイル", "appliapplication/vnd.openxmlformats-officedocument.presentationml.presentation"),
    /**
     * CSVファイル。
     */
    CSV("csv", "CSVファイル", "text/csv"),
    /**
     * ZIPファイル。
     */
    ZIP("zip", "ZIPファイル", "application/zip"),
    ;

    /**
     * コンストラクタ。
     * 
     * @param id 拡張子のID。
     * @param name 名前。
     * @param contentType コンテンツタイプ。
     */
    private FileExtension(String id, String name, String contentType) {
        this.id = id;
        this.name = name;
        this.contentType = contentType;
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

    private String contentType;
    
    /**
     * 拡張子のコンテンツタイプを取得する。
     * 
     * @return
     */
    public String getContentType() {
        return this.contentType;
    }

    /**
     * 指定されたIDから、該当する列挙子を取得する。該当するものがない場合はnullを返す。
     * 
     * @param id
     * @return
     */
    public static FileExtension enumOf(String id) {
        String fileExtension = id.toLowerCase();
        if (fileExtension.equals("jpeg")) {
            fileExtension = "jpg";
        }
        return IdentifiableEnum.enumOf(fileExtension, FileExtension.class);
    }

    /**
     * 指定されたファイル名から、該当する列挙子を取得する。該当するものがない場合はnullを返す。
     * 
     * @param fileName
     * @return
     */
    public static FileExtension fromFileName(String fileName) {
        String[] parts = fileName.split("\\.");
        String id = parts[parts.length - 1];
        return FileExtension.enumOf(id);
    }

    /**
     * すべての列挙子で、キーがID、値が名前の連想配列を作成する。
     * 
     * @return
     */
    public static LinkedHashMap<String, String> createLinkedHashMap() {
        return IdentifiableEnum.createLinkedHashMap(FileExtension.class);
    }
}
