package pl.dernovyi.workingermanyback.service.blobService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.dernovyi.workingermanyback.exception.CompressBytesException;
import pl.dernovyi.workingermanyback.exception.EmptyFileException;
import pl.dernovyi.workingermanyback.exception.ForbiddenFileException;
import pl.dernovyi.workingermanyback.exception.NotAnImageFileException;
import pl.dernovyi.workingermanyback.model.CV;
import pl.dernovyi.workingermanyback.model.User;
import pl.dernovyi.workingermanyback.repositoty.CvRepository;
import pl.dernovyi.workingermanyback.repositoty.UserRepository;
import pl.dernovyi.workingermanyback.service.interf.CvService;

import java.io.IOException;
import java.util.Arrays;

import static org.springframework.http.MediaType.*;
import static pl.dernovyi.workingermanyback.constant.FileConstant.*;

@Service
public class CvServiceImpl implements CvService {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private UserRepository userRepository;
    private CvRepository cvRepository;
    private Compressor compressor;

    public CvServiceImpl(UserRepository userRepository, CvRepository cvRepository,Compressor compressor) {
        this.userRepository = userRepository;
        this.cvRepository = cvRepository;
        this.compressor = compressor;
    }


    @Override
    public String  uploadCvProfile(MultipartFile multipartFile, User user) throws EmptyFileException, ForbiddenFileException, IOException, CompressBytesException {
        String url = "";
        CV cv  = new CV();
        if(multipartFile.getSize()>1){
            if(!Arrays.asList(APPLICATION_PDF_VALUE).contains(multipartFile.getContentType())){
                throw new ForbiddenFileException(multipartFile.getOriginalFilename() + "не является разрешенным файлом. Пожалуйста загрузите допустимый формат (PDF)");
            }
            if(user.getCv()!= null){
                if(user.getCv().contains(user.getUserId())){
                    cv = cvRepository.findByName(user.getUserId());
                }
            }
                cv.setName(user.getUserId());
                cv.setImageBytes(compressor.compressBytes(multipartFile.getBytes()));
                cvRepository.save(cv);
                url = ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_CV_PATH + FORWARD_SLASH + user.getUserId() + DOT + compressor.getTypeFile(multipartFile) ).toUriString();
                user.setCv(url);
                userRepository.save(user);
        }else {
            throw new EmptyFileException(EMPTY_FILE_EXCEPTION);
        }
        return url;
    }

    @Override
    public byte[] downloadCvProfile(String fileName) throws CompressBytesException {
        String cvName = fileName.substring(0, fileName.indexOf("."));
        CV cv = cvRepository.findByName(cvName);
        return compressor.decompressBytes(cv.getImageBytes());
    }



}
