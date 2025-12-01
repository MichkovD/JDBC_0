package jm.task.core.jdbc.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
@Data // подумать

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table (name = "user", schema = "public")
public class User implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Exclude
    private Long id;

    @Column
    private String name;

    @Column(name = "last_name")
    private String lastName;

    @Column
    private Byte age;
}
