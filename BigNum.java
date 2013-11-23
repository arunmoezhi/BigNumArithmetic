import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.Stack;

public class BigNum
{
	static String inputString;
	static final long BASE = (long) Math.pow(10,8);	
	static final int LOG10_OF_BASE = (int) Math.log10(BASE);
	static DecimalFormat nf;
	static HashMap<Integer,String> memoizationTable;
	static FileOutputStream out;
	static PrintStream p;

	public static void main(String args[])
	{
		long start,end;
		start = System.currentTimeMillis();
		setDecimalFormat();
		getUserInput();
		end = System.currentTimeMillis();
		System.out.println((end-start));
	}

	static void setDecimalFormat()
	{
		switch(LOG10_OF_BASE)
		{
		case 1:
			nf = new DecimalFormat("#0");
			break;
		case 2:
			nf = new DecimalFormat("#00");
			break;
		case 3:
			nf = new DecimalFormat("#000");
			break;
		case 4:
			nf = new DecimalFormat("#0000");
			break;
		case 5:
			nf = new DecimalFormat("#00000");
			break;
		case 6:
			nf = new DecimalFormat("#000000");
			break;
		case 7:
			nf = new DecimalFormat("#0000000");
			break;
		case 8:
			nf = new DecimalFormat("#00000000");
			break;
		case 9:
			nf = new DecimalFormat("#000000000");
			break;

		}
	}

