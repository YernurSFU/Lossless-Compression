package huffmanlzw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.util.Pair;

public final class BitWord {
    
    public final static int BYTE_SIZE = 8;
    public final static int MAX_SIZE = 255;
    private final List<Boolean> vals;

    public BitWord() {
        vals = new ArrayList<>();
    }

    public BitWord(int n) {
        vals = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            vals.add(false);
        }
    }

    public BitWord(byte[] bits) {
        vals = new ArrayList<>();
        add(bits);
    }
    
    public BitWord(BitWord arr){
        vals = arr.vals;
    }

    public BitWord add(boolean bit) {
        vals.add(bit);
        return this;
    }

    public BitWord add(BitWord bits) {
        for (int i = 0; i < bits.size(); i++) {
            add(bits.get(i));
        }
        return this;
    }

    public BitWord add(byte[] bits) {
        for (int i = 0; i < (bits.length * BYTE_SIZE); i++) {
            int posByte = i / BYTE_SIZE;
            int posBit = i % BYTE_SIZE;
            byte valByte = bits[posByte];
            int valInt = valByte >> (BYTE_SIZE - (posBit + 1)) & 0x0001;
            vals.add(valInt != 0);
            
            //System.out.print(""posByte + "  size :" + currentCode.length());
        }
        return this;
    }

    public boolean get(int idx) {
        return vals.get(idx);
    }

    public int size() {
        return vals.size();
    }


    public BitWord clear() {
        vals.clear();
        return this;
    }
    
    public byte[] toByteArray() {
        if (size() == 0) {
            return new byte[0];
        }
        int bytes = this.vals.size()/ BYTE_SIZE;
        byte[] toReturn = new byte[bytes];
        Arrays.fill(toReturn, (byte) 0);
        for (int i = 0; i < vals.size(); i++) {
            byte oldByte = toReturn[i / BYTE_SIZE];
            oldByte = (byte) (((0xFF7F >> i % BYTE_SIZE) & oldByte) & 0x00FF);
            byte newByte = (byte) (((vals.get(i) == true ? 1 : 0) << (BYTE_SIZE - (i % BYTE_SIZE + 1))) | oldByte);
            toReturn[i / BYTE_SIZE] = newByte;
        }
        return toReturn;
    }
    
    public String toByteString() {
        byte[] byteArray = toByteArray();
        String newString = "";
        for (byte b : byteArray) {
            if (!newString.equals("")) {
                newString += " ";
            }
            newString += Integer.toBinaryString(b & MAX_SIZE | MAX_SIZE + 1).substring(1);
        }
        return newString;
    }

    public static BitWord intToBitArray(int val) {
        boolean isNegative = val < 0;
        val = Math.abs(val);
        String valAsString = Integer.toBinaryString(val);
        int zerosToAdd = 6 - (valAsString.length() % 7);
        for (int i = 0; i < zerosToAdd; i++) {
            valAsString = '0' + valAsString;
        }
        
        BitWord toReturn = new BitWord();
        BitWord current = new BitWord(BYTE_SIZE);
        current.vals.set(6, isNegative);
        int currentStringIdx = 0;
        boolean first = true;
        while (true) {
            for (int i = 0; i < (first ? 6 : 7) && currentStringIdx < valAsString.length(); i++) {
                current.vals.set(i, valAsString.charAt(currentStringIdx) == '1');
                currentStringIdx++;
            }
            boolean willEnd = false;
            if (currentStringIdx < valAsString.length()) {
                current.vals.set(7, false); 
            } else {
                current.vals.set(7, true); 
                willEnd = true;
            }
            
            if (first) {
                first = false;
            }
            toReturn.add(current);
            current = new BitWord(BYTE_SIZE);

            if (willEnd) {
                break;
            }
        }
        return new BitWord(toReturn.toByteArray());
    }
  
    
    public static Pair<Integer, Integer> bitArrayToInt(BitWord val, int _idx){
        boolean first = true;
        boolean isNegative = false;
        int toReturn;
        int idx;
        String toReturnAsUnsignedString = "";
        for (idx = _idx; idx < val.size(); idx += BYTE_SIZE) {
            String current = "";
            for (int j = idx; j < (first ? (idx + 6) : (idx + 7)) && j < val.size(); j++) {
                current += val.get(j) ? '1' : '0';
                isNegative = val.get(idx + 6);
            }
            if (val.get(idx + 7)) {
                idx += BYTE_SIZE;
                toReturnAsUnsignedString += current;
                break;
            } else {
                toReturnAsUnsignedString += current;
            }
            if (first) {
                first = false;
            }
        }
        toReturn = Integer.parseInt(toReturnAsUnsignedString, 2);
        if (isNegative) {
            toReturn = toReturn * -1;
        }
        System.out.println("toReturn = "+ toReturn + "| idx = " + idx );
        return new Pair<>(toReturn, idx);
    }
}
