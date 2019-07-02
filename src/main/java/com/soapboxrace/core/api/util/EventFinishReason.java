package com.soapboxrace.core.api.util;

public enum EventFinishReason {
	UNKNOWN(0),
	COMPLETED(2),
	SUCCEEDED(6),
	DIDNOTFINISH(10),
	CROSSEDFINISH(22),
	KNOCKEDOUT(42),
	TOTALLED(74),
	ENGINEBLOWN(138),
	BUSTED(266),
	EVADED(518),
	CHALLENGECOMPLETED(1030),
	DISCONNECTED(2058),
	FALSESTART(4106),
	ABORTED(8202),
	TIMEDOUT(16394),
	TIMELIMITEXPIRED(32774),
	PAUSEDETECTED(65546),
	SPEEDHACKING(131082),
	CODEPATCHDETECTED(262154),
	BADVERIFIERRESPONSE(524298);

	private int integer;

    EventFinishReason(int integer) {
        this.integer = integer;
    }

    public int getValue() {
        return integer;
    }
}