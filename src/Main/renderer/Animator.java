package Main.renderer;

import Main.Components.Sprite;
import Main.utilities.AssetRepository;

import java.util.ArrayList;
import java.util.List;

public class Animator {
    public String label;
    public List<Frame> animFrames = new ArrayList<>();

    private static Sprite defaultSpr = new Sprite();
    private transient double tracker = 0.0f;
    private transient int currentSpr = 0;
    public boolean loops = false;

    public void refreshTextures(){
        for (Frame frame: animFrames){
            frame.spr.setTex(AssetRepository.getTexture(frame.spr.getTex().getFilepath()));
        }
    }

    public void addFrame(Sprite spr, float FTime){
        animFrames.add(new Frame(spr, FTime));

    }

    public void update(double dt){
        if(currentSpr < animFrames.size()){
            tracker -= dt;
            if(tracker <= 0){
                if(currentSpr != animFrames.size() - 1 || loops){
                    currentSpr = (currentSpr + 1) % animFrames.size();
                }
                tracker = animFrames.get(currentSpr).FTime;
            }
        }
    }

    public Sprite getCurrentSpr(){
        if(currentSpr < animFrames.size()){
            return animFrames.get(currentSpr).spr;
        }
        return defaultSpr;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setLoops(boolean loops){
        this.loops = loops;
    }

    public boolean getLoops(){
        return loops;
    }


}
