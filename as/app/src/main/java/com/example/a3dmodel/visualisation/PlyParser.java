
package com.example.a3dmodel.visualisation;

import android.util.Log;

//import androidx.lifecycle.ViewModelProvider;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class PlyParser {
    private final BufferedReader bufferedReader;
    private final int NO_INDEX = 100;
    private int vertexIndex = NO_INDEX;
    private int colorIndex = NO_INDEX;
    private int normalIndex = NO_INDEX;
    private boolean inHeader = true;
    public int currentElement = 0;
    public int currentFace = 0;
    public float[] vertices = null;
    public float[] colors = null;
    public float[] normals = null;
    public int[] faces = null;
    public int vertexSize = 0;
    public int colorSize = 0;
    public int normalSize = 0;
    public int faceSize = 3;
    public float vertexMax = 0;
    public float colorMax = 0;
    public int vertexCount = 0;
    public int faceCount = 0;
    private int elementCount = 0;

    private static final int DOT_FREQUENCY = 2;

    public PlyParser(InputStream plyFile) {
        bufferedReader = new BufferedReader(new InputStreamReader(plyFile));
    }

    boolean ParsePly() throws IOException {
        String line = bufferedReader.readLine();
        if (!line.equals("ply")) {
            Log.e("ReadHeader", "File is not a PLY! Leave us.");
            return false;
        }

        line = bufferedReader.readLine();
        String[] words = line.split(" ");
        if (!words[1].equals("ascii")) {
            Log.e("ReadHeader", "File is not ASCII format! Cannot read.");
            return false;
        }

        line = bufferedReader.readLine();
        while (line != null && inHeader) {
            ReadHeader(line);
            line = bufferedReader.readLine();
        }

        if (vertexSize != 3) {
            Log.e("ParsePly", "Incorrect count of vertices! Expected 3.");
            return false;
        }
        vertices = new float[vertexCount * vertexSize];
        faces = new int[faceCount * faceSize];
        if (colorSize != 0) {
            colors = new float[vertexCount * colorSize];
        }
        if (normalSize != 0) {
            normals = new float[vertexCount * normalSize];
        }
        while (line != null) {
            ReadData(line);
            line = bufferedReader.readLine();
        }
        ScaleData();
        return true;
    }

    void ReadHeader(String line) {
        String[] words = line.split(" ");
        if (words[0].equals("comment")) {
            return;
        }
        if (words[0].equals("element")) {
            if (words[1].equals("vertex")) {
                vertexCount = Integer.parseInt(words[2]);
            } else if (words[1].equals("face")) {
                faceCount = Integer.parseInt(words[2]);
            }
        }
        if (words[0].equals("property")) {
            switch (words[2]) {
                case "x":
                case "y":
                case "z":
                    if (vertexIndex > elementCount) {
                        vertexIndex = elementCount;
                    }
                    vertexSize++;
                    break;
                case "nx":
                case "ny":
                case "nz":
                    if (normalIndex > elementCount) {
                        normalIndex = elementCount;
                    }
                    normalSize++;
                    break;
                case "red":
                case "green":
                case "blue":
                case "alpha":
                    if (colorIndex > elementCount) {
                        colorIndex = elementCount;
                    }
                    colorSize++;
                    break;
            }
            elementCount++;
        }

        if (words[0].equals("end_header")) {
            inHeader = false;
        }
    }

    void ReadData(String line) {
        String[] words = line.split(" ");
        if (currentElement < vertexCount) {
            for (int i = 0; i < vertexSize; i++) {
                vertices[currentElement * vertexSize + i] = Float.parseFloat(words[vertexIndex + i]);
                if (vertexMax < Math.abs(vertices[currentElement * vertexSize + i])) {
                    vertexMax = Math.abs(vertices[currentElement * vertexSize + i]);
                }
            }
            for (int i = 0; i < colorSize; i++) {
                colors[currentElement * colorSize + i] = Float.parseFloat(words[colorIndex + i]);
                if (colorMax < colors[currentElement * colorSize + i]) {
                    colorMax = colors[currentElement * colorSize + i];
                }
            }
            for (int i = 0; i < normalSize; i++) {
                normals[currentElement * normalSize + i] = Float.parseFloat(words[normalIndex + i]);
            }
            currentElement++;
        } else if (currentFace < faceCount) {
            //System.out.println(currentFace);
            for (int i = 0; i < 3; i++) {
                faces[currentFace * faceSize + i] = Integer.parseInt(words[i + 1]);
            }
            currentFace++;
        }
    }

    void ScaleData() {
        for (int i = 0; i < vertexCount * vertexSize; i++) {
            vertices[i] /= vertexMax;
        }
        for (int i = 0; i < vertexCount * colorSize; i++) {
            colors[i] /= colorMax;
        }
    }

    public float[] getVertices() {
        return vertices;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public float[] getColors() {
        return colors;
    }

    public float[] getNormals() {
        return normals;
    }

    public int getFaceCount() {
        return faceCount;
    }

    public int[] getFaces() {
        return faces;
    }

    private static void writeLine(@NonNull BufferedWriter writer, String line) throws IOException {
        writer.write(line);
        writer.newLine();
    }

    private static class Point {
        double x, y, z;
        double nx, ny, nz;
        String red, green, blue, alpha = "255";

        static final List<List<Double>> shifts = new ArrayList<>(8);

        static {
            List<Double> lst0 = new ArrayList<>(3);
            lst0.add(-1.0);
            lst0.add(-1.0);
            lst0.add(-1.0);
            shifts.add(lst0);
            List<Double> lst1 = new ArrayList<>(3);
            lst1.add(-1.0);
            lst1.add(-1.0);
            lst1.add(1.0);
            shifts.add(lst1);
            List<Double> lst2 = new ArrayList<>(3);
            lst2.add(-1.0);
            lst2.add(1.0);
            lst2.add(-1.0);
            shifts.add(lst2);
            List<Double> lst3 = new ArrayList<>(3);
            lst3.add(-1.0);
            lst3.add(1.0);
            lst3.add(1.0);
            shifts.add(lst3);
            List<Double> lst4 = new ArrayList<>(3);
            lst4.add(1.0);
            lst4.add(-1.0);
            lst4.add(-1.0);
            shifts.add(lst4);
            List<Double> lst5 = new ArrayList<>(3);
            lst5.add(1.0);
            lst5.add(-1.0);
            lst5.add(1.0);
            shifts.add(lst5);
            List<Double> lst6 = new ArrayList<>(3);
            lst6.add(1.0);
            lst6.add(1.0);
            lst6.add(-1.0);
            shifts.add(lst6);
            List<Double> lst7 = new ArrayList<>(3);
            lst7.add(1.0);
            lst7.add(1.0);
            lst7.add(1.0);
            shifts.add(lst7);
        }

        static final int scale = 1000;
        static final double delta = 0.0003 * scale;

        Point(String[] features) {
            x = Double.parseDouble(features[0]) * scale;
            y = Double.parseDouble(features[1]) * scale;
            z = Double.parseDouble(features[2]) * scale;
            nx = Double.parseDouble(features[3]);
            nx = 0;
            ny = Double.parseDouble(features[4]);
            ny = 0;
            nz = Double.parseDouble(features[5]);
            nz = 0;
            red = features[6];
            green = features[7];
            blue = features[8];
        }

        private String getFaces(int num) {
            int shift = num * 8;
            return "3 " + shift + " " + shift + " " + shift + "\n" +
                    "3 " + shift + " " + (shift + 1) + " " + (shift + 2) + "\n" +
                    "3 " + (shift + 2) + " " + (shift + 1) + " " + (shift + 3) + "\n" +

                    "3 " + shift + " " + (shift + 2) + " " + (shift + 6) + "\n" +
                    "3 " + shift + " " + (shift + 6) + " " + (shift + 4) + "\n" +

                    "3 " + (shift + 3) + " " + (shift + 1) + " " + (shift + 5) + "\n" +
                    "3 " + (shift + 3) + " " + (shift + 5) + " " + (shift + 7) + "\n" +

                    "3 " + (shift + 2) + " " + (shift + 3) + " " + (shift + 7) + "\n" +
                    "3 " + (shift + 2) + " " + (shift + 7) + " " + (shift + 6) + "\n" +

                    "3 " + (shift + 5) + " " + (shift + 4) + " " + (shift + 6) + "\n" +
                    "3 " + (shift + 5) + " " + (shift + 6) + " " + (shift + 7) + "\n" +

                    "3 " + (shift + 1) + " " + shift + " " + (shift + 4) + "\n" +
                    "3 " + (shift + 1) + " " + (shift + 4) + " " + (shift + 5) + "\n" +
                    "3 " + shift + " " + shift + " " + shift;
        }

        @NonNull
        private String getVertexes() {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                double new_x = Math.round((x + delta * shifts.get(i).get(0)) * 10000);
                new_x /= 10000;
                double new_y = Math.round((y + delta * shifts.get(i).get(1)) * 10000);
                new_y /= 10000;
                double new_z = Math.round((z + delta * shifts.get(i).get(2)) * 10000);
                new_z /= 10000;
                stringBuilder.append(new_x).append(" ")
                        .append(new_y).append(" ")
                        .append(new_z).append(" ")
                        .append(nx).append(" ")
                        .append(ny).append(" ")
                        .append(nz).append(" ")
                        .append(red).append(" ")
                        .append(green).append(" ")
                        .append(blue).append(" ")
                        .append(alpha).append(" ")
                        .append("\n");
            }
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
            return stringBuilder.toString();
        }
    }

    public static void rewritePlyFile(File source, File result) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(source)));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(result)));
        writeLine(writer, reader.readLine()); // ply
        writeLine(writer, reader.readLine()); // format
        String str = reader.readLine();
        String[] words = str.split(" ");
        int vertex_count = Integer.parseInt(words[2]) / DOT_FREQUENCY;
        writeLine(writer, words[0] + ' ' + words[1] + ' ' + vertex_count * 8); //vertex count
        writeLine(writer, reader.readLine()); // x
        writeLine(writer, reader.readLine()); // y
        writeLine(writer, reader.readLine()); // z
        writeLine(writer, reader.readLine()); // nx
        writeLine(writer, reader.readLine()); // ny
        writeLine(writer, reader.readLine()); // nz
        writeLine(writer, reader.readLine().replaceAll("diffuse_", "")); // red
        writeLine(writer, reader.readLine().replaceAll("diffuse_", "")); // green
        writeLine(writer, reader.readLine().replaceAll("diffuse_", "")); // blue
        writeLine(writer, reader.readLine().replaceAll("float quality", "uchar alpha")); // alpha
        writeLine(writer, "element face " + vertex_count * 14);
        writeLine(writer, "property list uchar int vertex_indices");
        writeLine(writer, reader.readLine()); // end
        List<Point> vertexes = new ArrayList<>(vertex_count);
        for (int i = 0; i < vertex_count; i++) {
            for (int j = 0; j < DOT_FREQUENCY; j++) {
                str = reader.readLine();
            }
            words = str.split(" ");
            vertexes.add(new Point(words));
        }
        double x = vertexes.get(0).x;
        double y = vertexes.get(0).y;
        double z = vertexes.get(0).z;

        for (int i = 0; i < vertex_count; i++) {
            vertexes.get(i).x -= x;
            vertexes.get(i).y -= y;
            vertexes.get(i).z -= z;
            writeLine(writer, vertexes.get(i).getVertexes());
        }
        for (int i = 0; i < vertex_count; i++) {
            writeLine(writer, vertexes.get(i).getFaces(i));
        }
        writer.flush();
    }

}