	public static void getUserInput()
	{
		FileInputStream fs;
		try
		{
			out = new FileOutputStream("output" + ".txt");
			p = new PrintStream(out);
			fs = new FileInputStream("input.txt");
			DataInputStream ds = new DataInputStream(fs);
			BufferedReader reader = new BufferedReader(new InputStreamReader(ds));
			while(!(inputString = reader.readLine()).equalsIgnoreCase("quit"))
			{
				evaluateExpression(inputString);
			}
			ds.close();
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	static boolean compareStrings(String s1, String s2)
	{
		if(s1.length() < s2.length())
		{
			return false;
		}
		else
		{
			if(s1.length() == s2.length())
			{
				if(s1.compareTo(s2) <= 0)
				{
					return false;
				}
				else
				{
					return true;
				}
			}
			return true;
		}

	}

	static String div(String dividend, String divisor)
	{
		String quotient="0";
		String iterator="2",prevIterator="1";
		LinkedList<Long> one = new LinkedList<Long>();
		one.add(1L);
		while(compareStrings(dividend,divisor))
		{
			while(compareStrings(dividend,mulInputAsString(divisor,iterator)))
			{
				prevIterator = iterator;
				iterator = mulInputAsString(iterator,"2");		
			}
			if(mulInputAsString(divisor,iterator).equals(dividend))
			{
				quotient=add(stringNumberToLinkedList(quotient),stringNumberToLinkedList(iterator));
				break;
			}
			quotient=add(stringNumberToLinkedList(quotient),stringNumberToLinkedList(prevIterator));
			dividend = sub(stringNumberToLinkedList(dividend),stringNumberToLinkedList(mulInputAsString(prevIterator,divisor)));
			iterator="2";
			prevIterator="1";
		}
		return quotient;
	}

	static String mod(String dividend, String divisor)
	{
		String quotient="0";
		quotient = div(dividend, divisor);
		return(sub(stringNumberToLinkedList(dividend),stringNumberToLinkedList(mulInputAsString(quotient,divisor))));

	}

	static String exp(LinkedList<Long> number1AsList, int n)
	{
		String storedString;

		if( (storedString = memoizationTable.get(n))  != null)
		{
			return storedString;
		}

		if(n%2 == 0)
		{
			memoizationTable.put(n, mulInputAsString(exp(number1AsList,n/2),exp(number1AsList,n/2)));
		}
		else
		{
			memoizationTable.put(n, mul(number1AsList , stringNumberToLinkedList(mulInputAsString(exp(number1AsList,n/2),exp(number1AsList,n/2)))));	
		}
		return memoizationTable.get(n);
	}

	static String mulInputAsString(String number1AsString, String number2AsString)
	{
		LinkedList<Long> number1AsList = new LinkedList<Long>();
		LinkedList<Long> number2AsList = new LinkedList<Long>();
		number1AsList = stringNumberToLinkedList(number1AsString);
		number2AsList = stringNumberToLinkedList(number2AsString);
		long number1AsListLength = number1AsList.size();
		long number2AsListLength = number2AsList.size();
		int outputArraySize;
		long num1,num2;
		long product=1;
		String intermediateOutput="";
		outputArraySize = number1AsList.size() + number2AsList.size();
		long[] carry = new long[outputArraySize];
		long[] outputArray = new long[outputArraySize];
		ListIterator<Long> llIteratorOnNum1 = number1AsList.listIterator();
		for(int i=0;i<outputArray.length;i++)
		{
			outputArray[i] = 0;
			carry[i] = 0;
		}
		for(int i=0;i<number1AsListLength;i++)
		{
			num1 = llIteratorOnNum1.next();
			ListIterator<Long> llIteratorOnNum2 = number2AsList.listIterator();
			for(int j=0;j<number2AsListLength;j++)
			{
				num2 = llIteratorOnNum2.next();
				product = num1 * num2;
				if(outputArray[i+j] + product < BASE)
				{
					outputArray[i+j] = outputArray[i+j] + product;
				}
				else
				{		
					carry[i+j+1] = carry[i+j+1] + ((outputArray[i+j] + product) / BASE);
					outputArray[i+j] = (outputArray[i+j] + product) % BASE;
				}
			}		
		}
		for(int i=0;i<outputArray.length;i++)
		{
			if(outputArray[i] + carry[i] <BASE)
			{
				outputArray[i] = outputArray[i] + carry[i];
			}
			else
			{
				carry[i+1] = carry[i+1] + ((outputArray[i] + carry[i]) / BASE);
				outputArray[i] = (outputArray[i] + carry[i]) % BASE;
			}
		}
		intermediateOutput = outputLongToString(outputArray);
		return intermediateOutput;
	}

	static String mul(LinkedList<Long> number1AsList, LinkedList<Long> number2AsList)
	{
		int outputArraySize;
		long num1,num2;
		long product=1;
		String intermediateOutput="";
		outputArraySize = number1AsList.size() + number2AsList.size();
		long[] outputArray = new long[outputArraySize];
		long[] carry = new long[outputArraySize];
		ListIterator<Long> llIteratorOnNum1 = number1AsList.listIterator();
		long number1AsListLength = number1AsList.size();
		long number2AsListLength = number2AsList.size();
		for(int i=0;i<outputArray.length;i++)
		{
			outputArray[i] = 0;
			carry[i] = 0;
		}
		for(int i=0;i<number1AsListLength;i++)
		{
			num1 = llIteratorOnNum1.next();
			ListIterator<Long> llIteratorOnNum2 = number2AsList.listIterator();
			for(int j=0;j<number2AsListLength;j++)
			{
				num2 = llIteratorOnNum2.next();
				product = num1 * num2;
				if(outputArray[i+j] + product < BASE)
				{
					outputArray[i+j] = outputArray[i+j] + product;
				}
				else
				{		
					carry[i+j+1] = carry[i+j+1] + ((outputArray[i+j] + product) / BASE);
					outputArray[i+j] = (outputArray[i+j] + product) % BASE;
				}

			}		
		}
		for(int i=0;i<outputArray.length;i++)
		{
			if(outputArray[i] + carry[i] <BASE)
			{
				outputArray[i] = outputArray[i] + carry[i];
			}
			else
			{
				carry[i+1] = carry[i+1] + ((outputArray[i] + carry[i]) / BASE);
				outputArray[i] = (outputArray[i] + carry[i]) % BASE;
			}

		}
		intermediateOutput = outputLongToString(outputArray);
		return intermediateOutput;
	}

	static String add(LinkedList<Long> number1AsList, LinkedList<Long> number2AsList)
	{
		int outputArraySize;
		int numOfMainIterations;
		int numOfExtraIterations;

		if(number1AsList.size() >= number2AsList.size())
		{
			outputArraySize = number1AsList.size() + 1;
			numOfMainIterations = number2AsList.size();
			numOfExtraIterations = number1AsList.size() - number2AsList.size();
		}
		else
		{
			outputArraySize = number2AsList.size() + 1;
			numOfMainIterations = number1AsList.size();
			numOfExtraIterations = number2AsList.size() - number1AsList.size();
		}

		long[] outputArray = new long[outputArraySize];
		long carry = 0;
		long sum = 0;
		String output ="";
		ListIterator<Long> llIteratorOnNum1 = number1AsList.listIterator();
		ListIterator<Long> llIteratorOnNum2 = number2AsList.listIterator();

		for(int i=0;i<numOfMainIterations;i++)
		{
			sum = llIteratorOnNum1.next() + llIteratorOnNum2.next() + carry;
			outputArray[i] = sum % BASE;
			carry = sum / BASE;
		}
		if(number1AsList.size() == number2AsList.size())
		{
			outputArray[numOfMainIterations] = carry;
		}
		else
		{
			if(number1AsList.size() > number2AsList.size())
			{
				for(int i=0;i<numOfExtraIterations;i++)
				{
					sum = llIteratorOnNum1.next() + carry;
					outputArray[i+numOfMainIterations] = sum % BASE;
					carry = sum / BASE;
				}
			}
			else
			{
				for(int i=0;i<numOfExtraIterations;i++)
				{
					sum = llIteratorOnNum2.next() + carry;
					outputArray[i+numOfMainIterations] = sum % BASE;
					carry = sum / BASE;
				}
			}
			outputArray[outputArraySize-1] = carry;
		}

		output = outputLongToString(outputArray);
		return output;

	}

	static String sqrt(String number)
	{
		int minSize = (int) Math.floor((number.length() + 1 )/2);
		StringBuffer test = new StringBuffer("");
		for(int i=0;i<minSize;i++)
		{
			test.append(0);
		}

		String sqr="";
		int index;
		for(int i=0;i<test.length();i++)
		{
			index=1;
			while(!compareStrings(sqr,number) && index <10)
			{

				test.replace(i, i+1, Integer.toString(index++));
				sqr = mul(stringNumberToLinkedList(test.toString()),stringNumberToLinkedList(test.toString()));
			}
			if(compareStrings(sqr,number))
			{
				test.replace(i, i+1, Integer.toString(index-2));
				sqr = mul(stringNumberToLinkedList(test.toString()),stringNumberToLinkedList(test.toString()));
			}
		}

		return test.toString();
	}

	static String sub(LinkedList<Long> number1AsList, LinkedList<Long> number2AsList)
	{
		int outputArraySize;
		int numOfMainIterations;
		int numOfExtraIterations;
		long num1,num2;

		outputArraySize = number1AsList.size();
		numOfMainIterations = number2AsList.size();
		numOfExtraIterations = number1AsList.size() - number2AsList.size();


		long[] outputArray = new long[outputArraySize];
		long borrow = 0;
		long diff = 0;
		String output ="";
		ListIterator<Long> llIteratorOnNum1 = number1AsList.listIterator();
		ListIterator<Long> llIteratorOnNum2 = number2AsList.listIterator();

		for(int i=0;i<numOfMainIterations;i++)
		{
			num1 = llIteratorOnNum1.next();
			num2 = llIteratorOnNum2.next();
			if(num1 >= num2)
			{
				diff = num1 - num2 + borrow;
				if(diff>=0)
				{
					outputArray[i] = diff;
					borrow = 0;
				}
				else
				{
					outputArray[i] = BASE-1;
					borrow = -1;
				}
			}
			else
			{
				diff = num1 + BASE - num2 + borrow;
				outputArray[i] = diff;
				borrow = -1;
			}
		}

		if(number1AsList.size() > number2AsList.size())
		{
			for(int i=0;i<numOfExtraIterations;i++)
			{
				num1 = llIteratorOnNum1.next();

				if(num1 + borrow >= 0)
				{
					diff = num1 + borrow;
					outputArray[i+numOfMainIterations] = diff;
					borrow = 0;
				}
				else
				{
					diff = num1 + BASE + borrow;
					outputArray[i+numOfMainIterations] = diff;
				}
			}
		}

		output = outputLongToString(outputArray);
		return output;
	}

	static String outputLongToString(long[] outputArray)
	{
		StringBuffer outputBuffer= new StringBuffer();
		String output;
		output = Long.toString(outputArray[outputArray.length-1]);

		for(int i=outputArray.length-2;i>=0;i--)
		{
			outputBuffer.append(nf.format(outputArray[i]));
		}
		output = output + outputBuffer.toString();
		output = output.replaceFirst("^0+(?!$)", "");
		return output;

	}

	static String performOperation(String value1, String value2, String operator)
	{
		boolean isValue1Negative=false;
		boolean isValue2Negative=false;
		LinkedList<Long> number1AsList = new LinkedList<Long>();
		LinkedList<Long> number2AsList = new LinkedList<Long>();
		if(value1.substring(0, 1).equals("-"))
		{
			value1 = value1.substring(1);
			isValue1Negative=true;
		}
		if(value2.substring(0, 1).equals("-"))
		{
			value2 = value2.substring(1);
			isValue2Negative=true;
		}
		number1AsList = stringNumberToLinkedList(value1);
		number2AsList = stringNumberToLinkedList(value2);

		if(operator.equals("+"))
		{
			if( !(isValue1Negative || isValue2Negative) ) //a+b = a+b
			{
				return (add(number1AsList, number2AsList));
			}
			else
			{
				if( (isValue1Negative && isValue2Negative) ) //(-a) + (-b) = -(a+b)
				{
					return ("-" + add(number1AsList, number2AsList));
				}
				else
				{
					if(isValue1Negative) //(-a) + (b) = b - a
					{
						if(value2.length() < value1.length())
						{
							return ("-" + sub(number1AsList, number2AsList));
						}
						if(value2.length() == value1.length())
						{
							if(value2.compareTo(value1) < 0)
							{
								return ("-" + sub(number1AsList, number2AsList));
							}
							if(value1.compareTo(value2) == 0)
							{
								return ("0");
							}
						}
						return (sub(number2AsList, number1AsList));
					}
					else //a + (-b) = a - b
					{
						if(value1.length() < value2.length())
						{
							return ("-" + sub(number2AsList, number1AsList));
						}
						if(value1.length() == value2.length())
						{
							if(value1.compareTo(value2) < 0)
							{
								return ("-" + sub(number2AsList, number1AsList));
							}
							if(value1.compareTo(value2) == 0)
							{
								return ("0");
							}
						}
						return (sub(number1AsList, number2AsList));
					}
				}
			}
		}
		if(operator.equals("-"))
		{
			if( !(isValue1Negative || isValue2Negative) ) //a-b = a-b
			{
				if(value1.length() < value2.length())
				{
					return ("-" + sub(number2AsList, number1AsList));
				}
				if(value1.length() == value2.length())
				{
					if(value1.compareTo(value2) < 0)
					{
						return ("-" + sub(number2AsList, number1AsList));
					}
					if(value1.compareTo(value2) == 0)
					{
						return ("0");
					}
				}
				return (sub(number1AsList, number2AsList));
			}
			else
			{
				if( (isValue1Negative && isValue2Negative) ) //(-a) - (-b) = -a + b = b - a
				{
					if(value2.length() < value1.length())
					{
						return ("-" + sub(number1AsList, number2AsList));
					}
					if(value2.length() == value1.length())
					{
						if(value2.compareTo(value1) < 0)
						{
							return ("-" + sub(number1AsList, number2AsList));
						}
						if(value1.compareTo(value2) == 0)
						{
							return ("0");
						}
					}
					return (sub(number2AsList, number1AsList));
				}
				else
				{
					if(isValue1Negative) //(-a) - (b) = -(a+b)
					{
						return ("-" + add(number2AsList, number1AsList));
					}
					else //a - (-b) = a + b
					{
						return (add(number2AsList, number1AsList));
					}
				}
			}
		}
		if(operator.equals("*"))
		{
			if( !(isValue1Negative || isValue2Negative) ) //a*b = a*b
			{
				return (mul(number1AsList, number2AsList));
			}
			else
			{
				if( (isValue1Negative && isValue2Negative) ) //(-a) * (-b) = (a*b)
				{
					return (mul(number1AsList, number2AsList));
				}
				else
				{
					return ("-" + mul(number1AsList, number2AsList));
				}
			}	
		}
		if(operator.equals("/"))
		{
			if( !(isValue1Negative || isValue2Negative) ) //a/b = a/b
			{
				return(div(value1,value2));
			}
			else
			{
				if( (isValue1Negative && isValue2Negative) ) //(-a) / (-b) = (a/b)
				{
					return(div(value1,value2));
				}
				else
				{
					return("-" + div(value1,value2));
				}
			}		
		}
		if(operator.equals("%"))
		{
			if( !(isValue1Negative || isValue2Negative) ) //a/b = a/b
			{
				return(mod(value1,value2));
			}
			else
			{
				if( (isValue1Negative && isValue2Negative) ) //(-a) / (-b) = (a/b)
				{
					return(mod(value1,value2));
				}
				else
				{
					return("-" + mod(value1,value2));
				}
			}		
		}
		if(operator.equals("^"))
		{
			memoizationTable = new HashMap<Integer,String>();
			memoizationTable.put(0, "1");
			memoizationTable.put(1, value1);
			if(!isValue2Negative)
			{
				if(!isValue1Negative)
				{
					return (exp(number1AsList, Integer.parseInt(value2)));
				}
				else
				{
					if(Integer.parseInt(value2) % 2 == 0)
					{
						return (exp(number1AsList, Integer.parseInt(value2)));
					}
					else
					{
						return ("-" + exp(number1AsList, Integer.parseInt(value2)));
					}
				}
			}
			else
			{
				if(value1.equalsIgnoreCase("1"))
				{
					if(Integer.parseInt(value2) % 2 == 0)
					{
						return ("1");
					}
					else
					{
						return ("-1");
					}
				}
				else
				{
					if(Integer.parseInt(value2) == 0)
					{
						return("1");
					}
					else
					{
						return ("0");
					}
				}
			}	
		}
		if(operator.equals("r"))
		{
			return (sqrt(value1));
		}
		return null;
	}

	static void evaluateExpression(String in)
	{
		Stack<String> operandStack = new Stack<String>();
		Stack<String> operatorStack = new Stack<String>();
		HashMap<String,Integer> precedenceRules = new HashMap<String,Integer>();
		precedenceRules.put("+", 1);
		precedenceRules.put("-", 1);
		precedenceRules.put("*", 2);
		precedenceRules.put("/", 2);
		precedenceRules.put("^", 3);
		precedenceRules.put("r", 3);
		precedenceRules.put("%", 3);
		precedenceRules.put("(", 0);
		precedenceRules.put(")", 0);
		String currentToken;
		String previousOperator;
		String currentOutput;
		StringTokenizer st = new StringTokenizer(in);

		try
		{

			while(st.hasMoreTokens())
			{
				currentToken = st.nextToken();
				if(currentToken.matches("-?[0-9]+"))
				{
					operandStack.add(currentToken);
				}
				else
				{
					while(true)
					{
						if(operatorStack.isEmpty() || currentToken.equals("(") || precedenceRules.get(currentToken) > precedenceRules.get(operatorStack.peek()))
						{
							operatorStack.add(currentToken);
							break;
						}
						else
						{
							if(currentToken.equals(")"))
							{

								previousOperator = operatorStack.pop();
								while(!previousOperator.equals("("))
								{
									String value2    = operandStack.pop();
									String value1    = operandStack.pop();
									currentOutput    = performOperation(value1, value2, previousOperator);
									if(currentOutput !=null)
									{
										operandStack.push(currentOutput);
										previousOperator = operatorStack.pop();
									}
									else
									{
										break;
									}
								}
								break;
							}
							else
							{
								previousOperator = operatorStack.pop();
								String value2    = operandStack.pop();
								String value1    = operandStack.pop();
								currentOutput    = performOperation(value1, value2, previousOperator);

								if(currentOutput !=null)
								{
									operandStack.push(currentOutput);
								}
								else
								{
									break;
								}

							}
						}
					}
				}
			}
			while(!operatorStack.isEmpty())
			{
				previousOperator = operatorStack.pop();
				String value2    = operandStack.pop();
				String value1    = operandStack.pop();
				currentOutput    = performOperation(value1, value2, previousOperator);
				if(currentOutput !=null)
				{
					operandStack.push(currentOutput);
				}
				else
				{
					break;
				}	
			}
			p.println(operandStack.pop());
		}
		catch(Exception e)
		{
			p.println("Syntax error");
		}
	}

	static LinkedList<Long> stringNumberToLinkedList(String stringNumber)
	{ 
		LinkedList<Long> ll = new LinkedList<Long>();
		int counter = stringNumber.length() / LOG10_OF_BASE ;
		int startPos = stringNumber.length() - LOG10_OF_BASE;
		while(counter >0)
		{
			ll.add(Long.parseLong(stringNumber.substring(startPos, startPos+ LOG10_OF_BASE)));
			startPos = startPos - LOG10_OF_BASE;
			counter--;
		}
		if( stringNumber.length() % LOG10_OF_BASE > 0 )
		{
			ll.add(Long.parseLong(stringNumber.substring(0, stringNumber.length() % LOG10_OF_BASE)));
		}

		return ll;
	}

	static LinkedList<Long> stringBufferNumberToLinkedList(StringBuffer stringNumber)
	{ 
		LinkedList<Long> ll = new LinkedList<Long>();
		int counter = stringNumber.length() / LOG10_OF_BASE ;
		int startPos = stringNumber.length() - LOG10_OF_BASE;
		while(counter >0)
		{
			ll.add(Long.parseLong(stringNumber.substring(startPos, startPos+ LOG10_OF_BASE)));
			startPos = startPos - LOG10_OF_BASE;
			counter--;
		}
		if( stringNumber.length() % LOG10_OF_BASE > 0 )
		{
			ll.add(Long.parseLong(stringNumber.substring(0, stringNumber.length() % LOG10_OF_BASE)));
		}

		return ll;
	}


	static void printNumberInLinkedList(LinkedList<Long> ll)
	{
		ListIterator<Long> llIterator = ll.listIterator();
		System.out.print("Number:" + "\t");
		while(llIterator.hasNext())
		{
			System.out.print(llIterator.next() + "\t");
		}
		System.out.println();
	}
}
