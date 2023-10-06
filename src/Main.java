import java.util.*;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

/**
 *  A command-line clone of the write feature in quizlet.
 *  (i.e. memorize each and every flashcard by writing the term/definition fully)
 *  See STATIC_CONSTANTS settings below for
 *  terms to use, case sensitivity, whether to answer with term/definition,
 *  and whether to ignore anything in parentheses (),
 *  which are STATIC_CONSTANTS.
 *  Note that questions are always randomized.
 */
class Main {

    // Choose file here for your list of terms.
    // Expected format:
    // Each "pair" is separated by a newline.
    // A pair is "term<tab>definition"
    // where <tab> is a tab character. Multiple tab characters are allowed
    // Within a pair, any whitespace before and after "term" and "definition" is ignored.
    // Any lines with no tab characters AND any lines that only contain whitespace are ignored.
    // See numbers.txt for an example.

    public static String TERMS_FILE = "multiWordTest.txt";
    //public static String TERMS_FILE = "numbers.txt";
    //public static String TERMS_FILE = "smallNumbers.txt";

    // If true, you will answer with definition (right), otherwise
    // you will answer with term (left).
    public static boolean ANSWER_WITH_DEFINITION = false;
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
        List<String> terms = parseTerms(TERMS_FILE);
        String userAnswer = "y";
        while (userAnswer.equals("y")) {
            testTerms(terms);
            System.out.print("Practice everything again? (y/n) ");
            userAnswer = input.nextLine();
            System.out.println();
        }
    }

    public static List<String> parseTerms(String filename) {
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

    public static void testTerms(List<String> terms) {
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
                pageBreak();
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
            if (userAnswer.equals(answer)) {
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

    private static boolean answerIsCorrect(String userAnswer, String answer) {
        if (!CASE_SENSITIVE) {
            userAnswer = userAnswer.toLowerCase();
            answer = answer.toLowerCase();
        }
        if (IGNORE_PARENTHESES) {
            userAnswer = deleteParens(userAnswer);
            answer = deleteParens(answer);
        }
        return userAnswer.equals(answer);
    }

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

    private static String left(String str) {
        String[] tokens = str.split("\t");
        return tokens[0].trim();
    }

    private static String right(String str) {
        String[] tokens = str.split("\t");
        return tokens[tokens.length - 1].trim();
    }

    private static void pageBreak() {
        for (int i = 0; i < 100; i++) {
            System.out.println();
        }
    }
}