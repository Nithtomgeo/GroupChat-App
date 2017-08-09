package Emoji;
//package im.delight.java.emoji.Emoji;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.HashMap;

public class Emoji {
    private static final String REGEX_SURROUNDING_CHARS_DISALLOWED = "[-_a-zA-Z0-9)(;:*<>=/]";
	private static final String REGEX_NEGATIVE_LOOKBEHIND = "(?<!"+REGEX_SURROUNDING_CHARS_DISALLOWED+")";
	private static final String REGEX_NEGATIVE_LOOKAHEAD = "(?!"+REGEX_SURROUNDING_CHARS_DISALLOWED+")";
	private static class ReplacementsMap extends HashMap<String,Integer> {
	private static final long serialVersionUID = 4948071414363715959L;
	private static ReplacementsMap mInstance;
	/*HEXADECIMAL UNICODE FOR THE EMOJI CHARACTERS*/
	private ReplacementsMap() {
			super();
			put(":-)", 0x1F60A);
			put(":)", 0x1F60A);
			put(":-(", 0x1F61E);
			put(":(", 0x1F61E);
			put(":-D", 0x1F603);
			put(":D", 0x1F603);
			put(";-)", 0x1F609);
			put(";)", 0x1F609);
			put(":-P", 0x1F61C);
			put(":P", 0x1F61C);
			put(":-p", 0x1F61C);
			put(":p", 0x1F61C);
			put(":-*", 0x1F618);
			put(":*", 0x1F618);
			put("<3", 0x2764);
			put(":3", 0x2764);
			put(">:[", 0x1F621);
			put(":'|", 0x1F625);
			put(":-[", 0x1F629);
			put(":'(", 0x1F62D);
			put("=O", 0x1F631);
			put("xD", 0x1F601);
			put(":')", 0x1F602);
			put(":-/", 0x1F612);
			put(":/", 0x1F612);
			put(":-|", 0x1F614);
			put(":|", 0x1F614);
			put("*_*", 0x1F60D);
		}
		public static ReplacementsMap getInstance() {
			if (mInstance == null) {
				mInstance = new ReplacementsMap();
			}
			return mInstance;
		}
	}
	/*FUNCTION THAT GETS THE UNICODE*/
	private static String getUnicodeChar(int codepoint) {
		return new String(Character.toChars(codepoint));
	}
	
	private static String getEmoticonSearchRegex(String emoticon) {
		return REGEX_NEGATIVE_LOOKBEHIND+"("+Pattern.quote(emoticon)+")"+REGEX_NEGATIVE_LOOKAHEAD;
	}

	public static String replaceInText(String text) {
		return replaceInText(text, false);
	}
	
	public static String replaceInText(String text,boolean reverse) {
		final ReplacementsMap replacements = ReplacementsMap.getInstance();
		String emoticon;
		Integer codepoint;
		for (Map.Entry<String, Integer> entry : replacements.entrySet()) {
			if (entry != null) {
				emoticon = entry.getKey();
				codepoint = entry.getValue();
				if (emoticon != null && codepoint != null) {
					String unicodeChar = getUnicodeChar(codepoint.intValue());
					if (reverse) {
						text = text.replace(unicodeChar, emoticon);
					}
					else {
						text = text.replaceAll(getEmoticonSearchRegex(emoticon), unicodeChar);
					}
				}
			}
		}
		return text;
	}
}