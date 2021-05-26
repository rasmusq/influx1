package com.ralleq.influx.files;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class FileHandler {

    public static File filesDir;
    public static final String readWriteMode = "rw", readMode = "r";

    private static int[] readShortBuffer;
    private static int tempWriteShort;
    private static byte[] readByteBuffer, writeByteBuffer;

    public FileHandler(Context context) {
        filesDir = context.getFilesDir();
    }
    public static void deleteFile(String path) {
        File file = new File(filesDir, path);
        boolean deleted = file.delete();
        if(deleted)
            Log.i("FileHandler", "File at " + path + " was deleted");
        else
            Log.i("FileHandler", "File at " + path + " was NOT deleted");
    }
    public static void deleteFile(File file) {
        boolean deleted = file.delete();
        if(deleted)
            Log.i("FileHandler", "File at " + file.getPath() + " was deleted");
        else
            Log.i("FileHandler", "File at " + file.getPath() + " was NOT deleted");
    }

    private static File writeFile;
    public static void writeFile(String filePath, String[] lines) {
        writeFile = new File(filesDir, filePath);
        try {
            boolean exists = writeFile.exists();
            if(!exists) {
                exists = writeFile.createNewFile();
            }
            if(exists) {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(writeFile)));
                for(String line: lines) {
                    bw.write(line);
                    bw.newLine();
                }
                bw.flush();
                bw.close();
            }
        } catch(IOException e) {
            Log.i(FileHandler.class.getName(), "Could not write file at path: " + filePath);
            e.printStackTrace();
        }
    }
    public static ArrayList<String> readFile(String filePath) {
        File file = new File(filesDir, filePath);
        ArrayList<String> lines = null;
        try {
            boolean exists = file.exists();
            if(!exists) {
                exists = file.createNewFile();
                Log.e(FileHandler.class.getName(),
                        "Created file that did not exist at path: " + filePath);
            }
            if(exists) {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String temp;
                lines = new ArrayList<>();
                while((temp = br.readLine()) != null) {
                    lines.add(temp);
                }
                br.close();
            }
        } catch(IOException e) {
            Log.e(FileHandler.class.getName(), "Could not read file at path: " + filePath);
            e.printStackTrace();
        }
        return lines;
    }
    public long getLengthInShorts(String filePath) {
        File file = new File(filesDir, filePath);
        return (file.length() / 2);
    }

    public static void writeShortArrayToRAF(RandomAccessFile rWriteFile, int[] shorts, long offset) {
        //Converts the shorts to bytes so it can be written
        writeByteBuffer = new byte[shorts.length*2];
        for(int i = 0; i < shorts.length; i++) {
            tempWriteShort = shorts[i];
            if(tempWriteShort > Short.MAX_VALUE)
                tempWriteShort = Short.MAX_VALUE;
            else if(tempWriteShort < Short.MIN_VALUE)
                tempWriteShort = Short.MIN_VALUE;

            writeByteBuffer[i*2] = (byte) (tempWriteShort >>> 8);
            writeByteBuffer[i*2+1] = (byte) (tempWriteShort);
        }
        //Writes the bytes into the RandomAccessFile
        try {
            rWriteFile.seek(Math.max(0, offset*2));
            rWriteFile.write(writeByteBuffer);
        } catch(IOException e) {
            Log.i(FileHandler.class.getName(), "RandomAccessFile writing error");
            e.printStackTrace();
        }
    }
    public static int[] readShortArrayFromRAF(RandomAccessFile rReadFile, long offset, int length) {
        if(rReadFile != null) {
            //Convert position data into bytes
            int lengthInBytes = length*2;
            long offsetInBytes = offset*2;

            if(lengthInBytes > 0) {
                //Writes the bytes from the RandomAccessFile into the byte buffer
                readByteBuffer = new byte[lengthInBytes];
                try {
                    rReadFile.seek(Math.max(0, offsetInBytes));
                    rReadFile.read(readByteBuffer);
                } catch(IOException e) {
                    Log.i(FileHandler.class.getName(), "RandomAccessFile reading error");
                    e.printStackTrace();
                }
                //Converts the bytes into shorts and returns it as an integer array for overhead
                readShortBuffer = new int[length];
                for(int i = 0; i < length; i++) {
                    if(i*2 >= 0 && i*2+1 < readByteBuffer.length) {
                        readShortBuffer[i] = (short) ((readByteBuffer[i * 2] & 0xFF) << 8 | (readByteBuffer[i * 2 + 1] & 0xFF));
                    }
                }
                return readShortBuffer;
            }
        }
        return new int[0];
    }
}
