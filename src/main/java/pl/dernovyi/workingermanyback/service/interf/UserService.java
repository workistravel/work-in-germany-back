package pl.dernovyi.workingermanyback.service.interf;

import org.springframework.web.multipart.MultipartFile;
import pl.dernovyi.workingermanyback.exception.EmailExistException;
import pl.dernovyi.workingermanyback.exception.EmailNotFoundException;
import pl.dernovyi.workingermanyback.exception.PasswordNotCorrectException;
import pl.dernovyi.workingermanyback.exception.UserNotFoundException;
import pl.dernovyi.workingermanyback.model.User;

import java.util.List;

public interface UserService  {

    void register(String email, String firstName, String lastName, String telephone) throws UserNotFoundException, EmailExistException, EmailNotFoundException;

    List<User> getUsers();

    User findUserByEmail(String email) throws EmailNotFoundException;

    User updateUser(String currentEmail, String newFirstName, String newLastName, String newEmail, String role, boolean isNotLocked, boolean isActive, String biography, String profession, String telephone) throws UserNotFoundException, EmailExistException, EmailNotFoundException;

    void deleteUser(String email) throws EmailNotFoundException;

    void resetPassword(String email) throws EmailNotFoundException;

    User updateProfileImage(String userId, MultipartFile profileImage);

    void updatePassword(String email, String oldPassword, String newPassword) throws EmailNotFoundException, PasswordNotCorrectException;


}
