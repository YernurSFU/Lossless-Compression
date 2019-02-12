/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffmanlzw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javafx.util.Pair;

/**
 *
 * @author yernurnursultanov
 */
public class HuffmanCompressor {

    private final List<Byte> buffer;

    public HuffmanCompressor(byte[] data) throws IOException {
        buffer = new ArrayList<>();
        byte[] bytes = data;
        for (byte bt : bytes) {
            this.buffer.add(bt);
        }
    }

    public List<Byte> getSamples() {
        return buffer;
    }

    public void addSample(byte data) {
        buffer.add(data);
    }

    public void addSample(List<Byte> data) {
        data.forEach((d) -> {
            buffer.add(d);
        });
    }

    private Map<Byte, Integer> getCounts() {
        Map<Byte, Integer> counts = new HashMap<>();
        buffer.forEach((chr) -> {
        if (!counts.containsKey(chr)) {
            counts.put(chr, 1);
            } else {
            counts.put(chr, counts.get(chr) + 1);
            }
        });
        return counts;
    }

    private Pair<List<HuffmanNode>, Set<Byte>> getNodeList() {
        Map<Byte, Integer> counts = getCounts();
        List<HuffmanNode> initialNodes = new ArrayList<>();
        counts.keySet().forEach((key) -> {
            initialNodes.add(new HuffmanNode(
                    counts.get(key) / buffer.size(),
                    key)
            );
        });
        return new Pair<>(initialNodes, getCounts().keySet());
    }

    private HuffmanNode getNodeTree() {
        List<HuffmanNode> topNodes = getNodeList().getKey();
        topNodes.sort(Comparator.comparing(HuffmanNode::getValue));
        while (topNodes.size() > 1) {
            HuffmanNode left = topNodes.get(0);
            HuffmanNode right = topNodes.get(1);
            topNodes.remove(left);
            topNodes.remove(right);
            topNodes.add(new HuffmanNode(left.getValue() + right.getValue(), left, right));
            topNodes.sort(Comparator.comparing(HuffmanNode::getValue));
        }
        return topNodes.get(0);
    }

    public byte[] compress() {
        Map<Byte, String> codes = getCodes(new Pair<>(getNodeTree(), getNodeList().getValue()));
        return convertToBytes(new Pair<>(getFinalCode(codes), codes));
    }

    String getCode(int sample, String currentCode, HuffmanNode currentNode) {
        String leftCode;
        if (currentNode.isLeaf() && sample == currentNode.getSample()) {
            return currentCode;
        } else if (currentNode.isLeaf()) {
            return null;
        }
        if (null != (leftCode = getCode(sample, currentCode + '1', currentNode.getLeftChild()))) {
            return leftCode;
        }
        return getCode(sample, currentCode + '0', currentNode.getRightChild());
    }

    private Map<Byte, String> getCodes(Pair<HuffmanNode, Set<Byte>> huffmanData) {
        Map<Byte, String> ret;
        HuffmanNode rootNode = huffmanData.getKey();
        Set<Byte> samples = huffmanData.getValue();
        ret = new ConcurrentHashMap<>();
        samples.forEach((sample) -> {
            ret.put(sample, getCode(sample, "", rootNode));

        });
        return ret;
    }

    private String getFinalCode(Map<Byte, String> codes) {
        String finalCode = "";
        for (Byte sample : buffer) {
            finalCode = finalCode.concat(codes.get(sample));
        }
        return finalCode;
    }

    BitWord constructDictionaryData(Map<Byte, String> dic) {
        BitWord ret = new BitWord();

        ret.add(BitWord.intToBitArray(dic.size()));
        dic.entrySet().stream().map((entrySet) -> {
            ret.add(BitWord.intToBitArray((int) entrySet.getKey()));
            return entrySet;
        }).map((entrySet) -> {
            ret.add(BitWord.intToBitArray(Integer.parseInt(entrySet.getValue(), 2)));
            return entrySet;
        }).forEachOrdered((entrySet) -> {
            String converted = Integer.toBinaryString(Integer.parseInt(entrySet.getValue(), 2));
            ret.add(BitWord.intToBitArray(entrySet.getValue().length() - converted.length()));
        });
        return ret;
    }

    BitWord loadDataBits(String data) {
        BitWord ret = new BitWord(BitWord.intToBitArray(data.length()));
        for (char ch : data.toCharArray()) {
            ret.add((ch == '1'));
        }
        return ret;
    }

    private byte[] convertToBytes(Pair<String, Map<Byte, String>> compressionResults) {
        Map<Byte, String> asd = new HashMap<>(compressionResults.getValue());
        BitWord arr = loadDataBits(compressionResults.getKey());
        return constructDictionaryData(compressionResults.getValue()).add(arr).toByteArray();
    }
}
