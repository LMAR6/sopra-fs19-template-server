package ch.uzh.ifi.seal.soprafs19.controller;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * AUTHENTICATION
 * needed for get and put mappings
 * using user tokens
 */

@RestController
public class UserController {

    @Autowired
    private final UserService service;
    UserController(UserService service) {
        this.service = service;
    }

    /**
     * USERS (not id specific)
     */

     @GetMapping("/users")
    //Request Header: looks for token in header and saves as string token
    ResponseEntity<Iterable<User>> all(@RequestHeader(value = "token") String token, HttpServletResponse response) {
        //if a user with this token exists, get /users is allowed
        if (service.getUserbyToken(token) != null) {
            System.out.println("GET /users");
            return new ResponseEntity<>(service.getUsers(), HttpStatus.OK);
        }
        else {
            throw new IllegalArgumentException("AUTH FAILED");
        }
    }

    @PostMapping("/users")
    @ResponseBody
    ResponseEntity<String> createUser(@RequestBody User newUser) {
        //create a new user, used in registering
        System.out.println("POST /users");
        //check if user already exists, if yes, return conflict status
        if (this.service.getUserbyUserName(newUser.getUsername()) != null) {
            return new ResponseEntity<>("not allowed", HttpStatus.CONFLICT);
        }
        //if user does not exist, continue template
        else {
            //needed string return according to mapping
            User currentUser = this.service.createUser(newUser);
            String response = "/users/"+ currentUser.getId().toString();
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }


    /** LOGIN
     *
     * @param loginuser - stated username and password from frontend
     * @return ResponseEntity - (from springframework, extends HttpEntity (adds HttpStatus code), containing user and status
     */

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<User> login(@RequestBody User loginuser){
            //DEBUGGING
            System.out.println("ID from login: " + loginuser.getId());
            System.out.println("Username from login: " + loginuser.getUsername());
            System.out.println("PW: " + loginuser.getPassword());
            //get user from db with the stated credentials
            User dbuser = service.getUserbyUserName (loginuser.getUsername());
            //debugging
            System.out.println("ID:" +dbuser.getId());
            System.out.println("PW:" +dbuser.getPassword());
            //check if user from db and stated user have same login credentials, if yes, login successful
            if (loginuser.getUsername().equals(dbuser.getUsername()) && loginuser.getPassword().equals(dbuser.getPassword())){
                System.out.println("Login successful");
                return new ResponseEntity<>(dbuser, HttpStatus.OK);
            } //if not, login failed
            else {
                System.out.println("Login failed");
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
    }

    /**
     * SETTINGS
     */


    @GetMapping("/users/{userId}")
    //Request Header: looks for token in header and saves as string token
    ResponseEntity<User> getId(@PathVariable long userId, @RequestHeader(value = "token") String token, HttpServletResponse response) {
        //if a user with this token exists, get /users is allowed
        if (service.getUserbyToken(token) != null) {
            System.out.println("GET /users/userid");
            return new ResponseEntity<>(this.service.getUserById(userId), HttpStatus.OK);
        }
        else {
            throw new IllegalArgumentException("AUTH FAILED");
        }
    }


    @PutMapping("/users/{userId}")
    @ResponseBody
    //Request Header: looks for token in header and saves as string token
        public ResponseEntity<User> update(@RequestBody User user, @RequestHeader(value = "token") String token, HttpServletResponse response){
        if (service.getUserbyToken(token) != null) {
            //if user does not exist return 404
            if(service.getUserById(user.getId())==null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            long id = user.getId();
            //debugging
            System.out.println("ID is: " + id);
            User current = service.getUserbyToken(user.getToken());
            if (user.getUsername() != null) {
                //check if username is free
                if (service.getUserbyUserName(user.getUsername()) == null) {
                    current.setUsername(user.getUsername());
                } else {
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
                }
            }

            if (user.getBirthday() != null) {
                current.setBirthday(user.getBirthday());
            }
            if (user.getStatus() != null) {
                current.setStatus(user.getStatus());
            }
            service.updateUser(current);
            return new ResponseEntity<>(user, HttpStatus.NO_CONTENT);
            }
        else {
            throw new IllegalArgumentException("AUTH FAILED");
        }
    }
}

