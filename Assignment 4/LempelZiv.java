import java.util.*;

public class LempelZiv {

    private static final int WINDOW_SIZE = 100;

    /**
     * Take uncompressed input as a text string, compress it, and return it as a
     * text string.
     */comp261

    public static String compress(String input) {
        // TODO fill this in.
        int inputSize = input.length();
        int cursor = 0; // Cursor to track the current position in the input string
        StringBuilder output = new StringBuilder();

        while (cursor < inputSize) {
            int length = 0; // Length of the matched substring
            int matchedIndex = 0; // Index of the matched substring in the window
            int windowStart = Math.max(cursor - WINDOW_SIZE, 0); // Start position of the window
            String str = input.substring(windowStart, cursor); // Extract the window substring up until the cursor

            int index;
            // Find the longest matching substring between the window and the current position in the input string
            while (cursor + length < inputSize) {
                String match = input.substring(cursor, cursor + length + 1);
                index = str.indexOf(match);

                if (index >= 0) {
                    matchedIndex = index; // Update the position of the matched substring
                    length++; // Increase the length of the matched substring
                } else {
                    break;
                }
            }

            char nextChar;
            if (length > 0 && cursor + length < inputSize) {
                nextChar = input.charAt(cursor + length);
                output.append("[" + (str.length() - matchedIndex) + "|" + length + "|" + nextChar + "]");
            }
            else { // No match found
                nextChar = input.charAt(cursor);
                output.append("[0|0|" + nextChar + "]");
            }

            cursor += length + 1; // Move the cursor to the next position
        }

        return output.toString();
    }

    /**
     * Take compressed input as a text string, decompress it, and return it as a
     * text string.
     */
    public static String decompress(String compressed) {
        // TODO fill this in.
        StringBuilder output = new StringBuilder();
        int cursor = 0; // Current position in the compressed string
        String[] compressArray = compressed.split("\\["); // Split compressed input into tuples

        for (int i = 1; i < compressArray.length; i++) { // Start from index 1 to skip the empty element
            String tuple = compressArray[i].substring(0, compressArray[i].length() - 1); // Remove the closing bracket
            String[] tupleParts = tuple.split("\\|"); // Split the tuple into offset, length, and next character
            int offset = Integer.parseInt(tupleParts[0]); // Offset (distance back to the start of the match)
            int length = Integer.parseInt(tupleParts[1]); // Length of the match
            if (offset == 0 || length == 0) {
                output.append(tupleParts[2]); // Append the next character as there was no match
                cursor++;
            } else {
                String match = output.substring(cursor - offset, cursor - offset + length); // Retrieve the matched substring
                output.append(match); // Append the matched substring
                cursor += length + 1; // Move the cursor forward by length + 1
                output.append(tupleParts[2]); // Append the next character
            }
        }

        return output.toString();
    }

    /**
     * The getInformation method is here for your convenience, you don't need to
     * fill it in if you don't want to. It is called on every run and its return
     * value is displayed on-screen. You can use this to print out any relevant
     * information from your compression.
     */
    public String getInformation() {
        return "";
    }
}