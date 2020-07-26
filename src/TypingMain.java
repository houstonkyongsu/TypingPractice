import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;

public class TypingMain extends JPanel {

    private JFrame frame;
    private JPanel ui;
    private JTextField input;
    private JLabel errorLabel;
    private JTextPane textPane;
    private VocabGenerator vocabGenerator;
    private int errors = 0;
    final static Color ERROR_COL = Color.RED;
    final static Color HILIT_COL = Color.GREEN;
    private int wordIndex;
    private int charIndex;
    private int progress;
    private int colIndex;
    private ArrayList<String> setText;

    /*
    TypingMain constructor with no arguments
     */
    public TypingMain() {
        vocabGenerator = new VocabGenerator("dict.csv");
        initFrame();
    }

    /*
    TypingMain constructor with 1 argument
     */
    public TypingMain(String path) {
        vocabGenerator = new VocabGenerator(path);
        initFrame();
    }

    /*
    function to initialise the frame (and other variables)
     */
    public void initFrame() {
        setText = new ArrayList<>();
        wordIndex = 0;
        charIndex = 0;
        progress = 0;
        colIndex = 0;
        frame = new JFrame("Typing Practice");
        initialiseUI();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(ui);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /*
    method to initialize and build the GUI
     */
    private void initialiseUI() {
        ui = new JPanel();
        ui.setBorder(new EmptyBorder(5, 5, 5, 5));
        ui.setLayout(new BoxLayout(ui, BoxLayout.PAGE_AXIS));
        ui.setPreferredSize(new Dimension(900, 300));
        errorLabel = new JLabel("Total errors: " + errors);
        textPane = new JTextPane();
        textPane.setFont(new Font("Serif", Font.PLAIN, 30));
        textPane.setEditable(false);

        JPanel stats = new JPanel();
        stats.setLayout(new BoxLayout(stats, BoxLayout.LINE_AXIS));
        stats.add(errorLabel);
        input = new JTextField();
        input.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                determineAction(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                if (progress == 0 && charIndex == 0) {
                    input.setText("");
                }
            }
        });
        // add all the panels to the main ui panel
        ui.add(stats);
        ui.add(textPane);
        ui.add(input);

    }

    /*
    function to determine what type of KeyEvent occurred
     */
    private void determineAction(KeyEvent e) {
        if (e.getKeyChar() == '\b' && charIndex > 0) {
            charIndex--;
        } else if (e.getID() == KeyEvent.KEY_TYPED) {
            char c = e.getKeyChar();
            checkSpelling(c);
        }
    }

    /*
    function to get the randomly generated sentence and display it in the JTextPane
     */
    private void run() {
        // reset all the counters
        input.setText("");
        errors = 0;
        wordIndex = 0;
        charIndex = 0;
        progress = 0;
        colIndex = 0;
        // get array of random words, change the value of max to change the number of words added
        setText = vocabGenerator.generateSentence(10);
        StringBuilder temp = new StringBuilder();
        for (String s : setText) { // build the sentence
            temp.append(s).append(" ");
        }
        // display the sentence
        textPane.setText(temp.toString());
        // next 4 lines from https://stackoverflow.com/questions/3213045/centering-text-in-a-jtextarea-or-jtextpane-horizontal-text-alignment
        // to centre align the text in the JTextPane
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
    }

    /*
    function to check the spelling of the word being input, and update the colour of the words
    in the JTextPane accordingly
     */
    private void checkSpelling(char c) {
        try {
            StyledDocument doc = textPane.getStyledDocument();
            Style style = textPane.addStyle("style 1", null);
            String current = setText.get(wordIndex);
            String typed = input.getText();
            int hindex = textPane.getText().indexOf(current);

            if (current.equals(typed) && c == ' ') { // word completed followed by space
                if (wordIndex == setText.size() - 1) { // last word in sentence completed
                    run();
                } else { // mid-sentence word completed
                    wordIndex++;
                    charIndex = 0;
                    progress = 0;
                    colIndex = 0;
                }
            } else if (current.equals(typed)) { // character typed when should be space
                errors++;
                charIndex++;
                errorLabel.setText("Total errors: " + errors);
            } else { // word still being typed
                if (charIndex == progress && progress < current.length()) {
                    if (c == current.charAt(progress)) { // when correct letter typed
                        if (colIndex == progress) { // if current letter has not yet been coloured
                            hindex += progress;
                            StyleConstants.setForeground(style, HILIT_COL);
                            doc.remove(hindex, 1);
                            doc.insertString(hindex, Character.toString(current.charAt(progress)), style);
                            colIndex++;
                        }
                        charIndex++;
                        progress++;
                    } else { // wrong letter typed
                        hindex += progress;
                        errors++;
                        charIndex++;
                        if (colIndex == progress) { // if current letter has not yet been coloured
                            StyleConstants.setForeground(style, ERROR_COL);
                            doc.remove(hindex, 1);
                            doc.insertString(hindex, Character.toString(current.charAt(progress)), style);
                            colIndex++;
                        }
                        errorLabel.setText("Total errors: " + errors);
                    }
                } else { // wrong letter already been typed
                    charIndex++;
                }
            }
        } catch (Exception e) {
            System.out.println("Exception caught: " + e.getMessage());
        }

    }

    /*
    main method accepts one command line argument
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TypingMain main = new TypingMain();
                    main.run();
                }
            });
        } else if (args.length == 1) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TypingMain main = new TypingMain(args[0]);
                    main.run();
                }
            });
        } else {
            System.out.println("please provide path to dictionary file as an argument, or move dictionary file to default location");
        }
    }


}
