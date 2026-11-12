import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Starter test suite for Project 3 (Huffman Encoding/Decoding).
 * Write more tests!
 * These tests assume the test files are in the same directory as HuffmanTest.java
 */
public class HuffmanTest {

    @Test
    public void testCntFreq() throws IOException {
        int[] freqList = Huffman.cntFreq("src/test/java/file2.txt");
        int[] ansList = {2, 4, 8, 16, 0, 2, 0}; // counts for ascii 97..103
        for (int i = 0; i < ansList.length; i++) {
            assertEquals(ansList[i], freqList[97 + i], "mismatch at ascii " + (97 + i));
        }
    }

    @Test
    public void testComesBefore() {
        HuffmanLeaf d = new HuffmanLeaf(5, 'd');
        HuffmanLeaf k = new HuffmanLeaf(5, 'k');
        assertTrue(d.comesBefore(k));
        assertFalse(k.comesBefore(d));
    }

    @Test
    public void testCombine() {
        HuffmanLeaf a = new HuffmanLeaf(2, 'a');
        HuffmanLeaf b = new HuffmanLeaf(3, 'b');
        HuffmanTree combined = a.combine(b);

        assertEquals(5, combined.freq());
        assertEquals('a', combined.charAscii()); // checks min ascii tie
    }

    @Test
    public void testCreateHuffTree() {
        int[] freq = new int[Huffman.NUM_CHARS];
        freq['a'] = 3;
        freq['b'] = 4;

        HuffmanTree root = Huffman.createHuffTree(freq);
        assertNotNull(root);
        assertEquals(7, root.freq());
    }

    @Test
    public void testCreateCode() {
        int[] freq = new int[Huffman.NUM_CHARS];
        freq['a'] = 3;
        freq['b'] = 4;

        HuffmanTree root = Huffman.createHuffTree(freq);
        String[] codes = Huffman.createCode(root);

        // codes should exist for chars in tree
        assertFalse(codes['a'].isEmpty());
        assertFalse(codes['b'].isEmpty());
        // unused chars stay empty string
        assertEquals("", codes['z']);
    }

    @Test
    public void testCreateHeader() throws IOException {
        int[] freqList = Huffman.cntFreq("src/test/java/file2.txt");
        assertEquals("97 2 98 4 99 8 100 16 102 2", Huffman.createHeader(freqList));
    }

    @Test
    public void testParseHeader() {
        String header = "97 2 98 4 99 8 100 16 102 2";
        int[] freq = Huffman.parseHeader(header);

        assertEquals(2, freq[97]);
        assertEquals(4, freq[98]);
        assertEquals(8, freq[99]);
        assertEquals(16, freq[100]);
        assertEquals(2, freq[102]);
        assertEquals(0, freq[101]);
    }

    @Test
    public void test01Textfile() throws IOException {
        String inFile = "src/test/java/file1.txt";
        String outFile = "src/test/java/file1_out.txt";
        String solnFile = "src/test/java/file1_soln.txt";
        String decodedFile = "src/test/java/file1_decoded.txt";

        // encode and check vs soln file
        Huffman.huffmanEncode(inFile, outFile);
        assertTrue(compareFiles(outFile, solnFile));

        // decode back and check vs original
        Huffman.huffmanDecode(outFile, decodedFile);
        assertTrue(compareFiles(inFile, decodedFile));
    }

    @Test
    public void testSingleCharacterEdgeCase() throws IOException {
        Path singleIn = Path.of("src/test/java/single_test.txt");
        Path singleEnc = Path.of("src/test/java/single_enc.txt");
        Path singleDec = Path.of("src/test/java/single_dec.txt");

        Files.writeString(singleIn, "aaaaa");

        // encode single char file
        Huffman.huffmanEncode(singleIn.toString(), singleEnc.toString());

        // verify only header line written, no bits line
        String encodedContent = Files.readString(singleEnc);
        assertEquals("97 5", encodedContent.trim());

        // decode single char file
        Huffman.huffmanDecode(singleEnc.toString(), singleDec.toString());
        String decodedContent = Files.readString(singleDec);
        assertEquals("aaaaa", decodedContent);

        // cleanup temp files
        Files.deleteIfExists(singleIn);
        Files.deleteIfExists(singleEnc);
        Files.deleteIfExists(singleDec);
    }

    @Test
    public void testEmptyFileEdgeCase() throws IOException {
        Path emptyIn = Path.of("src/test/java/empty_test.txt");
        Path emptyEnc = Path.of("src/test/java/empty_enc.txt");
        Path emptyDec = Path.of("src/test/java/empty_dec.txt");

        Files.writeString(emptyIn, "");

        // encode empty file
        Huffman.huffmanEncode(emptyIn.toString(), emptyEnc.toString());
        assertEquals(0, Files.size(emptyEnc), "encoded file should be empty");

        // decode empty file
        Huffman.huffmanDecode(emptyEnc.toString(), emptyDec.toString());
        assertEquals(0, Files.size(emptyDec), "decoded file should be empty");

        // cleanup temp files
        Files.deleteIfExists(emptyIn);
        Files.deleteIfExists(emptyEnc);
        Files.deleteIfExists(emptyDec);
    }

    @Test
    public void testFileNotFoundException() {
        assertThrows(IOException.class, () -> {
            Huffman.cntFreq("src/test/java/non_existent_file_9999.txt");
        });
    }

    /**
     * Compares two files line by line, trimming trailing CR/LF so the
     * comparison works the same on Windows and Unix-style line endings.
     */
    private static boolean compareFiles(String file1, String file2) throws IOException {
        try (BufferedReader r1 = new BufferedReader(new FileReader(file1));
             BufferedReader r2 = new BufferedReader(new FileReader(file2))) {
            String line1, line2;
            while (true) {
                line1 = r1.readLine();
                line2 = r2.readLine();
                if (line1 == null && line2 == null) {
                    return true;
                }
                if (line1 == null || line2 == null) {
                    return false;
                }
                if (!line1.trim().equals(line2.trim())) {
                    return false;
                }
            }
        }
    }
}