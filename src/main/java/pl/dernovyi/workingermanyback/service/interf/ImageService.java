package pl.dernovyi.workingermanyback.service.interf;

import org.springframework.web.multipart.MultipartFile;
import pl.dernovyi.workingermanyback.exception.CompressBytesException;
import pl.dernovyi.workingermanyback.exception.EmptyFileException;
import pl.dernovyi.workingermanyback.exception.ImageNotFoundException;
import pl.dernovyi.workingermanyback.exception.NotAnImageFileException;
import pl.dernovyi.workingermanyback.model.User;

import java.io.IOException;

public interface ImageService {

    String  uploadImageProfile(MultipartFile multipartFile, User user) throws NotAnImageFileException, IOException, CompressBytesException, EmptyFileException;

    byte[] downloadImageProfile(String fileName) throws CompressBytesException, ImageNotFoundException;
}
