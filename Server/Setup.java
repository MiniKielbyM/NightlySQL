import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.net.URISyntaxException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Base64;

public class Setup {
    public static void main(String[] args) throws URISyntaxException, Exception{
        Properties prop = new Properties();
        File configFile = new File("./.cfg");

        try (FileOutputStream output = new FileOutputStream(configFile)) {
            // Set configuration properties (Key, Value)
            prop.setProperty("db.name", "MyNightlySqlDb");
            prop.setProperty("db.port", "3306");
                    // 1. Initialize KeyPairGenerator for RSA
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            
            // 2. Initialize with a secure 3072-bit key size
            SecureRandom secureRandom = new SecureRandom();
            keyPairGen.initialize(3072, secureRandom);
            
            // 3. Generate the public/private pair
            KeyPair pair = keyPairGen.generateKeyPair();
            
            String publicKey = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
            String privateKey = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
        
            prop.setProperty("server.publicKey", publicKey);
            prop.setProperty("server.privateKey", privateKey);

            // Save the file with header comment
            prop.store(output, "NightlySQL database configuration settings");
            System.out.println("Config file created successfully at: " + configFile.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Error creating config file: " + e.getMessage());
        }
        try {
            // Create the bucket parent directory
            File dbDir = new File("./buckets");
            if (!dbDir.exists()) {
                if (dbDir.mkdir()) {
                    System.out.println("Bucket parent directory created successfully at: " + dbDir.getAbsolutePath());
                } else {
                    System.err.println("Failed to create bucket parent directory.");
                }
            } else {
                System.out.println("Bucket parent directory already exists at: " + dbDir.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Error creating bucket parent directory: " + e.getMessage());
        }
        try {
            // Create the bucket subdirectory
            File dbSubDir = new File("./buckets/main");
            if (!dbSubDir.exists()) {
                if (dbSubDir.mkdir()) {
                    System.out.println("Bucket \"main\" created successfully at: " + dbSubDir.getAbsolutePath());
                } else {
                    System.err.println("Failed to create bucket \"main\".");
                }
            } else {
                System.out.println("Bucket \"main\" already exists at: " + dbSubDir.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Error creating bucket subdirectory: " + e.getMessage());
        }
    }
}
