package com.JJ.morse;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

//Thanks to https://github.com/karlotoy/perfectTune

class ownsound {

    static void tonegen(int millis, int freq) {
        ownsound soundgen = new ownsound();
        soundgen.setTuneFreq(freq);
        soundgen.playTune();
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        soundgen.stopTune();
    }

    private TuneThread tuneThread;
    private double tuneFreq = 0f;

    ownsound playTune(){
        if(tuneThread == null){
            tuneThread = new TuneThread();
            tuneThread.setTuneFreq(tuneFreq);
            tuneThread.start();
        }
        return this;
    }

    void setTuneFreq(double tuneFreq) {
        this.tuneFreq = tuneFreq;
        if(tuneThread != null){
            tuneThread.setTuneFreq(tuneFreq);
        }
    }

    double getTuneFreq() {
        return tuneFreq;
    }

    void stopTune(){
        if(tuneThread != null){
            tuneThread.stopTune();
            tuneThread = null;
        }
    }

}

class TuneThread extends Thread {

    private boolean isRunning;
    private int sampleRate = 44100;
    private double tuneFreq = 440;

    @Override
    public void run() {
        super.run();
        isRunning = true;
        int buffsize = AudioTrack.getMinBufferSize(sampleRate,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        // create an audiotrack object
        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, buffsize,
                AudioTrack.MODE_STREAM);
    
        short[] samples = new short[buffsize];
        int amp = 10000;
        double twopi = 8. * Math.atan(1.);
        double ph = 0.0;

        audioTrack.play();
        // start audio
        double fr = tuneFreq;

        boolean testmode = false;

        if (MainActivity.speedbar >= 120 || testmode) {
            double ramp = (double) buffsize / 4;
            for (int i = 0; i < buffsize / 4; i++) {
                samples[i] = (short) (amp * Math.sin(ph) * i / ramp);
                ph += twopi * fr / sampleRate;
            }
            audioTrack.write(samples, 0, buffsize / 4);
        }
        // synthesis loop
        while (isRunning) {
            //double fr = tuneFreq;
            for (int i = 0; i < buffsize; i++) {
                samples[i] = (short) (amp * Math.sin(ph));
                ph += twopi * fr / sampleRate;
            }
            audioTrack.write(samples, 0, buffsize);
        }
        if (MainActivity.speedbar >= 240 || testmode) {
            //audioTrack.play();
            buffsize = buffsize / 4;
            double ramp = buffsize;
            fr = tuneFreq;
            for (int i = 0; i < buffsize; i++) {
                samples[i] = (short) (amp * Math.sin(ph) * (buffsize - i) / ramp);
                ph += twopi * fr / sampleRate;
            }
            audioTrack.write(samples, 0, buffsize);
            int x = audioTrack.getPlaybackHeadPosition();
            /*do{                                                              // Monitor playback to find when done
                x = audioTrack.getPlaybackHeadPosition();
                Log.i("soundendtester", "run: got repeated!");
            } while (x<buffsize);*/
            //noinspection StatementWithEmptyBody
            while (audioTrack.getPlaybackHeadPosition() < x + buffsize / 4);
                //Log.i("audiotester", "run: playback not stopped yet " + audioTrack.getPlaybackHeadPosition() + "  " + (x + buffsize / 4));
        }
        audioTrack.stop();
        audioTrack.release();
    }

    public double getTuneFreq() {
        return tuneFreq;
    }

    void setTuneFreq(double tuneFreq) {
        this.tuneFreq = tuneFreq;
    }

    public boolean isRunning() {
        return isRunning;
    }

    void stopTune() {
        isRunning = false;

        /*try {
            this.join();
            this.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }
}