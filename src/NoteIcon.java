import javax.swing.*;
import java.awt.*;

class NoteIcon extends ImageIcon {
    private String noteName;
    private double time;

    NoteIcon(Image path, String noteName, double time ){
        super(path);
        this.noteName = noteName;
        this.time = time;
    }

    String getNoteName() {
        return noteName;
    }


    double getTime() {
        return time;
    }
}
