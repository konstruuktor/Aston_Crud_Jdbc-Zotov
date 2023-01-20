package entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Market implements Entity {
    private int id;
    private Manufacturer manufacturer;
    private Model model;
    private double price;
    private int amount;
}
