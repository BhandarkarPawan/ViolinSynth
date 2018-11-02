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

    //holds the number of staff currently in use
    private int staffCount = 0;
    private ArrayList<Double> NotesPerStaff;
    private NoteLabel bufferLabel;
    //Height of the staff
    private static final int HEIGHT = 100;

    private boolean paused = false;
    private boolean finished = true;


    //Width that the note takes up on each note
    private static final int
            WIDTH_HALF = 25,
            WIDTH_ONE = 50,
            WIDTH_TWO = 75,
            WIDTH_FOUR = 100,
            WIDTH_FULL = 600;

    //GUI Components
    private JPanel root;
    private ArrayList<JLabel> staff;
    private double noteCount;
    private ArrayList<NoteLabel> notesPlay;

    private ArrayList<Integer> staffWidth;

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
    private JScrollPane selectedScroll;
    private JPanel buttonPanel;

    //This map will hold the NoteIcon objects for each note
    private HashMap<String, NoteIcon> noteMap = new HashMap<>();

    //This map holds the time value for each time character
    private HashMap<String, Integer> timeCharMap = new HashMap<>();
    private HashMap<Double, Integer> timeIntMap = new HashMap<>();
    private Set<String> set = new HashSet<>();

    private int timerI = 0;

    private void makeGUI(){

        NotesPerStaff = new ArrayList<>();

        //Load all the Icons and Sound Files required for the program to run
        loadMap();

        // Main array that holds the staff GUI
        staff = new ArrayList<>();
        staffWidth = new ArrayList<>();

        // Main panel that holds all the staff (Scrollable)
        staffPanel.setLayout(new BoxLayout(staffPanel, BoxLayout.Y_AXIS));
        staffPanel.setLayout(new BoxLayout(staffPanel, BoxLayout.Y_AXIS));

        notesPlay = new ArrayList<>();

        addStaff();
        addStaff();
        addStaff();


        NoteLabel[] notesHalf = new NoteLabel[11];
        initNotes(notesHalf, "h");

        NoteLabel[] notesOne = new NoteLabel[11];
        initNotes(notesOne, "O");

        NoteLabel[] notesTwo = new NoteLabel[11];
        initNotes(notesTwo, "T");

        NoteLabel[] notesFour = new NoteLabel[11];
        initNotes(notesFour, "F");

        setContentPane(root);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // These panels will hold 11 notes each, depending on the time

        PanelHalf = new JPanel();
        PanelHalf.setLayout(new BoxLayout(PanelHalf, BoxLayout.LINE_AXIS));

        PanelOne = new JPanel();
        PanelOne.setLayout(new BoxLayout(PanelOne, BoxLayout.LINE_AXIS));

        PanelTwo = new JPanel();
        PanelTwo.setLayout(new BoxLayout(PanelTwo, BoxLayout.LINE_AXIS));

        PanelFour = new JPanel();
        PanelFour.setLayout(new BoxLayout(PanelFour, BoxLayout.LINE_AXIS));

        // Initialize all the panels with notes for later use
        for(int i =0;i<11;i++) {
            PanelHalf.add(notesHalf[i]);
            PanelOne.add(notesOne[i]);
            PanelTwo.add(notesTwo[i]);
            PanelFour.add(notesFour[i]);
        }

        //selectedPanel.add(PanelHalf);



        playButton.addActionListener(e -> {
            String command = playButton.getText();
            if (command.equals("Pause")){
                playButton.setText("Play");
                paused = true;
            }
            else {
                playButton.setText("Pause");
                if(!finished){
                    paused = false;
                }
                 else {
                    timerI = 0;
                    for (int i = 0; i < (staffCount) * 24; i++) {
                        int staffNumber = i / 24;
                        int noteNumber = i % 24;
                        NoteLabel thisNote = (NoteLabel) staff.get(staffNumber).getComponent(noteNumber);
                        thisNote.removeMouseListener(thisNote.getMouseListeners()[0]);
                    }

                    System.out.println("Entered onAction");
                    Timer t = new Timer(250, e1 -> {
                        finished = false;
                        //TODO: Only iterate till the last legit index
                        if (timerI < (staffCount) * 24) {
                            if(!paused) {
                                int staffNumber = timerI / 24;
                                int noteNumber = timerI % 24;
                                NoteLabel thisNote = (NoteLabel) staff.get(staffNumber).getComponent(noteNumber);
                                NoteIcon thisIcon = thisNote.getIcon();
                                String noteName = thisIcon.getNoteName();
                                thisNote.setIcon(noteMap.get(noteName + "S"));
                                //                    Timer t1 = (Timer)e1.getSource();
                                //                    System.out.print((int)(thisNote.getIcon().getTime()*1000)+ " ");
                                //                    t1.setDelay((int)(thisNote.getIcon().getTime()*1000));
                                timerI++;
                                System.out.print(timerI + " ");
                            }
                        } else {
                            finished = true;
                            playButton.setText("Play");
                            Timer t1 = (Timer) e1.getSource();
                            for (int i = 0; i < (staffCount) * 24; i++) {
                                int staffNumber = i / 24;
                                int noteNumber = i % 24;
                                NoteLabel thisNote = (NoteLabel) staff.get(staffNumber).getComponent(noteNumber);
                                thisNote.addMouseListener(new noDragMouseListener());
                                NoteIcon thisIcon = thisNote.getIcon();
                                String noteName = thisIcon.getNoteName();
                                if (noteName.endsWith("S"))
                                    thisNote.setIcon(noteMap.get(noteName.substring(0, noteName.length() - 1)));
                            }

                            t1.stop();
                        }

                    });
                    t.start();
                }
            }
            //setPreferredSize(new Dimension(600,600));

        });


        addButton.addActionListener(e -> {
            /***
             * Every time the add button is clicked, add a new staff row
             * to the ScrollPanel and also to the list of staff. We increment
             * staffCount in order to keep track of how many staff are are
             * currently added. The then we repeat the same procedure as we
             * did for the first staff row.
             */

            addStaff();

            //Refresh panel to make the changes visible
            staffPanel.repaint();
            staffPanel.revalidate();

            staffWidth.add(0);

            System.out.println("Number of rows: " + staffCount);
        });


        //These are the Icons that serve as buttons to load the corresponding lists
        NoteLabel h = new NoteLabel();
        h.setIcon(noteMap.get("h"));
        h.addMouseListener(new buttonMouseListener());
        dynamicPanel.add(h);

        NoteLabel o = new NoteLabel();
        o.setIcon(noteMap.get("O"));
        o.addMouseListener(new buttonMouseListener());
        dynamicPanel.add(o);

        NoteLabel t = new NoteLabel();
        t.setIcon(noteMap.get("T"));
        t.addMouseListener(new buttonMouseListener());
        dynamicPanel.add(t);

        NoteLabel f = new NoteLabel();
        f.setIcon(noteMap.get("F"));
        f.addMouseListener(new buttonMouseListener());
        dynamicPanel.add(f);

        // Dynamic Panel is the one which holds all four buttons



        // Display the window
        pack();
        setResizable(false);
        setVisible(true);

    }

    private void addStaff(){
        NotesPerStaff.add(0.0);

        // Add a new staff row to the list
        staff.add(new JLabel());
        staff.get(staffCount).
                setLayout(new BoxLayout(staff.get(staffCount), BoxLayout.LINE_AXIS));
        staff.get(staffCount).setIcon(noteMap.get("staff"));

        // Add the new staff row to the GUI
        staffPanel.add(staff.get(staffCount));
        staff.get(staffCount).setVisible(true);
        // Add note containers for the new row
        for(int i =0; i< 24; i++) {
            NoteLabel addedNote = new NoteLabel();
            notesPlay.add(addedNote);
            addedNote.setIcon(noteMap.get("staffBase"));
            staff.get(staffCount).add(addedNote);
            // Add listeners for user-interaction
            addedNote.addMouseListener(new noDragMouseListener());
            addedNote.setTransferHandler(new myHandler("icon"));
        }

        staffCount++;


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

        /**
         * This is used for staff where dragging isn't allowed but clicking is.
         * We use this instead of the ActionListener for the highlight on mouseover
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            //Remove the current note and replace it with the default
            NoteLabel thisNote = (NoteLabel)e.getSource();

            int targetIndex = notesPlay.indexOf(thisNote);
            Double targetCount = thisNote.getIcon().getTime();

            int staffNum = targetIndex/24;
            Double targetStaffCount = NotesPerStaff.get(staffNum);
            targetStaffCount-= targetCount;
            NotesPerStaff.set(staffNum, targetStaffCount);

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
        /**
         * This one is used for the 4 large notes which will serve as buttons.
         * Similar to the staff notes, we do not want these t be draggable.
         * Along with that, we want to load rhe corresponding notes-Panel when
         * the button is clicked. Hence we override the mouseClicked() method.
         */

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
                    selectedPanel.setPreferredSize(new Dimension(275,100));
                    break;
                case "OS":
                    selectedPanel.add(PanelOne);
                    selectedPanel.setPreferredSize(new Dimension(550,100));

                    break;
                case "TS":
                    selectedPanel.add(PanelTwo);
                    selectedPanel.setPreferredSize(new Dimension(825,100));

                    break;
                case "FS":
                    selectedPanel.add(PanelFour);
                    selectedPanel.setPreferredSize(new Dimension(1100,100));

                    break;
            }
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

        Set<String> set = new HashSet<>();

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
        /***
         * This function will initialize notes[] array with the corresponding
         * note icons. These are the ones which will later be displayed in the
         * selected panel when the user clicks a particular note (button)
         */
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

        /**
         * This class takes care of the heavy-lifting when it comes to the
         * Actual drag and drop mechanism that governs the UI of the notes
         */

        myHandler(String property){
            super(property);
        }

        @Override
        public void exportAsDrag(JComponent comp, InputEvent e, int action) {
            bufferLabel = (NoteLabel)comp;


            super.exportAsDrag(comp, e, action);
        }

        @Override
        public boolean canImport(TransferSupport support) {

            NoteLabel target = (NoteLabel)support.getComponent();
            NoteLabel source = bufferLabel;

            double sourceCount = source.getIcon().getTime();
            double targetCount = target.getIcon().getTime();

            int targetIndex = notesPlay.indexOf(target);
            int staffNumber = targetIndex/24;

            System.out.println();

            for(int i = 0; i < staffCount; i++ ){
                System.out.print(NotesPerStaff.get(i) + " ");
            }

            System.out.println("Staff Number: " + staffNumber);
            Double targetStaffCount = NotesPerStaff.get(staffNumber);
            System.out.println("TargetStaffCount: " + targetStaffCount);
            System.out.println("SourceCount: " + sourceCount);
            System.out.println("TargetCount: " + targetCount);


            if(targetStaffCount + sourceCount - targetCount > 12){

                return false;

            }
            else{
                return true;
            }
        }

        @Override
        public boolean importData(TransferSupport support){
            NoteLabel target = (NoteLabel)support.getComponent();
            NoteLabel source = bufferLabel;
            double sourceCount = source.getIcon().getTime();
            double targetCount = target.getIcon().getTime();

            int targetIndex = notesPlay.indexOf(target);
            int staffNumber = targetIndex/24;
            Double targetStaffCount = NotesPerStaff.get(staffNumber);

            targetStaffCount = targetStaffCount + sourceCount - targetCount;
            NotesPerStaff.set(staffNumber, targetStaffCount);
                return super.importData(support);

        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {

            checkWidth();
            super.exportDone(source, data, action);

        }
    }

    private void checkWidth(){
        /**
         * This function checks the width to see if anything is off screen
         */
//        int lastIndex = -1;
//        for(int i= 0;i<24;i++){
//            System.out.print(notesPlay.get(i).getIcon().getTime()+ " " );
//            if(notesPlay.get(i).getIcon().getTime()!=0){
//                lastIndex = i;
//            }
//        }
//
//        System.out.println();
//
//        System.out.println("Last note index: " + lastIndex);
//
//        for(int i=0; i<=staffCount;i++) {
//            staffWidth.set(i, 0);
//            int newWidth = 0;
//            System.out.println("Notes up to last note are: ");
//            for (int j = i*24; j <= lastIndex; j++) {
//                System.out.print(notesPlay.get(j).getIcon().getTime() + ": "
//                        + timeIntMap.get(notesPlay.get(j).getIcon().getTime()) + "| ");
//                 newWidth += staffWidth.get(i) + timeIntMap.get(notesPlay.get(i).getIcon().getTime());
//            }
//            staffWidth.set(i, newWidth);
//            System.out.println();
//            System.out.println("Current Total Width: " + newWidth);
//            if (newWidth> WIDTH_FULL) {
//                compress();
//            }
//        }
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
                int staffNumber = i/24;
                int noteNumber = i%24;
               staff.get(staffNumber).remove(noteNumber);
               staff.get(staffNumber).add(notesPlay.get(i));
                notesPlay.add(notesPlay.remove(i));
            }
        }

        System.out.println("compressed notesPlay: ");
        for (NoteLabel aNotesPlay : notesPlay) {
            System.out.print(aNotesPlay.getIcon().getTime() + "  ");
        }

        for(int i =0; i< notesPlay.size(); i++) {
            staff.get(i).repaint();
            staff.get(i).revalidate();
        }
    }


    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeLater(MusicForm::new);
    }

    private MusicForm(){
        makeGUI();
    }

    //TODO: IMPLEMENT CHECKWIDTH and COMPRESSED


}
