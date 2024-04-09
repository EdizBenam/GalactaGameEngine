package Main.galacta;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class Audio {
    private int bufferID;
    private int srcID;
    private String filepath;
    private boolean playing = false;
    private float gain = 0.3f;

    public Audio(String filepath, boolean loops) {
        this.filepath = filepath;


        stackPush();
        IntBuffer channelBuffer = stackMallocInt(1);
        stackPush();
        IntBuffer sampleRateBuffer = stackMallocInt(1);

        ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(filepath, channelBuffer, sampleRateBuffer);
        if (rawAudioBuffer == null) {
            System.out.println("Couldn't load audio '" + filepath + "'");
            stackPop();
            stackPop();
            return;
        }


        int channels = channelBuffer.get();
        int sampleRate = sampleRateBuffer.get();
        stackPop();
        stackPop();


        int format = -1;
        if (channels == 1) {
            format = AL_FORMAT_MONO16;
        } else if (channels == 2) {
            format = AL_FORMAT_STEREO16;
        }

        bufferID = alGenBuffers();
        alBufferData(bufferID, format, rawAudioBuffer, sampleRate);


        srcID = alGenSources();

        alSourcei(srcID, AL_BUFFER, bufferID);
        alSourcei(srcID, AL_LOOPING, loops ? 1 : 0);
        alSourcei(srcID, AL_POSITION, 0);
        alSourcef(srcID, AL_GAIN, gain);


        free(rawAudioBuffer);
    }

    public void delete() {
        alDeleteSources(srcID);
        alDeleteBuffers(bufferID);
    }

    public void play() {
        int state = alGetSourcei(srcID, AL_SOURCE_STATE);
        if (state == AL_STOPPED) {
            playing = false;
            alSourcei(srcID, AL_POSITION, 0);
        }

        if (!playing) {
            alSourcePlay(srcID);
            playing = true;
        }
    }

    public void stop() {
        if (playing) {
            alSourceStop(srcID);
            playing = false;
        }
    }

    public String getFilepath() {
        return this.filepath;
    }

    public boolean isPlaying() {
        int state = alGetSourcei(srcID, AL_SOURCE_STATE);
        if (state == AL_STOPPED) {
            playing = false;
        }
        return playing;
    }

    public void setGain(float gain) {
        this.gain = gain;
        alSourcef(srcID, AL_GAIN, gain);
    }
}
