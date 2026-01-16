package technikal.task.fishmarket.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fish_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FishImage {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    private String fileName;

    @ManyToOne
    @JoinColumn(name = "fish_id")
    private Fish fish;
}
