package com.example.unoonline.controller;

import com.example.unoonline.player.Player;
import com.example.unoonline.room.Room;
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
@RequestMapping("")
public class PlayersController {
    @Autowired
    private SimpMessagingTemplate template;

    @PostMapping(path = "/createroom")
    public Integer CreateNewRoom(){
        Integer code = RoomService.getNewKey();
        RoomService.rooms.put(code,new Room());
        return code;
    }
    @PostMapping(path="/allowconnect")
    public boolean AllowConnect(@RequestBody String code){
        return RoomService.AllowConnect(code);
    }

    @MessageMapping("room/{code}")
    public ArrayList<Player> AddPlayer(@DestinationVariable ("code") Integer code, Player player){
        boolean alreadyExist = false;
        for (Player user : RoomService.rooms.get(code).players) {
            if (Objects.equals(user.getId(), player.getId())) {
                alreadyExist = true;
                break;
            }
        }
        if(!alreadyExist) {
            RoomService.rooms.get(code).Add(player);
        }
        this.template.convertAndSend("room/"+code,RoomService.rooms.get(code).players);
        return RoomService.rooms.get(code).players;
    }

}
