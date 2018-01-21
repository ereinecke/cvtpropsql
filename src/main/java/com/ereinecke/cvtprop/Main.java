package com.ereinecke.cvtprop;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.util.Iterator;

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
    // static String inputFile = "data/resm-listings-modified.wordpress.2017-12-07.xml";
    static Document document;

    public static void main(String[] args) {

        // input file from command line
        if (args.length == 1) {
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

        // Show elements
        Element root = document.getRootElement();
        Logger.info(root.toString());




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
     * Prints out content of reader DOM tree
     */
//    public static printNodes(Document document) {
//        for (Node node : nodes) {
//            Element element = (Element) node;
//            Iterator<Element> iterator = element.elementIterator()
//        }
//    }
}
