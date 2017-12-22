package fi.handslife.dumbbfa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ali on 12/21/2017.
 */

public class PutInFile {

    PutInFile(){

    }

    static void eatAndShitOverMyFile(File outFile, String s) throws IOException
    {

//        File envFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
//        File myFile = new File(context.getFilesDir(), outFile);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(outFile, true);
            fileOutputStream.write(s.getBytes());
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null)
                fileOutputStream.close();
        }

    }
}
