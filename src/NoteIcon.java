import javax.swing.*;
import java.awt.*;

public class NoteIcon extends ImageIcon {
    private String noteName;
    int width;

    NoteIcon(Image path, String noteName, int width){
        super(path);
        this.noteName = noteName;
        this.width = width;

    }

    public String getNoteName() {
        return noteName;
    }

    public int getWidth() {
        return width;
    }
}
