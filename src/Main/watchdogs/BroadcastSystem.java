package Main.watchdogs;

import Main.galacta.GameObject;
import Main.watchdogs.broadcasts.Broadcast;

import java.util.ArrayList;
import java.util.List;

public class BroadcastSystem {
    private static List<WatchDog> watchDogs = new ArrayList<>();

    public static void addWatchDog(WatchDog watchDog){
        watchDogs.add(watchDog);
    }

    public static void notif(GameObject obj, Broadcast broadcast){
        for(WatchDog watchDog: watchDogs){
            watchDog.onNotif(obj, broadcast);
        }
    }
}
