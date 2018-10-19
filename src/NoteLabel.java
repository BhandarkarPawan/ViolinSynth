import javax.swing.*;

public class NoteLabel extends JLabel {

    NoteIcon icon;

    public void setIcon(NoteIcon icon) {
        super.setIcon(icon);
        this.icon = icon;
    }

    public NoteIcon getIcon() {
        return icon;
    }
}
