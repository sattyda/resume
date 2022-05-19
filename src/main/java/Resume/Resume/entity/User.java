package Resume.Resume.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Size( min = 4 , max = 20 , message = "Name must have minimum 4 and max 20 characters")
    String name;

    @Email( message = "Invalid Email")
    @Column( unique = true )
    String email;

    @Size( min = 4 , max = 20 , message = "Name must have minimum 4 and max 20 characters")
    String password;

    String resume;


}
