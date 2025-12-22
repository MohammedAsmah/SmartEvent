package com.SmartEvent.SmartEvent.Model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "Geusts")
public class Geust extends Person{
    public  Geust(){
        super();
    }
    public Geust(String firstName, String lastName,String email){
        super(firstName,lastName,email);
    }
}
