package rs.ac.uns.walletapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Currency {
    @Id
    private String name;
    private double value;

    public Currency(){}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public double getValue() {return value;}
    public void setValue(double value) {this.value = value;}
}
