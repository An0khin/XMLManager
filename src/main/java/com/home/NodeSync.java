package com.home;
import java.util.List;
import java.util.HashMap;

public interface NodeSync {
	HashMap<String, String> getMapElements();
	List<String> getIds(); //List of {key, value}
	List<String> getKeys();
}
