package com.example.unoonline.player;

import com.example.unoonline.gamecard.Card;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

public class MatchPlayer extends Player {
    @JsonIgnore
    public ArrayList<Card> cards = new ArrayList<>();
    public MatchPlayer(){
    }

    public MatchPlayer(Player player){
        setId(player.getId());
        setName(player.getName());
        setImage(player.getImage());
        setStatus(player.getStatus());
    }

    public void AddCard(Card card){
        cards.add(card);
    }

}
