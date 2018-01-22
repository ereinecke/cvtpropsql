package com.ereinecke.cvtprop;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import com.ereinecke.cvtprop.Constants;

import static com.ereinecke.cvtprop.Constants.*;
import static com.ereinecke.cvtprop.Main.printItems;

/**
 * Reads and parses property files (export xml format)
 * <p>
 * Input is an export xml file from the WordPress theme WP Pro Real Estate
 * Writes out export xml file in the format required by Realty v 3.0.1
 *
 * @author  Erik Reinecke  <erik@ereinecke.com>
 * @version 0.1
 * @since   1/21/2018
 *
 * @param  inputxml  file name
 *
 *    Test files:  full sized: data/resm-listings-modified.wordpress.2017-12-07.xml
 *                 small:      data/2427-prod.xml
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

        // Conversion
        long items = 0;
        for (Node node : nodes) {
            items++;
            Element element = (Element)node;

            // Change link
            convertLink(element);
        }

        // output DOM4J tree
        String outputFileName = (outputFile(inputFile));
        try {
            XMLWriter writer =
                    new XMLWriter(new FileWriter(new File(outputFileName)));
            writer.write(document);
            writer.close();
        } catch (IOException e) {
            Logger.error(e, "Error writing {}", outputFileName);
            e.printStackTrace();
        }
    }

    /**
     *  Parses input xml file
     *
     *  @param  inputFile  input file specifier
     *
     *
     */
    public static Document parse(String inputFile) throws DocumentException {
        Document doc = null;
        SAXReader reader = new SAXReader();
        doc = reader.read(inputFile);

        return doc;
    }

    /**
     * Converts input property link to output property link
     *
     *  @param  element  xml element containing <link> to convert
     *
     */
    public static void convertLink(Element element) {
        String linkIn = element.selectSingleNode(LINK).getText();
        String linkOut = linkIn.replace(INPUT_LINK, OUTPUT_LINK);
        Logger.info("{} replaced with {}", linkIn, linkOut);
        element.selectSingleNode(LINK).setText(linkOut);
    }

    /**
     * Generates output file name by inserting .cvt before .xml or at end of file
     *
     *  @param  inputFileName
     *  @return outputFileName
     */
    public static String outputFile(String inputFileName) {

        String outputFileName = inputFileName.replace(".xml", "-cvt.xml");
        Logger.info("Output file name: {}", outputFileName);
        return outputFileName;
    }

    /**
     * Prints out list of items (properties) found
     *
     *  @param  node  xml node to display
     *
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
