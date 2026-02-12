import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author feng3302
 * @version 0.1.0
 * @Since 1/29/26
 **/
public class Jotto {
    private static final int WORD_SIZE = 5;
    private String currentWord;
    private int score;
    private ArrayList<String> playGuesses;
    private ArrayList<String> playWords;
    private String filename;
    private ArrayList<String> wordList;
    private static final boolean DEBUG = true;

    public Jotto(String filename) {
        this.filename = filename;
        readWords();
    }

    public boolean pickWord() {

    }

    public String showWordList() {
        StringBuilder sb = new StringBuilder();
        sb.append("Current word list:\n");
        for (String w : wordList) {
            sb.append(w).append("\n");
        }
        return sb.toString();
    }

    public ArrayList<String> showPlayerGuesses() {
        Scanner scan = new Scanner(System.in);

        if (playGuesses.isEmpty()) {
            System.out.println("No guesses yet");
        } else {
            System.out.println("Current player guesses:\n");
            for (String g : playGuesses) {
                System.out.println(g);
            }
        }

        System.out.println("Would you like to add the words to the word list? (y/n)");
        String input = scan.next().trim().toLowerCase();
        if (input.equals("y")) {
            System.out.println("Updating word list.");
            updateWordList();
            System.out.println(showWordList());
        }

        return playGuesses;
    }

    public void playerGuessScores(ArrayList<String> arrayList) {

    }

    public void setCurrentWord(String str) {

    }

    public ArrayList<String> readWords() {
        File file = new File(filename);

        try (Scanner fs = new Scanner(file)) {
            while (fs.hasNextLine()) {
                String word = fs.nextLine();

                if (!wordList.contains(word)) {
                    wordList.add(word);
                }
            }
        } catch (Exception e) {
            System.out.println("Couldn't open " + filename);
            return wordList;
        }
        return wordList;
    }

    public void play() {
        Scanner in = new Scanner(System.in);

        System.out.println("Welcome to the game.");

        boolean running = true;
        while(running) {
            System.out.println("=-=-=-=-=-=-=-=-=-=-= \nChoose one of the following: " +
                    "\n1:\t Start the game \n2:\t See the word list\n" +
                    "3:\t See the chosen words \n4:\t Show Player guesses" +
                    "\nzz to exit \n=-=-=-=-=-=-=-=-=-=-=" );
            System.out.print("What is your choice: ");

            String input = in.nextLine().trim().toLowerCase();

            if (input.equals("zz")) {
                running = false;
            } else if (input.equals("1") || input.equals("one")) {
                if (!pickWord()) {
                    showPlayerGuesses();
                } else {
                    score += guess();
                    System.out.println();
                    System.out.println("Your score is " + score);
                }
            } else if (input.equals("2") || input.equals("two")) {
                showWordList();
            } else if (input.equals("3") || input.equals("three")) {
                showPlayedWords();
            } else if (input.equals("4") || input.equals("four")) {
                showPlayerGuesses();
            } else {
                System.out.println();
                System.out.println("I don't know what \"" + input + "\" is.");
            }

            if (running) {
                System.out.println("Press enter to continue");
                in.nextLine();
            }
        }

        System.out.println("Final score: " + score);
        System.out.println("Thank you for playing");
    }

    public int guess() {
        ArrayList<String> currentGuesses = new ArrayList<>();
        Scanner scan = new Scanner(System.in);
        int letterCount = 0;
        int score = WORD_SIZE + 1;
        String wordGuess;

    }

    public ArrayList<String> getPlayedWords() {

    }

    public String getCurrentWord() {

    }

    public int getLetterCount(String str) {

    }

    public String showPlayedWords() {
        if (playWords.isEmpty()) {
            return "No words have been played\n";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Current list of played words:\n");
        for (String w : playWords) {
            sb.append(w).append("\n");
        }
        return sb.toString();
    }

    public boolean addPlayerGuess(String str) {

    }

    public void updateWordList() {

    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ArrayList<String> getPlayGuesses() {
        return playGuesses;
    }

    public void setPlayGuesses(ArrayList<String> playGuesses) {
        this.playGuesses = playGuesses;
    }

    public ArrayList<String> getPlayWords() {
        return playWords;
    }

    public void setPlayWords(ArrayList<String> playWords) {
        this.playWords = playWords;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public ArrayList<String> getWordList() {
        return wordList;
    }

    public void setWordList(ArrayList<String> wordList) {
        this.wordList = wordList;
    }
}
