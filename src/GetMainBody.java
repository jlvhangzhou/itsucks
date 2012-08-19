/**
 *  @author Wu Hualiang <wizawu@gmail.com>
 *  从parse后的网页纯文本提取正文内容
 */

public class GetMainBody {
	
	public static final int minStart = 10;
	public static final int blockThickness = 3;
	public static final int minChars = 80;
	
	private static final boolean beingTested = false;
	
	public static String getResult(String text) {
		
		String[] lines = text.split("\n");
		int totalLines = lines.length;
		int[] lineLength = new int[totalLines];
		for (int i = 0; i < totalLines; i++)
			lineLength[i] = lines[i].length();

		int[] blockLength = new int[totalLines - blockThickness + 1];
		
		for (int i = 0; i < blockLength.length; i++) {
			int sum = 0;
			for (int j = 0; j < blockThickness; j++) {
				sum += lineLength[i + j];
			}
			blockLength[i] = sum;
			
			if (beingTested) System.out.println(sum);
		}

		int start = minStart, end = totalLines;
		for (int i = start; i < blockLength.length - 1; i++) {
			if (blockLength[i] < minChars) continue;
			if (blockLength[i + 1] == 0) continue;
			start = i;
			for (int j = i; j < totalLines; j++)
				if (blockLength[j] == 0) {
					end = j;
					break;
				}
			break;
		}

		if (beingTested) System.out.println("start: " + start);
		if (beingTested) System.out.println("end: " + end);
		
		String result = lines[start] + "\n";
		for (int i = start + 1; i < end; i++) {
			result += lines[i] + "\n";
		}
		return result;
	}

}
