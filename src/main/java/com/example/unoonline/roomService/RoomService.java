package com.example.unoonline.roomService;

import com.example.unoonline.player.MatchPlayer;
import com.example.unoonline.room.Room;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
@Service
public class RoomService {
    public static HashMap<Integer, Room> rooms = new HashMap<>();

    public static Integer getNewKey(){
        return rooms.isEmpty() ? 1000 : Collections.max(rooms.keySet())+1;
    }
    public static boolean AllowConnect(String code){
        if(RoomService.rooms.containsKey(Integer.parseInt(code)))
            return RoomService.rooms.get(Integer.parseInt(code)).players.size() < 10;
        return false;
    }
    public static void StartGame(Integer code){
        for(int i=0;i<rooms.get(code).players.size();i++) {
            rooms.get(code).matchPlayers.add(new MatchPlayer(rooms.get(code).players.get(i)));
            rooms.get(code).master.SetStartHand((rooms.get(code).matchPlayers).get(i));
        }
    }
}
