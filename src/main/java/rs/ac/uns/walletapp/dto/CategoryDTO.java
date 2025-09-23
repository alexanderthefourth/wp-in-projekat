package rs.ac.uns.walletapp.dto;

import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.walletapp.model.Category;
import rs.ac.uns.walletapp.model.Type;

@Getter
@Setter
public class CategoryDTO {
    private int id;
    private String name;
    private Type type;
    private boolean predefined;
    private int userId;

    public CategoryDTO(Category category) {
        id =  category.getId();
        name = category.getName();
        type = category.getType();
        predefined = category.isPredefined();
        userId = category.getUser().getId();
    }
}
