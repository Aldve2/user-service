package yps.systems.ai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yps.systems.ai.model.User;
import yps.systems.ai.repository.IUserRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/command/userService")
public class UserController {

    private final IUserRepository userRepository;

    @Autowired
    public UserController(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/{elementId}")
    ResponseEntity<User> getByElementId(@PathVariable String elementId) {
        Optional<User> userOptional = userRepository.findById(elementId);
        return userOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{username}")
    ResponseEntity<User> getByUsername(@PathVariable String username) {
        Optional<User> userOptional = userRepository.findUserByUsername(username);
        return userOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/byPersonElementId/{personElementId}")
    ResponseEntity<User> getByPersonElementId(@PathVariable String personElementId) {
        User user = userRepository.getUserByPersonElementId(personElementId);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{personElementId}")
    ResponseEntity<String> save(@RequestBody User user, @PathVariable String personElementId) {
        User userSaved = userRepository.save(user);
        userRepository.setUserRelationTo(personElementId, user.getElementId());
        return new ResponseEntity<>(userSaved.getElementId(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{elementId}")
    public ResponseEntity<String> delete(@PathVariable String elementId) {
        Optional<User> userOptional = userRepository.findById(elementId);
        if (userOptional.isPresent()) {
            userRepository.deleteUserRelation(elementId);
            userRepository.deleteById(elementId);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("User not founded", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{elementId}")
    public ResponseEntity<String> update(@PathVariable String elementId, @RequestBody User user) {
        Optional<User> userOptional = userRepository.findById(elementId);
        if (userOptional.isPresent()) {
            user.setElementId(userOptional.get().getElementId());
            userRepository.save(user);
            return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("User not founded", HttpStatus.NOT_FOUND);
    }

}
