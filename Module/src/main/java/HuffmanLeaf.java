public record HuffmanLeaf (int freq, char charASCII) implements HuffmanTree{
    //same thing with huffmannode. compares ascaii value and freq
    @Override
    public boolean comesBefore(HuffmanTree other) {
        if (this.freq() != other.freq()) {
            return this.freq() < other.freq();
        }
        return this.charAscii() < other.charAscii();
    }
    //retruns the int ascii value of the char in this leaf
    public int charAscii() {
        return charASCII;
    }
    //combines this leaf and another tree into a new parent node
    @Override
    public HuffmanTree combine(HuffmanTree other) {
        // unimplemented...
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

    @Override
    public int compareTo(HuffmanTree other) {
        // unimplemented ...
        if (this.comesBefore(other)) return -1;
        if (other.comesBefore(this)) return 1;
        return 0;
    }
}
