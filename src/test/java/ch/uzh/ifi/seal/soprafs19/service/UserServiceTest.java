package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import java.util.ArrayList;


/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
public class UserServiceTest {


    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    public void createUser() {
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");


        User createdUser = userService.createUser(testUser);

        Assert.assertNotNull(createdUser.getToken());
        Assert.assertEquals(createdUser.getStatus(),UserStatus.ONLINE);
        Assert.assertEquals(createdUser, userRepository.findByToken(createdUser.getToken()));
    }

    @Test
    public void getUser(){
        //first create users to get
        User testUserA = new User();
        testUserA.setUsername("testUsernameA");
        testUserA.setPassword("testPasswordA");

        User testUserB = new User();
        testUserB.setUsername("testUsernameB");
        testUserB.setPassword("testPasswordB");

        User testUserAA = userService.createUser(testUserA);
        User testUserBB = userService.createUser(testUserB);

        //add users to a list, that can be asserted
        Iterable<User> users = userService.getUsers();
        ArrayList<User> list = new ArrayList<>();
        for (User user: users) {
            list.add(user);
        }

        //check users one by one from list
        Assert.assertEquals(testUserAA, list.get(0));
        Assert.assertEquals(testUserBB, list.get(1));
    }

    @Test
    public void updateUser() {
        //create new user
        User testUser = new User();
        testUser.setUsername("user");
        testUser.setPassword("pw");
        userService.createUser(testUser);

        //updating user
        testUser.setUsername("new name");
        testUser.setBirthday("01.01.1990");
        userService.updateUser(testUser);

        //assert (identify user over token)
        Assert.assertEquals("new name", userRepository.findByToken(testUser.getToken()).getUsername());
        Assert.assertEquals("01.01.1990", userRepository.findByToken(testUser.getToken()).getBirthday());
    }

    @Test
    public void getUserByToken() {
        //create new user
        User testUser = new User();
        testUser.setUsername("user");
        testUser.setPassword("pw");
        userService.createUser(testUser);
        //recheck the user by get user by token
        User temp = userService.getUserbyToken(testUser.getToken());
        Assert.assertEquals(testUser, temp);
    }

    @Test
    public void getUserById() {
        //create new user
        User testUser = new User();
        testUser.setUsername("user");
        testUser.setPassword("pw");
        userService.createUser(testUser);
        //recheck the user by get user by id
        User temp = userService.getUserById(testUser.getId());
        Assert.assertEquals(testUser, temp);
    }

}
