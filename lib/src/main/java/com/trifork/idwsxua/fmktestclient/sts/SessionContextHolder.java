package com.trifork.idwsxua.fmktestclient.sts;

public class SessionContextHolder {
	private static ThreadLocal<SessionContext> context = new ThreadLocal<>();
	
	public static SessionContext get() {
		return context.get();
	}
	
	public static void set(SessionContext sessionContext) {
		context.set(sessionContext);
	}
	
	public static void clear() {
		context.remove();
	}
}
