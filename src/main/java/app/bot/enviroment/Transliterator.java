package app.bot.enviroment;

import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class Transliterator {
        private static final Map<Character, String> translitMap = Collections.synchronizedMap(new HashMap<>(){{
        put('А', "A");
        put('Б', "B");
        put('В', "V");
        put('Г', "G");
        put('Д', "D");
        put('Е', "E");
        put('Ё', "E");
        put('Ж', "ZH");
        put('З', "Z");
        put('И', "I");
        put('Й', "Y");
        put('К', "K");
        put('Л', "L");
        put('М', "M");
        put('Н', "N");
        put('О', "O");
        put('П', "P");
        put('Р', "R");
        put('С', "S");
        put('Т', "T");
        put('У', "U");
        put('Ф', "F");
        put('Х', "KH");
        put('Ц', "TS");
        put('Ч', "CH");
        put('Ш', "SH");
        put('Щ', "SHCH");
        put('Ъ', "");
        put('Ы', "Y");
        put('Ь', "");
        put('Э', "E");
        put('Ю', "YU");
        put('Я', "YA");
        put('а', "a");
        put('б', "b");
        put('в', "v");
        put('г', "g");
        put('д', "d");
        put('е', "e");
        put('ё', "e");
        put('ж', "zh");
        put('з', "z");
        put('и', "i");
        put('й', "y");
        put('к', "k");
        put('л', "l");
        put('м', "m");
        put('н', "n");
        put('о', "o");
        put('п', "p");
        put('р', "r");
        put('с', "s");
        put('т', "t");
        put('у', "u");
        put('ф', "f");
        put('х', "kh");
        put('ц', "ts");
        put('ч', "ch");
        put('ш', "sh");
        put('щ', "shch");
        put('ъ', "");
        put('ы', "y");
        put('ь', "");
        put('э', "e");
        put('ю', "yu");
        put('я', "ya");
    }});

    public static synchronized String transliterate(String input) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            String replacement = translitMap.get(c);
            if (replacement == null) {
                output.append(c);
            } else {
                output.append(replacement);
            }
        }
        return output.toString();
    }
}