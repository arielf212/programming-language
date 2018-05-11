import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

public class Commands {
	public final static HashMap COMMANDS = new HashMap();
	public static HashMap<String,Object> vars = new HashMap<String,Object>();
	public static Object math(String expression) {
		//53+7/2-(3-5*2)
		expression="0+"+expression+"+0"; //this is added to stop the function from crashing when it cant find another sign in the power, multiplication and division parts;
		for(String key : vars.keySet()) {
			expression.replace(key, String.valueOf(vars.get(key)));
		}
		if(expression.contains("(")) {
			int index = 0;
			while(expression.contains("(")) {
				int startIndex = expression.indexOf("("); //finds the opening bracket;
				index=startIndex+1;
				int opening = 1; //counts how many opening brackets there are and how many of the were closed(+1 for an opening bracket, -1 for a closing bracket)
				String inside = "";
				while(opening>0) {
					inside+=expression.charAt(index);
					if(expression.charAt(index) == '(')
						opening++;
					else if(expression.charAt(index) == ')')
						opening--;
					index++;
				}
				expression=expression.substring(0, startIndex)+Commands.math(inside.substring(0,inside.length()-1))+expression.substring(index);
			}
		}
		if(expression.contains("^")) {
			while(expression.contains("^")) {
				int mainIndex = expression.indexOf("^");
				int before = mainIndex-1; //this will go backwards and find out the location of the math symbol that came before this one;
				int after = mainIndex+1;  //this does the same thing as before, but for the one after;
				boolean[] done = new boolean[2];
				while(!done[0] && !done[1]) {
					if(StringUtils.isNumeric(String.valueOf(expression.charAt(before)))||expression.charAt(before)=='.')
						before--;
					else
						done[0]=true;
					if(StringUtils.isNumeric(String.valueOf(expression.charAt(after)))||expression.charAt(after)=='.')
						after++;
					else
						done[1]=true;
				}
				expression = expression.substring(0,before+1)+Math.pow(Double.parseDouble(expression.substring(before+1,mainIndex)),Double.parseDouble(expression.substring(mainIndex+1,after))) + expression.substring(after);
			}
		}
		if(expression.contains("*")||expression.contains("/")) {
			while(expression.contains("*")||expression.contains("/")) {
				int mainIndex;
				if(!expression.contains("*"))
					mainIndex=expression.indexOf("/");
				else if(!expression.contains("/"))
					mainIndex=expression.indexOf("*");
				else
					mainIndex = (expression.indexOf("*")<expression.indexOf("/")?expression.indexOf("*"):expression.indexOf("/"));
				int before = mainIndex-1; //this will go backwards and find out the location of the math symbol that came before this one;
				int after = mainIndex+1;  //this does the same thing as before, but for the one after;
				boolean[] done = new boolean[2];
				while((!done[0] && !done[1]) && (after<expression.length() && before>0)) {
					if(StringUtils.isNumeric(String.valueOf(expression.charAt(before)))||expression.charAt(before)=='.')
						before--;
					else
						done[0]=true;
					if(StringUtils.isNumeric(String.valueOf(expression.charAt(after)))||expression.charAt(after)=='.')
						after++;
					else
						done[1]=true;
				}
				double num = (expression.charAt(mainIndex)=='*'? Double.parseDouble(expression.substring(before+1,mainIndex))*Double.parseDouble(expression.substring(mainIndex+1,after)):Double.parseDouble(expression.substring(before+1,mainIndex))/Double.parseDouble(expression.substring(mainIndex+1,after)));
				expression = expression.substring(0,before+1)+num+ expression.substring(after);
			}
		}
		if(expression.contains("+")||expression.contains("-")) {
			expression=expression.replace("--", "+").replaceAll("-", "+-");
			double num = 0;
			for(String s : expression.split("\\+"))
				num+=Double.parseDouble(s);
			expression=String.valueOf(num);
		}
		if(Double.parseDouble(expression)%1==0)
			return Integer.parseInt(expression.substring(0, expression.length()-2));
		return Double.parseDouble(expression);
	}
	public static void write(String text, String path) throws IOException {
		if(path.equals("console"))
			System.out.println(text);
		else {
			Files.write(Paths.get(path),text.getBytes());
		}
	}
	public static void main(String[] args) throws IOException {
		Scanner s = new Scanner(System.in);
		while(s.hasNextLine()) {
			String line = s.nextLine();
			int place = 0;
			for(int i = 0; i<StringUtils.countMatches(line,'"');i+=2) {
				int[] placing = new int[2]; //symbolizes the start and end of a string"'
				placing[0] = line.indexOf('"',place);
				placing[1] = line.indexOf('"', placing[0]);
				line = line.substring(0,placing[0])+line.substring(placing[0],placing[1]+1).replace(" ", "~")+line.substring(placing[1]+1);
			}
			System.out.println(line);
			if(line.startsWith("write")) {
				String[] parts = line.substring(6).split(" ");
				if(parts.length==1)
					Commands.write(parts[0].replace("~", " "), "console");
				else
					Commands.write(parts[0].replace("~", " "), parts[2]);
			}
		}
	}
}