import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.util.logging.Level;
import java.util.logging.Logger;


public class Translator {

    public static String targetLanguage=Main.getTargetLanguage();
    private static final String API_KEY = "AIzaSyCYReFHt3ZtOA-wGR3s_A6mGXvMV-pcAG0";

    static {
        Logger logger = Logger.getLogger(TranslateOptions.class.getName());
        logger.setLevel(Level.SEVERE);
    }

    public static String translateHeading(String heading) {
        Translate translate = TranslateOptions.newBuilder().setApiKey(API_KEY).build().getService();
        Translation translation = translate.translate(heading, Translate.TranslateOption.targetLanguage(targetLanguage));
        return translation.getTranslatedText();
    }
}
