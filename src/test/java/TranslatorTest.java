import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class TranslatorTest {
    String englishHeading = "english heading";
    String englishToGerman = "englische Überschrift";
    String deutscheUeberschrift = "deutsche Ueberschrift";
    String germanToEnglish = "German heading";

    @Test
    public void testEnglishToGerman(){
        Translator.targetLanguage = "de";
        assertEquals(englishToGerman, Translator.translateHeading(englishHeading));
    }

    @Test
    public void testGermanToEnglish(){
        Translator.targetLanguage = "en";
        assertEquals(germanToEnglish, Translator.translateHeading(deutscheUeberschrift));
    }
}
