import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Name: Von Andre Llacuna
 * Date: 02/12/26
 * Explanation: This class creates a game where it takes 5-letter words from a file,
 * and you have to guess the word.
 */
public class Jotto {
    // Constant Fields
    private static final int WORD_SIZE = 5;
    private static final boolean DEBUG = true;

    // Members
    private String currentWord;
    private int score;
    private final ArrayList<String> playGuesses = new ArrayList<>();
    private final ArrayList<String> playWords = new ArrayList<>();
    private String filename;
    private final ArrayList<String> wordList = new ArrayList<>();

    // Constructor
    public Jotto(String filename) {
        this.filename = filename;
        readWords();
    }

    // METHODS
    // Randomly chooses word from wordlist to make key word and avoids repeats
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
        playWords.add(currentWord);
        if (DEBUG) {
            System.out.println(currentWord);
        }
        return true;
    }

    // Returns each word on its own line from the current wordlist
    public String showWordList() {
        StringBuilder sb = new StringBuilder();
        sb.append("Current word list:\n");
        for (String w : wordList) {
            sb.append(w).append("\n");
        }
        return sb.toString();
    }

    /* Prints all guesses from the player.
     * If no guesses, prints "No guesses yet"
     * Asks to update wordlist
     */
    public ArrayList<String> showPlayerGuesses() {
        Scanner scan = new Scanner(System.in);

        if (playGuesses.isEmpty()) {
            System.out.println("No guesses yet");
            return playGuesses;
        } else {
            System.out.println("Current player guesses:");
            for (String g : playGuesses) {
                System.out.println(g);
            }
        }

        System.out.println("Would you like to add the words to the word list? (y/n)");
        String input = scan.next().trim().toLowerCase();
        if (input.equals("y")) {
            System.out.println("Updating word list.");
            updateWordList();
            System.out.print(showWordList());
        }

        return playGuesses;
    }

    //Prints Guess / Score table for the guesses in the round
    public void playerGuessScores(ArrayList<String> guesses) {
        System.out.println("Guess\t\tScore");
        for (String g : guesses) {
            int s = getLetterCount(g);
            System.out.println(g + "\t\t" + s);
        }
        System.out.println();
    }

    //Uses File and Scanner to read words and put into wordlist
    public ArrayList<String> readWords() {
        File file = new File(filename);

        try (Scanner fs = new Scanner(file)) {
            while (fs.hasNextLine()) {
                String word = fs.nextLine();
                if (word.isEmpty()) {
                    continue;
                }

                if (word.length() != WORD_SIZE) {
                    continue;
                }

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

    /* Creates the main menu and gives 5 options
     * 1 -> lets you play the game and guess words to find the key word
     * 2 -> prints out the entire list of possible words
     * 3 -> prints a list of words that you've already played
     * 4 -> prints a list of all the words you've guessed in all rounds
     * zz -> stops the program
     */
    public void play() {
        Scanner in = new Scanner(System.in);

        System.out.println("Welcome to the game.");
        System.out.println("Current Score: " + score);

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
                    System.out.println("Your score is " + score);
                }
            } else if (input.equals("2") || input.equals("two")) {
                System.out.print(showWordList());
            } else if (input.equals("3") || input.equals("three")) {
                System.out.print(showPlayedWords());
            } else if (input.equals("4") || input.equals("four")) {
                showPlayerGuesses();
            } else {
                System.out.println("I don't know what \"" + input + "\" is.");
            }

            if (running) {
                System.out.print("Press enter to continue\n");
                in.nextLine();
            }
        }

        System.out.println("Final score: " + score);
        System.out.println("Thank you for playing");
    }

    // Loop that allows you to guess words and keeps track of score for the round
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
            letterCount = getLetterCount(wordGuess);
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

    // Returns a string that shows all of the words that have been played already
    public String showPlayedWords() {
        if (playWords.isEmpty()) {
            return "No words have been played.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Current list of played words:\n");
        ArrayList<String> words = getPlayedWords();
        for (int i = 0; i < words.size(); i++) {
            sb.append(words.get(i)).append("\n");
        }
        return sb.toString();
    }

    // Adds word to playGuesses if not already there
    public boolean addPlayerGuess(String wordGuess) {
        if (!playGuesses.contains(wordGuess)) {
            playGuesses.add(wordGuess);
            return true;
        }
        return false;
    }

    // Uses filewriter to add playGuesses into the wordlist, without duplicates
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

    // GETTERS AND SETTERS
    public ArrayList<String> getPlayedWords() {
        return playWords;
    }

    public int getLetterCount(String wordGuess) {
        wordGuess = wordGuess.toLowerCase();
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
                uniqueInWord.remove((Character) c);
                count++;
            }
        }
        return count;
    }

    public void setCurrentWord(String str) {
        this.currentWord = str;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public ArrayList<String> getPlayGuesses() {
        return playGuesses;
    }

    public ArrayList<String> getPlayWords() {
        return playWords;
    }

    public ArrayList<String> getWordList() {
        return wordList;
    }
}
