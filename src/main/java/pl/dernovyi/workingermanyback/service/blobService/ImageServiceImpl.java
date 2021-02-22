package pl.dernovyi.workingermanyback.service.blobService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.dernovyi.workingermanyback.exception.CompressBytesException;
import pl.dernovyi.workingermanyback.exception.EmptyFileException;
import pl.dernovyi.workingermanyback.exception.ImageNotFoundException;
import pl.dernovyi.workingermanyback.exception.NotAnImageFileException;
import pl.dernovyi.workingermanyback.model.Image;
import pl.dernovyi.workingermanyback.model.User;
import pl.dernovyi.workingermanyback.repositoty.ImageRepository;
import pl.dernovyi.workingermanyback.repositoty.UserRepository;
import pl.dernovyi.workingermanyback.service.blobService.Compressor;
import pl.dernovyi.workingermanyback.service.interf.ImageService;

import java.io.IOException;
import java.util.Arrays;

import static org.springframework.http.MediaType.*;
import static pl.dernovyi.workingermanyback.constant.FileConstant.*;

@Service
public class ImageServiceImpl implements ImageService {
    private  ImageRepository imageRepository;
    private UserRepository userRepository;
    private Compressor compressor;

    @Autowired
    public ImageServiceImpl(ImageRepository imageRepository,UserRepository userRepository, Compressor compressor) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.compressor = compressor;
    }

    @Override
    public byte[] downloadImageProfile(String fileName) throws CompressBytesException, ImageNotFoundException {
        String imageName = fileName.substring(0, fileName.indexOf("."));

        Image image = imageRepository.findByName(imageName);
        if(image==null){
            throw new ImageNotFoundException(IMAGE_NOT_FOUND);
        }
        return  compressor.decompressBytes(image.getImageBytes());
    }


    @Override
    public String  uploadImageProfile(MultipartFile multipartFile, User user) throws NotAnImageFileException, IOException, CompressBytesException, EmptyFileException {
        String url = "";
        Image image = new Image();
        if(multipartFile.getSize() > 0){
//            разрешенные типы файлов
            if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(multipartFile.getContentType())){
                throw new NotAnImageFileException(multipartFile.getOriginalFilename() + "не является разрешенным файлом. Пожалуйста загрузите допустимый формат (PNG, JPEG, GIF)");
            }
            if(user.getProfileImageUrl().contains(user.getUserId())){
                 image = imageRepository.findByName(user.getUserId());
            }
            image.setImageBytes(compressor.compressBytes(multipartFile.getBytes()));
            image.setName(user.getUserId());
            imageRepository.save(image);
            url = ServletUriComponentsBuilder.fromCurrentContextPath().path( USER_IMAGE_PATH + FORWARD_SLASH+ user.getUserId() + DOT + compressor.getTypeFile(multipartFile) ).toUriString();
            user.setProfileImageUrl(url);
            userRepository.save(user);
        }else {
            throw new EmptyFileException(EMPTY_FILE_EXCEPTION);
        }
        return url;
    }


}
