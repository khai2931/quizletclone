import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 *  A command-line clone of the write feature in quizlet.
 *  (i.e. memorize each and every flashcard by writing the term/definition fully)
 *  See STATIC_CONSTANTS settings below for
 *  terms to use, case sensitivity, whether to answer with term/definition,
 *  and whether to ignore anything in parentheses (),
 *  which are STATIC_CONSTANTS.
 */
class Main {

    // Choose file here for your list of terms.
    // If null, the user can select the file, otherwise,
    // this overrides user selection
    // Expected format:
    // Each "pair" is separated by a newline.
    // A pair is "term<tab>definition"
    // where <tab> is a tab character. Multiple tab characters are allowed
    // Within a pair, any whitespace before and after "term" and "definition" is ignored.
    // Any lines with no tab characters are ignored.
    // Any lines that only contain whitespace are ignored.
    // Order and whitespace is ignored in comma-separated lists
    // (i.e. "a, b, c" is the same as "b, c, a" and "b,c,a")
    // See numbers.txt for an example.

    public static String TERMS_FILE = null;

    // If true, you will answer with definition (right), otherwise
    // you will answer with term (left).
    public static boolean ANSWER_WITH_DEFINITION = true;
    // If true, all answers must match casing to be correct,
    // otherwise casing is ignored
    public static boolean CASE_SENSITIVE = false;
    // If true, cards will be randomized
    // otherwise cards will appear in order of the file provided
    public static boolean RANDOMIZE = true;
    // If true, will ignore anything in parentheses () and any ( or )
    // (which will ignore any extra whitespace)
    public static boolean IGNORE_PARENTHESES = true;

    private static Scanner input;
    private static Random rand;

