/*
 * Copyright 2018
 * Kollins Lima (kollins.lima@gmail.com)
 * Otávio Sumi (otaviosumi@hotmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.kollins.braille_souls2.custom_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.kollins.braille_souls2.R;

import java.util.ArrayList;

public class TouchScreenView extends View implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    SensiveAreaListener sensiveListener;

    private int rows, columns;
    private int positionX, positionY;
    GestureDetector gestureDetector;

    private int screenWidth, screenHeight;
    private int stepX, stepY;

    private ArrayList<ArrayList<Integer>> sensitiveArea;

    public TouchScreenView(Context context) {
        super(context);
        gestureDetector = new GestureDetector(context, this);
        rows = 1;
        columns = 1;
        sensitiveArea = new ArrayList<>(1);
        sensitiveArea.add(new ArrayList<Integer>(1));
        sensitiveArea.get(0).add(0);
        setFocusable(true);
    }

    public TouchScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.TouchScreenView);
        gestureDetector = new GestureDetector(context, this);
        rows = arr.getInteger(R.styleable.TouchScreenView_rows, 1);
        columns = arr.getInteger(R.styleable.TouchScreenView_columns, 1);

        sensitiveArea = new ArrayList<>(rows);
        for (int i = 0; i < rows; i++) {
            sensitiveArea.add(new ArrayList<Integer>(columns));
            for (int j = 0; j < columns; j++) {
                sensitiveArea.get(i).add(0);
            }
        }

        setFocusable(true);
    }

    public int setSensitive(int row, int column) {
        try {
            sensitiveArea.get(row).set(column, 1);
        } catch (IndexOutOfBoundsException e) {
            return 1;
        }
        return 0;
    }

    public void cleanAllSensitive() {
        for (ArrayList<Integer> a : sensitiveArea) {
            for (int i = 0; i < a.size(); i++) {
                a.set(i, 0);
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setSensiveAreaListener(SensiveAreaListener eventListener) {
        sensiveListener = eventListener;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        this.screenWidth = w;
        this.screenHeight = h;

        stepX = screenWidth / columns;
        stepY = screenHeight / rows;

    }

    public int getScreenHeight(){
        return screenHeight;
    }

    //Desenha tela
//    @Override
//    protected void onDraw(Canvas canvas) {
//
//    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int newPositionX, newPositionY;

        gestureDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:

                newPositionX = (int) (event.getX() / stepX);
                newPositionY = (int) (event.getY() / stepY);

                if (positionX != newPositionX || positionY != newPositionY){
                    sensiveListener.onChangeArea();
                }

                positionX = newPositionX;
                positionY = newPositionY;

                if (sensitiveArea.get(positionY).get(positionX) == 1) {
                    if (sensiveListener != null)
                        sensiveListener.onSensiveArea();
                } else {
                    if (sensiveListener != null)
                        sensiveListener.onNonSensiveArea();
                }

                break;

            case MotionEvent.ACTION_UP:
                if (sensiveListener != null)
                    sensiveListener.onNonSensiveArea();
                break;
        }

        switch(event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_POINTER_DOWN:
                sensiveListener.onDoubleFingerTap();
                Log.d("MOTION", "Pointer: " + event.getPointerCount());
                Log.d("MOTION", "Pointer: " + event.getY(0));
                Log.d("MOTION", "Pointer: " + event.getY(1));
                break;

        }

        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d("MOTION", "X: " + e.getX());
        Log.d("MOTION", "Y: " + e.getY());
        sensiveListener.onTap(e.getX(), e.getY());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d("MOTION", "onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d("MOTION", "onSingleTapUp");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d("MOTION", "onScroll");
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        sensiveListener.onLongPress();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("MOTION", "onFling");
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        sensiveListener.onDoubleTap();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }
}
