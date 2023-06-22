/**
 * A new KMP instance is created for every substring search performed. Both the
 * pattern and the text are passed to the constructor and the search method. You
 * could, for example, use the constructor to create the match table and the
 * search method to perform the search itself.
 */
public class KMP {

	/**
	 * Perform KMP substring search on the given text with the given pattern.
	 * 
	 * This should return the starting index of the first substring match if it
	 * exists, or -1 if it doesn't.
	 */
	public static int search(String pattern, String text) {
		// TODO fill this in.
		int[] matchTable = computeMatchTable(pattern); // Compute the match table for the pattern
		int patternIndex = 0;
		int textIndex = 0;

		while (textIndex < text.length()) {
			if (pattern.charAt(patternIndex) == text.charAt(textIndex)) { // If the characters match, move to the next pattern and text indices
				if (patternIndex == pattern.length() - 1) { // If the patter index reaches the end, a match is found
					return textIndex - patternIndex;
				}
				patternIndex++;
				textIndex++;
			}
			// If there is a mismatch and the pattern index in not at the beginning,
			// update teh patternn index using the match table
			else if (patternIndex > 0) {
				patternIndex = matchTable[patternIndex - 1];
			}
			// If there is a mismatch and the pattern index is already at the beginning,
			// move to the next character in the text
			else {
				textIndex++;
			}
		}
		return -1; // No match found
	}


	/**
	 * Compute the match table for the given pattern.
	 * The match table stores the length of the longest proper prefix that is also a
	 * proper suffix for each index in the pattern.
	 */
	private static int[] computeMatchTable(String pattern) {
		int[] matchTable = new int[pattern.length()];
		int prefixIndex = 0;
		int suffixIndex = 1;

		// Iterate over the pattern
		while (suffixIndex < pattern.length()) {
			// If the characters at the prefix and suffinx indices match,
			// update the match table and move to the next indices
			if (pattern.charAt(prefixIndex) == pattern.charAt(suffixIndex)) {
				matchTable[suffixIndex] = prefixIndex + 1;
				prefixIndex++;
				suffixIndex++;
			}
			// If there is a mismatch and the prefix index is not at the beginning,
			// update the prefix index using the match table
			else if (prefixIndex > 0) {
				prefixIndex = matchTable[prefixIndex - 1];
			}
			// If there is a mismatch and the prefix index in already at the beginning,
			// update the match table and move to the next suffix index
			else {
				matchTable[suffixIndex] = 0;
				suffixIndex++;
			}
		}
		return matchTable;
	}
}
