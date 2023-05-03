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
            //System.out.println(volume - 100);

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));

            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            FloatControl gainControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue((volume  - 100) * 0.5f);
            clip.start();
        } catch (Exception ex){
            //do exception handling here
        }

    }

    public void playSound_music (float volume) {
        String filePath = "src/res/d_e1m1.mid";

        try {
            AudioInputStream audioInputStream =
                    AudioSystem.getAudioInputStream(new File(filePath));

            Clip clip = AudioSystem.getClip();
            //System.out.println(clip);
            clip.open(audioInputStream);

            System.out.println(clip);
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue((volume  - 100) * 0.6f);


            clip.loop(100);
        } catch (Exception ex){
            //do exception handling here
        }
    }
}
