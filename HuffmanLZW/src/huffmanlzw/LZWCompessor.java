/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffmanlzw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 *
 * @author yernurnursultanov
 */
public class LZWCompessor {
    private final int ASCII_SIZE = 256;
    private final List<Byte> buffer;
    
    public LZWCompessor(byte[] data) throws IOException {
        buffer = new ArrayList<>();
        for (byte chr : data) {
            this.buffer.add(chr);
        }
    }
     public List<Byte> getSamples() {
        return buffer;
    }

    public void addSample(byte data) {
        buffer.add(data);
    }

    public void addSample(List<Byte> data) {
        data.forEach((chr) -> {
            buffer.add(chr);
        });
    }
    public Map<String, Integer> getInitialDictionary() {
        Map<String, Integer> toReturn = new ConcurrentHashMap<>();
        IntStream.range(0, ASCII_SIZE).forEachOrdered(n -> {
            toReturn.put(Integer.toString(n),  n);
        });
        return toReturn;
    }
    


    public byte[] compress() {
        Map<String, Integer> codeWordsDictionary = getInitialDictionary();
        String p = "";
        BitWord finalOutput = new BitWord();
        for (int i = 0; i < buffer.size(); i++) {
            String c = Integer.toUnsignedString(buffer.get(i) & 0xFF);
            String currentCodeword;
            if (!("".equals(p))) {
                currentCodeword = p + "|" + c;
            } else {
                currentCodeword = c;
            }
            if (codeWordsDictionary.containsKey(currentCodeword)) {
                p = currentCodeword;
            } else {
                finalOutput.add(BitWord.intToBitArray(codeWordsDictionary.get(p)));
                codeWordsDictionary.put(currentCodeword, (int) codeWordsDictionary.size());
                p = c;
            }
        }
        if (!buffer.isEmpty()) {
            finalOutput.add(BitWord.intToBitArray(codeWordsDictionary.get(p)));
        }
        return finalOutput.toByteArray();
    }

}
