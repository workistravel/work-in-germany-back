package pl.dernovyi.workingermanyback.service.blobService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.dernovyi.workingermanyback.exception.CompressBytesException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
@Service
public class Compressor {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    public byte[] compressBytes(byte[] data) throws CompressBytesException {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()){
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0,count);
        }
        try{
            outputStream.close();
        }catch (IOException ex){
            LOGGER.error("Cannot compress bytes");
            throw new CompressBytesException("Cannot compress image");
        }
        return outputStream.toByteArray();
    }

    public byte[] decompressBytes(byte[] data) throws CompressBytesException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try{
            while (!inflater.finished()){
                int count = inflater.inflate(buffer);
                outputStream.write(buffer,0,count);
            }
            outputStream.close();
        }catch (IOException | DataFormatException ex){
            LOGGER.error("Cannot dec bytes");
            throw new CompressBytesException("Cannot decompress image");
        }
        return outputStream.toByteArray();
    }

    public String getTypeFile(MultipartFile multipartFile) {
        Pattern pattern = Pattern.compile("[^\\/]*$");
        Matcher matcher = pattern.matcher(multipartFile.getContentType());

        String ext = null;
        if (matcher.find()) {
            ext = matcher.group();
        }
        return ext;
    }
}
