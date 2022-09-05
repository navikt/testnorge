package no.nav.testnav.apps.tpsmessagingservice.utils;

import com.ibm.icu.text.Transliterator;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.text.Normalizer;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.Map.Entry;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;

@UtilityClass
public class TranslittereringUtil {

    private final String LATIN_CYRILLIC = "Latin-Russian/BGN";
    private final Transliterator cyrillicToLatinTrans = Transliterator.getInstance(LATIN_CYRILLIC).getInverse();

    private final Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    private final Map<Character, Character> PRINTABLE_BY_NON_PRINTABLE = ofEntries(
            entry('å', '\u0001'),
            entry('Å', '\u0002'),
            entry('ä', '\u0003'),
            entry('Ä', '\u0004'),
            entry('é', '\u0005'),
            entry('É', '\u0006'),
            entry('è', '\u0007'),
            entry('È', '\u0008'),
            entry('ö', '\u000B'),
            entry('Ö', '\u0010'),
            entry('ü', '\u0011'),
            entry('Ü', '\u0012')
    );

    /**
     * Implementerer følgende regler:
     * <ul>
     * <li>{@code äöü éè æøå ÄÖÜ ÉÈ ÆØÅ} forblir uendret</li>
     * <li>Folkeregisterets regler for translitterering mot DSF  https://confluence.adeo.no/download/attachments/229949696/Regler%20for%20translitterering.docx </li>
     * <li>DIFI sin erstatningstabell for samiske tegn https://www.difi.no/fagomrader-og-tjenester/digitalisering-og-samordning/standarder/forslag-og-utredninger/hvorfor-felles-tegnsett#Erstatningstabell </li>
     * <li>Bokstavene i Unicode Basic Latin (ISO 8859-1) og noen enkeltbokstaver i Latin Extended-A og Extended-B</li>
     * </ul>
     * Spesialregler for å håndtere tegn utover overstående regelsett blir laget for hvert enkelt tilfelle, se {@code convertKnownCharactersNotHandledByNormalization()}.
     * Resten ignoreres/bevares. Se read-me for detaljer. <br>
     */
    public static String translitterer(String str) {
        if (str == null) {
            return null;
        }

        val prepared = preprocess(str);
        val normalized = pattern.matcher(Normalizer.normalize(prepared, Normalizer.Form.NFD)).replaceAll("");
        val processed = postprocess(normalized);
        String text = convertKnownCharactersNotHandledByNormalization(processed);
        return cyrillicToLatinTrans.transliterate(text);
    }

    private static String preprocess(String str) {
        // replace wanted characters with non-printable characters to save them from normalization
        for (Entry<Character, Character> entry : PRINTABLE_BY_NON_PRINTABLE.entrySet()) {
            Character original = entry.getKey();
            Character nonPrintable = entry.getValue();
            str = str.replace(nonPrintable.toString(), "");
            str = str.replace(original, nonPrintable);
        }
        return str;
    }

    private String postprocess(String str) {
        // replace non-printable characters with original characters after normalization
        for (Entry<Character, Character> entry : PRINTABLE_BY_NON_PRINTABLE.entrySet()) {
            Character original = entry.getKey();
            Character nonPrintable = entry.getValue();
            str = str.replace(nonPrintable, original);
        }
        return str;
    }

    private String convertKnownCharactersNotHandledByNormalization(String str) {
        // replace characters not covered by normalization
        return str
                .replace('\u0189', 'D') // 'Ɖ' Afrikansk D
                .replace('\u0256', 'd') // 'ɖ' Liten afrikansk d med hale
                .replace('\u00D0', 'D') // 'Ð' Stor eth (islandsk)
                .replace('\u00F0', 'd') // 'ð' Liten eth
                .replace('\u0110', 'D') // 'Đ' Stor D med strek (samisk, slavisk, vietnamesisk, mf.)
                .replace('\u0111', 'd') // 'đ' Liten d med strek
                .replace('\u01E4', 'G') // 'Ǥ' Stor G med strek (samisk)
                .replace('\u01E5', 'g') // Liten g med strek (samisk)
                .replace('ı', 'i') // LATIN SMALL LETTER DOTLESS I U+0131
                .replace('Ł', 'L')
                .replace('ł', 'l')
                .replace('Ŋ', 'N')
                .replace('ŋ', 'n')
                .replace('Ŧ', 'T')
                .replace('ŧ', 't')
                .replace('Ɓ', 'B') // B med krok
                .replace('ɓ', 'b') // b med krok
                .replace('\u0187', 'C') // C med krok
                .replace('\u0188', 'c') // c med krok
                .replace('Ɗ', 'D') // D med krok
                .replace('ɗ', 'd') // d med krok
                .replace('Ɠ', 'G') // G med krok
                .replace('ɠ', 'g') // g med krok
                .replace('Ƙ', 'K') // K med krok
                .replace('ƙ', 'k') // k med krok
                .replace('Ƥ', 'P') // P med krok
                .replace('ƥ', 'p') // p med krok
                .replace('Ƭ', 'T') // T med krok
                .replace('ƭ', 't') // t med krok
                .replace('\u01B3', 'Y') // 'Ƴ', Y med krok U+01B3
                .replace('\u01B4', 'y') // 'ƴ', y med krok U+01B4
                .replace("Þ", "TH")
                .replace("þ", "th")
                .replace("ß", "ss")
                .replace("⁰", "")
                .replace("Ԉ", "(komi lje)")
                .replace("Б", "be") // cyrillisk Komi Lje
                .replace("\u001A", " ") // "substitute" control char (ascii 26)

                // strek symboler, finnes mange fler men tar en så lenge bare de vi har sett (https://en.wikipedia.org/wiki/Dash#Unicode)
                .replace("–", "-") //  Kort Tankestrek til Bindestrek
                .replace("—", "-"); // Lang Tankestrek til Bindestrek
    }
}