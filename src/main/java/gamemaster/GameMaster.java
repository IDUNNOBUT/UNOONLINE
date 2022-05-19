package gamemaster;

import com.example.unoonline.gamecard.Card;
import com.example.unoonline.player.MatchPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameMaster {
    String[] type=new String[] {"zero","one","two","three","four","five","six","seven","eight","nine"};
    String[] value=new String[] {"blue","green","red","yellow"};
    public ArrayList<Card> deck = new ArrayList<>();
    public ArrayList<Card> discard = new ArrayList<>();
   public GameMaster(){
       for (String s : value) {
           deck.add(new Card(deck.size(), "zero", s));
       }
        for (int i=1;i<type.length;i++){
            for (String s : value) {
                deck.add(new Card(deck.size(), type[i], s));
                deck.add(new Card(deck.size(), type[i], s));
            }
        }
       Collections.shuffle(deck);
        discard.add(deck.get(0));
        deck.remove(0);
    }
    public void SetStartHand(MatchPlayer player){
       for(int i=0;i<5;i++){
           player.AddCard(deck.get(0));
           deck.remove(0);
       }
    }
    public void RefreshDeck(){
       if(discard.size()>1){
           List<Card> tempDeck = discard.subList(0, discard.size() - 2);
           System.out.println(tempDeck);
           Collections.shuffle(tempDeck);
           deck.addAll(tempDeck);
           discard.removeAll(tempDeck);
       }
    }
}
