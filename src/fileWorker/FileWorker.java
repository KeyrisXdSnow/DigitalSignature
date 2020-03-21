package fileWorker;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FileWorker {

    private File file;

    public FileWorker(String filePath) {
        file = new File(filePath);
    }
    public FileWorker(File file) {
        this.file = file;
    }

    public byte[] readFile() {

        StringBuilder buffer = new StringBuilder();
        int rbyte;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {

            rbyte = reader.read();
            while (-1 != rbyte) {
                buffer.append((char) rbyte);
                rbyte = reader.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffer.toString().getBytes();
    }

    public byte[] readSignFile () {
        String text = new String(readFile());
        String[] split = text.split(" ");
        return text.substring(0, text.lastIndexOf(split[split.length-1])-1).getBytes();
    }

    public ArrayList<Byte> readFileBits(ArrayList<Byte> bitsList) {

        final int bitSize = 8;
        int rbyte;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {

            rbyte = reader.read();
            while (-1 != rbyte) {
                System.out.print(rbyte + " ");
                for (int i = 0; i < bitSize; i++) {
                    bitsList.add((byte) ((rbyte >> (bitSize - i - 1)) & (byte) 0b00000000000000001));
                }
                rbyte = reader.read();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
        return bitsList;
    }

    public BigInteger getSign() {

        String line, oldLine = null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {

            line = reader.readLine();
            while (line != null) {
                oldLine = line;
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (oldLine == null) return BigInteger.ZERO;
        else {
            String[] split = oldLine.split(" ");
            return new BigInteger(split[split.length - 1]);
        }
    }

    public void writeFile(String sign) {

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file,true), StandardCharsets.UTF_8))) {
            writer.write(" " + sign);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
