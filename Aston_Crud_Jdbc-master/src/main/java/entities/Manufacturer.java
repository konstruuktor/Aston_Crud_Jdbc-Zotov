package entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Manufacturer implements Entity {
    private int id;
    private Models name;
}
