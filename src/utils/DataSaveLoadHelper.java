package utils;

import java.io.*;

/**
 * Created by Novemser on 6/7/2016.
 */
public class DataSaveLoadHelper {

    /**
     * Write object to file.
     *
     * @param obj      the obj
     * @param fileName the file name
     */
    public static void writeObjectToFile(Object obj, String fileName) {
        File file = new File(fileName);
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(obj);
            objOut.flush();
            objOut.close();
//            System.out.println("write object success!");
        } catch (IOException e) {
//            System.out.println("write object failed");
            e.printStackTrace();
        }
    }

    /**
     * Read object from file object.
     *
     * @param fileName the file name
     * @return the object
     */
    public static Object readObjectFromFile(String fileName) {
        Object temp = null;
        File file = new File(fileName);
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            ObjectInputStream objIn = new ObjectInputStream(in);
            temp = objIn.readObject();
            objIn.close();
//            System.out.println("read object success!");
        } catch (IOException | ClassNotFoundException e) {
//            System.out.println("read object failed");
            e.printStackTrace();
        }
        return temp;
    }
}
