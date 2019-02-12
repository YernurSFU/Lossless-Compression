/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffmanlzw;

/**
 *
 * @author yernurnursultanov
 */
public class HuffmanNode {

        private final int freq;
        private final byte _char;
        private final boolean isLeafNode;
        private final HuffmanNode leftChild, rightChild;

        public HuffmanNode(int freq, HuffmanNode leftChild, HuffmanNode rightChild) {
            this.freq = freq;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
            this.isLeafNode = false;
            this._char = -1;
        }

        public HuffmanNode(int freq, byte sample) {
            this.freq = freq;
            this.leftChild = null;
            this.rightChild = null;
            this.isLeafNode = true;
            this._char = sample;
        }

        public boolean isLeaf() {
            return isLeafNode;
        }

        public byte getSample() {
            return _char;
        }

        public int getValue() {
            return freq;
        }
        
        public HuffmanNode getLeftChild() {
            return this.leftChild;
        }
        public HuffmanNode getRightChild() {
            return this.rightChild;
        }
        
        
    }