package Main.watchdogs;

import Main.galacta.GameObject;
import Main.watchdogs.broadcasts.Broadcast;

public interface WatchDog {
    void onNotif(GameObject obj, Broadcast broadcast);
}
