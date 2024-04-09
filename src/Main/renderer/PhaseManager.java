package Main.renderer;

import Main.Components.Component;
import Main.Components.SpriteRenderer;
import imgui.ImGui;

import imgui.type.ImBoolean;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PhaseManager extends Component {

    public HashMap<PhaseSignal, String> phaseTransfers = new HashMap<>();
    private List<Animator> phases = new ArrayList<>();
    private transient Animator currentPhase = null;
    private String defaultPhaseLabel = "";


    @Override
    public void start() {
        for (Animator phase : phases) {
            if (phase.label.equals(defaultPhaseLabel)) {
                currentPhase = phase;
                break;
            }
        }
    }

    @Override
    public void update(double dt) {
        if (currentPhase != null) {
            currentPhase.update(dt);
            SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
            if (sprite != null) {
                sprite.setSprite(currentPhase.getCurrentSpr());
            }
        }
    }


    class PhaseSignal {
        public String phase;
        public String Signal;

        public PhaseSignal() {
        }

        public PhaseSignal(String phase, String Signal) {
            this.phase = phase;
            this.Signal = Signal;
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() != PhaseSignal.class) return false;
            PhaseSignal t2 = (PhaseSignal) o;
            return t2.Signal.equals(this.Signal) && t2.phase.equals(this.phase);
        }

        @Override
        public int hashCode() {
            return Objects.hash(phase, Signal);
        }
    }

    public void refreshTextures() {
        for (Animator phase : phases) {
            phase.refreshTextures();
        }
    }

    public void setDefaultPhase(String animationTitle) {
        for (Animator phase : phases) {
            if (phase.label.equals(animationTitle)) {
                defaultPhaseLabel = animationTitle;
                if (currentPhase == null) {
                    currentPhase = phase;
                }
                return;
            }
        }
    }

    public void addPhase(String from, String to, String onSignal) {
        this.phaseTransfers.put(new PhaseSignal(from, onSignal), to);
    }

    public void addPhase(Animator phase) {
        this.phases.add(phase);
    }

    public void signal(String Signal) {
        for (PhaseSignal phase : phaseTransfers.keySet()) {
            if (phase.phase.equals(currentPhase.label) && phase.Signal.equals(Signal)) {
                if (phaseTransfers.get(phase) != null) {
                    String newPhase = phaseTransfers.get(phase);
                    int newPhaseIndex = phaseIndexOf(newPhase);
                    if (newPhaseIndex > -1) {
                        currentPhase = phases.get(newPhaseIndex);
                    }
                }
                return;
            }
        }
    }



    @Override
    public void editorUpdate(double dt) {
        if (currentPhase != null) {
            currentPhase.update(dt);
            SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
            if (sprite != null) {
                sprite.setSprite(currentPhase.getCurrentSpr());
            }
        }
    }

    @Override
    public void imgui() {
        for (Animator phase : phases) {
            ImString title = new ImString(phase.label);
            ImGui.inputText("phase: ", title);
            phase.label = title.get();

            int index = 0;
            for (Frame frame : phase.animFrames) {
                float[] tmp = new float[1];
                tmp[0] = frame.FTime;
                ImGui.dragFloat("Frame(" + index + ") Time: ", tmp, 0.01f);
                frame.FTime = tmp[0];
                index++;
            }
        }
    }

    private int phaseIndexOf(String phaseTitle) {
        int index = 0;
        for (Animator phase : phases) {
            if (phase.label.equals(phaseTitle)) {
                return index;
            }
            index++;
        }

        return -1;
    }
}