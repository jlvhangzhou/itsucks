/**
 *  @author Wu Hualiang <wizawu@gmail.com>
 *  计算页面是post页面的概率
 */

public class PostScore {

	private static final boolean beingTested = true;
	
	private static int numberOfCommentsWithin(int first, int last, int[] sumOfComments) {
		return sumOfComments[last] - sumOfComments[first - 1];
	}
	
	public static double getResult(String origHtml) {
			
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
			numberOfComments[i] = lines[i].split("[Cc]omment").length - 1;
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
			
			double minProduct = (double)(lastComment - firstComment + 1) 
					/ numberOfCommentsWithin(firstComment, lastComment, sumOfComments) 
					/ firstComment / (lastLine - lastComment);
			
			for (int i = firstComment; i <= lastComment; i++) {
				for (int j = i; j <= lastComment; j++) {
					double product = (double)(j - i + 1) / numberOfCommentsWithin(i, j, sumOfComments)
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
			lines[i] = YearsInLine.removeAll(lines[i]);
		
		String main = lines[0] + "\n";
		for (int i = 1; i <= lastLine; i++)
			main += lines[i] + "\n";
		
		String mainText = TagFliter.deleteEmptyLines(TagFliter.fliter(main));
		System.out.println(mainText);
		lines = mainText.split("\n");
		int[] numberOfYears = new int[lines.length];
		for (int i = 0; i < lines.length; i++) {
			numberOfYears[i] = YearsInLine.getSum(lines[i]);
			System.out.println(numberOfYears[i] + "  " + lines[i]);
		}
		int countYears = 0;
		for (int i = 2; i <= lines.length - 3; i++) {
			if (numberOfYears[i] == 0) continue;
			if (numberOfYears[i-2] + numberOfYears[i-1] + numberOfYears[i] >= 3) continue;
			if (numberOfYears[i+1] + numberOfYears[i-1] + numberOfYears[i] >= 3) continue;
			if (numberOfYears[i+2] + numberOfYears[i+1] + numberOfYears[i] >= 3) continue;
			countYears++;
		}
		
		double titleRate = (double)(body.substring(0, bodySize/5*4).split("[Tt]itle").length - 1) / totalLines;
		System.out.println(origHtml.split("[Tt]itle").length - 1);
		
		if (beingTested) System.out.println("title rate: " + titleRate);
		if (beingTested) System.out.println("year occurs: " + countYears);
//		if (beingTested) System.out.println(mainText);
		
		return titleRate * titleRate * countYears * 10;
	}

}
