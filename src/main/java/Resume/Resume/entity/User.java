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

    @Size( min = 4 , max = 20 , message = "Name Error! must have minimum 4 and max 20 characters")
    String name;

    @Email( message = "Email Error!")
    @Column( unique = true )
    String email;

    @Size( min = 4 , message = "Password Error! must have minimum 4 and max 20 characters")
    String password;

    String username;

    Boolean enabled = true;

    String role;

    String resume;
}
//// --- 100 --== ram == processor ==