    public static void main(String[] args) {
        System.out.println("Welcome to Khai's Quizlet Clone!");
        System.out.println("Happy studying :)");
        System.out.println();
        input = new Scanner(System.in);
        rand = new Random();
        printSettings();
        System.out.print("Would you like to adjust settings? (y/n) ");
        String setSettings = input.nextLine();
        System.out.println();
        if (setSettings.equals("y")) {
            adjustSettings();
        }
        printSettings();
        System.out.println();
        boolean isStudying = true;
        while (isStudying) {
            if (TERMS_FILE == null) {
                System.out.print("Which file to use? ");
                TERMS_FILE = input.nextLine();
                System.out.println();
            }
            List<String> terms = parseTerms(TERMS_FILE);
            String userAnswer = "y";
            while (userAnswer.equals("y")) {
                testTerms(terms);
                System.out.print("Practice everything again? (y/n) ");
                userAnswer = input.nextLine();
                System.out.println();
            }
            System.out.print("Study a different set? (y/n) ");
            userAnswer = input.nextLine();
            System.out.println();
            isStudying = userAnswer.equals("y");
            TERMS_FILE = null;
        }
        System.out.println();
        System.out.println("Bye! See you later :)");
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            return;
        }
    }

    /**
     * Prints the current settings.
     */
    private static void printSettings() {
        System.out.println("Current settings:");
        String ansWithDef = ANSWER_WITH_DEFINITION ? "answer with definition" : "answer with term";
        String ignoreCase = CASE_SENSITIVE ?        "case sensitive" :          "ignore casing";
        String randomQ    = RANDOMIZE ?             "randomize questions" :     "keep questions in file's order";
        String ignorePar  = IGNORE_PARENTHESES ?    "ignore parentheses" :      "don't ignore parentheses";

        System.out.println(ansWithDef + ", " + ignoreCase + ", " + randomQ + ", " + ignorePar + ".");
    }

    /**
     * Adjusts the current settings.
     */
    private static void adjustSettings() {
        System.out.print("Answer with definition or term? (d for definition, t for term) ");
        ANSWER_WITH_DEFINITION = input.nextLine().equals("d");
        System.out.println();
        System.out.print("Require exact casing for correct answer? (y/n) ");
        CASE_SENSITIVE = input.nextLine().equals("y");
        System.out.println();
        System.out.print("Randomize the order of questions? (y/n) ");
        RANDOMIZE = input.nextLine().equals("y");
        System.out.println();
        System.out.print("Ignore parentheses and anything inside them when grading? (y/n) ");
        IGNORE_PARENTHESES = input.nextLine().equals("y");
        System.out.println();
    }

    /**
     * Parses the file into term/definition pairs.
     *
     * @param filename The file to parse
     * @return A list of "pairs" of terms/definitions as
     * described in the class comment
     */
    private static List<String> parseTerms(String filename) {
        List<String> terms = null;
        try {
            File file = new File(filename);
            System.out.println(file.getCanonicalPath());
            Scanner myReader = new Scanner(file);
            terms = new ArrayList<>();
            while (myReader.hasNextLine()) {
                String nextLine = myReader.nextLine().trim();
                if (nextLine.contains("\t")) {
                    terms.add(nextLine);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } finally {
            return terms;
        }
    }

    /**
     * (Possibly repeatedly) tests the user on the list of pairs.
     * While the user answers any questions wrong, the user is given
     * an option to test through all the questions they got wrong.
     *
     * @param terms list of term/definition pairs to use
     */
    private static void testTerms(List<String> terms) {
        String userAnswer = "";
        List<String> remainingTerms = new ArrayList<>(terms);
        List<String> incorrectTerms = new ArrayList<>();
        int numCorrect = goThroughTerms(remainingTerms, incorrectTerms);
        int total = remainingTerms.size();
        System.out.println("You got " + numCorrect + " out of " + total + " correct!");
        while (numCorrect < total) {
            System.out.print("Practice wrong answers? (y/n) ");
            userAnswer = input.nextLine();
            System.out.println();
            if (userAnswer.equals("y")) {
                pageBreak(); // to prevent user from copying correct answers already seen
                remainingTerms = incorrectTerms;
                incorrectTerms = new ArrayList<>();
                numCorrect = goThroughTerms(remainingTerms, incorrectTerms);
                total = remainingTerms.size();
                System.out.println("You got " + numCorrect + " out of " + total + " correct!");
            } else {
                break;
            }
        }
    }

    /**
     * Tests the user on the list of pairs.
     * Note that the behavior of order of terms and what is considered "correct" is modified
     * by the above settings/public constants. See the top of this class for more detail.
     *
     * @param remainingTerms list of term/definition pairs to use
     * @param incorrectTerms list of pairs that the user got wrong
     * @return the number of questions the user answered correctly
     */
    private static int goThroughTerms(List<String> remainingTerms, List<String> incorrectTerms) {
        int numCorrect = 0;
        int numComplete = 0;
        String userAnswer;
        if (RANDOMIZE) {
            Collections.shuffle(remainingTerms, rand);
        }
        System.out.println("Please write the answer for each a -> b pair.");
        System.out.println("(" + (remainingTerms.size() - numComplete) + " cards left)");
        System.out.println();
        for (String pair : remainingTerms) {
            String question;
            String answer;
            if (ANSWER_WITH_DEFINITION) {
                question = left(pair);
                answer = right(pair);
            } else {
                question = right(pair);
                answer = left(pair);
            }
            System.out.print(question + "\t -> \t");
            userAnswer = input.nextLine();
            if (answerIsCorrect(userAnswer, answer)) {
                numCorrect++;
            } else {
                incorrectTerms.add(pair);
            }
            while (!answerIsCorrect(userAnswer, answer)) {
                System.out.println();
                System.out.println("The answer is: \"" + answer + "\". Try again!");
                System.out.println();
                System.out.print(question + "\t -> \t");
                userAnswer = input.nextLine();
            }
            numComplete++;
            int choice = rand.nextInt(4);
            System.out.println();
            if (!answer.equals(userAnswer)) {
                System.out.println("Alternative answers: \"" + answer + "\"");
            }
            switch (choice) {
                case 0:
                    System.out.println("Correct! Good job :)");
                    break;
                case 1:
                    System.out.println("Awesome stuff :)");
                    break;
                case 2:
                    System.out.println("You got this!");
                    break;
                default:
                    System.out.println("Wow that's so cool!");
                    break;
            }
            System.out.println("(" + (remainingTerms.size() - numComplete) + " cards left)");
            System.out.println();
        }
        return numCorrect;
    }

    /**
     * Returns whether the answer is correct.
     *
     * @param userAnswer what the user answered
     * @param answer what the actual answer is
     * @return true if the answer is correct, false otherwise
     */
    private static boolean answerIsCorrect(String userAnswer, String answer) {
        if (!CASE_SENSITIVE) {
            userAnswer = userAnswer.toLowerCase();
            answer = answer.toLowerCase();
        }
        if (IGNORE_PARENTHESES) {
            userAnswer = deleteParens(userAnswer);
            answer = deleteParens(answer);
        }
        // commas are removed and standardized so order is ignored
        userAnswer = alphabeticalNoComma(userAnswer);
        answer = alphabeticalNoComma(answer);
        return userAnswer.equals(answer);
    }

    /**
     * Given a comma-separated list of items,
     * returns an alphabetically ordered comma-separated list of those items
     * with each item trimmed of any leading/trailing whitespace.
     *
     * @param str comma-separated list of items (or no comma for one item)
     * @return alphabetically ordered comma-separated list of those items
     * with each item trimmed of any leading/trailing whitespace.
     * (as a string)
     */
    private static String alphabeticalNoComma(String str) {
        String[] items = str.split(",");
        for (int i = 0; i < items.length; i++) {
            items[i] = items[i].trim();
        }
        Arrays.sort(items);
        return String.join(",", items);
    }

    /**
     * Given a string,
     * returns the same string with all text inside parentheses removed,
     * all parentheses removed, and finally trimmed.
     *
     * @param str any string
     * @return the same string with all text inside parentheses removed,
     * all parentheses removed, and finally trimmed.
     */
    private static String deleteParens(String str) {
        int[] pair = getFirstParenPair(str);
        while (pair != null) {
            str = str.substring(0, pair[0]) + str.substring(pair[1] + 1);
            pair = getFirstParenPair(str);
        }
        str = str.
                replaceAll("[)]", "").
                replaceAll("[(]", "");
        return str.trim();
    }

    /**
     * Given a string, finds the first pair of parentheses in the string
     * that has NO parentheses between them.
     * Returns null if no such pair is found.
     * Returns an int[] such that int[0] is the index of the left parentheses in that pair,
     * and int[1] is the index of the right parentheses in that pair,
     * only if that pair is found.
     *
     * @param str any string
     * @return null if no such pair (above) is found. Otherwise, returns an
     * int[] such that int[0] is the index of the left parentheses in that pair,
     * and int[1] is the index of the right parentheses in that pair,
     * only if that pair is found.
     */
    private static int[] getFirstParenPair(String str) {
        if (str.contains("(")) {
            int left = str.indexOf("(");
            for (int i = left; i < str.length(); i++) {
                if (str.charAt(i) == '(') {
                    left = i;
                } else if (str.charAt(i) == ')'){
                    int[] pair = {left, i};
                    return pair;
                }
            }
        }
        return null;
    }

    /**
     * Given a string, returns the "leftmost" token after splitting by tab characters.
     *
     * @param str any string
     * @return the "leftmost" token after splitting by tab characters.
     */
    private static String left(String str) {
        String[] tokens = str.split("\t");
        return tokens[0].trim();
    }

    /**
     * Given a string, returns the "rightmost" token after splitting by tab characters.
     *
     * @param str any string
     * @return the "rightmost" token after splitting by tab characters.
     */
    private static String right(String str) {
        String[] tokens = str.split("\t");
        return tokens[tokens.length - 1].trim();
    }

    /**
     * Prints 100 newlines.
     */
    private static void pageBreak() {
        for (int i = 0; i < 100; i++) {
            System.out.println();
        }
    }
}