package com.example.unoonline.gamecard;

public class Card {
    private Integer id;
    private String type;
    private String value;

    public Card(){}
    public Card(Integer id,String type, String value){
        setId(id);
        setType(type);
        setValue(value);
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
