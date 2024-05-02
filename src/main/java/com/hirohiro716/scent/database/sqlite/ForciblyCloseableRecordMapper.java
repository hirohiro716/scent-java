package com.hirohiro716.scent.database.sqlite;

import java.io.IOException;

/**
 * マップしようとしているレコードの編集中を強制的に解除できる機能を持つインターフェース。
 * 
 * @author hiro
 *
 */
public interface ForciblyCloseableRecordMapper {

    /**
     * マップしようとしているレコードの編集中を強制的に解除するメソッド。
     * 
     * @throws IOException
     */
    public abstract void forciblyClose() throws IOException;
}
