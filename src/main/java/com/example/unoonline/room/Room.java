package com.example.unoonline.room;

import com.example.unoonline.player.MatchPlayer;
import com.example.unoonline.player.Player;
import com.example.unoonline.roomService.RoomService;
import gamemaster.GameMaster;

import java.util.ArrayList;
import java.util.Collection;

public class Room {
    public GameMaster master = new GameMaster();
    public ArrayList<Player> players = new ArrayList<>();
    public ArrayList<MatchPlayer> matchPlayers = new ArrayList<>();

    public Room(){
    }

    public void Add(Player player){
        players.add(player);
    }

    public void Remove(Player player){
        players.remove(player);
    }

}
