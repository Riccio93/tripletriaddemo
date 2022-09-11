package com.ricci.tt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.List;

public class CardSelectScene implements Scene{

    //Crea oggetto DbHandler
    private DbHandler db = new DbHandler(Constants.CURRENT_CONTEXT);
    //Lista delle carte
    private List<Card> cardList;
    //Pagina di carte (Ogni pagina contiene 5 carte)
    private int page = 0;
    //Numero di carte selezionate finora
    private int handCounter = 0;

    private java.io.FileInputStream input;

    //Toast che avvisa che non sono state inserite 5 carte
    private Toast not5Cards = Toast.makeText(Constants.CURRENT_CONTEXT, "Occorre scegliere 5 carte per giocare!", Toast.LENGTH_SHORT);

    private Bitmap background;
    private Bitmap listSpace;
    private Bitmap chooseSpace;
    private Bitmap pageUp;
    private Bitmap pageDown;
    private Bitmap back;
    private Bitmap play;
    //Definisce font delle stringhe delle carte
    private Typeface typeface = Typeface.create("arial", Typeface.BOLD_ITALIC);

    //Bitmap delle carte in mano
    private Bitmap faceCard1 = null;
    private Bitmap faceCard2 = null;
    private Bitmap faceCard3 = null;
    private Bitmap faceCard4 = null;
    private Bitmap faceCard5 = null;

    //Nomi delle carte in mano
    private String nameFacecCard1;
    private String nameFacecCard2;
    private String nameFacecCard3;
    private String nameFacecCard4;
    private String nameFacecCard5;

    //Id delle carte in mano
    private int idFaceCard1;
    private int idFaceCard2;
    private int idFaceCard3;
    private int idFaceCard4;
    private int idFaceCard5;

    private Rect chooseRect;
    private Rect pageUpRect;
    private Rect pageDownRect;
    private Rect backRect;
    private Rect playRect;
    private Rect[] spaces = new Rect[5];
    private Rect[] hand = new Rect[5];

    //Oggetti card delle carte in mano
    private Card c1;
    private Card c2;
    private Card c3;
    private Card c4;
    private Card c5;

    //Oggetti card della mano avversaria
    private Card ac1;
    private Card ac2;
    private Card ac3;
    private Card ac4;
    private Card ac5;


