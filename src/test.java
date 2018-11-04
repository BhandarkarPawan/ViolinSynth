public class test {


    public static void main(String[] args)
    {
        try
        {
            AudioPlayer audioPlayer;

            String t= "T";
            String[] CHAR = {"D"+t,"E"+t,"F"+t,"G"+t,"A"+t, "B"+t,"C"+t,"D"+t+"2","E"+t+"2","F"+t+"2","G"+t+"2", "A"+t+"2"};


            for(int i = 0; i < CHAR.length;i++){

                System.out.println(CHAR[i]);
                audioPlayer =
                        new AudioPlayer("Notes Audio/"+ CHAR[i] + ".wav");
                audioPlayer.play();
                while (audioPlayer.status == "play");
            }

        }
        catch (Exception ex)
        {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();

        }
    }


}
