package datatransfer;

/**
 * Utility class containing methods to aid in the continuous testing of code.
 * Contains methods to assist in testing the project on different developers
 * machines.
 *
 * @author Lewis Foster
 */
public final class CodeTestUtils {

    private static final String USERNAME_PROPERTY = "storj_username";
    private static final String DEFAULT_USERNAME = "steveswfc@gmail.com";

    private static final String PASSWORD_PROPERTY = "storj_password";
    private static final String DEFAULT_PASSWORD = "testpassword";

    private static final String BASEPATH_PROPERTY = "storj_basepath";
    private static final String DEFAULT_BASEPATH = "http://localhost:6382";

    private static final String ENCRYPTIONKEY_PROPERTY = "storj_encryoptionkey";
    private static final String DEFAULT_ENCRYPTIONKEY = "DefaultKey";

    /**
     * Returns the value of the system property with the key of {@value #ENCRYPTIONKEY_PROPERTY}.
     * If the system property is not set the default value of {@value #DEFAULT_ENCRYPTIONKEY} is used.
     *
     * @return the test encryption key.
     */
    public static String getEncryptionKey() {
        return System.getProperty(ENCRYPTIONKEY_PROPERTY, DEFAULT_ENCRYPTIONKEY);
    }


    /**
     * Returns the value of the system property with the key of
     * {@value #USERNAME_PROPERTY}. If the system property is not set the
     * default of {@value #DEFAULT_USERNAME} is used.
     *
     * @return the test Storj username
     */
    public static String getStorjUsername() {
        return System.getProperty(USERNAME_PROPERTY, DEFAULT_USERNAME);
    }

    /**
     * Returns the value of the system property with the key of
     * {@value #PASSWORD_PROPERTY}. If the system property is not set the
     * default of {@value #DEFAULT_PASSWORD} is used.
     *
     * @return the test Storj password
     */
    public static String getStorjPassword() {
        return System.getProperty(PASSWORD_PROPERTY, DEFAULT_PASSWORD);
    }

    /**
     * Returns the value of the system property with the key of
     * {@value #BASEPATH_PROPERTY}. If the system property is not set the
     * default of {@value #DEFAULT_BASEPATH} is used.
     *
     * @return the test Storj base path
     */
    public static String getStorjBasePath() {
        return System.getProperty(BASEPATH_PROPERTY, DEFAULT_BASEPATH);
    }

}
