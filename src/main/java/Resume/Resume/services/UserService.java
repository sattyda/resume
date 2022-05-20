package Resume.Resume.services;

import Resume.Resume.entity.LoginResponse;
import Resume.Resume.entity.User;
import Resume.Resume.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public boolean save(User user) {
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e){
            return false;
        }

    }

    public LoginResponse login(User user) {

        LoginResponse loginResponse = new LoginResponse();
        List<User> users = userRepository.findByEmail(user.getEmail());

        if(users.size() == 0){
            loginResponse.setStatus("failure");
            loginResponse.setMessage("Email not registered!");
            return loginResponse;
        }

        User databaseUser = users.get(0);

        if(user.getPassword().equals( databaseUser.getPassword())){
            loginResponse.setStatus("success");
            loginResponse.setMessage("Logged in Success!");
            return loginResponse;
        } else {
            loginResponse.setStatus("failure");
            loginResponse.setMessage("Password doesn't match!");
            return loginResponse;
        }

    }

    public boolean isExist(String email) {
        List<User> users = userRepository.findByEmail(email);

        if(users.size() > 0){
            return true;
        }

        return false;
    }

    public void update(Long id, String mylink) {
        userRepository.update( mylink , id);
    }
}
