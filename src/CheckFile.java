import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CheckFile {

    public static String getCheckSum(String path) {
        try {

            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");

            FileInputStream fileInput = new FileInputStream(path);
            byte[] dataBytes = new byte[1024];

            int bytesRead = 0;

            while ((bytesRead = fileInput.read(dataBytes)) != -1) {
                messageDigest.update(dataBytes, 0, bytesRead);
            }


            byte[] digestBytes = messageDigest.digest();

            StringBuffer sb = new StringBuffer("");

            for (int i = 0; i < digestBytes.length; i++) {
                sb.append(Integer.toString((digestBytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            fileInput.close();
            return sb.toString();


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
