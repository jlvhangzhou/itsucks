
public class Util {
	
	/**
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  从parse后的网页纯文本提取正文内容
	 */
	
	public static final int minStart = 10;
	public static final int blockThickness = 3;
	public static final int minChars = 80;
	
	public static String getMainBody(String text) {
		
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

		String result = lines[start] + "\n";
		for (int i = start + 1; i < end; i++) {
			result += lines[i] + "\n";
		}
		return result;
	}
	
	/**
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  计算页面是post页面的概率
	 */
	
	private static int numberOfCommentsWithin(int first, int last, int[] sumOfComments) {
		return sumOfComments[last] - sumOfComments[first - 1];
	}
	
	public static double isPost(String origHtml) {
			
		int bodyIndex = Math.max(origHtml.indexOf("<body>"), 0);
		String body = origHtml.substring(bodyIndex);
		int bodySize = body.length();
		String[] lines = body.split("\n");
		int totalLines = lines.length;
		int[] numberOfComments = new int[totalLines];
		int[] sumOfComments = new int[totalLines];
		int lastLine = totalLines - 1;
		int firstComment = -1, lastComment = -1;
		
		numberOfComments[0] = sumOfComments[0] = 0;
		for (int i = 1; i <= lastLine; i++) {
			numberOfComments[i] = Math.min(1, lines[i].split("[Cc]omment").length - 1);
			sumOfComments[i] = sumOfComments[i-1] + numberOfComments[i];
			if (numberOfComments[i] == 0) continue;
			if (firstComment == -1) {
				firstComment = i; 
			}
			lastComment = i;
		}

		System.out.println("first comment: " + firstComment);
		System.out.println("last comment:" + lastComment);
		System.out.println("last line:" + lastLine);

		int beginOfCommentBlock = lastLine + 1;
		int endOfCommentBlock = lastLine + 1;
		
		if (firstComment != -1) {
			
			double minProduct = (double)(lastComment - firstComment + 1) * (lastComment - firstComment + 1)  
					/ numberOfCommentsWithin(firstComment, lastComment, sumOfComments) 
					/ firstComment / (lastLine - lastComment);
			
			for (int i = firstComment; i <= lastComment; i++) {
				for (int j = i; j <= lastComment; j++) {
					double product = Math.sqrt((double)(j - i + 1) / numberOfCommentsWithin(i, j, sumOfComments))
							* (numberOfCommentsWithin(1, i-1, sumOfComments) + 1)
							* (numberOfCommentsWithin(j+1, lastLine, sumOfComments) + 1)
							/ i / (lastLine - j);
					
					if (product < minProduct) {
						beginOfCommentBlock = i;
						endOfCommentBlock = j + 1;
						minProduct = product;
					}
				}
			}
		}
		
		System.out.println("begin comment blog: " + beginOfCommentBlock);
		System.out.println("end comment blog: " + endOfCommentBlock);
		
		for (int i = beginOfCommentBlock; i < endOfCommentBlock; i++)
			lines[i] = removeYears(lines[i]);
		
		String main = lines[0] + "\n";
		for (int i = 1; i <= lastLine; i++)
			main += lines[i] + "\n";
		
		String mainText = removeEmptyLines(fliterTag(main));
		lines = mainText.split("\n");
		int[] numberOfYears = new int[lines.length];
		for (int i = 0; i < lines.length; i++) {
			numberOfYears[i] = numberOfYears(lines[i]);
		}
		
		int countYears = 0;
		for (int i = 2; i <= lines.length - 3; i++) {
			if (numberOfYears[i] == 0) continue;
			if (numberOfYears[i-2] + numberOfYears[i-1] + numberOfYears[i] >= 3) continue;
			if (numberOfYears[i+1] + numberOfYears[i-1] + numberOfYears[i] >= 3) continue;
			if (numberOfYears[i+2] + numberOfYears[i+1] + numberOfYears[i] >= 3) continue;
			countYears++;
		}
		countYears = Math.max(1, countYears);
		
		double titleRate = (double)(body.substring(0, bodySize/5*4).split("[Tt]itle").length - 1) / totalLines;
		System.out.println(origHtml.split("[Tt]itle").length - 1);
		
		System.out.println("title rate: " + titleRate);					// useless code
		System.out.println("year occurs: " + countYears);					// useless code
		
		return titleRate * titleRate * countYears * 10;
	}
	
	/**
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  计算字符串中年份个数
	 */
	
	private static final char Y = '2';
	private static final char E = '0';
	private static final char A = ' ';
	private static final char R = ' ';
	
	public static int numberOfYears(String line) {
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
	
	/**
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  删除字符串中的所有年份
	 */
	
	public static String removeYears(String line) {
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
	
	/**
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  对(不)规则html除去标签
	 */
	
	public static String fliterTag(String html) {
		String result = html.replaceAll("<script[^>]*>[^<]*</script>", "");
		result = result.replaceAll("<[^>]*>", "");
		return result;
	}
	
	/**
	 *  @author Wu Hualiang <wizawu@gmail.com>
	 *  删除空行
	 */
	
	public static String removeEmptyLines(String text) {
		String result = text.replaceAll("[ \t]+\n", "\n").replaceAll("\n+", "\n");
		return result;
	}
	
}
