import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
public class Translator {
    private static final String targetLanguage=Main.getTargetLanguage();

    public static String translateHeading(String heading){
        Translate translate = TranslateOptions.getDefaultInstance().getService();
        Translation translation = translate.translate(heading, Translate.TranslateOption.targetLanguage(targetLanguage));
        return translation.getTranslatedText();
    }
}
