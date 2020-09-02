package com.JJ.morse;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Looper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    static boolean flashavailable = false;
    static boolean boxflash = false;
    static boolean boxtone = false;
    static boolean boxbackground = false;
    static ConstraintLayout constraintLayout;
    final Context maincontext = this;
    static boolean flash = false;
    static boolean flashready = false;
    
    static final int PERMISSION_REQUEST_CAMERA = 77;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flashavailable =  getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
		((CheckBox)findViewById(R.id.checkBoxflash)).setEnabled(flashavailable);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_CAMERA);
            }
        }
        constraintLayout = (ConstraintLayout)findViewById(R.id.constraintLayout);
        ((SeekBar) findViewById(R.id.speedbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress) {
                    case 0: speedbar = 60; break;
                    case 1: speedbar = 120; break;
                    default:
                    case 2: speedbar = 240; break;
                    case 3: speedbar = 1200; break;
                }
                switch (progress) {
                    case 0:
                    	runOnUiThread(new Runnable() {
							public void run() {
								((CheckBox)findViewById(R.id.checkBoxflash)).setChecked(false);
								((CheckBox)findViewById(R.id.checkBoxflash)).setEnabled(false);
							}
						});
                        break;
                    case 1:
                    case 2:
                    case 3:
                    	runOnUiThread(new Runnable() {
							public void run() {
								if (flashavailable) {
									((CheckBox)findViewById(R.id.checkBoxflash)).setEnabled(true);
								}
							}
						});
                        break;
                    default: break;
                }
                ((TextView)findViewById(R.id.speedtext)).setText(getResources().getString(R.string.speedtext)+speedbar+getResources().getString(R.string.speedtextms));
            }
        });
        final Context context = this;
        ((CheckBox)findViewById(R.id.checkBoxbackground)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boxbackground = ((CheckBox)findViewById(R.id.checkBoxbackground)).isChecked();
                if (boxbackground) {
                    design_black();
                } else {
                    design_normal();
                }
            }
        });
       ((EditText)findViewById(R.id.frequency)).setText(String.valueOf(getResources().getInteger(R.integer.defaultfrequency)));
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA: {
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
            ((Button)findViewById(R.id.button)).setText(getResources().getString(R.string.buttonstopping));
            return;
        }
        final EditText inputtext = (EditText)findViewById(R.id.textinput);
        final EditText frequencyinput = (EditText) findViewById(R.id.frequency);
        
        worker = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                active = true;
                boolean result = morse(inputtext.getText().toString(), Integer.parseInt(frequencyinput.getText().toString()));
                if (result) {
                    updateinfo(100,getResources().getString(R.string.titletext),getResources().getString(R.string.titlefinished),getResources().getString(R.string.buttonstart));
                    active = false;
                } else {
                    updateinfo(0,getResources().getString(R.string.titletext),getResources().getString(R.string.titlecancelled),getResources().getString(R.string.buttonstart));
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
                ((TextView)findViewById(R.id.progresstext)).setText(progress + getResources().getString(R.string.percent));
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
        flashready = !flashavailable;
        
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
                    } catch (Exception e) {
                    	flashready = true;
					}
                    boolean prevflash = flash;
                    flashready = true;
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
		while (!flashready) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ignored) {}
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
                        design_white();
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
                        design_black();
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
    
    int[] designchangeids = {
            R.id.currentmorse,
            R.id.textinput,
            R.id.currentmorseletter,
            R.id.progresstext,
            R.id.speedtext,
            R.id.frequency,
            R.id.frequencytext,
    };
    
    void design_normal() {
        apply_design(R.color.normaltext, R.color.normalbackground);
    }
    
    void design_white() {
        apply_design(R.color.darktext, R.color.lightbackground);
    }
    
    void design_black() {
    apply_design(R.color.lighttext, R.color.darkbackground);
    }
    
    void apply_design(final int colorid, final int backgroundcolorid) {
        runOnUiThread(new Runnable() {
            public void run() {
                constraintLayout.setBackgroundColor(ContextCompat.getColor(maincontext, backgroundcolorid));
                for (int item : designchangeids) {
                    try {
                        ((TextView)findViewById(item)).setTextColor(ContextCompat.getColor(maincontext, colorid));
                    } catch (Exception ignored) {}
                    try {
                        ((EditText)findViewById(item)).setTextColor(ContextCompat.getColor(maincontext, colorid));
                    } catch (Exception ignored) {}
                }
            }
        });
    }
    
}

