package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.Null;

@RestController
public class UserController {

    private final UserService service;

    UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/users")
    Iterable<User> all() {
        return service.getUsers();
    }

    @PostMapping("/users")
    User createUser(@RequestBody User newUser) {
        return this.service.createUser(newUser);
    }


    /** LOGIN
     *
     * @param loginuser - stated username and password from frontend
     * @return ResponseEntity - (from springframework, extends HttpEntity (adds HttpStatus code), containing user and status
     */

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<User> login(@RequestBody User loginuser){
            //get user from db with the stated username
            User dbuser = service.getUserbyUserName (loginuser.getUsername());
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
}
