import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer
{

    // to store current position
    Clip clip;

    // current status of clip
    static boolean playing = false;

    AudioInputStream audioInputStream;

    // constructor to initialize streams and clip
    public AudioPlayer(String filePath)
            throws UnsupportedAudioFileException,
            IOException, LineUnavailableException
    {
        // create AudioInputStream object
        audioInputStream =
                AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());

        // create clip reference
        clip = AudioSystem.getClip();

        // open audioInputStream to the clip
        clip.open(audioInputStream);
    }


    // Method to play the audio
    public void play()
    {
        //start the clip
        clip.start();
        playing = true;
        clip.addLineListener(new LineListener() {
            @Override
            public void update(LineEvent event) {
                if (event.getType()== LineEvent.Type.STOP) {
                    playing = false;
                    return;
                }
            }
        });
    }

    public static boolean isPlaying() {
        return playing;
    }
}