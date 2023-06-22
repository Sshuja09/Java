/**
 * A new instance of HuffmanCoding is created for every run. The constructor is
 * passed the full text to be encoded or decoded, so this is a good place to
 * construct the tree. You should store this tree in a field and then use it in
 * the encode and decode methods.
 */

import java.util.*;
public class HuffmanCoding {

	private Node root;

	/**
	 * This would be a good place to compute and store the tree.
	 */
	public HuffmanCoding(String text) {
		// TODO fill this in.
		root = buildHuffmanTree(text);
	}

	/**
	 * Take an input string, text, and encode it with the stored tree. Should
	 * return the encoded text as a binary string, that is, a string containing
	 * only 1 and 0.
	 */
	public String encode(String text) {
		// TODO fill this in.
		Map<Character, String> codebook = buildCodebook(root);
		StringBuilder encodedText = new StringBuilder();

		for(char c: text.toCharArray()){
			encodedText.append(codebook.get(c));
		}
		return encodedText.toString();
	}

	/**
	 * Take encoded input as a binary string, decode it using the stored tree,
	 * and return the decoded text as a text string.
	 */
	public String decode(String encoded) {
		// TODO fill this in.
		StringBuilder decodedText = new StringBuilder();
		Node currentNode = root;

		for(char c: encoded.toCharArray()){
			if(c == '0'){
				currentNode = currentNode.leftChild;
			}else if(c == '1'){
				currentNode = currentNode.rightChild;
			}

			if(currentNode.isLeaf()){
				decodedText.append((currentNode.character));
				currentNode = root;
			}
		}

		return decodedText.toString();
	}

	// Build the Huffman tree from the input text
	private Node buildHuffmanTree(String text){
		Map<Character, Integer> frequencyMap = computeFrequency(text);
		PriorityQueue<Node> queue = new PriorityQueue<>();

		// Create a leaf node for each character and add it to the priority queue
		for(Map.Entry<Character, Integer> entry: frequencyMap.entrySet()){
			char character = entry.getKey();
			int frequency = entry.getValue();

			queue.add(new Node(character, frequency));
		}

		// Build the Huffman tree by merging nodes from the priority queue
		while(queue.size() > 1){
			Node leftChild = queue.poll();
			Node rightChild = queue.poll();
			int totalFrequency = leftChild.frequency + rightChild.frequency;


			Node parent = new Node('\0', totalFrequency, leftChild, rightChild);
			queue.add(parent);
		}

		// Return the root of the Huffman tree
		return queue.poll();

	}

	// Compute the frequency of each character in the input text
	private Map<Character, Integer> computeFrequency(String text){
		Map<Character, Integer> frequency = new HashMap<Character, Integer>();

		for(char c: text.toCharArray()){
			frequency.put(c, frequency.getOrDefault(c, 0) + 1);
		}

		return frequency;
	}

	// Build the codebook (The binary codes for each character
	private Map<Character, String> buildCodebook(Node node){
		Map<Character, String> codebook = new HashMap<>();
		buildCodebookHelper(node, "", codebook);
		return codebook;
	}

	private void buildCodebookHelper(Node node, String code, Map<Character, String> codebook){
		if(node.isLeaf()){ // If the node is a leaf node
			codebook.put(node.character, code); // Assign the code to the character in the codebook
		}else{
			// If the node is not a leaf node, recursively traverse the left child with 0 appended to the code
			buildCodebookHelper(node.leftChild, code + "0", codebook);
			// Recursively traverse the right child with 1 appended to the code
			buildCodebookHelper(node.rightChild, code + "1", codebook);
		}
	}

	/**
	 * The getInformation method is here for your convenience, you don't need to
	 * fill it in if you don't want to. It is called on every run and its return
	 * value is displayed on-screen. You could use this, for example, to print
	 * out the encoding tree.
	 */
	public String getInformation() {
		return "";
	}

	// Node class for the Huffman tree
	private static class Node implements Comparable<Node> {
		char character;
		int frequency;
		Node leftChild, rightChild;

		public Node(char character, int frequency){
			this.character = character;
			this.frequency = frequency;
		}

		public Node(char character, int frequency, Node leftChild, Node rightChild){
			this.character = character;
			this.frequency = frequency;
			this.leftChild = leftChild;
			this.rightChild = rightChild;
		}

		public boolean isLeaf(){
			return this.leftChild == null && this.rightChild == null;
		}


		@Override
		public int compareTo(Node other) {
			if(this.frequency != other.frequency){
				return Integer.compare(this.frequency, other.frequency);
			}else{
				return Character.compare(this.character, other.character);
			}
		}
	}
}
