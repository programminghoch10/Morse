package com.JJ.morse;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.karlotoy.perfectune.instance.PerfectTune;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    final String TAG = "main";
    static int speedbar = 240;
    static boolean active = false;
    static Thread worker;
    static boolean flashavailable = false;
    static boolean boxflash = false;
    static boolean boxtone = false;
    static boolean boxbackground = false;
    static ConstraintLayout constraintLayout;
    final Context maincontext = this;
    static boolean flash = false;

    static final int MY_PERMISSIONS_REQUEST_CAMERA = 77;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flashavailable =  getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }
        constraintLayout = (ConstraintLayout)findViewById(R.id.constraintLayout);
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
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress) {
                    case 0: speedbar = 60; break;
                    case 1: speedbar = 120; break;
                    case 2: speedbar = 240; break;
                    case 3: speedbar = 1200; break;
                    default: speedbar = 240; break;
                }
                switch (progress) {
                    case 0: runOnUiThread(new Runnable() {
                                public void run() {
                                    ((CheckBox)findViewById(R.id.checkBoxflash)).setChecked(false);
                                    ((CheckBox)findViewById(R.id.checkBoxflash)).setEnabled(false);
                                }
                            });
                            break;
                    case 1:
                    case 2:
                    case 3: runOnUiThread(new Runnable() {
                                public void run() {
                                    if (flashavailable) {
                                        ((CheckBox)findViewById(R.id.checkBoxflash)).setEnabled(true);
                                    }
                                }
                            });

                            break;
                    default: break;
                }
                ((TextView)findViewById(R.id.speedtext)).setText("Speed: "+speedbar+"ms");
            }
        });
        final Context context = this;
        ((CheckBox)findViewById(R.id.checkBoxbackground)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boxbackground = ((CheckBox)findViewById(R.id.checkBoxbackground)).isChecked();
                if (boxbackground) {
                    constraintLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.dark));
                } else {
                    constraintLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.background));
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    flashavailable = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                    // permission was granted, yay!
                    if (flashavailable && speedbar >= 120) {
                        ((CheckBox)findViewById(R.id.checkBoxflash)).setEnabled(true);
                    }
                } else {
                    flashavailable = false;
                    ((CheckBox)findViewById(R.id.checkBoxflash)).setChecked(false);
                    ((CheckBox)findViewById(R.id.checkBoxflash)).setEnabled(false);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void encodebuttonclick(View view) {
        boxflash = ((CheckBox)findViewById(R.id.checkBoxflash)).isChecked();
        boxtone = ((CheckBox)findViewById(R.id.checkBoxsound)).isChecked();
        boxbackground = ((CheckBox)findViewById(R.id.checkBoxbackground)).isChecked();
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
                boolean result = morse(inputtext.getText().toString(),Integer.valueOf(getString(R.string.frequency)));
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

        if(flashavailable) {
            Thread camworker;
            camworker = new Thread(new Runnable() {
                @Override
                public void run() {
                    Camera camera = null;
                    Camera.Parameters p = null;
                    try {
                        camera = Camera.open();
                        p = camera.getParameters();
                    } catch (Exception e) {}
                    boolean prevflash = flash;
                    while (active) {
                        if (boxflash && flash != prevflash) {
                            if (flash) {
                                prevflash = true;
                                try {
                                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                    camera.setParameters(p);
                                    camera.startPreview();
                                } catch (Exception e) {}
                            } else {
                                prevflash = false;
                                try {
                                    p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                    camera.setParameters(p);
                                    camera.stopPreview();
                                } catch (Exception e) {}
                            }
                        }
                        if (prevflash && !boxflash) {
                            prevflash = false;
                            try {
                                p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                camera.setParameters(p);
                                camera.stopPreview();
                            } catch (Exception e) {}
                        }
                    }
                    if (prevflash) {
                        prevflash = false;
                        try {
                            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                            camera.setParameters(p);
                            camera.stopPreview();
                        } catch (Exception e) {}
                    }
                }
            });
            camworker.start();
        }
        //soundgenerator2 soundgen = new soundgenerator2();
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
                    boxflash = ((CheckBox)findViewById(R.id.checkBoxflash)).isChecked();
                    boxtone = ((CheckBox)findViewById(R.id.checkBoxsound)).isChecked();
                    boxbackground = ((CheckBox)findViewById(R.id.checkBoxbackground)).isChecked();
                    int length = Integer.valueOf(Character.toString(current));
                    if (!active) {return false;}
                    flash = true;
                    if (boxbackground) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                constraintLayout.setBackgroundColor(ContextCompat.getColor(maincontext, R.color.light));
                                ((TextView)findViewById(R.id.currentmorse)).setTextColor(ContextCompat.getColor(maincontext, R.color.darktext));
                                ((EditText)findViewById(R.id.textinput)).setTextColor(ContextCompat.getColor(maincontext, R.color.darktext));
                                ((TextView)findViewById(R.id.currentmorseletter)).setTextColor(ContextCompat.getColor(maincontext, R.color.darktext));
                                ((TextView)findViewById(R.id.progresstext)).setTextColor(ContextCompat.getColor(maincontext, R.color.darktext));
                                ((TextView)findViewById(R.id.speedtext)).setTextColor(ContextCompat.getColor(maincontext, R.color.darktext));
                            }
                        });
                    }
                    if(boxtone) {
                        //using 2 soundengines because of each better performances in different speeds
                        if (speedbar >= 240) {
                            ownsound.tonegen(length*speedbar, frequency);
                        } else {
                            perfectTune.tonegen(length*speedbar,frequency);
                            //soundgen.sonos(length*speedbar,frequency);
                        }
                    } else {
                        try {
                            Thread.sleep(length * speedbar);
                        } catch (InterruptedException e) {}
                    }
                    flash = false;
                    if (boxbackground) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                constraintLayout.setBackgroundColor(ContextCompat.getColor(maincontext, R.color.dark));
                                ((TextView)findViewById(R.id.currentmorse)).setTextColor(ContextCompat.getColor(maincontext, R.color.lighttext));
                                ((EditText)findViewById(R.id.textinput)).setTextColor(ContextCompat.getColor(maincontext, R.color.lighttext));
                                ((TextView)findViewById(R.id.currentmorseletter)).setTextColor(ContextCompat.getColor(maincontext, R.color.lighttext));
                                ((TextView)findViewById(R.id.progresstext)).setTextColor(ContextCompat.getColor(maincontext, R.color.lighttext));
                                ((TextView)findViewById(R.id.speedtext)).setTextColor(ContextCompat.getColor(maincontext, R.color.lighttext));
                            }
                        });
                    }
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

