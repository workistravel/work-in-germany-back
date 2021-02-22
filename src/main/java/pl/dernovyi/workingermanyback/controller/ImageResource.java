package pl.dernovyi.workingermanyback.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.dernovyi.workingermanyback.exception.*;
import pl.dernovyi.workingermanyback.exception.handling.ExceptionHandling;
import pl.dernovyi.workingermanyback.model.User;
import pl.dernovyi.workingermanyback.service.interf.ImageService;
import pl.dernovyi.workingermanyback.service.interf.UserService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Principal;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static pl.dernovyi.workingermanyback.constant.FileConstant.TEMP_PROFILE_IMAGE_BASE_URL;

@RestController
@RequestMapping(path = {"/images", "/"})
public class ImageResource extends ExceptionHandling {
    private ImageService imageService;
    private UserService userService;


    public ImageResource(ImageService imageService,UserService userService) {
        this.imageService = imageService;
        this.userService = userService;
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<String> updateProfileImage(@RequestParam(value = "Image") MultipartFile multipartFile, Principal principal) throws EmailNotFoundException, CompressBytesException, NotAnImageFileException, IOException, EmptyFileException {
        String email = principal.getName();
        User userByEmail = userService.findUserByEmail(email);
        String url = imageService.uploadImageProfile(multipartFile, userByEmail);
        return new ResponseEntity<>(url,  OK);
    }


    @GetMapping(path = "/profile/{fileName}", produces = {IMAGE_JPEG_VALUE , IMAGE_PNG_VALUE})
    public byte[] getProfileImage( @PathVariable("fileName") String fileName) throws CompressBytesException, ImageNotFoundException {

        return imageService.downloadImageProfile( fileName);
    }

    //   для дефолтного фото
    @GetMapping(path = "/default/{name}", produces = {IMAGE_JPEG_VALUE , IMAGE_PNG_VALUE})
    public byte[] getTemporaryProfileImage(@PathVariable("name") String name) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + name);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(InputStream inputStream = url.openStream()){
            int bytesRead;
            byte[] chunk = new byte[1024];
            while ((bytesRead = inputStream.read(chunk)) > 0){
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

}
