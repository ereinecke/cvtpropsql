package com.ereinecke.cvtprop;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import com.ereinecke.cvtprop.Constants;

import static com.ereinecke.cvtprop.Constants.*;
import static com.ereinecke.cvtprop.Main.printItems;

/**
 * Reads and parses property files (export xml format) from the WordPress theme WP Pro Real Estate
 * Writes out export xml file in the formate required by Realty v 3.0.1
 *
 * @param  inputxml  file name (default location PROJECT_DIR/data)
 *
 *                  Test files:  full sized: resm-listings-modified.wordpress.2017-12-07.xml
 *                               small: 2427-prod.xml
 *
 * */

public class Main {

    static String inputFile = "";
    // static String inputFile = FULL_INPUT;
    static Document document;
    static List<Node> nodes;

    public static void main(String[] args) {

        // input file from command line unless hardcoded above
        if (inputFile.length() == 0 && args.length == 1) {
            inputFile = args[0];
        } else {
            Logger.info("No input file specified.\n  Usage: cvtprop [xmlInputFilename]");
            System.exit(1);
        }

        //  read input xml
        try {
            document = parse(inputFile);
        } catch (DocumentException e) {
            Logger.error(e, "File {} not found.\n", inputFile);
        }

        // root is rss
        Element root = document.getRootElement();
        Logger.info("Root element attribute: {}", root.getName());

        // select all items
        List<Node> nodes = document.selectNodes(ITEM);
        if (nodes == null) Logger.info("No nodes found for {}", ITEM);
        else Logger.info("Number of nodes found for {}: {}", ITEM, nodes.size());

        // log list of items
        printItems(nodes);

//        // Conversion
//        long items = 0;
//        for (Node node : nodes) {
//            Element element = (Element)node;
//            Iterator<Element> iterator = element.elementIterator("item");
//            while (iterator.hasNext()) {
//                items++;
//                Element item = (Element)iterator.next();
//                Logger.info("Item#: {}; {}", items, item.toString());
//            }
//
//        }

        // output DOM4J tree
        // printDOM(reader);
    }

    /**
     *  Parses input xml file
     *
     */
    public static Document parse(String inputFile) throws DocumentException {
        Document doc = null;
        SAXReader reader = new SAXReader();
        doc = reader.read(inputFile);

        return doc;
    }


    /**
     * Prints out list of items (properties) found
     */
    public static void printItems(List<Node> nodes) {
        long nodeNum = 0;

        for (Node node : nodes) {
            nodeNum++;
            String propId = "";
            Element e1 = (Element) node;
            String title = e1.selectSingleNode(TITLE).getText();
            List<Node> postmetas = node.selectNodes(POSTMETA);
            // step through wp:postmeta entries to get property ID
            for (Node postmeta : postmetas) {
                Element e2 = (Element) postmeta.selectSingleNode(METAKEY);
                if (e2.getText().equals(PROPID_KEY)) {
                    propId = postmeta.selectSingleNode(METAVALUE).getText();
                }
            }
            System.out.printf("#%03d: Prop ID: %s; %s\n", nodeNum,
                    propId, title);

        }
    }
}
