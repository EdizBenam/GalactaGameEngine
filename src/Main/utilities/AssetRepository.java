package Main.utilities;

import Main.Components.SpriteSheet;
import Main.galacta.Audio;
import Main.renderer.Shader;
import Main.renderer.Texture;


import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AssetRepository {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, SpriteSheet> spritesheets = new HashMap<>();
    private static Map<String, Audio> audios = new HashMap<>();

    public static Shader getShader(String resourceName) {
        File file = new File(resourceName);
        if (AssetRepository.shaders.containsKey(file.getAbsolutePath())) {
            return AssetRepository.shaders.get(file.getAbsolutePath());
        } else {
            Shader shader = new Shader(resourceName);
            shader.compile();
            AssetRepository.shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }

    public static Texture getTexture(String resourceName) {
        File file = new File(resourceName);
        if (AssetRepository.textures.containsKey(file.getAbsolutePath())) {
            return AssetRepository.textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture();
            texture.init(resourceName);
            AssetRepository.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }

    public static void addSprSheet(String resourceName, SpriteSheet spritesheet) {
        File file = new File(resourceName);
        if (!AssetRepository.spritesheets.containsKey(file.getAbsolutePath())) {
            AssetRepository.spritesheets.put(file.getAbsolutePath(), spritesheet);
        }
    }

    public static SpriteSheet getSprSheet(String resourceName) {
        File file = new File(resourceName);
        if (!AssetRepository.spritesheets.containsKey(file.getAbsolutePath())) {
            assert false : "Error: Tried to access spritesheet '" + resourceName + "' and it has not been added to asset pool.";
        }
        return AssetRepository.spritesheets.getOrDefault(file.getAbsolutePath(), null);
    }

    public static Collection<Audio> getAllAudios(){
        return audios.values();
    }

    public static Audio getAudio(String audioFile) {
        File file = new File(audioFile);
        if (audios.containsKey(file.getAbsolutePath())) {
            return audios.get(file.getAbsolutePath());
        } else {
            assert false: "Audio file not added '" + audioFile + "'";
        }
        return null;
    }

    public static Audio addAudio(String audioFile, boolean loops){
        File file = new File(audioFile);
        if (audios.containsKey(file.getAbsolutePath())) {
            return audios.get(file.getAbsolutePath());
        } else {
            Audio audio = new Audio(file.getAbsolutePath(), loops);
            AssetRepository.audios.put(file.getAbsolutePath(), audio);
            return audio;
        }
    }
}