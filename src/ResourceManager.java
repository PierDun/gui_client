import java.util.Locale;
import java.util.ResourceBundle;

class ResourceManager {
    private ResourceBundle resourceBundle;
    private String path;

    ResourceManager (String path, String locale) {
        this.path = path;
        changeLocale(locale);
    }

    String getString(String key) {
        try {
            return new String(resourceBundle.getString(key).getBytes("ISO-8859-1"), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    void changeLocale(String locale) {
        resourceBundle = ResourceBundle.getBundle(path, Locale.forLanguageTag(locale));
    }
}
