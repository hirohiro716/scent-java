package com.hirohiro716.gui;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import com.hirohiro716.filesystem.File;

/**
 * 音源を再生するクラス。
 * 
 * @author hiro
 *
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
     * 再生する音源のバイト入力ストリームを指定する。
     * 
     * @param inputStream
     * @throws Exception
     */
    public SoundPlayer(InputStream inputStream) throws Exception {
        this();
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
        this.clip.flush();
        this.clip.setFramePosition(0);
    }
    
    @Override
    public void close() throws IOException {
        this.clip.close();
    }
}
