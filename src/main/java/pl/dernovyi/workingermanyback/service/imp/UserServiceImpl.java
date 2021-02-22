package pl.dernovyi.workingermanyback.service.imp;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Transient;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.dernovyi.workingermanyback.enumaration.Role;
import pl.dernovyi.workingermanyback.exception.EmailExistException;
import pl.dernovyi.workingermanyback.exception.EmailNotFoundException;
import pl.dernovyi.workingermanyback.exception.PasswordNotCorrectException;
import pl.dernovyi.workingermanyback.exception.UserNotFoundException;
import pl.dernovyi.workingermanyback.model.User;
import pl.dernovyi.workingermanyback.model.UserPrincipal;
import pl.dernovyi.workingermanyback.repositoty.UserRepository;
import pl.dernovyi.workingermanyback.service.interf.UserService;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.springframework.http.MediaType.*;
import static pl.dernovyi.workingermanyback.constant.FileConstant.*;
import static pl.dernovyi.workingermanyback.constant.UserImplConstant.*;
@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {
    private Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private LoginAttemptService loginAttemptService;
    private BCryptPasswordEncoder passwordEncoder;
    private EmailGridService emailGridService;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, LoginAttemptService loginAttemptService, BCryptPasswordEncoder passwordEncoder, EmailGridService emailGridService) {
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
        this.passwordEncoder =  passwordEncoder;
        this.emailGridService = emailGridService;
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email);
        if(user == null){
            LOGGER.error(NO_USER_FOUND_BY_EMAIL + email);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_EMAIL + email);

        }else {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginData());
            user.setLastLoginData(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info(RETURNING_FOUND_USER_BY_EMAIL + email);
            return userPrincipal;
        }
    }

    @Override
    public void register(String email, String firstName, String lastName, String telephone) throws UserNotFoundException, EmailExistException, EmailNotFoundException {
        validateEmail(EMPTY , email);
        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setPassword(encodedPassword);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setTelephone(telephone);
        user.setJoinDate(new Date());
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(Role.ROLE_USER.name());
        user.setAuthorities(Role.ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(firstName));
        emailGridService.sendNewPasswordEmail(user.getFirstName() +" "+ user.getLastName() , password, email);
        this.userRepository.save(user);
//        удалить
        LOGGER.info("Новый пароль пользователя " + password);
    }
    @Override
    public User updateUser(String currentEmail, String newFirstName, String newLastName, String newEmail, String role, boolean isNotLocked, boolean isActive, String biography, String profession, String telephone) throws UserNotFoundException, EmailExistException, EmailNotFoundException {
        User currentUser = validateEmail(currentEmail, newEmail);
        if (!currentEmail.equalsIgnoreCase(newEmail)){
            currentUser.setEmail(newEmail);
            String password = generatePassword();
            String encodedPassword = encodePassword(password);
            currentUser.setPassword(encodedPassword);
            emailGridService.sendNewPasswordEmail(currentUser.getFirstName() +" "+ currentUser.getLastName() , password, newEmail);
        }
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setTelephone(telephone);
        currentUser.setBiography(biography);
        currentUser.setProfession(profession);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNotLocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        this.userRepository.save(currentUser);
        return currentUser;
    }

    private String getTemporaryProfileImageUrl(String name) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path( DEFAULT_USER_IMAGE_PATH + FORWARD_SLASH + name).toUriString();
    }

    @Override
    public User updateProfileImage(String userId, MultipartFile profileImage) {
        return null;
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserByEmail(String email) throws EmailNotFoundException {
        User user = userRepository.findUserByEmail(email);
        if(user ==null){
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        }
        return user;
    }

    @Override
    public void deleteUser(String email) throws EmailNotFoundException {
        User user = findUserByEmail(email);
        userRepository.deleteByEmail(user.getEmail());

    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException {
        User user = findUserByEmail(email);
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        userRepository.save(user);
        emailGridService.sendNewPasswordEmail(user.getFirstName() +" "+ user.getLastName() ,password, email);
    }

    @Override
    public void updatePassword(String email, String oldPassword, String newPassword) throws EmailNotFoundException, PasswordNotCorrectException {
        User user = findUserByEmail(email);
        if(!passwordEncoder.matches(oldPassword, user.getPassword())){
            throw new PasswordNotCorrectException(YOUR_OLD_PASSWORD_NOT_CORRECT);
        }
        user.setPassword(encodePassword(newPassword));
        userRepository.save(user);
        emailGridService.sendNewPasswordEmail(user.getFirstName() +" "+ user.getLastName() ,newPassword, email);
    }

    private User validateEmail(String currentEmail, String newEmail) throws UserNotFoundException, EmailExistException, EmailNotFoundException {
        User userByEmail = userRepository.findUserByEmail(newEmail);
        if(StringUtils.isNoneBlank(currentEmail)){
            User currentUser = userRepository.findUserByEmail(currentEmail);
            if(currentUser == null){
                throw new UserNotFoundException(NO_USER_FOUND_BY_EMAIL + currentEmail);
            }
            if(userByEmail != null && !currentUser.getId().equals(userByEmail.getId())){
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        }else {
            if(userByEmail != null ){
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return  null;
        }

    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(14);
    }
    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(14);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }



    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());

    }
    private void validateLoginAttempt(User user) {
        if(user.isNotLocked()){
            if(loginAttemptService.hasExceededMaxAttempts(user.getEmail())){
                user.setNotLocked(false);
            }else {
                user.setNotLocked(true);
            }
        }else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getEmail());
        }
    }
}
