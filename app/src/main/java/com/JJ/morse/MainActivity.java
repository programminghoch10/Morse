package com.JJ.morse;

import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    final String TAG = "main";
    static int speedbar = 240;
    static boolean active = false;
    static Thread worker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((SeekBar) findViewById(R.id.speedbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub
                switch (progress) {
                    case 0: speedbar = 60; break;
                    case 1: speedbar = 120; break;
                    case 2: speedbar = 240; break;
                    case 3: speedbar = 1200; break;
                    default: speedbar = 240; break;
                }
                ((TextView)findViewById(R.id.speedtext)).setText("Speed: "+speedbar+"ms");
            }
        });
    }


    public void encodebuttonclick(View view) {
        if (active) {
            active = false;
            ((Button)findViewById(R.id.button)).setText("stopping...");
            return;
        }
        final EditText inputtext = (EditText)findViewById(R.id.textinput);
        worker = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                active = true;
                boolean result = morse(inputtext.getText().toString(),440);
                if (result) {
                    updateinfo(100,"MORSE","made by JJ","Morse!");
                    active = false;
                } else {
                    updateinfo(0,"MORSE","cancelled","Morse!");
                }
            }
        });
        worker.start();
    }
    public void updateinfo(final int progress, final String currentmorsetext, final String currentmorse, final String button) {
        runOnUiThread(new Runnable() {
            public void run() {
                ((TextView)findViewById(R.id.currentmorseletter)).setText(currentmorsetext);
                ((ProgressBar)findViewById(R.id.morseprogress)).setProgress(progress);
                ((TextView)findViewById(R.id.progresstext)).setText(progress + " %");
                ((TextView)findViewById(R.id.currentmorse)).setText(currentmorse);
                ((Button)findViewById(R.id.button)).setText(button);
            }
        });

    }



    private Map<String, Integer> morse = new HashMap<String, Integer>() {
        {
            put("A", 13);
            put("B", 3111);
            put("C", 3131);
            put("D", 311);
            put("E", 1);
            put("F", 1131);
            put("G", 331);
            put("H", 1111);
            put("I", 11);
            put("J", 1333);
            put("K", 313);
            put("L", 1311);
            put("M", 33);
            put("N", 31);
            put("O", 333);
            put("P", 1331);
            put("Q", 3313);
            put("R", 131);
            put("S", 111);
            put("T", 3);
            put("U", 113);
            put("V", 1113);
            put("W", 133);
            put("X", 3113);
            put("Y", 3133);
            put("Z", 3311);
            put("1", 13333);
            put("2", 11333);
            put("3", 11133);
            put("4", 11113);
            put("5", 11111);
            put("6", 31111);
            put("7", 33111);
            put("8", 33311);
            put("9", 33331);
            put("0", 33333);
            put("À", 13313);
            put("Å", 13313);
            put("Ä", 1313);
            put("È", 13113);
            put("É", 11311);
            put("Ö", 3331);
            put("Ü", 1133);
            put("ß", 1113311);
            put("CH", 3333);
            put("Ñ", 33133);
            put(".", 131313);
            put(",", 331133);
            put(":", 333111);
            put(";", 313131);
            put("?", 113311);
            put("-", 311113);
            put("_", 113313);
            put("(", 31331);
            put(")", 313313);
            put("'", 133331);
            put("=", 31113);
            put("+", 13131);
            put("/", 31131);
            put("@", 133131);
            put("KA", 31313);
            put("BT", 31113);
            put("AR", 13131);
            put("VE", 11131);
            put("SK", 111313);
            put("SOS", 111333111);
            put("HH", 11111111);
            put("PAUSE", 1);
            put("LETTER", 3);
            put("WORD", 7);
        }
    };

    public boolean morse(String cleartext, int frequency) {
        int counter = 1;
        soundgenerator2 soundgen = new soundgenerator2();
        for (final char character: cleartext.toUpperCase().toCharArray()) {
            if (Character.toString(character).equals(" ") || morse.get(Character.toString(character)) == null) {
                updateinfo(counter*100/(cleartext.length()+1), "space","","stop");
                try {Thread.sleep(morse.get("WORD")* speedbar);} catch (InterruptedException e) {e.printStackTrace();}
            } else {
                String currentmorse = "";
                for (char current : String.valueOf(morse.get(Character.toString(character))).toCharArray()) {
                    int length = Integer.valueOf(Character.toString(current));
                    if (length > 1) {currentmorse += "-";} else { currentmorse += ".";}
                    currentmorse += " ";
                }
                updateinfo(counter*100/(cleartext.length()+1),Character.toString(character),currentmorse,"stop");
                for (char current : String.valueOf(morse.get(Character.toString(character))).toCharArray()) {
                    int length = Integer.valueOf(Character.toString(current));
                    if (!active) {return false;}
                    //perfectTune.tonegen(length*speedbar,frequency);
                    ownsound.tonegen(length*speedbar,frequency);
                    //soundgen.sonos(length*speedbar,frequency);
                    try {Thread.sleep(morse.get("PAUSE")* speedbar);} catch (InterruptedException e) {e.printStackTrace();}
                }
            }
            if (!active) {return false;}
            //updateinfo(counter*100/(cleartext.length()+1),"","pause","stop");
            //updateinfo(counter*100/(cleartext.length()+1),"","","stop");
            try {Thread.sleep(morse.get("LETTER")* speedbar);} catch (InterruptedException e) {e.printStackTrace();}
            counter += 1;
        }
        return true;
    }
}

