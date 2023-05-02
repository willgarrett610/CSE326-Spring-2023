package game.settings;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class SoundControl {

    public SoundControl() {

    }
    public void playSound_shoot (float volume) {
        try {
            String filePath = "src/res/dspistol.wav";
            System.out.println(volume - 100);

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
            AudioFormat audioFormat = audioInputStream.getFormat();

            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            FloatControl gainControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue((volume  - 100) * 0.5f); // Reduce volume by 10 decibels.
            clip.start();

            /* DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

            // Check if the audio line is supported by the system
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Audio line not supported");
                return;
            }

            SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);
            audioLine.open(audioFormat);

            // Start playing the audio file
            audioLine.start();

            byte[] buffer = new byte[4096];
            int bytesRead = 0;
            while ((bytesRead = audioInputStream.read(buffer)) != -1) {
                audioLine.write(buffer, 0, bytesRead);
            }*/

            // Wait for the audio to finish playing
            //audioLine.drain();

            // Close the audio line and the audio input stream
            //audioLine.close();
            //audioInputStream.close();
        } catch (Exception ex){
            //do exception handling here
        }

    }

    public void closeStream (SourceDataLine audioLine, AudioInputStream audioInputStream){
        // Close the audio line and the audio input stream
        audioLine.close();
        try {
            audioInputStream.close();
        } catch (Exception ex) {

        }
    }
}
