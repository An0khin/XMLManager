package com.home;

import java.io.File;
import java.util.List;

public interface XMLManager {
    void createXml(String nameRoot);
    void addElement(String elementTag, String parentTag);
    void addNode(Nodeable nodeable, String childTag, String parentTag);
    void removeNode(Nodeable nodeable, String nodeTag, String parentTag);
    void editNode(Nodeable oldNodeable, Nodeable newNodeable, String nodeTag, String parentTag);
    void clear(String parentTag);

    void saveXml(File toPath);
    void loadXml(File fromPath);
    List<String[]> getListOf(String nodeListTag, String nodeTags, Nodeable nodeable);
}
