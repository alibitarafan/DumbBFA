package fi.handslife.dumbbfa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static fi.handslife.dumbbfa.FetchFile.spitMeFile;
import static fi.handslife.dumbbfa.PutInFile.eatAndShitOverMyFile;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {
    final String TAG = "DumbBFA TAG";
    Handler mHandler;
    Handler handlerDigger;

    TextView    outPutResult;
    EditText    newStringBox, fileNameBox;
    Button      enterBtn, fileBtn;

    File    myFile;

    List<String>    dataListFetched = null;
    List<String>    dataListAdded   = null;
    final String searchingDataTag   = "diggingResult";
    final String searchingWordTag   = "searchingWord";

    class dataDigger implements Runnable {

        @Override
        public void run() {
            Looper.prepare();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Digger is prepared to dig!",
                            Toast.LENGTH_SHORT).show();
                }
            });
            handlerDigger = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    String searchMe = msg.obj.toString();
                    Log.d(TAG, "handleMessage: " + searchMe);
                    Message result = Message.obtain();
                    Bundle bundle = new Bundle();
                    Boolean digginResult = false;
                    if (dataListFetched.contains(searchMe)) { digginResult = true; }
                    if (digginResult != true){
                        if (dataListAdded.contains(searchMe)){ digginResult = true; }
                    }
                    bundle.putBoolean(searchingDataTag, digginResult);
                    bundle.putString(searchingWordTag, searchMe);
                    result.setData(bundle);
                    mHandler.sendMessage(result);

                }
            };

            Looper.loop();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        outPutResult    = findViewById(R.id.outPutResult);
        newStringBox    = findViewById(R.id.newStringBox);
        fileNameBox     = findViewById(R.id.fileNameBox);
        fileNameBox.setHint(R.string.myFileName);
        enterBtn        = findViewById(R.id.enterBtn);
        fileBtn         = findViewById(R.id.fileBtn);
        fileBtn.setOnClickListener(this);
        myFile = new File(getFilesDir(), getString(R.string.myFileName));
        dataListFetched = new ArrayList<>();
        fetchData();


        enterBtn.setOnClickListener(this);
        dataListAdded = new ArrayList<>();

        new Thread(new dataDigger()).start();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundleResult = msg.getData();
                boolean result = bundleResult.getBoolean(searchingDataTag);
                String searchedWord = bundleResult.getString(searchingWordTag);
                if (result != true) showDialog(searchedWord);
                else Toast.makeText(MainActivity.this, "Tested", Toast.LENGTH_SHORT).show();
            }
        };

//        try { data = spitMeFile(myFile); }
//        catch (IOException e) { e.printStackTrace(); }
//        outPutResult.setText(data);

    }

    private void fetchData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String rcvdString = null;
                try { rcvdString = spitMeFile(myFile); }
                catch (IOException e) { e.printStackTrace(); }
                if (rcvdString != null) {
                    String[] data = rcvdString.split(getString(R.string.seperator));
                    Collections.addAll(dataListFetched, data);
                    Log.d(TAG, "Fetched Data size: " + dataListFetched.size());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            outPutResult.setText(getString(R.string.dataFetchedMsg) + " " +
                                    myFile.getName() + " size: " + dataListFetched.size() + "\n");
                        }
                    });
                }
            }
        }).start();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.enterBtn:
                String justEntered = newStringBox.getText().toString();
                if (justEntered.length() > 0 ) {
                    Message msgToDigger = Message.obtain();
                    msgToDigger.obj = justEntered;
                    handlerDigger.sendMessage(msgToDigger);
                    Log.d(TAG, "onClick: " + justEntered + " sent for processing.");
                }
                newStringBox.setText("");
            break;

            case R.id.fileBtn:
                String newFile = fileNameBox.getText().toString();
                if (newFile.length() > 0){
                    myFile = new File(getFilesDir(), newFile);
                    fetchData();
                    fileNameBox.setHint(newFile);
                    outPutResult.append(getString(R.string.fileChangedMsg) + " " + myFile.getName() + "\n");
                }
        }
    }

    void showDialog(String s){
        final String justEntered = s;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Result")
                .setMessage("Did " + s + " worked?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The string worked
                        outPutResult.setText("Congrats " + justEntered + " Worked.\n");
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataListAdded.add(justEntered);
                        outPutResult.append("No worries " + justEntered + " didn't work. Move on.\n");
                    }
                }).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dataListAdded.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String s : dataListAdded)
                sb.append(s + getString(R.string.seperator));

            try {
                eatAndShitOverMyFile(myFile, sb.toString());
                Log.d(TAG, "onDestroy: added " + dataListAdded.size() + " to " + myFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataListAdded.clear();
    }
}
