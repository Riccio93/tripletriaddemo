package com.ricci.tt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Random;

public class CamActivity extends Activity{
    DbHandler db = new DbHandler(Constants.CURRENT_CONTEXT);
    Random random = new Random();

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    int lvalue;
    int tvalue;
    int rvalue;
    int bvalue;

    int dbSize;

    ImageView imageView;

    java.io.FileInputStream input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Prende controllo dell'intero schermo, senza barra del titolo e status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Setta il layout dell'activity al file di layout xml
        setContentView(R.layout.cam_activity);
        //Dimensione del database
        dbSize = db.getAllCards().size();
        //imageview in cui verrà visualizzato il bitmap finito della carta
        imageView = findViewById(R.id.imageView1);
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        //Definisce l'intent, chiama l'applicazione camera ed aspetta un risultato
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Prende l'extra passato dalla camera
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            saveImageToInternalStorage(imageBitmap);
            //Visualizza nell'imageView
            try{
                input = openFileInput((dbSize+1)+"b.png");
            } catch(FileNotFoundException e){
                e.printStackTrace();
            }
            imageView.setImageBitmap(BitmapFactory.decodeStream(input));
        }
        else{
            //Se la camera non restituisce un'immagine (per esempio se l'utente torna indietro), torna all'activity principale
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public boolean saveImageToInternalStorage(Bitmap image) {
        try {
            //Valori della carta generati casualmente
            lvalue = random.nextInt((10 - 1) + 1)+1;
            tvalue = random.nextInt((10 - 1) + 1)+1;
            rvalue = random.nextInt((10 - 1) + 1)+1;
            bvalue = random.nextInt((10 - 1) + 1)+1;

            Paint paint = new Paint();
            Canvas canvas = new Canvas(image);

            //Rect contenenti i valori e il cuore rappresentante l'owner della carta
            Rect leftRect = new Rect(7, 38, 32, 73);
            Rect rightRect = new Rect(32, 38, 57, 73);
            Rect topRect = new Rect(22, 5, 47, 40);
            Rect bottomRect = new Rect(22, 73, 47, 108);
            Rect heartRect = new Rect(3*canvas.getWidth()/5, 7*canvas.getHeight()/10+2, canvas.getWidth()-10, canvas.getHeight()-10);

            //Disegna contorno della carta
            Bitmap cardOutside = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.card_outside);
            canvas.drawBitmap(cardOutside, null, new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), paint);

            //Disegna cuore rappresentante l'owner della carta (versione blu)
            Bitmap blueHeart = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.blue_heart);
            Bitmap redHeart = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.red_heart);
            canvas.drawBitmap(blueHeart, null, heartRect, paint);

            //Crea stringa con il nome del PNG, e prende l'immagine da drawable con l'identifier dato
            String lValueString = "valore_"+Integer.toString(lvalue);
            int lValueId = Constants.CURRENT_CONTEXT.getResources().getIdentifier(lValueString, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
            Bitmap lValueImage = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(),lValueId);
            String tValueString = "valore_"+Integer.toString(tvalue);
            int tValueId = Constants.CURRENT_CONTEXT.getResources().getIdentifier(tValueString, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
            Bitmap tValueImage = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(),tValueId);
            String rValueString = "valore_"+Integer.toString(rvalue);
            int rValueId = Constants.CURRENT_CONTEXT.getResources().getIdentifier(rValueString, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
            Bitmap rValueImage = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(),rValueId);
            String bValueString = "valore_"+Integer.toString(bvalue);
            int bValueId = Constants.CURRENT_CONTEXT.getResources().getIdentifier(bValueString, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
            Bitmap bValueImage = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(),bValueId);

            //Disegna valori sulla carta
            canvas.drawBitmap(lValueImage, null, leftRect, paint);
            canvas.drawBitmap(tValueImage, null, topRect, paint);
            canvas.drawBitmap(rValueImage, null, rightRect, paint);
            canvas.drawBitmap(bValueImage, null, bottomRect, paint);

            //Salva versione blu della carta
            FileOutputStream fos = openFileOutput((dbSize+1)+"b.png", Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            //Salva versione rossa della carta
            canvas.drawBitmap(redHeart, null, heartRect, paint);
            FileOutputStream fos2 = openFileOutput((dbSize+1)+"r.png", Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos2);
            fos2.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void onClickButton(View v){
        //EditText per l'inserimento del nome
        EditText editText = findViewById(R.id.editText);

        //Se l'editText contenente il nome non è vuoto, aggiunge la carta nel database e torna all'activity principale
        if(!(TextUtils.isEmpty(editText.getText()))){
            Card card = new Card(dbSize+1, editText.getText().toString(), lvalue, tvalue, rvalue, bvalue);
            db.addCard(card);
            this.finish();
        }
        //Se l'editText contenente il nome è vuoto, mostra un toast
        else{
            Toast toast = Toast.makeText(this, "Lo spazio nome è vuoto!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onClickButton1(View v){
        //Bottone indietro, torna all'activity principale
        this.finish();
    }
}
