import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MusicForm extends JFrame{

    //Height of the staff
    private static final int HEIGHT = 100;

    //Width that the note takes up on each note
    private static final int
            WIDTH_HALF = 25,
            WIDTH_ONE = 50,
            WIDTH_TWO = 75,
            WIDTH_FOUR = 100,
            WIDTH_FULL = 600;

    //GUI Components
    private JPanel root;
    private JLabel staff;


    //These are the panels that hold all the notes for a particular time signature
    private JPanel
            PanelHalf,
            PanelOne,
            PanelTwo,
            PanelFour;

    private JButton playButton;
    private JPanel dynamicPanel;
    private JPanel selectedPanel;

    //This map will hold the NoteIcon objects for each note
    private HashMap<String, NoteIcon> noteMap = new HashMap<>();

    //This map holds the time value for each time character
    private HashMap<String, Integer> timeMap = new HashMap<>();
    private Set<String> set = new HashSet<>();

    private MusicForm(){
        makeGUI();
    }





    private void makeGUI(){

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //Load all the Icons and Sound Files required for the program to run
        loadMap();

        //These arrays will hold the Icons for each note., corresponding to each time signature
        NoteLabel[] notesHalf = new NoteLabel[11];
        NoteLabel[] notesOne = new NoteLabel[11];
        NoteLabel[] notesTwo = new NoteLabel[11];
        NoteLabel[] notesFour = new NoteLabel[11];

        //Load the Icons into the arrays declared above
        initNotes(notesHalf, "h");
        initNotes(notesOne, "O");
        initNotes(notesTwo, "T");
        initNotes(notesFour, "F");

        setContentPane(root);
        staff.setLayout(new BoxLayout(staff, BoxLayout.LINE_AXIS));
        staff.setIcon(noteMap.get("staff"));

        //This is the main Staff that holds all the notes to be played
        NoteLabel[] notesPlay = new NoteLabel[24];

        //Initialize the array to hold a maximum of 24 quavers (quarter note)
        for(int i =0; i< 24; i++) {
            notesPlay[i] = new NoteLabel();
            notesPlay[i].setIcon(noteMap.get("staffBase"));
            staff.add(notesPlay[i]);
            notesPlay[i].addMouseListener(new noDragMouseListener());
            notesPlay[i].setTransferHandler(new TransferHandler("icon"));
        }

        PanelHalf = new JPanel();
        PanelHalf.setLayout(new BoxLayout(PanelHalf, BoxLayout.LINE_AXIS));

        PanelOne = new JPanel();
        PanelOne.setLayout(new BoxLayout(PanelOne, BoxLayout.LINE_AXIS));

        PanelTwo = new JPanel();
        PanelTwo.setLayout(new BoxLayout(PanelTwo, BoxLayout.LINE_AXIS));

        PanelFour = new JPanel();
        PanelFour.setLayout(new BoxLayout(PanelFour, BoxLayout.LINE_AXIS));

        //TODO: Fill in this function
        playButton.addActionListener(e -> {
            //Play music
        });
        for(int i =0;i<11;i++) {
            PanelHalf.add(notesHalf[i]);
            PanelOne.add(notesOne[i]);
            PanelTwo.add(notesTwo[i]);
            PanelFour.add(notesFour[i]);
        }

        //These are the Icons that serve as buttons to load the corresponding lists
        NoteLabel h = new NoteLabel();
        h.setIcon(noteMap.get("h"));
        h.addMouseListener(new buttonMouseListener());

        NoteLabel o = new NoteLabel();
        o.setIcon(noteMap.get("O"));
        o.addMouseListener(new buttonMouseListener());

        NoteLabel t = new NoteLabel();
        t.setIcon(noteMap.get("T"));
        t.addMouseListener(new buttonMouseListener());

        NoteLabel f = new NoteLabel();
        f.setIcon(noteMap.get("F"));
        f.addMouseListener(new buttonMouseListener());

        //Dynamic Panel is the one which holds all four buttons
        dynamicPanel.add(h);
        dynamicPanel.add(o);
        dynamicPanel.add(t);
        dynamicPanel.add(f);

        //Set Window Size to whatever is required to house all the components
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MusicForm::new);
    }

    /**
     * This function takes in two arguments:
     * imageName : name of the image file
     * width : width that the file is expected to occupy on the
     *
     * It returns a NoteIcon for the corresponding image file that is scaled
     * to the size of the NoteLabel that contains it.
     */
    private NoteIcon getImage(String imageName, int width){
        BufferedImage img  = null;
        String NOTE_IMAGE_PATH = "Notes Images/";
        try {
            img = ImageIO.read(new File(NOTE_IMAGE_PATH + imageName+".png"));

        } catch (IOException e) {
            System.out.println("ERROR ACCESSING" + NOTE_IMAGE_PATH + imageName+".png\"");
            e.printStackTrace();
        }
        assert img != null;
        return new NoteIcon(img.getScaledInstance(width, HEIGHT, Image.SCALE_SMOOTH), imageName, width);
    }




    class noDragMouseListener implements MouseListener{

        @Override
        public void mouseClicked(MouseEvent e) {
            //Remove the current note and replace it with the default
            NoteLabel thisNote = (NoteLabel)e.getSource();
            thisNote.setIcon(noteMap.get("staffBaseS"));
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {
            //Highlight on mouseover
            NoteLabel thisNote = (NoteLabel)e.getSource();
            NoteIcon thisIcon = thisNote.getIcon();
            String noteName = thisIcon.getNoteName();
            thisNote.setIcon(noteMap.get(noteName + "S"));

        }

        @Override
        public void mouseExited(MouseEvent e) {
            //un-highlight on mouse exit
            NoteLabel thisNote = (NoteLabel)e.getSource();
            NoteIcon thisIcon = thisNote.getIcon();
            String noteName = thisIcon.getNoteName();
            thisNote.setIcon(noteMap.get(noteName.substring(0, noteName.length() - 1)));
        }
    }

    class buttonMouseListener extends noDragMouseListener{

        @Override
        public void mouseClicked(MouseEvent e) {
            //Upon selection, load the corresponding panel of notes
            selectedPanel.removeAll();
            selectedPanel.revalidate();
            selectedPanel.repaint();

            NoteLabel thisNote = (NoteLabel)e.getSource();
            NoteIcon thisIcon = thisNote.getIcon();
            String noteName = thisIcon.getNoteName();
            switch (noteName){
                case "hS":
                    selectedPanel.add(PanelHalf);
                    break;
                case "OS":
                    selectedPanel.add(PanelOne);
                break;
                case "TS":
                    selectedPanel.add(PanelTwo);
                    break;
                case "FS":
                    selectedPanel.add(PanelFour);
                    break;
            }
            setSize(WIDTH_FULL+20, 420);
            setResizable(false);
        }
    }

    /**
     * This function loads all the data required for the program to work.
     */
    private void loadMap(){
        noteMap.put("staffBase", getImage("staffBase", WIDTH_HALF));
        noteMap.put("staffBaseS", getImage("staffBaseS", WIDTH_HALF));
        noteMap.put("staff", getImage("staff", 600));

        timeMap.put("h", WIDTH_HALF);
        timeMap.put("O", WIDTH_ONE);
        timeMap.put("T", WIDTH_TWO);
        timeMap.put("F", WIDTH_FOUR);

        set.add("D");
        set.add("E");
        set.add("F");
        set.add("G");

        String[] CHAR = {"A","B","C","D","E","F","G"};
         String[] TIME = {"h", "O", "T", "F"};
         for(String c: CHAR){
             for (String t: TIME){
                 noteMap.put(t, getImage(t, timeMap.get("F")));
                 noteMap.put(t+"S", getImage(t+"S", timeMap.get("F")));
                 String note = c+t;
                 String noteS = note+"S";
                 noteMap.put(note, getImage(note, timeMap.get(t)));
                 noteMap.put(noteS, getImage(noteS, timeMap.get(t)));
                 if(set.contains(c)){
                     note+="2";
                     noteMap.put(note, getImage(note, timeMap.get(t)));
                     note+="S";
                     noteMap.put(note, getImage(note, timeMap.get(t)));
                 }
             }
         }
    }


    private void initNotes(NoteLabel[] notes, String t){
        String[] CHAR = {"A"+t, "B"+t,"C"+t,"D"+t,"E"+t,"F"+t,
                "G"+t,"D"+t+"2","E"+t+"2","F"+t+"2","G"+t+"2"};

        for(int i = 0; i < 11;i++){
            notes[i] = new NoteLabel();
            notes[i].setIcon(noteMap.get(CHAR[i]));
            notes[i].addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {

                    NoteLabel jc = (NoteLabel)e.getSource();
                    TransferHandler th = jc.getTransferHandler();
                    th.exportAsDrag(jc, e, TransferHandler.COPY);
                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }

            });
            notes[i].setTransferHandler(new TransferHandler("icon"));
        }
    }
}
