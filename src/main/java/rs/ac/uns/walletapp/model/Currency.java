package rs.ac.uns.walletapp.model;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@Entity
public class Currency {
    @Id
    private String name;
    private BigDecimal value;

    public Currency(){}

}
