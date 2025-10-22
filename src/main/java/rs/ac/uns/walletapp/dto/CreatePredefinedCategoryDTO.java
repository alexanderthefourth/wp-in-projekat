package rs.ac.uns.walletapp.dto;

import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.walletapp.model.Type;

@Getter
@Setter
public class CreatePredefinedCategoryDTO {
    private String name;
    private Type type;
}
