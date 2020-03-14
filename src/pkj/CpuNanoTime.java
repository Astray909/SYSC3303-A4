package pkj;

import java.lang.System.*;
import java.text.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class CpuNanoTime {

	public static void main(String[] args) throws ParseException
	{
		Thread intermediate2;
		
		intermediate2 = new Thread(new IntHost());
		long startTime = System.nanoTime();
		intermediate2.start();
		long endTime = System.nanoTime();
		long durationInNano = (endTime - startTime);
		System.out.println("The following is CPU time with nanoTime:\n" + durationInNano);
	}

}
