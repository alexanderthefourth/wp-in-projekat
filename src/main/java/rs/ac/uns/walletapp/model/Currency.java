package rs.ac.uns.walletapp.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;
import java.time.LocalDate;

@Entity
public class Currency implements Serializable {
    @Id
    private String name;
    private double value;

    public Currency(){}

    public Currency(String name, double value){
        this.name = name;
        this.value = value;
    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public double getValue() {return value;}
    public void setValue(double value) {this.value = value;}
}
