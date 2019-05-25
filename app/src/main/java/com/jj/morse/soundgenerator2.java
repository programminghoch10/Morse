package com.jj.morse;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;

public class soundgenerator2 {

    Handler handler = new Handler();

    public void generatesound() {
        double duration = 1;            // seconds
        double freqOfTone = 440;       // hz
        int sampleRate = 8000;          // a number

        double dnumSamples = duration * sampleRate;
        dnumSamples = Math.ceil(dnumSamples);
        int numSamples = (int) dnumSamples;
        double sample[] = new double[numSamples];
        byte generatedSnd[] = new byte[2 * numSamples];


        for (int i = 0; i < numSamples; ++i) {    // Fill the sample array
            sample[i] = Math.sin(freqOfTone * 2 * Math.PI * i / (sampleRate));
        }

            // convert to 16 bit pcm sound array
            // assumes the sample buffer is normalized.
            // convert to 16 bit pcm sound array
            // assumes the sample buffer is normalised.
        int idx = 0;
        int i = 0 ;

        int ramp = numSamples / 20 ;                                     // Amplitude ramp as a percent of sample count


        for (i = 0; i< ramp; ++i) {                                      // Ramp amplitude up (to avoid clicks)
            double dVal = sample[i];
            // Ramp up to maximum
            final short val = (short) ((dVal * 32767 * i/ramp));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }


        for (i = i; i< numSamples - ramp; ++i) {                         // Max amplitude for most of the samples
            double dVal = sample[i];
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        for (i = i; i< numSamples; ++i) {                                // Ramp amplitude down
            double dVal = sample[i];
            // Ramp down to zero
            final short val = (short) ((dVal * 32767 * (numSamples-i)/ramp ));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        AudioTrack audioTrack = null;                                    // Get audio track
        try {
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, (int)numSamples*2,
                    AudioTrack.MODE_STATIC);
            audioTrack.write(generatedSnd, 0, generatedSnd.length);        // Load the track
            audioTrack.play();                                             // Play the track
        }
        catch (Exception e){
            //RunTimeError("Error: " + e);
            //return false;
        }

        int x =0;
        do{                                                              // Monitor playback to find when done
            if (audioTrack != null)
                x = audioTrack.getPlaybackHeadPosition();
            else
                x = numSamples;
        } while (x<numSamples);

        if (audioTrack != null) audioTrack.release();                    // Track play done. Release track.
    }

    public void sonos() {
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {

                    public void run() {
                        generatesound();
                    }
                });
            }
        });
        thread.start();
    }
}
