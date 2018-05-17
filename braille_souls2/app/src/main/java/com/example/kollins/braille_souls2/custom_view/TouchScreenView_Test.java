package com.example.kollins.braille_souls2.custom_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.kollins.braille_souls2.MainActivity;
import com.example.kollins.braille_souls2.R;
import com.example.kollins.braille_souls2.TTSManager;

import java.util.Locale;

public class TouchScreenView_Test extends View {

    private boolean selecionou;
    private Drawable img;
    private int x,y;
    private int larguraTela, alturaTela;
    private int larguraImg, alturaImg;



    public TouchScreenView_Test(Context context) {
        super(context);

        //Recupera Imagem
        img = context.getResources().getDrawable(R.drawable.android_test);

        //Recupera largura e altura da imagem
        larguraImg = img.getIntrinsicWidth();
        alturaImg = img.getIntrinsicHeight();


        //Configura View para receber foco e tratar eventos
        setFocusable(true);
    }

    //Chamado quando a tela é redimensionada ou iniciada
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        this.larguraTela = w;
        this.alturaTela = h;

        x = (w/2) - (larguraImg/2);
        y = (h/2) - (alturaImg/2);
    }

    //Desenha tela

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Fundo branco
        Paint pincel = new Paint();
        pincel.setColor(Color.WHITE);
        canvas.drawRect(0,0,larguraTela,alturaTela,pincel);

        //Define limites/área para desenhar
        img.setBounds(x,y,x+larguraImg,y+alturaImg);

        //Desenha imagem
        img.draw(canvas);
    }

    //Move imagem
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //Inicia movimento se pressionou imagem
                selecionou = img.copyBounds().contains((int)x, (int)y);
                if(selecionou){
                    MainActivity.tts.speak("Ouch!!! too much pressure", TextToSpeech.QUEUE_ADD, null);
                }else{
                    MainActivity.tts.speak("Missed android", TextToSpeech.QUEUE_ADD, null);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                //Arrasta boneco
                if (selecionou){
                    this.x = (int) x - (larguraImg/2);
                    this.y = (int) y - (alturaImg/2);
                }
                break;

            case MotionEvent.ACTION_UP:
                if(selecionou){
                    MainActivity.tts.speak("Wow!!! What a relief", TextToSpeech.QUEUE_ADD, null);
                }
                //Finaliza movimento
                selecionou = false;
                break;
        }

        invalidate();
        return true;
    }





}
