package com.hirohiro716.gui;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineEvent.Type;

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
        this.clip.addLineListener(new LineListener() {
            
            @Override
            public void update(LineEvent event) {
                if (event.getType() == Type.OPEN) {
                    SoundPlayer.this.isOpen = true;
                }
            }
        });
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
    
    private boolean isOpen = false;
    
    /**
     * 音源を再生する。
     */
    public void play() {
        while (this.isOpen == false) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException exception) {
            }
        }
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
