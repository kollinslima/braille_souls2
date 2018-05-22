package com.example.kollins.braille_souls2;

public class ProgressHandler {
    private int hitCount;
    private int level;

    public ProgressHandler() {
        this.hitCount = 0;
        this.level = 0;
    }

    public int setTimeAnswer(int timeAnswer) {
        int ta = timeAnswer;
        if(level < 10){
            ta = level*1000;
        }else{
            ta = 1000;
        }
        return ta;
    }

    public void addHit() {
        hitCount++;
        checkLevel(1);
    }

    private void checkLevel(int flag) {
        if(hitCount%10 == 0){
            level += flag;
        }
    }

    public void takeHit(){
        hitCount = 0;
        checkLevel(-1);
    }

    public int getHitCount() {
        return hitCount;
    }
}
