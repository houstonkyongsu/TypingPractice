import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class VocabGenerator {

    private ArrayList<String> temp;
    private ArrayList<String> dict;

    public VocabGenerator(String path) {
        dict = readDict(path);
        temp = new ArrayList<>();
    }

    /*
    function to read in a dictionary csv file and return a list of all the words in it
     */
    public ArrayList<String> readDict(String fileName) {
        ArrayList<String> result = new ArrayList<>();
        try {
            File file = new File(fileName);
            Scanner input = new Scanner(file);
            // for each line in the file
            while (input.hasNext()) {
                // split line by commas into string array
                String[] values = input.next().split(",");
                // add each word to the result list
                result.addAll(Arrays.asList(values));
            }
            input.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found exception caught");
            e.printStackTrace();
        }
        return result;
    }

    /*
    function to build a string using randomly selected words from the provided list
    with no repeated words
     */
    public ArrayList<String> generateSentence(int max) {
        ArrayList<String> res = new ArrayList<>();
        temp.clear();
        temp.addAll(dict);
        Random random = new Random();
        while (res.size() < max) { // add 'max' words to the list
            int i = random.nextInt(temp.size());
            res.add(temp.get(i));
            temp.remove(i);
        }

        return res;
    }
}
