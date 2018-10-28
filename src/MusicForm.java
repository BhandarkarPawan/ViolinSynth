import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private double noteCount = 0;
    private ArrayList<NoteLabel> notesPlay;


    //These are the panels that hold all the notes for a particular time signature
    private JPanel
            PanelHalf,
            PanelOne,
            PanelTwo,
            PanelFour;

    private JButton playButton;
    private JPanel dynamicPanel;
    private JPanel selectedPanel;
    private JButton addButton;
    private JScrollPane staffScroll;
    private JPanel staffPanel;

    //This map will hold the NoteIcon objects for each note
    private HashMap<String, NoteIcon> noteMap = new HashMap<>();

    //This map holds the time value for each time character
    private HashMap<String, Integer> timeCharMap = new HashMap<>();
    private HashMap<Double, Integer> timeIntMap = new HashMap<>();

    private Set<String> set = new HashSet<>();

    private MusicForm(){
        makeGUI();
    }
    private int timerI = 0;

    private void makeGUI(){
        staffPanel.setLayout(new BoxLayout(staffPanel, BoxLayout.Y_AXIS));

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
        notesPlay = new ArrayList<>();

        //Initialize the array to hold a maximum of 24 quavers (quarter note)
        for(int i =0; i< 24; i++) {
            NoteLabel addedNote = new NoteLabel();
            notesPlay.add(addedNote);
            addedNote.setIcon(noteMap.get("staffBase"));
            staff.add(addedNote);
            addedNote.addMouseListener(new noDragMouseListener());
            addedNote.setTransferHandler(new myHandler("icon"));
        }

        PanelHalf = new JPanel();
        PanelHalf.setLayout(new BoxLayout(PanelHalf, BoxLayout.LINE_AXIS));

        PanelOne = new JPanel();
        PanelOne.setLayout(new BoxLayout(PanelOne, BoxLayout.LINE_AXIS));

        PanelTwo = new JPanel();
        PanelTwo.setLayout(new BoxLayout(PanelTwo, BoxLayout.LINE_AXIS));

        PanelFour = new JPanel();
        PanelFour.setLayout(new BoxLayout(PanelFour, BoxLayout.LINE_AXIS));

        playButton.addActionListener(e -> {
            timerI = 0;

            System.out.println("Entered onAction");
            Timer t = new Timer(250, e1 -> {
                //TODO: Only iterate till the last legit index
                if (timerI < 24) {
                    NoteLabel thisNote = (NoteLabel)staff.getComponent(timerI);
                    NoteIcon thisIcon = thisNote.getIcon();
                    String noteName = thisIcon.getNoteName();
                    thisNote.setIcon(noteMap.get(noteName + "S"));
                    //thisNote.removeMouseListener(thisNote.getMouseListeners()[0]);
//                    Timer t1 = (Timer)e1.getSource();
//                    System.out.print((int)(thisNote.getIcon().getTime()*1000)+ " ");
//                    t1.setDelay((int)(thisNote.getIcon().getTime()*1000));
                    timerI++;
                }

            });
            t.start();

        });


        addButton.addActionListener(e -> {

            JLabel newStaff = new JLabel();
            newStaff.setLayout(new BoxLayout(newStaff, BoxLayout.LINE_AXIS));
            newStaff.setIcon(noteMap.get("staff"));

            staffPanel.add(newStaff);
            newStaff.setVisible(true);

            for(int i =0; i< 24; i++) {
                NoteLabel addedNote = new NoteLabel();
                notesPlay.add(addedNote);
                addedNote.setIcon(noteMap.get("staffBase"));
                newStaff.add(addedNote);
                addedNote.addMouseListener(new noDragMouseListener());
                addedNote.setTransferHandler(new myHandler("icon"));
            }
            staffPanel.repaint();
            staffPanel.revalidate();
            System.out.println("DONE ");
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
        //setResizable(false);
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
        double time;

        switch (width){
            case WIDTH_HALF:
                time = 0.5;
                break;
            case WIDTH_ONE:
                time = 1;
                break;
            case WIDTH_TWO:
                time = 2;
                break;
            case WIDTH_FOUR:
                time = 4;
                break;
            case 0:
                width = WIDTH_HALF;
            default: time = 0;


        }

        return new NoteIcon(img.getScaledInstance(width, HEIGHT, Image.SCALE_SMOOTH), imageName, time);
    }


    class noDragMouseListener implements MouseListener{

        @Override
        public void mouseClicked(MouseEvent e) {
            //Remove the current note and replace it with the default
            NoteLabel thisNote = (NoteLabel)e.getSource();
            noteCount-=thisNote.getIcon().getTime();
            thisNote.setIcon(noteMap.get("staffBaseS"));
            checkWidth();
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
            if(!noteName.endsWith("S"))
                thisNote.setIcon(noteMap.get(noteName + "S"));

        }

        @Override
        public void mouseExited(MouseEvent e) {
            //un-highlight on mouse exit
            NoteLabel thisNote = (NoteLabel)e.getSource();
            NoteIcon thisIcon = thisNote.getIcon();
            String noteName = thisIcon.getNoteName();
            if(noteName.endsWith("S"))
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
            setSize(WIDTH_FULL+20, 435);
            //setResizable(false);
        }
    }

    /**
     * This function loads all the data required for the program to work.
     */
    private void loadMap(){
        noteMap.put("staffBase", getImage("staffBase", 0));
        noteMap.put("staffBaseS", getImage("staffBaseS", 0));
        noteMap.put("staff", getImage("staff", 600));

        timeCharMap.put("h", WIDTH_HALF);
        timeCharMap.put("O", WIDTH_ONE);
        timeCharMap.put("T", WIDTH_TWO);
        timeCharMap.put("F", WIDTH_FOUR);

        timeIntMap.put(0.5, WIDTH_HALF);
        timeIntMap.put(1., WIDTH_ONE);
        timeIntMap.put(2., WIDTH_TWO);
        timeIntMap.put(4., WIDTH_FOUR);
        timeIntMap.put(0., WIDTH_HALF);


        set.add("D");
        set.add("E");
        set.add("F");
        set.add("G");

        String[] CHAR = {"A","B","C","D","E","F","G"};
         String[] TIME = {"h", "O", "T", "F"};
         for(String c: CHAR){
             for (String t: TIME){
                 noteMap.put(t, getImage(t, WIDTH_FOUR));
                 noteMap.put(t+"S", getImage(t+"S", WIDTH_FOUR));
                 String note = c+t;
                 String noteS = note+"S";
                 noteMap.put(note, getImage(note, timeCharMap.get(t)));
                 noteMap.put(noteS, getImage(noteS, timeCharMap.get(t)));
                 if(set.contains(c)){
                     note+="2";
                     noteMap.put(note, getImage(note, timeCharMap.get(t)));
                     note+="S";
                     noteMap.put(note, getImage(note, timeCharMap.get(t)));
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
            notes[i].setTransferHandler(new myHandler("icon"));
        }
    }

    class myHandler extends TransferHandler{

        myHandler(String property){
            super(property);
        }

        @Override
        public void exportAsDrag(JComponent comp, InputEvent e, int action) {
            NoteLabel source = (NoteLabel)comp;
            double sourceCount = source.getIcon().getTime();
            if(noteCount+sourceCount>12) {
                JOptionPane.showMessageDialog(playButton, "You cannot have more than 12 beats in one row");
                System.out.println("Too much");
                return;
            }
            noteCount+=sourceCount;
            super.exportAsDrag(comp, e, action);
        }

        @Override
        public boolean importData(TransferSupport support){
            NoteLabel target = (NoteLabel)support.getComponent();
            double targetCount = target.getIcon().getTime();
            int targetIndex = notesPlay.indexOf(target);
            noteCount-=targetCount;
            System.out.println("Replaced: " + target.getIcon().getNoteName());
            System.out.println("Its index is: " + targetIndex);
            System.out.println("Sum of notes: " + noteCount);

            return super.importData(support);
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            checkWidth();
            super.exportDone(source, data, action);

        }
    }

    private void checkWidth(){
        int lastIndex = -1;
        for(int i= 0;i<24;i++){
            System.out.print(notesPlay.get(i).getIcon().getTime()+ " " );
            if(notesPlay.get(i).getIcon().getTime()!=0){
                lastIndex = i;
            }
        }

        System.out.println();

        System.out.println("Last note index: " + lastIndex);
        int totalWidth = 0;
        System.out.println("Notes up to last note are: ");
        for(int i= 0;i<=lastIndex;i++) {
            System.out.print(notesPlay.get(i).getIcon().getTime()+ ": " + timeIntMap.get(notesPlay.get(i).getIcon().getTime())+ "| ");
            totalWidth +=timeIntMap.get(notesPlay.get(i).getIcon().getTime());
        }
        System.out.println();
        System.out.println("Current Total Width: " + totalWidth);
        if(totalWidth >WIDTH_FULL){
            compress();
        }
    }


    /***
     * This function compresses the contents of the staff
     * This is done to ensure that no notes go off the screen
     */
    private void compress(){
        System.out.println("original notesPlay: ");
        for(int i = 0;i <notesPlay.size();i++){
            System.out.print(notesPlay.get(i).getIcon().getTime()+ "  ");
        }

        System.out.println("\ncompress() called ");
        for(int i =0; i< notesPlay.size(); i++){
            if(notesPlay.get(i).getIcon().getNoteName().equals("staffBase")){
               staff.remove(i);
               staff.add(notesPlay.get(i));
                notesPlay.add(notesPlay.remove(i));
            }
        }

        System.out.println("compressed notesPlay: ");
        for (NoteLabel aNotesPlay : notesPlay) {
            System.out.print(aNotesPlay.getIcon().getTime() + "  ");
        }

        staff.repaint();
        staff.revalidate();
    }
}