    public CardSelectScene(){
        Sound.startSound(3);

        this.background = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.menu_background);
        this.listSpace = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.list_space);
        this.chooseSpace = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.card_choose_button);
        this.pageUp = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.pagup_button);
        this.pageDown = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.pagdown_button);
        this.back = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.back);
        this.play = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.chosen_button);

        //In base alla difficoltà (settata in OptionsScene) sceglie le carte dell'avversario
        switch(Constants.difficulty){
            case 0:
                ac1=db.getCard(13);
                ac2=db.getCard(14);
                ac3=db.getCard(15);
                ac4=db.getCard(16);
                ac5=db.getCard(17);
                break;
            case 1:
                ac1=db.getCard(7);
                ac2=db.getCard(8);
                ac3=db.getCard(9);
                ac4=db.getCard(10);
                ac5=db.getCard(11);
                break;
            case 2:
                ac1=db.getCard(1);
                ac2=db.getCard(2);
                ac3=db.getCard(3);
                ac4=db.getCard(4);
                ac5=db.getCard(5);
        }

        cardList = db.getAllCards();

        //Rect contenente "Scegli le tue carte"
        chooseRect = new Rect(25, 15, Constants.SCREEN_WIDTH-25, 175);
        //Rect contenenti le stringhe con le informazioni delle carte
        spaces[0] = new Rect(25, 190, Constants.SCREEN_WIDTH-25, 390);
        spaces[1] = new Rect(25, 405, Constants.SCREEN_WIDTH-25, 605);
        spaces[2] = new Rect(25, 620, Constants.SCREEN_WIDTH-25, 820);
        spaces[3] = new Rect(25, 835, Constants.SCREEN_WIDTH-25, 1035);
        spaces[4] = new Rect(25, 1050, Constants.SCREEN_WIDTH-25, 1250);
        pageDownRect = new Rect(25, 1275, Constants.SCREEN_WIDTH/2-25, 1425);
        pageUpRect = new Rect(Constants.SCREEN_WIDTH/2+25, 1275, Constants.SCREEN_WIDTH-25, 1425);
        backRect = new Rect(25, 1470, Constants.SCREEN_WIDTH/2-25, 1640);
        playRect = new Rect(Constants.SCREEN_WIDTH/2+25, 1470, Constants.SCREEN_WIDTH-25, 1640);
        //Rect contenenti le immagini delle carte già scelte per la mano
        hand[0] = new Rect(50, 1700, 320, 2120);
        hand[1] = new Rect(230, 1700, 500, 2120);
        hand[2] = new Rect(410, 1700, 680, 2120);
        hand[3] = new Rect(590, 1700, 860, 2120);
        hand[4] = new Rect(770, 1700, 1040, 2120);
    }

    @Override
    public void draw(Canvas canvas) {
        //Paint utilizzato per tutto meno le stringhe con le informazioni sulle carte
        Paint backPaint = new Paint();
        //Paint utilizzato per le stringhe con le informazioni sulle carte
        Paint namePaint = new Paint();

        canvas.drawBitmap(background, null, new Rect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT), backPaint);
        canvas.drawBitmap(chooseSpace, null, chooseRect, backPaint);
        canvas.drawBitmap(pageDown, null, pageDownRect, backPaint);
        canvas.drawBitmap(pageUp, null, pageUpRect, backPaint);
        canvas.drawBitmap(back, null, backRect, backPaint);
        canvas.drawBitmap(play, null, playRect, backPaint);

        canvas.drawBitmap(listSpace, null, spaces[0], backPaint);
        canvas.drawBitmap(listSpace, null, spaces[1], backPaint);
        canvas.drawBitmap(listSpace, null, spaces[2], backPaint);
        canvas.drawBitmap(listSpace, null, spaces[3], backPaint);
        canvas.drawBitmap(listSpace, null, spaces[4], backPaint);

        //Setto font, dimensione e colore del testo con le info sulle carte
        namePaint.setColor(Color.WHITE);
        namePaint.setTextSize(50);
        namePaint.setTypeface(typeface);

        //Stampa stringhe con info sulle carte in base alla pagina (se siamo a pag.3, il primo posto sarà la carta con id 5*(pag-1)+1 e così via)
        for(int i=0; i<5; i++){
            if(!((page*5+1+i)>cardList.size())){
                canvas.drawText(Integer.toString(cardList.get(i + (page * 5)).getId()) + " " + cardList.get(i + (page * 5)).getName() + " L: "
                        + Integer.toString(cardList.get(i + page * 5).getlValue()) + " T: " + Integer.toString(cardList.get(i + page * 5).gettValue())
                        + " R: " + Integer.toString(cardList.get(i + page * 5).getrValue()) + " B: " + Integer.toString(cardList.get(i + page * 5).getbValue()), (spaces[i].left) + 50, (spaces[i].top) + 125, namePaint);
            }
        }

        //Se l'utente ha già scelto la carta i della mano, disegna l'immagine della carta nel rect i (altrimenti il bitmap è null)
        if(faceCard1!=null){
            canvas.drawBitmap(faceCard1, null, hand[0], backPaint);
        }
        if(faceCard2!=null){
            canvas.drawBitmap(faceCard2, null, hand[1], backPaint);
        }
        if(faceCard3!=null){
            canvas.drawBitmap(faceCard3, null, hand[2], backPaint);
        }
        if(faceCard4!=null){
            canvas.drawBitmap(faceCard4, null, hand[3], backPaint);
        }
        if(faceCard5!=null){
            canvas.drawBitmap(faceCard5, null, hand[4], backPaint);
        }
    }

    @Override
    public void update() {}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Se l'utente tocca...
        if(event.getAction()==0){
            //Vado alla pagina precedente (se a pag.1 non fa nulla)
            if(pageDownRect.contains((int)event.getX(), (int)event.getY())){
                Sound.startSound(2);
                if(page > 0)
                    page--;
            }
            //Vado alla pagina successiva (se non contiene carte non fa nulla)
            if(pageUpRect.contains((int)event.getX(), (int)event.getY())){
                Sound.startSound(2);
                if(!((page*5+6)>cardList.size()))
                    page++;
            }
            try{
                //Inserisce la carta nella posizione definita da handCounter
                switch(handCounter) {
                    case 0:
                        for (int i = 0; i < 5; i++) {
                            //Risponde al tocco solo se il Rect spaces[i] contiene la descrizione di una carta (e quindi la inserisce nella mano), altrimenti non fa nulla
                            if (spaces[i].contains((int) event.getX(), (int) event.getY()) && !(5*page+(i+1)>db.getAllCards().size())) {
                                Sound.startSound(2);
                                c1 = db.getCard(5 * page + (i + 1));
                                //Se la carta scelta è tra quelle predefinite cerca l'immagine in drawable
                                if(c1.getId()<=17){
                                    nameFacecCard1 = "b" + Integer.toString(5 * page + (i + 1));
                                    idFaceCard1 = Constants.CURRENT_CONTEXT.getResources().getIdentifier(nameFacecCard1, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
                                    this.faceCard1 = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), idFaceCard1);
                                }
                                //Se la carta scelta è personalizzata, cerca l'immagine nella memoria interna (con FileInputStream)
                                else{
                                    input = Constants.CURRENT_CONTEXT.openFileInput(Integer.toString(5 * page + (i + 1))+"b.png");
                                    this.faceCard1 = BitmapFactory.decodeStream(input);
                                    input.close();
                                }
                                //Passa alla carta successiva
                                handCounter++;
                            }
                        }
                        //Se si vuole iniziare la partita, un toast avverte che sono necessarie 5 carte e non lo permette
                        if(playRect.contains((int) event.getX(), (int) event.getY())){
                            Sound.startSound(2);
                            not5Cards.show();}
                        //Torna indietro alla scena del menù (solo se sono state scelte 0 carte)
                        if(backRect.contains((int) event.getX(), (int) event.getY())){
                            Sound.media.release();
                            Sound.startSound(2);
                            Sound.startSound(1);
                            SceneManager.ACTIVE_SCENE = 0;}
                        break;
                    case 1:
                        for (int i = 0; i < 5; i++) {
                            if (spaces[i].contains((int) event.getX(), (int) event.getY()) && !(5*page+(i+1)>db.getAllCards().size())) {
                                Sound.startSound(2);
                                c2 = db.getCard(5 * page + (i + 1));
                                if(c2.getId()<=17) {
                                    nameFacecCard2 = "b" + Integer.toString(5 * page + (i + 1));
                                    idFaceCard2 = Constants.CURRENT_CONTEXT.getResources().getIdentifier(nameFacecCard2, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
                                    this.faceCard2 = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), idFaceCard2);
                                }
                                else
                                {
                                    input = Constants.CURRENT_CONTEXT.openFileInput(Integer.toString(5 * page + (i + 1))+"b.png");
                                    this.faceCard2 = BitmapFactory.decodeStream(input);
                                    input.close();
                                }
                                handCounter++;
                            }
                        }
                        if(playRect.contains((int) event.getX(), (int) event.getY())){
                            Sound.startSound(2);
                            not5Cards.show();}
                        //Se sono state scelte più di 0 carte, il tasto indietro elimina dalla mano l'ultima carta aggiunta
                        if(backRect.contains((int) event.getX(), (int) event.getY())){
                            Sound.startSound(2);
                            c1=null;
                            nameFacecCard1 = null;
                            idFaceCard1 = 0;
                            this.faceCard1 = null;
                            handCounter--;
                        }
                        break;
                    case 2:
                        for (int i = 0; i < 5; i++) {
                            if (spaces[i].contains((int) event.getX(), (int) event.getY()) && !(5*page+(i+1)>db.getAllCards().size())) {
                                Sound.startSound(2);
                                c3 = db.getCard(5 * page + (i + 1));
                                if(c3.getId()<=17) {
                                    nameFacecCard3 = "b" + Integer.toString(5 * page + (i + 1));
                                    idFaceCard3 = Constants.CURRENT_CONTEXT.getResources().getIdentifier(nameFacecCard3, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
                                    this.faceCard3 = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), idFaceCard3);
                                }
                                else{
                                    input = Constants.CURRENT_CONTEXT.openFileInput(Integer.toString(5 * page + (i + 1))+"b.png");
                                    this.faceCard3 = BitmapFactory.decodeStream(input);
                                    input.close();
                                }
                                handCounter++;
                            }
                        }
                        if(playRect.contains((int) event.getX(), (int) event.getY())){
                            Sound.startSound(2);
                            not5Cards.show();}
                        if(backRect.contains((int) event.getX(), (int) event.getY())){
                            Sound.startSound(2);
                            c2=null;
                            nameFacecCard2 = null;
                            idFaceCard2 = 0;
                            this.faceCard2 = null;
                            handCounter--;
                        }
                        break;
                    case 3:
                        for (int i = 0; i < 5; i++) {
                            if (spaces[i].contains((int) event.getX(), (int) event.getY()) && !(5*page+(i+1)>db.getAllCards().size())) {
                                Sound.startSound(2);
                                c4 = db.getCard(5 * page + (i + 1));
                                if(c4.getId()<=17) {
                                    nameFacecCard4 = "b" + Integer.toString(5 * page + (i + 1));
                                    idFaceCard4 = Constants.CURRENT_CONTEXT.getResources().getIdentifier(nameFacecCard4, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
                                    this.faceCard4 = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), idFaceCard4);
                                }
                                else{
                                    input = Constants.CURRENT_CONTEXT.openFileInput(Integer.toString(5 * page + (i + 1))+"b.png");
                                    this.faceCard4 = BitmapFactory.decodeStream(input);
                                    input.close();
                                }
                                handCounter++;
                            }
                        }
                        if(playRect.contains((int) event.getX(), (int) event.getY())){
                            Sound.startSound(2);
                            not5Cards.show();}
                        if(backRect.contains((int) event.getX(), (int) event.getY())){
                            Sound.startSound(2);
                            c3=null;
                            nameFacecCard3 = null;
                            idFaceCard3 = 0;
                            this.faceCard3 = null;
                            handCounter--;
                        }
                        break;
                    case 4:
                        for (int i = 0; i < 5; i++) {
                            if (spaces[i].contains((int) event.getX(), (int) event.getY()) && !(5*page+(i+1)>db.getAllCards().size())) {
                                Sound.startSound(2);
                                c5 = db.getCard(5 * page + (i + 1));
                                if(c5.getId()<=17) {
                                    nameFacecCard5 = "b" + Integer.toString(5 * page + (i + 1));
                                    idFaceCard5 = Constants.CURRENT_CONTEXT.getResources().getIdentifier(nameFacecCard5, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
                                    this.faceCard5 = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), idFaceCard5);
                                }
                                else{
                                    input = Constants.CURRENT_CONTEXT.openFileInput(Integer.toString(5 * page + (i + 1))+"b.png");
                                    this.faceCard5 = BitmapFactory.decodeStream(input);
                                    input.close();
                                }
                                handCounter++;
                            }
                        }
                        if(playRect.contains((int) event.getX(), (int) event.getY())){
                            Sound.startSound(2);
                            not5Cards.show();}
                        if(backRect.contains((int) event.getX(), (int) event.getY())){
                            Sound.startSound(2);
                            c4=null;
                            nameFacecCard4 = null;
                            idFaceCard4 = 0;
                            this.faceCard4 = null;
                            handCounter--;
                        }
                        break;
                    case 5:
                        //Sono state scelte 5 carte, passa alla scena di gioco impostando le carte di entrambe le mani come parametri
                        if(playRect.contains((int) event.getX(), (int) event.getY())){
                            Sound.startSound(2);
                            SceneManager.addScene(new GameScene(c1, c2, c3, c4, c5, ac1, ac2, ac3, ac4, ac5));
                            SceneManager.ACTIVE_SCENE = 2;
                        }
                        if(backRect.contains((int) event.getX(), (int) event.getY())){
                            Sound.startSound(2);
                            c5=null;
                            nameFacecCard5 = null;
                            idFaceCard5 = 0;
                            this.faceCard5 = null;
                            handCounter--;
                        }
                    }
                }catch(Exception e){
                e.printStackTrace();
            }
        }
        return true;
    }
}
