package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/** USER SERVICE: CONTAINS FUNCTIONS FOR USER CONTROLLER, HAS ACCESS TO REPOSITORY**/

@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Iterable<User> getUsers() {
        return this.userRepository.findAll();
    }

    /** CREATE USER FUNCTION, USED  FOR @PostMapping("/users")
     *
     * @param newUser - user
     * @return newUSER - new user
     */

    public User createUser(User newUser) {

        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.ONLINE);
        //get current date
        String pattern = "MM/dd/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        Date today = Calendar.getInstance().getTime();
        String todayAsString = df.format(today);
        //set creation date
        newUser.setCreationdate(todayAsString);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        System.out.println("User created");
        return newUser;
    }

    /**  GET USER BY USERNAME, USED FOR LOGIN CHECK IN @PostMapping("/login")
     *      @param username - username from login
     *      @return user - user found in jpa database
     */

    public User getUserbyUserName (String username){
        return userRepository.findByUsername(username);
    }
}


