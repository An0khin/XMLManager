package com.home;

import org.junit.jupiter.api.*;

import javax.xml.transform.TransformerConfigurationException;
import java.io.File;
import java.util.*;

public class XMLManagerTest {
    XMLManager xmlManager;
    public static final String PATH = "src/test/resources/test_xml.xml";
    public static final String PATH_TEST = "src/test/resources/test2_xml.xml";
    public static List<Nodeable> nodesables;

    @BeforeAll
    public static void setUpNodeables() {
        nodesables = new ArrayList<>();

        nodesables.add(new Nodeable() {
            @Override
            public Map<String, String> getValues() {
                Map<String, String> values = new LinkedHashMap<>();
                values.put("name", "Boris");
                values.put("surname", "Frankenvilly");
                return values;
            }

            @Override
            public String[] getIdFieldValue() {
                return new String[] {"id", "1"};
            }
        });

        nodesables.add(new Nodeable() {
            @Override
            public Map<String, String> getValues() {
                Map<String, String> values = new LinkedHashMap<>();
                values.put("name", "Jimmy");
                values.put("surname", "Blanky");
                return values;
            }

            @Override
            public String[] getIdFieldValue() {
                return new String[] {"id", "2"};
            }
        });
    }

    @BeforeEach
    public void setUp() throws TransformerConfigurationException {
        xmlManager = new DefaultXMLManager(PATH);
    }

    @AfterEach
    public void tearDown() {
        boolean wasFileDeleted = new File(PATH).delete();
    }

    @Test
    public void createFile() {
        xmlManager.createXml("Base");
        xmlManager.saveXml(new File(PATH));

        Assertions.assertTrue(new File(PATH).exists());
    }

    @Test
    public void addElement() {
        xmlManager.createXml("Base");

        xmlManager.addElement("Check", "Base");
        xmlManager.saveXml(new File(PATH));

        List<String[]> list = xmlManager.getListOf("Base", "Check", Nodeable.EMPTY_NODEABLE);
        Assertions.assertTrue(list.size() > 0);
    }

    @Test
    public void addNode() {
        xmlManager.createXml("Base");

        xmlManager.addElement("Persons", "Base");
        xmlManager.addNode(nodesables.get(0), "Person", "Persons");
        xmlManager.saveXml(new File(PATH));

        List<String[]> listPersons = xmlManager.getListOf("Persons", "Person", nodesables.get(0));

        Assertions.assertArrayEquals(new String[] {"1", "Boris", "Frankenvilly"}, listPersons.get(0));
    }

    @Test
    public void removeNode() {
        xmlManager.createXml("Base");

        xmlManager.addElement("Persons", "Base");
        xmlManager.addNode(nodesables.get(0), "Person", "Persons");
        xmlManager.addNode(nodesables.get(1), "Person", "Persons");
        xmlManager.saveXml(new File(PATH));

        xmlManager.removeNode(nodesables.get(0), "Person", "Persons");
        List<String[]> listPersons = xmlManager.getListOf("Persons", "Person", nodesables.get(0));

        Assertions.assertEquals(1, listPersons.size());
        Assertions.assertArrayEquals(new String[] {"2", "Jimmy", "Blanky"}, listPersons.get(0));
    }

    @Test
    public void editNode() {
        xmlManager.createXml("Base");

        xmlManager.addElement("Persons", "Base");
        xmlManager.addNode(nodesables.get(0), "Person", "Persons");
        xmlManager.saveXml(new File(PATH));

        xmlManager.editNode(nodesables.get(0), nodesables.get(1), "Person", "Persons");
        List<String[]> listPersons = xmlManager.getListOf("Persons", "Person", nodesables.get(0));

        Assertions.assertArrayEquals(new String[] {"2", "Jimmy", "Blanky"}, listPersons.get(0));
    }

    @Test
    public void clear() {
        xmlManager.createXml("Base");

        xmlManager.addElement("Persons", "Base");
        xmlManager.addNode(nodesables.get(0), "Person", "Persons");
        xmlManager.addNode(nodesables.get(1), "Person", "Persons");
        xmlManager.saveXml(new File(PATH));

        xmlManager.clear("Persons");
        List<String[]> listPersons = xmlManager.getListOf("Persons", "Person", nodesables.get(0));

        Assertions.assertTrue(listPersons.isEmpty());
    }

    @Test
    public void load() {
        xmlManager.createXml("Base");

        xmlManager.addElement("Persons", "Base");
        xmlManager.addNode(nodesables.get(0), "Person", "Persons");
        xmlManager.saveXml(new File(PATH));
        xmlManager.loadXml(new File(PATH_TEST));

        List<String[]> listPersons = xmlManager.getListOf("Persons", "Person", nodesables.get(0));

        Assertions.assertEquals(2, listPersons.size());
        Assertions.assertArrayEquals(new String[] {"1", "Boris", "Frankenvilly"}, listPersons.get(0));
        Assertions.assertArrayEquals(new String[] {"1", "Boris", "Frankenvilly"}, listPersons.get(1));
    }
}
