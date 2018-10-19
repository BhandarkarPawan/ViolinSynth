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
    private static final int HEIGHT = 100;
    private static final int WIDTH_HALF = 25, WIDTH_ONE = 50, WIDTH_TWO = 75, WIDTH_FOUR = 100, WIDTH_FULL = 600;
    private JPanel root;
    private JLabel staff;
    private JPanel PanelHalf, PanelOne,PanelTwo,PanelFour;
    private JPanel MainScrollerPanel;
    private JButton playButton;
    private JPanel dynamicPanel;
    private JPanel selectedPanel;
    private JScrollPane selectedPane;
    private NoteLabel h,O,T,F;


    private NoteLabel[] notesPlay, notesHalf, notesOne, notesTwo, notesFour;
    private static String NOTE_IMAGE_PATH = "Notes Images/";
    private MusicForm(){
        makeGUI();
    }

    private HashMap<String, NoteIcon> noteMap = new HashMap<String, NoteIcon>();
    private HashMap<String, Integer> timeMap = new HashMap<>();
    private Set<String> set = new HashSet<>();



    private void makeGUI(){
        Dimension dim = new Dimension(WIDTH_FULL, HEIGHT);


        setSize(WIDTH_FULL+20,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        loadMap();

        notesHalf = new NoteLabel[11];
        notesOne = new NoteLabel[11];
        notesTwo = new NoteLabel[11];
        notesFour = new NoteLabel[11];

        initNotes(notesHalf, "h");
        initNotes(notesOne, "O");
        initNotes(notesTwo, "T");
        initNotes(notesFour, "F");


        noteMap.put("staffBaseS", getImage("staffBaseS", WIDTH_HALF));
        noteMap.put("staff", getImage("staff", WIDTH_FULL));

        setContentPane(root);
        staff.setLayout(new BoxLayout(staff, BoxLayout.LINE_AXIS));
        staff.setIcon(noteMap.get("staff"));

        notesPlay = new NoteLabel[24];
        for(int i =0; i< 24; i++) {
            notesPlay[i] = new NoteLabel();
            notesPlay[i].setIcon(noteMap.get("staffBase"));
            staff.add(notesPlay[i]);
            notesPlay[i].addMouseListener(new noDragMouseListener());
            notesPlay[i].setTransferHandler(new TransferHandler("icon"));
        }

        PanelHalf = new JPanel();
        PanelHalf.setLayout(new BoxLayout(PanelHalf, BoxLayout.LINE_AXIS));
        //anelHalf.setPreferredSize(new Dimension(WIDTH_FULL+20, HEIGHT));

        PanelOne = new JPanel();
        PanelOne.setLayout(new BoxLayout(PanelOne, BoxLayout.LINE_AXIS));
        //PanelOne.setPreferredSize(new Dimension(WIDTH_FULL+20, HEIGHT));

        PanelTwo = new JPanel();
        PanelTwo.setLayout(new BoxLayout(PanelTwo, BoxLayout.LINE_AXIS));
        //PanelTwo.setPreferredSize(new Dimension(WIDTH_FULL+20, HEIGHT));

        PanelFour = new JPanel();
        PanelFour.setLayout(new BoxLayout(PanelFour, BoxLayout.LINE_AXIS));
        //PanelFour.setPreferredSize(new Dimension(WIDTH_FULL+20, HEIGHT));



        for(int i =0;i<11;i++) {
            PanelHalf.add(notesHalf[i]);
            PanelOne.add(notesOne[i]);
            PanelTwo.add(notesTwo[i]);
            PanelFour.add(notesFour[i]);


        }

        h = new NoteLabel();
        O = new NoteLabel();
        T = new NoteLabel();
        F = new NoteLabel();

        h.setIcon(noteMap.get("h"));
        O.setIcon(noteMap.get("O"));
        T.setIcon(noteMap.get("T"));
        F.setIcon(noteMap.get("F"));

        h.addMouseListener(new buttonMouseListener());
        O.addMouseListener(new buttonMouseListener());
        T.addMouseListener(new buttonMouseListener());
        F.addMouseListener(new buttonMouseListener());

        dynamicPanel.add(h);
        dynamicPanel.add(O);
        dynamicPanel.add(T);
        dynamicPanel.add(F);

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                MusicForm form = new MusicForm();
            }
        });
    }

    private NoteIcon getImage(String imageName, int width){
        BufferedImage img  = null;
        try {
            img = ImageIO.read(new File(NOTE_IMAGE_PATH + imageName+".png"));

        } catch (IOException e) {
            System.out.println("ERROR ACCESSING" + NOTE_IMAGE_PATH + imageName+".png\"");
            e.printStackTrace();
        }
        assert img != null;
        return new NoteIcon(img.getScaledInstance(width, HEIGHT, Image.SCALE_SMOOTH), imageName, width);
    }


    class dragMouseListener implements MouseListener{

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
//            NoteLabel thisNote = (NoteLabel)e.getSource();
//            NoteIcon thisIcon = thisNote.getIcon();
//            String noteName = thisIcon.getNoteName();
//            System.out.println(noteName);
//            thisNote.setIcon(noteMap.get(noteName + "S"));

        }

        @Override
        public void mouseExited(MouseEvent e) {
//            NoteLabel thisNote = (NoteLabel)e.getSource();
//            NoteIcon thisIcon = thisNote.getIcon();
//            String noteName = thisIcon.getNoteName();
//            thisNote.setIcon(noteMap.get(noteName.substring(0, noteName.length() - 1)));
        }
    }

    class noDragMouseListener implements MouseListener{

        @Override
        public void mouseClicked(MouseEvent e) {
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
            NoteLabel thisNote = (NoteLabel)e.getSource();
            NoteIcon thisIcon = thisNote.getIcon();
            String noteName = thisIcon.getNoteName();
            System.out.println(noteName);
            thisNote.setIcon(noteMap.get(noteName + "S"));

        }

        @Override
        public void mouseExited(MouseEvent e) {
            NoteLabel thisNote = (NoteLabel)e.getSource();
            NoteIcon thisIcon = thisNote.getIcon();
            String noteName = thisIcon.getNoteName();
            thisNote.setIcon(noteMap.get(noteName.substring(0, noteName.length() - 1)));
        }
    }

    class buttonMouseListener implements MouseListener{

        @Override
        public void mouseClicked(MouseEvent e) {
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
                default:
                    System.out.println("Default: " + noteName);
            }
            setSize(WIDTH_FULL+20, 420);
            setResizable(false);
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            NoteLabel thisNote = (NoteLabel)e.getSource();
            NoteIcon thisIcon = thisNote.getIcon();
            String noteName = thisIcon.getNoteName();
            System.out.println(noteName);
            thisNote.setIcon(noteMap.get(noteName + "S"));
        }

        @Override
        public void mouseExited(MouseEvent e) {

            NoteLabel thisNote = (NoteLabel)e.getSource();
            NoteIcon thisIcon = thisNote.getIcon();
            String noteName = thisIcon.getNoteName();
            thisNote.setIcon(noteMap.get(noteName.substring(0, noteName.length() - 1)));

        }
    }

    private void loadMap(){

        noteMap.put("staffBase", getImage("staffBase", WIDTH_HALF));

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
                 System.out.println(note);
                 System.out.println(noteS);
                 if(set.contains(c)){
                     note+="2";
                     noteMap.put(note, getImage(note, timeMap.get(t)));
                     note+="S";
                     noteMap.put(note, getImage(note, timeMap.get(t)));
                     System.out.println(note);
                     System.out.println(noteS);

                 }
             }
         }
    }


    public void initNotes(NoteLabel[] notes, String t){
        String[] CHAR = {"A"+t,"B"+t,"C"+t,"D"+t,"E"+t,"F"+t,"G"+t,"D"+t+"2","E"+t+"2","F"+t+"2","G"+t+"2"};
        for(int i = 0; i < 11;i++){
            notes[i] = new NoteLabel();
            notes[i].setIcon(noteMap.get(CHAR[i]));
            notes[i].addMouseListener(new dragMouseListener());
            notes[i].setTransferHandler(new TransferHandler("icon"));
        }
    }
}
