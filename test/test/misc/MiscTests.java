package test.misc;

import java.util.Scanner;

import org.junit.Test;

public class MiscTests
{
	@Test
	public void scanner_experimentation()
	{
		String textToScan = "aa,ba.ca,dd.1e,21.e3";
		Scanner s = new Scanner(textToScan).useDelimiter("[.]");
		
		while (s.hasNext())
		{
			String next = s.next();
			System.out.println(next);
		}
	}
}
