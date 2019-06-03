package com.soapboxrace.core.api.util;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/Engine.svc")
public class Engine extends Application {
	public void onCreate() {
        System.out.println("hello world");
    }
}
