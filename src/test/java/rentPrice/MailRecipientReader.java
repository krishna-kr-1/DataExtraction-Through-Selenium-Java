package rentPrice;

import java.io.FileInputStream;
import java.util.Properties;

public class MailRecipientReader {
	public static Properties loadMailRecipients(String filePath) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            props.load(fis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return props;
    }
}
