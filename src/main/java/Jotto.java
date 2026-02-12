import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
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
        if (wordList.isEmpty()) {
            System.out.println("Couldn't open " + filename);
            return false;
        }

        if (playWords.size() >= wordList.size()) {
            System.out.println("You've guessed them all!");
            return false;
        }

        Random rand = new Random();
        String possibleWord = wordList.get(rand.nextInt(wordList.size()));

        if (playWords.contains(possibleWord)) {
            return pickWord();
        }

        setCurrentWord(possibleWord);
        if (DEBUG) {
            System.out.println(currentWord);
        }
        return true;
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

    public void playerGuessScores(ArrayList<String> guesses) {
        System.out.println("Guess\t\tScore\n");
        for (String g : guesses) {
            int s = getLetterCount(g);
            System.out.println(g + "\t\t" + s);
        }
    }

    public void setCurrentWord(String str) {
        this.currentWord = str;
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

        while (true) {
            System.out.println("Current Score: " + score);
            System.out.print("What is your guess (q to quit): ");
            wordGuess = scan.nextLine().trim().toLowerCase();

            if (wordGuess.equals("q")) {
                score = Math.min(0, score);
                break;
            }

            if (wordGuess.length() != WORD_SIZE) {
                System.out.println("Word must be " + WORD_SIZE + " characters (" + wordGuess +
                        " is " + wordGuess.length() + ")");
                continue;
            }

            addPlayerGuess(wordGuess);

            if (wordGuess.equals(currentWord)) {
                System.out.println("DINGDINGDING!!! the word was " + currentWord);
                currentGuesses.add(wordGuess);
                playerGuessScores(currentGuesses);
                return score;
            }

            if (currentGuesses.contains(wordGuess)) {
                System.out.println("\"" + wordGuess + "\" had already been entered.");
                continue;
            }

            currentGuesses.add(wordGuess);
            if (letterCount != WORD_SIZE) {
                System.out.println(wordGuess + " has a Jotto score of " + letterCount);
                playerGuessScores(currentGuesses);
            } else {
                System.out.println("That word is an anagram.");
                playerGuessScores(currentGuesses);
            }

            score--;
        }

        if (!currentGuesses.isEmpty()) {
            System.out.println("The word was " + currentWord);
            if (!currentGuesses.isEmpty()) {
                playerGuessScores(currentGuesses);
            }
        }

        return score;
    }

    public ArrayList<String> getPlayedWords() {
        return playWords;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public int getLetterCount(String wordGuess) {
        if (wordGuess.equals(currentWord)) {
            return WORD_SIZE;
        }

        int count = 0;

        ArrayList<Character> uniqueInWord = new ArrayList<>();
        for (int i = 0; i < currentWord.length(); i++) {
            char c = currentWord.charAt(i);
            if (!uniqueInWord.contains(c)) {
                uniqueInWord.add(c);
            }
        }

        ArrayList<Character> usedInGuessedWord = new ArrayList<>();
        for (int i = 0; i < wordGuess.length(); i++) {
            char c = wordGuess.charAt(i);

            if (usedInGuessedWord.contains(c)) {
                continue;
            }
            usedInGuessedWord.add(c);

            if (uniqueInWord.contains(c)) {
                uniqueInWord.remove(c);
                count++;
            }
        }
        return count;
    }

    public String showPlayedWords() {
        if (playWords.isEmpty()) {
            return "No words have been played\n";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Current list of played words:\n");
        ArrayList<String> words = getPlayedWords();
        for (int i = 0; i < words.size(); i++) {
            sb.append(i).append("\n");
        }
        return sb.toString();
    }

    public boolean addPlayerGuess(String wordGuess) {
        if (!playGuesses.contains(wordGuess)) {
            playGuesses.add(wordGuess);
            return true;
        }
        return false;
    }

    public void updateWordList() {
        try (FileWriter fw = new FileWriter(filename)) {
            for (String g : playGuesses) {
                if (g != null) {
                    String w = g.trim().toLowerCase();
                    if (!w.isEmpty() && w.length() == WORD_SIZE && !wordList.contains(w)) {
                        wordList.add(w);
                    }
                }
            }
            for (String w : wordList) {
                fw.write(w + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Couldn't open " + filename);
        }
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
