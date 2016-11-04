// 
// Decompiled by Procyon v0.5.30
// 

package top.vk.core.util;

public class StandardizeSymbol
{
    static boolean isUppercase;
    
    static {
        StandardizeSymbol.isUppercase = false;
    }
    
    public static String standardize(final String str) {
        String transformed = "";
        for (int i = 0; i < str.length(); ++i) {
            final char temp = str.charAt(i);
            transformed = String.valueOf(transformed) + getAlphabetNumber(temp);
        }
        return transformed;
    }
    
    public static char getAlphabetNumber(final char ch) {
        if (!StandardizeSymbol.isUppercase) {
            if (ch == 'A' || ch == '\uff21' || ch == 'a' || ch == '\uff41') {
                return 'a';
            }
            if (ch == 'B' || ch == '\uff22' || ch == 'b' || ch == '\uff42') {
                return 'b';
            }
            if (ch == 'C' || ch == '\uff23' || ch == 'c' || ch == '\uff43') {
                return 'c';
            }
            if (ch == 'D' || ch == '\uff24' || ch == 'd' || ch == '\uff44') {
                return 'd';
            }
            if (ch == 'E' || ch == '\uff25' || ch == 'e' || ch == '\uff45') {
                return 'e';
            }
            if (ch == 'F' || ch == '\uff26' || ch == 'f' || ch == '\uff46') {
                return 'f';
            }
            if (ch == 'G' || ch == '\uff27' || ch == 'g' || ch == '\uff47') {
                return 'g';
            }
            if (ch == 'H' || ch == '\uff28' || ch == 'h' || ch == '\uff48') {
                return 'h';
            }
            if (ch == 'I' || ch == '\uff29' || ch == 'i' || ch == '\uff49') {
                return 'i';
            }
            if (ch == 'J' || ch == '\uff2a' || ch == 'j' || ch == '\uff4a') {
                return 'j';
            }
            if (ch == 'K' || ch == '\uff2b' || ch == 'k' || ch == '\uff4b') {
                return 'k';
            }
            if (ch == 'L' || ch == '\uff2c' || ch == 'l' || ch == '\uff4c') {
                return 'l';
            }
            if (ch == 'M' || ch == '\uff2d' || ch == 'm' || ch == '\uff4d') {
                return 'm';
            }
            if (ch == 'N' || ch == '\uff2e' || ch == 'n' || ch == '\uff4e') {
                return 'n';
            }
            if (ch == 'O' || ch == '\uff2f' || ch == 'o' || ch == '\uff4f') {
                return 'o';
            }
            if (ch == 'P' || ch == '\uff30' || ch == 'p' || ch == '\uff50') {
                return 'p';
            }
            if (ch == 'Q' || ch == '\uff31' || ch == 'q' || ch == '\uff51') {
                return 'q';
            }
            if (ch == 'R' || ch == '\uff32' || ch == 'r' || ch == '\uff52') {
                return 'r';
            }
            if (ch == 'S' || ch == '\uff33' || ch == 's' || ch == '\uff53') {
                return 's';
            }
            if (ch == 'T' || ch == '\uff34' || ch == 't' || ch == '\uff54') {
                return 't';
            }
            if (ch == 'U' || ch == '\uff35' || ch == 'u' || ch == '\uff55') {
                return 'u';
            }
            if (ch == 'V' || ch == '\uff36' || ch == 'v' || ch == '\uff56') {
                return 'v';
            }
            if (ch == 'W' || ch == '\uff37' || ch == 'w' || ch == '\uff57') {
                return 'w';
            }
            if (ch == 'X' || ch == '\uff38' || ch == 'x' || ch == '\uff58') {
                return 'x';
            }
            if (ch == 'Y' || ch == '\uff39' || ch == 'y' || ch == '\uff59') {
                return 'y';
            }
            if (ch == 'Z' || ch == '\uff3a' || ch == 'z' || ch == '\uff5a') {
                return 'z';
            }
        }
        else {
            if (ch == 'A' || ch == '\uff21' || ch == 'a' || ch == '\uff41') {
                return 'A';
            }
            if (ch == 'B' || ch == '\uff22' || ch == 'b' || ch == '\uff42') {
                return 'B';
            }
            if (ch == 'C' || ch == '\uff23' || ch == 'c' || ch == '\uff43') {
                return 'C';
            }
            if (ch == 'D' || ch == '\uff24' || ch == 'd' || ch == '\uff44') {
                return 'D';
            }
            if (ch == 'E' || ch == '\uff25' || ch == 'e' || ch == '\uff45') {
                return 'E';
            }
            if (ch == 'F' || ch == '\uff26' || ch == 'f' || ch == '\uff46') {
                return 'F';
            }
            if (ch == 'G' || ch == '\uff27' || ch == 'g' || ch == '\uff47') {
                return 'G';
            }
            if (ch == 'H' || ch == '\uff28' || ch == 'h' || ch == '\uff48') {
                return 'H';
            }
            if (ch == 'I' || ch == '\uff29' || ch == 'i' || ch == '\uff49') {
                return 'I';
            }
            if (ch == 'J' || ch == '\uff2a' || ch == 'j' || ch == '\uff4a') {
                return 'J';
            }
            if (ch == 'K' || ch == '\uff2b' || ch == 'k' || ch == '\uff4b') {
                return 'K';
            }
            if (ch == 'L' || ch == '\uff2c' || ch == 'l' || ch == '\uff4c') {
                return 'L';
            }
            if (ch == 'M' || ch == '\uff2d' || ch == 'm' || ch == '\uff4d') {
                return 'M';
            }
            if (ch == 'N' || ch == '\uff2e' || ch == 'n' || ch == '\uff4e') {
                return 'N';
            }
            if (ch == 'O' || ch == '\uff2f' || ch == 'o' || ch == '\uff4f') {
                return 'O';
            }
            if (ch == 'P' || ch == '\uff30' || ch == 'p' || ch == '\uff50') {
                return 'P';
            }
            if (ch == 'Q' || ch == '\uff31' || ch == 'q' || ch == '\uff51') {
                return 'Q';
            }
            if (ch == 'R' || ch == '\uff32' || ch == 'r' || ch == '\uff52') {
                return 'R';
            }
            if (ch == 'S' || ch == '\uff33' || ch == 's' || ch == '\uff53') {
                return 'S';
            }
            if (ch == 'T' || ch == '\uff34' || ch == 't' || ch == '\uff54') {
                return 'T';
            }
            if (ch == 'U' || ch == '\uff35' || ch == 'u' || ch == '\uff55') {
                return 'U';
            }
            if (ch == 'V' || ch == '\uff36' || ch == 'v' || ch == '\uff56') {
                return 'V';
            }
            if (ch == 'W' || ch == '\uff37' || ch == 'w' || ch == '\uff57') {
                return 'W';
            }
            if (ch == 'X' || ch == '\uff38' || ch == 'x' || ch == '\uff58') {
                return 'X';
            }
            if (ch == 'Y' || ch == '\uff39' || ch == 'y' || ch == '\uff59') {
                return 'Y';
            }
            if (ch == 'Z' || ch == '\uff3a' || ch == 'z' || ch == '\uff5a') {
                return 'Z';
            }
        }
        if (ch == '0' || ch == '\uff10') {
            return '0';
        }
        if (ch == '1' || ch == '\uff11') {
            return '1';
        }
        if (ch == '2' || ch == '\uff12') {
            return '2';
        }
        if (ch == '3' || ch == '\uff13') {
            return '3';
        }
        if (ch == '4' || ch == '\uff14') {
            return '4';
        }
        if (ch == '5' || ch == '\uff15') {
            return '5';
        }
        if (ch == '6' || ch == '\uff16') {
            return '6';
        }
        if (ch == '7' || ch == '\uff17') {
            return '7';
        }
        if (ch == '8' || ch == '\uff18') {
            return '8';
        }
        if (ch == '9' || ch == '\uff19') {
            return '9';
        }
        if (ch == '\uff0c' || ch == ',' || ch == '\ufe50') {
            return '\uff0c';
        }
        if (ch == '\ufe52' || ch == '.' || ch == '\uff0e') {
            return '.';
        }
        if (ch == '\u3001' || ch == '\ufe51') {
            return '\u3001';
        }
        if (ch == '\u2018' || ch == '`') {
            return '\u2018';
        }
        if (ch == '\u2019' || ch == '´') {
            return '\u2019';
        }
        if (ch == '\u201c' || ch == '\u301d') {
            return '\u201c';
        }
        if (ch == '\u201d' || ch == '\u301e') {
            return '\u201d';
        }
        if (ch == '!' || ch == '\ufe57' || ch == '\uff01') {
            return '\uff01';
        }
        if (ch == '\uff1f' || ch == '?' || ch == '\ufe56') {
            return '\uff1f';
        }
        if (ch == '\uff1a' || ch == ':' || ch == '\ufe30' || ch == '\ufe55' || ch == '\u2236') {
            return '\uff1a';
        }
        if (ch == '\uff1b' || ch == ';' || ch == '\ufe54') {
            return '\uff1b';
        }
        if (ch == '-' || ch == '\uff0d' || ch == '\u2500' || ch == '\ufe63' || ch == '\u2013' || ch == '\u2015' || ch == '\u2501') {
            return '-';
        }
        if (ch == '\uff08' || ch == '(' || ch == '\ufe59' || ch == '\u3014') {
            return '\uff08';
        }
        if (ch == '\uff09' || ch == ')' || ch == '\ufe5a' || ch == '\u3015') {
            return '\uff09';
        }
        if (ch == '{' || ch == '\uff5b') {
            return '{';
        }
        if (ch == '}' || ch == '\uff5d') {
            return '}';
        }
        if (ch == '<' || ch == '\u3008' || ch == '\ufe64' || ch == '\uff1c') {
            return '<';
        }
        if (ch == '>' || ch == '\u3009' || ch == '\ufe65' || ch == '\uff1e') {
            return '>';
        }
        if (ch == '|' || ch == '\uff5c' || ch == '\u2502') {
            return '|';
        }
        if (ch == '/' || ch == '\uff0f' || ch == '\u2215') {
            return '/';
        }
        if (ch == '@' || ch == '\uff20') {
            return '@';
        }
        if (ch == '&' || ch == '\uff06') {
            return '&';
        }
        if (ch == '#' || ch == '\uff03') {
            return '#';
        }
        if (ch == '%' || ch == '\ufe6a' || ch == '\uff05') {
            return '%';
        }
        if (ch == '¥' || ch == '\uffe5') {
            return '¥';
        }
        if (ch == '$' || ch == '\uff04') {
            return '$';
        }
        if (ch == '£' || ch == '\u20a4' || ch == '\uffe1') {
            return '£';
        }
        if (ch == '\u20ac' || ch == '\ue76c') {
            return '\u20ac';
        }
        if (ch == '+' || ch == '\uff0b') {
            return '+';
        }
        if (ch == '=' || ch == '\uff1d') {
            return '=';
        }
        if (ch == '\u00d7' || ch == '\u2573') {
            return '\u00d7';
        }
        if (ch == '\u2265' || ch == '\u2267') {
            return '\u2265';
        }
        if (ch == '\u2264' || ch == '\u2266') {
            return '\u2264';
        }
        if (ch == '\u03a3' || ch == '\u2211') {
            return '\u03a3';
        }
        if (ch == '\u3010' || ch == '\u3016') {
            return '\u3010';
        }
        if (ch == '\u3011' || ch == '\u3017') {
            return '\u3011';
        }
        if (ch == '\u300c' || ch == '\u300e' || ch == '\ufe41' || ch == '\ufe43') {
            return '\u300c';
        }
        if (ch == '\u300d' || ch == '\u300f' || ch == '\ufe42' || ch == '\ufe44') {
            return '\u300d';
        }
        if (ch == '\u2022' || ch == '\u25cf' || ch == '\u3007' || ch == '\u25a0' || ch == '\u25c6' || ch == '\u25c7' || ch == '\u25b2' || ch == '\u25b3' || ch == '\u2605' || ch == '\u2606' || ch == '®' || ch == '\u534d' || ch == '\u203b') {
            return '\u2022';
        }
        return ch;
    }
}
