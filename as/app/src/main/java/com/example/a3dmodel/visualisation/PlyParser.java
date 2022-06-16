/*
  PLYParser: A reader for .ply files
  See the below links for info:
  http://paulbourke.net/dataformats/ply/
  http://stackoverflow.com/questions/6420293/reading-android-raw-text-file
 */

package com.example.a3dmodel.visualisation;

import android.util.Log;

import androidx.lifecycle.ViewModelProvider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PlyParser {
    // Parser mechanisms
    private BufferedReader bufferedReader;
    private final int NO_INDEX = 100;
    private int vertexIndex = NO_INDEX;
    private int colorIndex = NO_INDEX;
    private int normalIndex = NO_INDEX;
    private boolean inHeader = true;
    public int currentElement = 0;
    public int currentFace = 0;
    /* data fields to store points, colors, faces information read from PLY file */
    public float[] vertices = null;
    public float[] colors = null;
    public float[] normals = null;
    public int[] faces = null;
    // Size of an individual element, in floats
    public int vertexSize = 0;
    public int colorSize = 0;
    public int normalSize = 0;
    public int faceSize = 3;
    // Normalizing constants
    public float vertexMax = 0;
    public float colorMax = 0;
    // Number of elements in the entire PLY
    public int vertexCount = 0;
    public int faceCount = 0;
    // Counter for header
    private int elementCount = 0;

    public PlyParser(InputStream plyFile) {
        bufferedReader = new BufferedReader(new InputStreamReader(plyFile));
    }

    boolean ParsePly() throws IOException {
        // Check if this is even a PLY file.
        String line = bufferedReader.readLine();
        if (!line.equals("ply")) {
            Log.e("ReadHeader", "File is not a PLY! Leave us.");
            return false;
        }

        // Check for ASCII format
        line = bufferedReader.readLine();
        String words[] = line.split(" ");
        if (!words[1].equals("ascii")) {
            Log.e("ReadHeader", "File is not ASCII format! Cannot read.");
            return false;
        }

        // Read the header
        line = bufferedReader.readLine();
        while (line != null && inHeader) {
            ReadHeader(line);
            line = bufferedReader.readLine();
        }

        // Populate the data
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
        //line = bufferedReader.readLine();
        while (line != null) {
            ReadData(line);
            line = bufferedReader.readLine();
        }
        //System.out.println(Arrays.toString(faces));
        ScaleData();
        //System.out.println(Arrays.toString(vertices));
        return true;
    }

    void ReadHeader(String line) {
        // Make into a list of words, yo.
        String words[] = line.split(" ");
        if (words[0].equals("comment")) {
            return;
        }
        // Check if element or property
        if (words[0].equals("element")) {
            if (words[1].equals("vertex")) {
                vertexCount = Integer.parseInt(words[2]);
            } else if (words[1].equals("face")) {
                faceCount = Integer.parseInt(words[2]);
            }
        }
        if (words[0].equals("property")) {
            if (words[2].equals("x") ||
                    words[2].equals("y") ||
                    words[2].equals("z")) {
                if (vertexIndex > elementCount) {
                    vertexIndex = elementCount;
                }
                vertexSize++;
            } else if (words[2].equals("nx") ||
                    words[2].equals("ny") ||
                    words[2].equals("nz")) {
                if (normalIndex > elementCount) {
                    normalIndex = elementCount;
                }
                normalSize++;
            } else if (words[2].equals("red") ||
                    words[2].equals("green") ||
                    words[2].equals("blue") ||
                    words[2].equals("alpha")) {
                if (colorIndex > elementCount) {
                    colorIndex = elementCount;
                }
                colorSize++;
            }
            elementCount++;
        }

        if (words[0].equals("end_header")) {
            inHeader = false;
            return;
        }
    }

    void ReadData(String line) {
        String words[] = line.split(" ");
        // Compensate for extra line read with (vertexCount - 1)
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

    // Getters
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

    private static void writeLine(BufferedWriter writer, String line) throws IOException {
        writer.write(line);
        writer.newLine();
    }

    private static class Point {
        double x, y, z;
        double nx, ny, nz;
        String red, green, blue, alpha = "255";
//        static final List<List<Double>> shifts = List.of(
//                List.of(-1.0, -1.0, -1.0), // 0
//                List.of(-1.0, -1.0, 1.0), // 1
//                List.of(-1.0, 1.0, -1.0), // 2
//                List.of(-1.0, 1.0, 1.0), // 3
//                List.of(1.0, -1.0, -1.0), // 4
//                List.of(1.0, -1.0, 1.0), // 5
//                List.of(1.0, 1.0, -1.0), // 6
//                List.of(1.0, 1.0, 1.0)  // 7
//        );
        static final List<List<Double>> shifts = new ArrayList<>(8);
        static {
            List<Double> lst0 = new ArrayList<>(3);
            lst0.add(-1.0); lst0.add(-1.0); lst0.add(-1.0);
            shifts.add(lst0);
            List<Double> lst1 = new ArrayList<>(3);
            lst1.add(-1.0); lst1.add(-1.0); lst1.add(1.0);
            shifts.add(lst1);
            List<Double> lst2 = new ArrayList<>(3);
            lst2.add(-1.0); lst2.add(1.0); lst2.add(-1.0);
            shifts.add(lst2);
            List<Double> lst3 = new ArrayList<>(3);
            lst3.add(-1.0); lst3.add(1.0); lst3.add(1.0);
            shifts.add(lst3);
            List<Double> lst4 = new ArrayList<>(3);
            lst4.add(1.0); lst4.add(-1.0); lst4.add(-1.0);
            shifts.add(lst4);
            List<Double> lst5 = new ArrayList<>(3);
            lst5.add(1.0); lst5.add(-1.0); lst5.add(1.0);
            shifts.add(lst5);
            List<Double> lst6 = new ArrayList<>(3);
            lst6.add(1.0); lst6.add(1.0); lst6.add(-1.0);
            shifts.add(lst6);
            List<Double> lst7 = new ArrayList<>(3);
            lst7.add(1.0); lst7.add(1.0); lst7.add(1.0);
            shifts.add(lst7);
        }

        static final int scale = 1;
        static final double delta = 0.0003 * scale;

        Point(String[] features) {
            x = Double.parseDouble(features[0]) * scale;
            y = Double.parseDouble(features[1]) * scale;
            z = Double.parseDouble(features[2]) * scale;
            nx = Double.parseDouble(features[3]);
            ny = Double.parseDouble(features[4]);
            nz = Double.parseDouble(features[5]);
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

        private String getVertexes() {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                stringBuilder.append((x + delta * shifts.get(i).get(0))).append(" ")
                        .append(y + delta * shifts.get(i).get(1)).append(" ")
                        .append(z + delta * shifts.get(i).get(2)).append(" ")
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
        int vertex_count = Integer.parseInt(words[2]) / 5;
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
            for (int j = 0; j < 5; j++) {
                str = reader.readLine();
            }
            words = str.split(" ");
            vertexes.add(new Point(words));
        }
        double x = vertexes.get(0).x;
        double y = vertexes.get(0).y;
        double z = vertexes.get(0).z;
        System.out.println(x);
        System.out.println(y);
        System.out.println(z);


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
