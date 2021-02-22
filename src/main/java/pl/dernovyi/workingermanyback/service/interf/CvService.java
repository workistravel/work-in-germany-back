package pl.dernovyi.workingermanyback.service.interf;

import org.springframework.web.multipart.MultipartFile;
import pl.dernovyi.workingermanyback.exception.CompressBytesException;
import pl.dernovyi.workingermanyback.exception.EmptyFileException;
import pl.dernovyi.workingermanyback.exception.ForbiddenFileException;
import pl.dernovyi.workingermanyback.model.User;

import java.io.IOException;

public interface CvService {
    String  uploadCvProfile(MultipartFile multipartFile, User user) throws EmptyFileException, ForbiddenFileException, IOException, CompressBytesException;

    byte[] downloadCvProfile(String fileName) throws CompressBytesException;
}
