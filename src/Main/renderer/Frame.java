package Main.renderer;

import Main.Components.Sprite;

public class Frame {
    public Sprite spr;
    public float FTime;
    public Frame(){

    }

    public Frame(Sprite spr, float time){
        this.spr = spr;
        this.FTime = time;
    }
}
