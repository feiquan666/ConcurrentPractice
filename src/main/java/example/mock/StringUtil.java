package example.mock;

public class StringUtil {

	public static boolean isTrimEmpty(String value) {
		return (null == value || value.trim().isEmpty());
	}

}
