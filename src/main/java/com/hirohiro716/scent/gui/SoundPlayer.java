package com.hirohiro716.scent.gui;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import com.hirohiro716.scent.filesystem.File;

/**
 * 音源を再生するクラス。
 */
public class SoundPlayer implements Closeable {
    
    /**
     * コンストラクタ。
     * 
     * @throws Exception
     */
    private SoundPlayer() throws Exception {
        this.clip = AudioSystem.getClip();
    }
    
    /**
     * コンストラクタ。<br>
     * 再生する音源のバイト配列を指定する。
     * 
     * @param bytes 
     * @throws Exception
     */
    public SoundPlayer(byte[] bytes) throws Exception {
        this();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
        this.clip.open(audioInputStream);
    }
    
    /**
     * コンストラクタ。<br>
     * 再生する音源ファイルを指定する。
     * 
     * @param file
     * @throws Exception
     */
    public SoundPlayer(File file) throws Exception {
        this();
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file.toJavaIoFile());
        this.clip.open(audioInputStream);
    }
    
    private Clip clip;
    
    /**
     * 音源を再生する。
     */
    public void play() {
        this.clip.start();
    }
    
    /**
     * 音源を一時停止する。
     */
    public void pause() {
        this.clip.stop();
    }
    
    /**
     * 音源を停止する。
     */
    public void stop() {
        this.clip.stop();
        this.clip.setFramePosition(0);
    }
    
    @Override
    public void close() throws IOException {
        this.clip.close();
    }
}
