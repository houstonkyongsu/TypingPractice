import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;

public class TypingMain extends JPanel {

    private JFrame frame;
    private JPanel ui;
    private JTextField input;
    private JLabel errorLabel;
    private JTextArea textArea;
    private VocabGenerator vocabGenerator;
    private int errors = 0;
    final static Color ERROR_COL = Color.RED;
    final static Color HILIT_COL = Color.GREEN;
    private int wordIndex;
    private int charIndex;
    private int progress;
    private ArrayList<String> setText;

    final Highlighter hilit;
    final Highlighter.HighlightPainter painter;

    /*
    TypingMain constructor
     */
    public TypingMain(String path) {
        vocabGenerator = new VocabGenerator(path);
        setText = new ArrayList<>();
        wordIndex = 0;
        charIndex = 0;
        progress = 0;
        frame = new JFrame("Typing Practice");
        initialiseUI();
        hilit = new DefaultHighlighter();
        painter = new DefaultHighlighter.DefaultHighlightPainter(HILIT_COL);
        textArea.setHighlighter(hilit);
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
        textArea = new JTextArea();
        textArea.setColumns(50);
        textArea.setFont(new Font("Serif", Font.PLAIN, 20));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);

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
        ui.add(textArea);
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
    function to generate and display a random sentence
     */
    private void run() {
        input.setText("");
        errors = 0;
        wordIndex = 0;
        charIndex = 0;
        progress = 0;
        setText = vocabGenerator.generateSentence();
        StringBuilder temp = new StringBuilder();
        for (String s : setText) {
            temp.append(s).append(" ");
        }
        textArea.setText(temp.toString());
    }

    /*
    function to check the spelling of the word being input
     */
    private void checkSpelling(char c) {
        try {
            String current = setText.get(wordIndex);
            String typed = input.getText();
            int hindex = textArea.getText().indexOf(current);
            if (current.equals(typed) && c == ' ') { // word completed followed by space
                if (wordIndex == setText.size() - 1) { // last word in sentence completed
                    run();
                } else { // mid-sentence word completed
//                    input.setText("");
                    wordIndex++;
                    charIndex = 0;
                    progress = 0;
                }
            } else if (current.equals(typed)) { // character typed when should be space
                errors++;
                charIndex++;
                errorLabel.setText("Total errors: " + errors);
            } else { // word still being typed
                if (charIndex == progress && progress < current.length() - 1) {
                    if (c == current.charAt(progress)) { // when correct letter typed
                        hindex += progress;
                        hilit.addHighlight(hindex, hindex+1, painter);
                        charIndex++;
                        progress++;
                    } else { // wrong letter typed
                        errors++;
                        charIndex++;
                        errorLabel.setText("Total errors: " + errors);
                    }
                } else { // wrong letter already been typed
                    charIndex++;
                }
            }
        } catch (BadLocationException e) {
            System.out.println("BadLocationException caught: " + e.getMessage());
        }

    }

    /*
    main method accepts one command line argument
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TypingMain main = new TypingMain(args[0]);
                    main.run();
                }
            });
        } else {
            System.out.println("please provide path to dictionary file");
        }
    }


}
