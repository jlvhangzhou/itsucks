/**
 *  @author Wu Hualiang <wizawu@gmail.com>
 *  对(不)规则html除去标签
 */

public class TagFliter {

	public static String fliter(String html) {
		
		String result = html.replaceAll("<script[^>]*>[^<]*</script>", "");
		result = result.replaceAll("<[^>]*>", "");
		return result;

	}
	
	public static String deleteEmptyLines(String text) {
		String result = text.replaceAll("[ \t]+\n", "\n").replaceAll("\n+", "\n");
		return result;
	}

}
