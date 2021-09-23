package at.ac.univie.lumi.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import at.ac.univie.lumi.model.MapMarker;

/**
 * Created by phili on 6/4/2017.
 *
 * This class is used to serialize different classes and objects to use the app offline and to save time to redundant create again the same staff.
 * Moreover, the splashloading activity save the most files and the other classes mostly only read the files.
 */

public class DAOSerialisationManager {

    //private static final String fileName="savepointProfile.ser";

    /**
     * Saves a serializable object.
     *
     * @param objectToSave The object to save. As T object can it be every object it wants.
     * @param fileName The name of the file.
     */

    public static <T extends Serializable> void saveSerializedFile(T objectToSave, String fileName) { //Parameter String fileName Context context,
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(objectToSave);

            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * Loads a serializable object.
     *
     * @param fileName The filename of the loading .ser file.
     * @return the serializable object.
     */

    public static<T extends Serializable> T readSerializedFile(String fileName) {//Parameter String fileName Context context,
        T objectToReturn = null;

        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            //ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            objectToReturn = (T) objectInputStream.readObject();

            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return objectToReturn;
    }

    /**
     * Removes a specified file.
     *
     * @param fileName The name of the file to remove.
     */

    public static void removeSerializedFile(String fileName) { //Parameter: Context context, String filename
        //context.deleteFile(fileName);
        File toDelete = new File(fileName);
        if (toDelete.exists()) {
            toDelete.delete();
        }
    }

}
