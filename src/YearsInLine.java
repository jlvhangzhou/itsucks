/**
 *  @author Wu Hualiang <wizawu@gmail.com>
 *  计算单行字符串中年份个数，或删除所有年份
 */

public class YearsInLine {

	private static final char Y = '2';
	private static final char E = '0';
	private static final char A = ' ';
	private static final char R = ' ';
	
	public static int getSum(String line) {
		char[] chars = line.toCharArray();
		int result = 0;
		for (int i = 0; i < chars.length - 3; i++) {
			boolean ok = true;
			ok &= Character.isDigit(chars[i]) & chars[i] == Y;
			ok &= Character.isDigit(chars[i+1]) & chars[i+1] == E;
			ok &= Character.isDigit(chars[i+2]);
			ok &= Character.isDigit(chars[i+3]);
			if (i != 0) ok &= !Character.isDigit(chars[i-1]);
			if (i + 4 < chars.length) ok &= !Character.isDigit(chars[i+4]); 
			if (ok) result++;
		}	
		return result;
	}
	
	public static String removeAll(String line) {
		char[] chars = line.toCharArray();
		String result = "";
		for (int i = 0; i < chars.length - 3; i++) {
			boolean ok = true;
			ok &= Character.isDigit(chars[i]) & chars[i] == Y;
			ok &= Character.isDigit(chars[i+1]) & chars[i+1] == E;
			ok &= Character.isDigit(chars[i+2]);
			ok &= Character.isDigit(chars[i+3]);
			if (i != 0) ok &= !Character.isDigit(chars[i-1]);
			if (i + 4 < chars.length) ok &= !Character.isDigit(chars[i+4]); 
			if (ok) i += 3;
			else result += chars[i];
		}	
		return result;
	}

}
