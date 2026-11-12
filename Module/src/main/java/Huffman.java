import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * Project 3: Huffman Encoding / Decoding.
 *
 * Implement the methods below to compress and decompress text files using
 * Huffman coding. See the assignment write-up for the full specification.
 *
 *  - Treat characters as 8-bit values in the range [0, 255].
 *  - When writing output files, write the newline character explicitly as
 *    '\n' (do not rely on System.lineSeparator() / println()), so your
 *    output is identical across Windows/Mac/Linux. A BufferedWriter over
 *    a FileWriter, writing "\n" yourself, works well for this.
 *  - Encoded files store the code as literal '0'/'1' characters (one byte
 *    each), not as packed bits, so the "encoded" file will actually be
 *    larger than the original -- that's expected for this assignment.
 *  - createHuffTree() is built on top of addNode()/removeMin(), which
 *    operate generically over a java.util.Collection<HuffmanNode>.
 *  - HuffmanNode is a record, so its components are read with
 *    node.charAscii(), node.freq(), node.left(), and node.right() -- no
 *    "get" prefix.
 */
public class Huffman {

    /** Number of possible 8-bit character values. */
    public static final int NUM_CHARS = 256;

    /**
     * Opens the text file named filename and counts the frequency of
     * occurrence of every character in it.
     * Returns an int array of size NUM_CHARS, indexed by ASCII value, with
     * the count of each character (0 for characters that do not occur).
     */
    //opens a text file and coutns the frequency of each char
    public static int[] cntFreq(String filename) throws IOException {
        int[] freq = new int[NUM_CHARS];
        // TODO: implement
            /*okay so i used the internet to help me solve this
            i dont really understand how to do this alone
            and im using the file java classes
             */
        String content = Files.readString(new File(filename).toPath());
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c < NUM_CHARS) {
                freq[c]++;
            }
        }
        return freq;
    }


    /**
     * Builds a Huffman tree from an array of character frequencies (as
     * produced by cntFreq()) and returns the root HuffmanNode of the tree.
     *
     * Use an HQueue to manage the collection of
     * not-yet-combined nodes as you build the tree.
     *
     * Special cases:
     *  - If freq contains no non-zero counts (empty input file), return null
     *  - If freq contains exactly one non-zero count (input file has only
     *    one unique character), return a single leaf HuffmanNode for that
     *    character (no combine() call needed).
     */
    //creates and returns the root of the huffman tree from char freq
    public static HuffmanNode createHuffTree(int[] freq) {
        //
        List<HuffmanNode> nodes = new LinkedList<>();
        for(int i = 0; i < freq.length;i++)
        {
            int count = freq[i];
            if(count> 0)
            {
                HuffmanNode leaf = new HuffmanNode(i,count,null,null);
                insertSorted(nodes,leaf);
            }
        }
        if(nodes.isEmpty())
        {
            return null;
        }
        if(nodes.size()==1 )
        {
            return nodes.getFirst();
        }
        while(nodes.size() > 1)
        {
            HuffmanNode a = nodes.removeFirst();
            HuffmanNode b = nodes.removeFirst();
            HuffmanNode combo = (HuffmanNode) a.combine(b);
            insertSorted(nodes, combo);
        }
        return nodes.getFirst();
    }
    public static void enqueue(List<HuffmanNode> list, HuffmanNode node) {
        insertSorted(list, node);
    }
    //helper func to help keep the list in order by using the comesBefore method
    private static void insertSorted(List<HuffmanNode> list, HuffmanNode node)
    {
        int i = 0;
        while (i < list.size() && !node.comesBefore(list.get(i)))
        {
            i++;
        }
        list.add(i, node);
    }

    /**
     * Traverses the Huffman tree rooted at root and builds the Huffman
     * code for every character that appears in it. Going left appends
     * '0' to the code, going right appends '1'.
     *
     * Returns a String array of size NUM_CHARS, indexed by ASCII value.
     * Characters that do not appear in the tree have an empty string ""
     * at their index. The same is true of the lone character in a
     * single-node tree (the only-one-unique-character case) - it has no
     * left/right branch to build a code out of; huffmanEncode/huffmanDecode
     * handle that case entirely through the header.
     */
    //traverses the huffman tree to generate binart code stringsfor each char
    public static String[] createCode(HuffmanTree root) {
        String[] codes = new String[NUM_CHARS];
        Arrays.fill(codes, "");
        // TODO: implement
        //make all chars empty string
        for(int i = 0; i< codes.length; i++)
        {
            codes[i] = "";
        }
        if(root ==null) //return empty table if tree is empty
        {
            return codes;
        }
        recursiveHelper((HuffmanNode) root, "", codes);
        return codes;
    }
    public static HuffmanNode dequeue(List<HuffmanNode> list) {
        return list.removeFirst();
    }
    // helps by going through the tree and adding the codes to the path it is in
    public static void recursiveHelper(HuffmanNode node, String path, String[] codes) {
        if (node == null) {
            return;
        }
        if (node.left() == null && node.right() == null) //if its a leaf then return
        //the path at the characters ascii index
        {
            codes[node.charAscii()] = path;
            return;
        }
        recursiveHelper((HuffmanNode) node.left(), path + "0", codes);
        recursiveHelper((HuffmanNode) node.right(), path + "1", codes);
    }
    /**
     * Given a frequency array (as produced by cntFreq()), returns a
     * header string listing the ASCII values and their associated
     * frequencies, separated by single spaces, in ascending order of
     * ASCII value. Only characters with a non-zero count are included.
     *
     * Example: for the text "aaabbbbcc" this returns "97 3 98 4 99 2".
     */
    //creates a header string of asciii values and freq in ascending order
    public static String createHeader(int[] freq) {
        // TODO: implement
        String header= "";
        for(int i=0; i<freq.length;i++) {
            if(freq[i] > 0) {
                if(header.length() > 0) {
                    header += " ";
                }
                header += i +" "+ freq[i];
            }
        }
        return header;
    }

    /**
     * Reads the input text file inFile and writes to outFile:
     *   1. A header line (see createHeader), followed by a newline
     *   2. The Huffman-encoded contents of inFile, using the codes from
     *      createCode() -- UNLESS inFile has only one unique character,
     *      see below.
     * If outFile already exists, its old contents are overwritten.
     *
     * Special cases:
     *  - Empty input file: outFile should end up empty too
     *  - Input file with only one unique character: should contain only the
     *    header line (e.g. "97 5") and nothing else - no second line of code bits.
     */
    //reads a text file and encodes it using huffman codes, and then writes it ot an output file
    public static void huffmanEncode(String inFile, String outFile) throws IOException {
        File file = new File(inFile);
        String text = Files.readString(file.toPath());
        File outputFile = new File(outFile);
        FileWriter writer = new FileWriter(outputFile);
        int[] freq = cntFreq(inFile);
        //case 1
        int uniqueChars = 0;
        for (int j : freq) {
            if (j > 0) {
                uniqueChars++;
            }
        }
        if (uniqueChars == 0) {
            writer.close();
            return;
        }
        writer.write(createHeader(freq));
        writer.write("\n");
        //case 2
        if (uniqueChars == 1) {
            writer.close();
            return;
        }
        String[] array = createCode(createHuffTree(freq));
        for (int i = 0; i < text.length(); i++) {
            int index = text.charAt(i); // ascii
            writer.write(array[index]); //binary code string for this character
        }
        writer.close();
    }


    /**
     * Parses a header string (in the format produced by createHeader) and
     * returns the corresponding frequency array of size NUM_CHARS, indexed
     * by ASCII value, i.e. the same format returned by cntFreq().
     *
     * Example: for the header "97 3 98 4 99 2", freq[97, 98, 99] == {3, 4, 2}.
     */
    //pareses a header string back into a 256-enerty freq array
    public static int[] parseHeader(String headerString) {
        int[] freq = new int[NUM_CHARS];
        // TODO: implement
        if (headerString == null || headerString.trim().isEmpty()) {
            return freq;
        }
        Scanner scanner = new Scanner(headerString);
        while (scanner.hasNextInt())
        {
            int ascii = scanner.nextInt();
            int count = scanner.nextInt();
            if (ascii >= 0 && ascii < NUM_CHARS)
            {
                freq[ascii] = count;
            }
        }
        scanner.close();
        return freq;
    }

    /**
     * Reads an encoded file (in the format written by huffmanEncode) and
     * writes the decoded text to decodeFile.
     *
     * Recreate the frequency array from the header with parseHeader(),
     * rebuild the Huffman tree with createHuffTree(), then walk the tree
     * for each bit of the second line: '0' -> left child, '1' -> right
     * child. When a leaf is reached, emit its character and restart at
     * the root.
     *
     * Special case: if the recreated tree is a single leaf (the
     * only-one-unique-character case), there is no second line to read
     * The header's count alone tells you how many times that character occurred,
     * so write it out that many times directly.
     */
    //reads a huffman encoded file then rebuilds the tree and then writesthe decoded text
    public static void huffmanDecode(String encodedFile, String decodedFile) throws IOException {
        File file = new File(encodedFile);
        if (!file.exists() || file.length() == 0) {
            Files.writeString(new File(decodedFile).toPath(), "");
            return;
        }
        List<String> lines = Files.readAllLines(file.toPath());
        if (lines.isEmpty()) {
            return;
        }
        String header = lines.get(0);
        int[] freq = parseHeader(header);
        HuffmanNode root = (HuffmanNode) createHuffTree(freq);
        StringBuilder decodedText = new StringBuilder();
        // special case
        int uniqueChars = 0;
        int singleCharAscii = -1;
        int singleCharFreq = 0;
        for (int i = 0; i < freq.length; i++) {
            if (freq[i] > 0) {
                uniqueChars++;
                singleCharAscii = i;
                singleCharFreq = freq[i];
            }
        }
        if (uniqueChars == 1) {
            for (int i = 0; i < singleCharFreq; i++) {
                decodedText.append((char) singleCharAscii);
            }
            Files.writeString(new File(decodedFile).toPath(), decodedText.toString());
            return;
        }
        // recombine all left over lines as binary text
        StringBuilder encodedText = new StringBuilder();
        for (int i = 1; i < lines.size(); i++) {
            encodedText.append(lines.get(i));
        }

        // decode the binary string back to the original characters
        HuffmanNode current = root;

        for (int i = 0; i < encodedText.length(); i++) {
            char bit = encodedText.charAt(i);
            if (bit == '0') {
                current = (HuffmanNode) current.left();
            } else {
                current = (HuffmanNode) current.right();
            }
            if (current.left() == null && current.right() == null)
            {
                decodedText.append((char) current.charAscii());
                current = root;
            }
        }
        Files.writeString(new File(decodedFile).toPath(), decodedText.toString());
    }
}
