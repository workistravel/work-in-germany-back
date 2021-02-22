package pl.dernovyi.workingermanyback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import pl.dernovyi.workingermanyback.exception.EmailExistException;
import pl.dernovyi.workingermanyback.exception.EmailNotFoundException;
import pl.dernovyi.workingermanyback.exception.handling.ExceptionHandling;
import pl.dernovyi.workingermanyback.exception.UserNotFoundException;
import pl.dernovyi.workingermanyback.model.dto.HttpResponse;
import pl.dernovyi.workingermanyback.model.User;
import pl.dernovyi.workingermanyback.model.UserPrincipal;
import pl.dernovyi.workingermanyback.model.dto.UserRequest;
import pl.dernovyi.workingermanyback.security.filter.JwtTokenProvider;
import pl.dernovyi.workingermanyback.service.interf.UserService;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static pl.dernovyi.workingermanyback.constant.SecurityConstant.JWT_TOKEN_HEADER;


@RestController
@RequestMapping(path = {"/auth", "/"})
public class AuthResource extends ExceptionHandling {
    private static final String USER_WAS_CREATED = "Пользователь успешно создан";

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AuthResource(UserService userService, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }



    @PostMapping("/register")
    public ResponseEntity<HttpResponse> register(@RequestBody UserRequest user) throws UserNotFoundException, EmailExistException, EmailNotFoundException {
        userService.register(user.getEmail(),user.getFirstName(),user.getLastName(),user.getTelephone());
        return response(CREATED, USER_WAS_CREATED);
    }

    @GetMapping("/login")
    public ResponseEntity<User> login(@RequestBody UserRequest user) throws EmailNotFoundException {
        authenticate(user.getEmail(), user.getPassword());
        User loginUser =  userService.findUserByEmail(user.getEmail());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeader, OK);
    }



    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(
                httpStatus.value(),
                httpStatus,
                httpStatus.getReasonPhrase(),
                message);
        return new ResponseEntity<>(body, httpStatus);
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add( JWT_TOKEN_HEADER , tokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }
    private void authenticate(String email, String password) {
         authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }
}
