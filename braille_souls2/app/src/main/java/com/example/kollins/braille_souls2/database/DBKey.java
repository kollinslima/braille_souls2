package com.example.kollins.braille_souls2.database;

public class DBKey {

    private String text;
    private String braille;

    public DBKey(String text, String braille) {
        this.text = text;
        this.braille = braille;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getBraille() {
        return braille;
    }

    public void setBraille(String braille) {
        this.braille = braille;
    }
}
