import java.util.*;
/**
 * A single node in a Huffman tree, implemented as a record.
 *
 * A HuffmanNode represents either a leaf (an actual character together
 * with its occurrence count) or an internal node created by combining two
 * subtrees during tree construction.
 *
 * Records are immutable and give you charAscii(), freq(), left(), and
 * right() accessor methods automatically, along with
 * equals()/hashCode()/toString(). Do not rename the components below.
 */
public record HuffmanNode(int charAscii, int freq, HuffmanTree left, HuffmanTree right)
        implements HuffmanTree{
//compares this node with another tree based on freq and ascii value
     public boolean comesBefore(HuffmanTree other) {
        // TODO: implement
         if(this.freq < other.freq())
         {
             return true;
         }
         else if(this.freq == other.freq())
         {
             if(this.charAscii < other.charAscii())
             {
                 return true;
             }
         }
        return false;
    }
//combines this node and another tree into a new parent node
    public HuffmanTree combine(HuffmanTree other) {
        // TODO: implement
        HuffmanTree left;
        HuffmanTree right;
        if(this.comesBefore(other))
        {
            left = this;
            right = other;
        }
        else{
            left = other;
            right = this;
        }
        int combinedFreq = this.freq()+ other.freq();
        int combinedChar = Math.min(this.charAscii(), other.charAscii());
        return new HuffmanNode(combinedChar, combinedFreq,left,right);
    }

    /**
     * Natural ordering consistent with comesBefore(), so that HuffmanNode
     * objects can be stored directly in a sorted structure (e.g. a
     * PriorityQueue or a sorted List) while building the tree.
     */
    @Override
    public int compareTo(HuffmanTree other) {
        if (this.comesBefore(other)) return -1;
        if (other.comesBefore(this)) return 1;
        return 0;
    }
}
