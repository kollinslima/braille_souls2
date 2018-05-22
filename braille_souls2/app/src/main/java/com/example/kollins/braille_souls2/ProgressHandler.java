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
