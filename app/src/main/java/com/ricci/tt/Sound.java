package com.ricci.tt;

import android.media.MediaPlayer;

public class Sound {
    public static MediaPlayer media;

    public Sound(){}

    public static void startSound(int id){
        switch(id){
            case 1:
                //Se l'audio è settato su true, carica la musica e la mette in loop
                if(Constants.MUSIC_FLAG)
                    {
                        media = MediaPlayer.create(Constants.CURRENT_CONTEXT, R.raw.menu_music);
                        media.start();
                        media.setLooping(true);
                    }
            break;
            case 2:
                //Se gli effetti sonori sono abilitati, li riproduce e release del mediaPlayer appena finisce
                if(Constants.SFX_FLAG)
                {
                    MediaPlayer bip = MediaPlayer.create(Constants.CURRENT_CONTEXT, R.raw.bip);
                    bip.start();
                    bip.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer bip) {
                            bip.release();
                        }
                    });
                }
            break;
            case 3:
                if(Constants.MUSIC_FLAG)
                {
                    media = MediaPlayer.create(Constants.CURRENT_CONTEXT, R.raw.select_music);
                    media.start();
                    media.setLooping(true);
                }
                break;
            case 4:
                if(Constants.MUSIC_FLAG)
                {
                    media = MediaPlayer.create(Constants.CURRENT_CONTEXT, R.raw.match_music);
                    media.start();
                    media.setLooping(true);
                }
                break;
            case 5:
                if(Constants.SFX_FLAG)
                {
                    MediaPlayer bip = MediaPlayer.create(Constants.CURRENT_CONTEXT, R.raw.flip_card_sfx);
                    bip.start();
                    bip.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer bip) {
                            bip.release();
                        }
                    });
                }
        }
    }
}

