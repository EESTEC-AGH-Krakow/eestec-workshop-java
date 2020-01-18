package pl.eestec.workshop.java.controller;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;

import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import pl.eestec.workshop.java.model.User;
import pl.eestec.workshop.java.repository.UserRepository;

import javax.validation.Valid;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

//    @GetMapping
//    public List<User> getAllUsers(@RequestParam(value = "sort_by", defaultValue = "id") String[] sortBy,
//                                  @RequestParam(value = "order_by", defaultValue = "asc") String orderBy) {
//        return userRepository.findAll(Sort.by(Sort.Direction.fromString(orderBy), sortBy));
//    }

    @GetMapping
    public Page<User> getAllUsers(@RequestParam(value = "sort_by", defaultValue = "id") String[] sortBy,
                                  @RequestParam(value = "order_by", defaultValue = "asc") String orderBy,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "limit", defaultValue = "5") int limit,
                                  @RequestParam(value = "name", defaultValue = "id") String name) {

        User user = new User();
        user.setGender("Male");
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher(name, ExampleMatcher.GenericPropertyMatchers.contains());

        Example<User> example = Example.of(user, matcher);
        return userRepository.findAll(example, PageRequest.of(page, limit, Sort.Direction.fromString(orderBy), sortBy));
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
    public ResponseEntity updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()) {
            user.setId(id);
            user.setCreateDate(optionalUser.get().getCreateDate());
            userRepository.save(user);
            return ResponseEntity.accepted().build();
        } else {
            throw new IllegalArgumentException("not found user");
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity validationException(MethodArgumentNotValidException e) {
        e.printStackTrace();
        List<String> fieldErrors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + " - " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(fieldErrors);
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
