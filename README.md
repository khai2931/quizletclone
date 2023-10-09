# quizletclone
A command-line clone of the write feature in quizlet.

## Usage

Make sure you have the latest version of Java (JRE or JDK) installed:
(Windows Link)
https://www.oracle.com/java/technologies/downloads/#jdk21-windows

(General Link)
https://www.oracle.com/java/technologies/downloads/

To run QuizletClone:

For windows, double-click on `RunQuizletClone.bat`

For other users, open a terminal in this directory (containing QuizletClone.jar) and run:

`java -jar QuizletClone.jar`

You may use any text file. File paths begin where QuizletClone.jar is,
for example, `ital101/w2/smallNumbers.txt` if you clone this repo unmodified.

See `ital101/w2/smallNumbers.txt` for an example of how to format your text files for use with this app.

Expected format:

Each `pair` is separated by a newline.

A 'pair' is `term<tab>definition` where `<tab>` is a tab character. Multiple tab characters are allowed.

Within a pair, any whitespace before and after `term` and `definition` is ignored.

Any lines with no tab characters are ignored.

Any lines that only contain whitespace are ignored.

(i.e. you can organize your terms with comments that have no tabs)