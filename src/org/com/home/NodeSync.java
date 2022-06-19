package org.com.home;
import java.util.List;
import java.util.SortedMap;

public interface NodeSync {
	SortedMap<String, String> getMapElements();
	List<String> getIds();
}
