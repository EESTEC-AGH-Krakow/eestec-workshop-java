package pl.eestec.workshop.java.controller;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.eestec.workshop.java.model.User;
import pl.eestec.workshop.java.repository.UserRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping(value = "/{ids}")
    public List<User> getUsers(@PathVariable List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    @PostMapping
    public User createUser(@RequestBody @Valid User user) {
        return userRepository.save(user);
    }

    @PutMapping(value = "/{id}")
    public void updateUser(@PathVariable Long id, @RequestBody @Valid User user) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()) {
            user.setId(id);
            user.setCreateDate(optionalUser.get().getCreateDate());
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("not found user");
        }
    }

    @DeleteMapping(value = "/{ids}")
    public void deleteUser(@PathVariable List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        userRepository.deleteAll(users);
    }

    @DeleteMapping
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }
}
