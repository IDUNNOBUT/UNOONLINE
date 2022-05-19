package com.example.unoonline.controller;

import com.example.unoonline.gamecard.Card;
import com.example.unoonline.player.MatchPlayer;
import com.example.unoonline.player.Player;
import com.example.unoonline.roomService.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@RestController
public class GameFieldController {
    @Autowired
    private SimpMessagingTemplate template;

    //redirect all players to game field
    @MessageMapping("goto/{code}")
    public boolean RedirectAll(@DestinationVariable Integer code) {
        this.template.convertAndSend("topic/goto/" + code, true);
        return true;
    }

    // get list of players
    @MessageMapping("game/players/{code}")
    public ArrayList<MatchPlayer> GetPlayers(@DestinationVariable Integer code) {
        this.template.convertAndSend("game/players/" + code, RoomService.rooms.get(code).matchPlayers);
        return RoomService.rooms.get(code).matchPlayers;
    }

    // signal to start a game from host
    @GetMapping(value = "game/{code}")
    public void StartGame(@PathVariable Integer code) {
        RoomService.StartGame(code);
    }

    // amount of cards for everyone
    @MessageMapping("game/cardscount/{code}")
    public HashMap<String, Integer> GetStartCondition(@DestinationVariable Integer code) {
        HashMap<String, Integer> cardscount = new HashMap<>();
        for (MatchPlayer player : RoomService.rooms.get(code).matchPlayers) {
            cardscount.put(player.getId(), player.cards.size());
        }
        cardscount.put("deck", RoomService.rooms.get(code).master.deck.size());
        cardscount.put("discard", RoomService.rooms.get(code).master.discard.size());
        this.template.convertAndSend("game/cardscount/" + code, cardscount);
        return cardscount;
    }

    // cards in players hand
    @MessageMapping("game/{code}/cardshand/{id}")
    public ArrayList<Card> GetCardsHand(@DestinationVariable("code") Integer code, @DestinationVariable("id") String id) {
        for (MatchPlayer player : RoomService.rooms.get(code).matchPlayers) {
            if (Objects.equals(player.getId(), id)) {
                this.template.convertAndSend("topic/game/" + code + "/cardshand/" + id, player.cards);
                return player.cards;
            }
        }
        return null;
    }

    @GetMapping(value = "game/setup/{code}")
    public HashMap<String, Object> GameSetup(@PathVariable Integer code) {
        HashMap<String, Object> setup = new HashMap<>();
        setup.put("discard", RoomService.rooms.get(code).master.discard.get(0));
        setup.put("first", RoomService.rooms.get(code).matchPlayers.get(0).getId());
        return setup;
    }

    @MessageMapping("game/move/{code}")
    public HashMap<String, Object> PLayerMove(@DestinationVariable("code") Integer code, HashMap<String, String> move) {
        MatchPlayer currentPlayer = new MatchPlayer();
        Card currentCard = new Card();
        Integer cardId = Integer.parseInt(move.get("card"));
        String playerId = move.get("player");
        for (MatchPlayer player : RoomService.rooms.get(code).matchPlayers) {
            if (Objects.equals(player.getId(), playerId)) {
                currentPlayer = player;
            }
        }
        for (Card card : currentPlayer.cards) {
            if (Objects.equals(card.getId(), cardId)) {
                currentCard = card;
            }
        }
        RoomService.rooms.get(code).master.discard.add(currentCard);
        currentPlayer.cards.remove(currentCard);
        int currentPlayerPosition = RoomService.rooms.get(code).matchPlayers.indexOf(currentPlayer);
        int nextPlayerPosition = currentPlayerPosition + 1;
        if (currentPlayerPosition == (RoomService.rooms.get(code).matchPlayers.size() - 1)) {
            nextPlayerPosition = 0;
        }
        String nextPlayer = RoomService.rooms.get(code).matchPlayers.get(nextPlayerPosition).getId();
        Card discard = RoomService.rooms.get(code).master.discard.get(RoomService.rooms.get(code).master.discard.size()-1);
        HashMap<String, Object> state = new HashMap<>();
        state.put("discard", discard);
        state.put("current", nextPlayer);
        this.template.convertAndSend("topic/game/move/" + code, state);
        return state;
    }
    @MessageMapping("game/{code}/getfromdesk")
    public HashMap<String, Object> GetFromDesk(@DestinationVariable("code") Integer code,String id){
        int currentPlayerPosition=0;
        for (MatchPlayer player : RoomService.rooms.get(code).matchPlayers) {
            if (Objects.equals(player.getId(), id)) {
                player.AddCard(RoomService.rooms.get(code).master.deck.get(0));
                RoomService.rooms.get(code).master.deck.remove(0);
                currentPlayerPosition = RoomService.rooms.get(code).matchPlayers.indexOf(player);
                }
            }
        int nextPlayerPosition = currentPlayerPosition + 1;
        if (currentPlayerPosition == (RoomService.rooms.get(code).matchPlayers.size() - 1)) {
            nextPlayerPosition = 0;
        }
        Card discard = RoomService.rooms.get(code).master.discard.get(RoomService.rooms.get(code).master.discard.size()-1);
        if(Objects.equals(RoomService.rooms.get(code).matchPlayers.get(currentPlayerPosition).cards.get(RoomService.rooms.get(code).matchPlayers.get(currentPlayerPosition).cards.size() - 1).getType(), discard.getType()) ||
                Objects.equals(RoomService.rooms.get(code).matchPlayers.get(currentPlayerPosition).cards.get(RoomService.rooms.get(code).matchPlayers.get(currentPlayerPosition).cards.size() - 1).getValue(), discard.getValue())) {
            RoomService.rooms.get(code).master.discard.add(RoomService.rooms.get(code).matchPlayers.get(currentPlayerPosition).cards.get(RoomService.rooms.get(code).matchPlayers.get(currentPlayerPosition).cards.size() - 1));
            RoomService.rooms.get(code).matchPlayers.get(currentPlayerPosition).cards.remove(RoomService.rooms.get(code).matchPlayers.get(currentPlayerPosition).cards.size() - 1);
        }
        Card lastDiscard = RoomService.rooms.get(code).master.discard.get(RoomService.rooms.get(code).master.discard.size()-1);
        String nextPlayer = RoomService.rooms.get(code).matchPlayers.get(nextPlayerPosition).getId();
        if(RoomService.rooms.get(code).master.deck.isEmpty())
            RoomService.rooms.get(code).master.RefreshDeck();
        HashMap<String, Object> state = new HashMap<>();
        state.put("discard", lastDiscard);
        state.put("current", nextPlayer);
        this.template.convertAndSend("topic/game/" + code + "/getfromdesk", state);
        return state;
    }
    @MessageMapping("game/end/{code}")
    public Player EndGame(@DestinationVariable("code") Integer code, Player winner){
        RoomService.rooms.remove(code);
        this.template.convertAndSend("topic/game/end/" + code, winner);
        return winner;
    }
}


