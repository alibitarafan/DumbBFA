package fi.handslife.dumbbfa;

import android.content.Context;
import android.os.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Handler;

/**
 * Created by ali on 12/21/2017.
 */

public class FetchFile {

    FetchFile(){
    }

    static String spitMeFile(File inFile) throws IOException {
        FileInputStream fileInputStream = null;
        StringBuffer    stringBuffer    = new StringBuffer();
        try{
            fileInputStream = new FileInputStream(inFile);
            int read = -1;
            while((read = fileInputStream.read()) != -1){
                stringBuffer.append((char)read);
            }
            return stringBuffer.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            if (fileInputStream != null)
                fileInputStream.close();
        }
        return null;
    }

}
