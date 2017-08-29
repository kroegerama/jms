package de.janhoelscher.jms.logging;

public abstract class Logger {

	public static final int	DEBUG		= 0b0001;

	public static final int	INFO		= 0b0010;

	public static final int	WARN		= 0b0100;

	public static final int	ERROR		= 0b1000;

	private static int		LOG_LEVEL	= DEBUG | INFO | WARN | ERROR;

	public static void init(int logLevel) {
		LOG_LEVEL = logLevel;
	}

	public static void setDebug(boolean enabled) {
		set(DEBUG, enabled);
	}

	public static void setInfo(boolean enabled) {
		set(INFO, enabled);
	}

	public static void setWarn(boolean enabled) {
		set(WARN, enabled);
	}

	public static void setError(boolean enabled) {
		set(ERROR, enabled);
	}

	public static void set(int level, boolean enabled) {
		LOG_LEVEL = enabled ? (LOG_LEVEL | level) : (LOG_LEVEL & ~level);
	}

	public static void debug(String msg) {
		debug(msg, null);
	}

	public static void debug(String msg, Throwable t) {
		log("DEBUG", msg, t);
	}

	public static void info(String msg) {
		info(msg, null);
	}

	public static void info(String msg, Throwable t) {
		log("INFO ", msg, t);
	}

	public static void warn(String msg) {
		warn(msg, null);
	}

	public static void warn(String msg, Throwable t) {
		log("WARN ", msg, t);
	}

	public static void error(String msg) {
		error(msg, null);
	}

	public static void error(String msg, Throwable t) {
		log("ERROR", msg, t);
	}

	private static void log(String level, String msg, Throwable t) {
		System.out.println(getPrefix(level) + msg);
		if (t != null) {
			logThrowable(level, t);
		}
	}

	private static void logThrowable(String level, Throwable t) {
		System.out.println(getPrefix(level) + t.toString());
		for (StackTraceElement elem : t.getStackTrace()) {
			System.out.println(getPrefix(level) + "     " + elem.toString());
		}
		if (t.getCause() != null) {
			System.out.println(getPrefix(level) + "Caused by:");
			logThrowable(level, t.getCause());
		}
	}

	private static String getPrefix(String level) {
		return "[ " + getTime() + " | " + level + " ] ";
	}

	private static String getTime() {
		long tmp = System.currentTimeMillis() / 1000;
		int s = (int) (tmp % 60);
		tmp = (tmp - s) / 60;
		int m = (int) (tmp % 60);
		tmp = (tmp - m) / 60;
		int h = (int) (tmp % 24);
		return addLeadingZero(h) + ":" + addLeadingZero(m) + ":" + addLeadingZero(s);
	}

	private static String addLeadingZero(int i) {
		return i < 10 ? "0" + i : "" + i;
	}
}