/*program that is used to save the recording of the user*/

package Voice;

import java.io.File;
import java.io.FileInputStream;  
import sun.audio.AudioPlayer;  
import sun.audio.AudioStream;  

public class Sound {  
    public static void main(String args[]) {
        try {
                    FileInputStream fileau = new FileInputStream(
                    System.getProperty("user.dir") + "\\record.wav");
                    System.out.println(new File(System.getProperty("user.dir") + "\\record.wav").getName());
                    AudioStream as = new AudioStream(fileau);  
                    AudioPlayer.player.start(as);  

        } catch (Exception e) {
            e.printStackTrace();  
        }  
    }
}