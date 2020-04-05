package com.clivelewis.drivefx.driveAPI.logging;

public class ConsoleLoggingService implements ILoggingService {

	@Override
	public void log(String message, LogLevel level) {
		System.out.println(message);
	}
}
