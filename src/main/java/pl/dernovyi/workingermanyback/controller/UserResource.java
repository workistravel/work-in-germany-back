package pl.dernovyi.workingermanyback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.dernovyi.workingermanyback.exception.EmailExistException;
import pl.dernovyi.workingermanyback.exception.EmailNotFoundException;
import pl.dernovyi.workingermanyback.exception.PasswordNotCorrectException;
import pl.dernovyi.workingermanyback.exception.handling.ExceptionHandling;
import pl.dernovyi.workingermanyback.exception.UserNotFoundException;
import pl.dernovyi.workingermanyback.model.User;
import pl.dernovyi.workingermanyback.model.UserPrincipal;
import pl.dernovyi.workingermanyback.model.dto.HttpResponse;
import pl.dernovyi.workingermanyback.service.interf.UserService;


import java.security.Principal;
import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static pl.dernovyi.workingermanyback.constant.FileConstant.*;
@RestController
@RequestMapping(path = {"/user", "/"})
public class UserResource extends ExceptionHandling {

    private final UserService userService;

    @Autowired
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/update")
    public ResponseEntity<User> updateUser(@RequestParam("currentEmail") String currentEmail,
                                           @RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("role") String role,
                                           @RequestParam("email") String email,
                                           @RequestParam("biography") String biography,
                                           @RequestParam("profession") String profession,
                                           @RequestParam("telephone") String telephone,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNotLocked") String isNotLocked) throws UserNotFoundException, EmailNotFoundException, EmailExistException {
    User updatedUser = userService.updateUser(currentEmail,  firstName,  lastName,  email, role ,Boolean.parseBoolean(isNotLocked), Boolean.parseBoolean(isActive),  biography,  profession,  telephone);
        return new ResponseEntity<>(updatedUser, OK);
    }

    @GetMapping("/find/{email}")
    public ResponseEntity<User> findUser(@PathVariable("email") String email) throws EmailNotFoundException {
        User userByEmail = userService.findUserByEmail(email);
        return new ResponseEntity<>(userByEmail, OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(){
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, OK);
    }

    @PostMapping("/resetPassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException {
        userService.resetPassword(email);
        return response(OK, EMAIL_SENT_WITH_NEW_PASSWORD + email);
    }
    @PostMapping("/updatePassword")
    public ResponseEntity<HttpResponse> updatePassword(@RequestParam("oldPassword") String oldPassword,
                                                       @RequestParam("newPassword") String newPassword,
                                                       Principal principal) throws EmailNotFoundException, PasswordNotCorrectException {
        userService.updatePassword(principal.getName(), oldPassword, newPassword);
        return response(OK, PASSWORD_WAS_CHANGED);
    }

    @DeleteMapping("/delete/{email}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> delete(@RequestParam("email") String email) throws EmailNotFoundException {
        userService.deleteUser(email);
        return response(NO_CONTENT, USER_DELETED_SUCCESSFULLY);
    }



    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(
                httpStatus.value(),
                httpStatus,
                httpStatus.getReasonPhrase(),
                message);
        return new ResponseEntity<>(body, httpStatus);
    }
}
