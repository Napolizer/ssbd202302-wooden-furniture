package pl.lodz.p.it.ssbd2023.ssbd02.utils.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;

public final class FileUtils {

  public static final String JPG = ".jpg";
  public static final String JPEG = ".jpeg";
  public static final String PNG = ".png";

  public static byte[] readImageFromFileInputStream(InputStream inputStream, FormDataContentDisposition fileMetaData) {
    if (inputStream == null) {
      throw ApplicationExceptionFactory.createProductCreateDtoValidationException();
    }
    String fileName = fileMetaData.getFileName();
    if (fileName != null
            && !(fileName.toLowerCase().endsWith(JPG)
            || fileName.toLowerCase().endsWith(JPEG)
            || fileName.toLowerCase().endsWith(PNG))) {
      throw ApplicationExceptionFactory.createInvalidImageFileFormatException();
    }
    byte[] image;
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      byte[] buffer = new byte[8192];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
      image = outputStream.toByteArray();
    } catch (IOException e) {
      throw ApplicationExceptionFactory.createUnknownErrorException(e);
    }
    return image;
  }

}
