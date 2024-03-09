mport java.io.*;
import java.util.PriorityQueue;
import java.util.HashMap;

class HuffmanNode implements Comparable<HuffmanNode> {
    int data;
    char c;
    HuffmanNode left, right;

    public HuffmanNode(char c, int data) {
        this.c = c;
        this.data = data;
    }

    public HuffmanNode(char c, int data, HuffmanNode left, HuffmanNode right) {
        this.c = c;
        this.data = data;
        this.left = left;
        this.right = right;
    }

    @Override
    public int compareTo(HuffmanNode node) {
        return this.data - node.data;
    }
}

public class HuffmanEncoder {

    public static void compressFile(String inputFilePath, String outputFilePath) {
        try {
            FileInputStream fis = new FileInputStream(inputFilePath);
            FileOutputStream fos = new FileOutputStream(outputFilePath);
            HashMap<Character, Integer> frequencyMap = new HashMap<>();

            int data;
            while ((data = fis.read()) != -1) {
                char c = (char) data;
                frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
            }

            PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
            for (char c : frequencyMap.keySet()) {
                pq.add(new HuffmanNode(c, frequencyMap.get(c)));
            }

            while (pq.size() > 1) {
                HuffmanNode left = pq.poll();
                HuffmanNode right = pq.poll();
                pq.add(new HuffmanNode('\0', left.data + right.data, left, right));
            }

            HuffmanNode root = pq.peek();
            HashMap<Character, String> huffmanCodes = new HashMap<>();
            generateCodes(root, "", huffmanCodes);

            StringBuilder encodedData = new StringBuilder();
            fis.close();
            fis = new FileInputStream(inputFilePath);
            while ((data = fis.read()) != -1) {
                encodedData.append(huffmanCodes.get((char)data));
            }

            writeCompressedData(encodedData.toString(), huffmanCodes, fos);

            fis.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateCodes(HuffmanNode node, String code, HashMap<Character, String> huffmanCodes) {
        if (node == null)
            return;

        if (node.c != '\0') {
            huffmanCodes.put(node.c, code);
        }

        generateCodes(node.left, code + "0", huffmanCodes);
        generateCodes(node.right, code + "1", huffmanCodes);
    }

    private static void writeCompressedData(String encodedData, HashMap<Character, String> huffmanCodes, FileOutputStream fos) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < encodedData.length(); i++) {
            sb.append(encodedData.charAt(i));
            for (char c : huffmanCodes.keySet()) {
                if (huffmanCodes.get(c).equals(sb.toString())) {
                    fos.write(c);
                    sb.setLength(0);
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        String inputFilePath = "input.txt";
        String outputFilePath = "output.bin";
        
        compressFile(inputFilePath, outputFilePath);
        
        System.out.println("File compressed successfully!");
    }
}