import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class TranslatorTest {
    private Translator translator;
    String englishHeading = "english heading";
    String englishToGerman = "englische Überschrift";
    String deutscheUeberschrift = "deutsche Ueberschrift";
    String germanToEnglish = "German heading";

    @BeforeEach
    public void setUp(){
        this.translator=new Translator();
    }

    @Test
    public void testEnglishToGerman(){
        Translator.targetLanguage = "de";
        assertEquals(englishToGerman, translator.translateHeading(englishHeading));
    }

    @Test
    public void testGermanToEnglish(){
        Translator.targetLanguage = "en";
        assertEquals(germanToEnglish, translator.translateHeading(deutscheUeberschrift));
    }

    @AfterEach
    public void tearDown(){
        this.translator=null;
    }
}
