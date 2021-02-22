package pl.dernovyi.workingermanyback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.dernovyi.workingermanyback.exception.CompressBytesException;
import pl.dernovyi.workingermanyback.exception.EmailNotFoundException;
import pl.dernovyi.workingermanyback.exception.EmptyFileException;
import pl.dernovyi.workingermanyback.exception.ForbiddenFileException;
import pl.dernovyi.workingermanyback.exception.handling.ExceptionHandling;
import pl.dernovyi.workingermanyback.model.User;
import pl.dernovyi.workingermanyback.service.interf.CvService;
import pl.dernovyi.workingermanyback.service.interf.UserService;

import java.io.IOException;
import java.security.Principal;

import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(path = {"/cv", "/"})
public class CvResource extends ExceptionHandling {
    private UserService userService;
    private CvService cvService;

    @Autowired
    public CvResource(UserService userService, CvService cvService) {
        this.userService = userService;
        this.cvService = cvService;
    }

    @PostMapping("/updateCvProfile")
    public ResponseEntity<String> uploadCv(@RequestParam(value = "Cv") MultipartFile multipartFile, Principal principal) throws EmailNotFoundException, EmptyFileException, ForbiddenFileException, IOException, CompressBytesException {
        String email = principal.getName();
        User userByEmail = userService.findUserByEmail(email);
        String url = cvService.uploadCvProfile( multipartFile, userByEmail);
        return new ResponseEntity<>(url, HttpStatus.OK);
    }

    @GetMapping(path = "/profile/{fileName}", produces = APPLICATION_PDF_VALUE)
    public byte[] getProfileImage( @PathVariable("fileName") String fileName) throws CompressBytesException {
        return cvService.downloadCvProfile(fileName);
    }
}
