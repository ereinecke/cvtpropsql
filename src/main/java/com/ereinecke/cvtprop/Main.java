package com.ereinecke.cvtprop;

import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static com.ereinecke.cvtprop.Constants.*;
import static com.ereinecke.cvtprop.InitPropertyFeatures.initPropertyFeatures;
import static com.ereinecke.cvtprop.InitPropertyStatus.initPropertyStatus;
import static com.ereinecke.cvtprop.InitPropertyTypes.initPropertyTypes;
import static java.lang.System.exit;

/**
 * Reads and parses property files (export xml format)
 * <p>
 * Input is an export xml file from the WordPress theme WP Pro Real Estate.
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

    static long nodeNum = 0;
    static String inputFile = "";
    // static String inputFile = FULL_INPUT;
    static Document document;
    static List<Node> nodes;
    static PropertyStatus[] propertyStatuses;
    static PropertyTypeRealty[] propertyTypes;
    static PropertyFeature[] propertyFeatures;


    public static void main(String[] args) {

        // Set log level INFO < ERROR < OFF
        Configurator.defaultConfig()
                .level(Level.ERROR)
                .activate();

        propertyStatuses = initPropertyStatus();
        propertyTypes = initPropertyTypes();
        propertyFeatures = initPropertyFeatures();

        // input file from command line unless hardcoded above
        if (inputFile.length() == 0 && args.length == 1) {
            inputFile = args[0];
        } else {
            Logger.error("No input file specified.\n  Usage: cvtprop [xmlInputFilename]");
            exit(1);
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
        if (nodes == null) {
            Logger.error("No nodes found for {}", ITEM);
            exit(1);
        }
        else {
            Logger.info("Number of nodes found for {}: {}", ITEM, nodes.size());
            // log list of items
            // printItems(nodes);
        }

        // Conversion
        nodeNum = 0;
        for (Node node : nodes) {
            nodeNum++;
            System.out.println(">===========================================================================<");
            itemHeader(node);
            Element item = (Element)node;



            // Change link
            elementSubst(item, LINK, LINK_IN, LINK_OUT, false);
            // Change guid
            elementSubst(item, GUID, EXPORT_URL, IMPORT_URL, false);
            // Change post_type
            elementSubst(item, POST_TYPE, POST_TYPE_IN, POST_TYPE_OUT, true);
            // Change features attribute domain
            domainSubst(item, CATEGORY, DOMAIN,
                    FEATURES_IN, FEATURES_OUT, false);

            // Convert beds and baths to wp:metadata entries
            String numRooms = "";
            Element Rooms = getCategoryNode(item, BEDS);
            if (Rooms != null) numRooms = Rooms.getText();
            wpPostmeta(item, EP_BEDROOMS_KEY, numRooms );
            wpPostmeta(item, _EP_BEDROOMS_KEY,
                    _EP_BEDROOMS_VALUE);
            if (Rooms != null) Rooms.detach();
            numRooms = "";
            Rooms = getCategoryNode(item, BATHS);
            if (Rooms != null) numRooms = Rooms.getText();
            if (numRooms == null) numRooms = "";
            wpPostmeta(item, EP_BATHROOMS_KEY, numRooms );
            wpPostmeta(item, _EP_BATHROOMS_KEY,
                    _EP_BATHROOMS_VALUE);
            if (Rooms != null) Rooms.detach();

            // City
            Element city = getCategoryNode(item, CITY);
            citySubst(item, city);
            if (city != null) city.detach();
            city = getCategoryNode(item, STATE);
            if (city != null) city.detach();

            // Property type
            Element propertyType = getCategoryNode(item, PROPERTY_TYPE);
            if (propertyType != null ) propertyTypeWrite(item, propertyType);

            // write __thumbnail_id postmeta (field value)
            wpPostmeta(item, _THUMBNAIL_ID_KEY, _THUMBNAIL_ID_VALUE);

            // convert price postmeta and add price prefix & suffix
            String postmetaString = "";
            Element postmeta = getPostmeta(item, PRICE_KEY);
            if (postmeta != null) postmetaString = postmeta.getText();
            wpPostmeta(item, EP_PRICE_KEY, postmetaString);
            wpPostmeta(item, _EP_PRICE_KEY, _EP_PRICE_VALUE);
            if (postmeta != null) postmeta.detach();
            wpPostmeta(item, EP_PRICE_PREFIX_KEY, postmetaString);
            wpPostmeta(item, _EP_PRICE_PREFIX_KEY, _EP_PRICE_PREFIX_VALUE);
            wpPostmeta(item, EP_PRICE_SUFFIX_KEY, postmetaString);
            wpPostmeta(item, _EP_PRICE_SUFFIX_KEY, _EP_PRICE_SUFFIX_VALUE);
        }

        // output DOM4J tree
        String outputFileName = (outputFile(inputFile));
        try {
            XMLWriter writer =
                    new XMLWriter(new FileWriter(new File(outputFileName)),
                            OutputFormat.createPrettyPrint());
            writer.write(document);
            writer.close();
        } catch (IOException e) {
            Logger.error(e, "Error writing {}", outputFileName);
            e.printStackTrace();
        }
    }

    /**
     * Write a new wp:postmeta node (<item>) with specified key and value
     *
     *  @param  item            element to contain wp:postmeta
     *  @param  meta_key        wp:meta_key
     *  @param  meta_value      wp:meta_value
     *
     */
    public static void wpPostmeta(Element item,
                                   String meta_key, String meta_value) {

        Element postmeta = item.addElement(POSTMETA);
        Element postmetaKey = postmeta.addElement(METAKEY);
        postmetaKey.setText(toCDATA(meta_key));
        Element postmetaValue = postmeta.addElement(METAKEY);
        postmetaValue.setText(toCDATA(meta_value));
        Logger.info("Added wp:postmeta element with wp:meta_key \'{}\' and wp:meta_value \'{}\'",
                postmetaKey.getText(), postmetaValue.getText());
    }

    /**
     * Get specified wp:postmeta element with key metakey in specified item
     *
     *  @param  item            element to contain wp:postmeta
     *  @param  meta_key        wp:meta_key

     *  @return                 wp:postmeta element
     *
     */

    public static Element getPostmeta(Element item, String meta_key) {

        List<Node> postmetas = item.selectNodes(POSTMETA);
        for (Node postmeta : postmetas) {
            Element e2 = (Element) postmeta.selectSingleNode(METAKEY);
            if (e2.getText().equals(meta_key)) {
                return (Element)postmeta.selectSingleNode(METAVALUE);
            }
        }
        Logger.error(POSTMETA + " with " + METAKEY + "\'" + meta_key + "\' not found.");
        return null;
    }

    /**
     * Get value of specified wp:postmeta with key metakey in specified item
     *
     *  @param  item            element to contain wp:postmeta
     *  @param  meta_key        wp:meta_key

     *  @return                 wp:meta_value
     *
     */

    public static String getPostmetaValue(Element item, String meta_key) {

        Element postmeta = getPostmeta(item, meta_key);
        if (postmeta != null) {
            return postmeta.getText();
        }

        Logger.error(POSTMETA + " with " + METAKEY + "\'" + meta_key + "\' not found.");
        return null;
    }

    /**
     * returns specified string encoded as CDATA
     *
     *  @param   inString        string to encode
     *
     *  @returns  outString     encoded string
     *
     */

    public static String toCDATA(String inString) {
        return CDATA_OPEN + inString + CDATA_CLOSE;
    }

    /**
     * Find category node with specified domain attribute
     *
     *  @param  item            element containing categories
     *  @param  domain          category domain attribute
     *
     */
    public static Element getCategoryNode(Element item, String domain) {

        List<Element> categories = item.selectNodes(CATEGORY);
        for (Element category : categories) {
            if (category.attributeValue(DOMAIN).equals(domain)) {
                return category;
            }
        }
        Logger.error(CATEGORY + "\'" + domain +
                "\' not found for PropID: " + getPostmetaValue(item, PROPID_KEY));

        return null;
    }

    /**
     * Convert city element to proper wp:postmeta entry
     *
     *  @param  city        xml element needing change
     *
     */

    public static void propertyTypeWrite(Element item, Element propertyType) {
        String propType = null;
        if (propertyType == null) {
            propType = "unknown";
            Logger.error("Property Type not found.", propType);
        } else {
            propType = propertyType.attributeValue("nicename");
        };
        String propTypeCode = null;
        int found = 0;

        for (int i = 0; i < propertyTypes.length; i++) {
            if (propertyTypes[i].slug.equals(propType)) {
                propTypeCode = propertyTypes[i].propTypeNumSerialized;
                wpPostmeta(item, AP_TYPE_KEY, propTypeCode);
                wpPostmeta(item, _AP_TYPE_KEY, _AP_TYPE_VALUE);
                found++;
                Logger.info("Set Property Type to {}", propType,
                        propertyTypes[i].slug);
                if (found > 1)
                    Logger.error("Multiple property types found.");
            }
        }
    }

    /**
     * Convert city element to proper wp:postmeta entry
     *
     *  @param  city        xml element needing change
     *
     */

    public static void citySubst(Element item, Element city) {
        String cityName;
        if (city != null) {
            cityName = city.attributeValue("nicename");
        } else {
            cityName = "";
            Logger.error("City not found.", cityName);
        }
        String locationCode = null;
        switch (cityName) {
            case "san-miguel-de-allende":
                locationCode = "91";
                break;
            case "san-jose-iturbide":
                locationCode = "103";
                break;
            case "dolores-hidalgo":
                locationCode = "97";
                break;
            case "biznaga":
                locationCode = "102";
                break;
            case "tamazunchale":
                locationCode = "96";
                break;
            default:
                locationCode = "109";
        }
        String locationString = "a:1:{i:0;s:" + locationCode.length() +
                ":\"" + locationCode + "\";}";
        wpPostmeta(item, AP_LOCATIONS_KEY,locationString);
        wpPostmeta(item, _AP_LOCATIONS_KEY, _AP_LOCATIONS_VALUE);
        Logger.info("Set city {} to {}", cityName, locationString);
    }

    /**
     * Simple text substition in specified single element of supplied node
     *
     *  @param  node        xml node containing element to change
     *  @param  element     xml element needing change
     *  @param  inPattern   text string to change
     *  @param  outPattern  substitution string
     *  @param  cdata       output string as CDATA
     *
     */
    public static void elementSubst(Node node, String element,
                                    String inPattern, String outPattern,
                                    Boolean cdata) {
        String inString = node.selectSingleNode(element).getText();
        String outString = inString.replace(inPattern, outPattern);
        if (cdata) {
            node.selectSingleNode(element).setText(CDATA_OPEN + outString +
                    CDATA_CLOSE);
        } else {
            node.selectSingleNode(element).setText(outString);
        }
        Logger.info("\'{}\' replaced with \'{}\'", inString,
                node.selectSingleNode(element).getText());
    }

    /**
     * Change an element attribute in a specified node
     *
     *  @param  node        xml node containing element to change
     *  @param  element     xml element needing change
     *  @param  attribute   xml attribute to change
     *  @param  inPattern   text string to change
     *  @param  outPattern  substitution string
     *  @param  cdata       output string as CDATA
     *
     */
    public static void domainSubst( Node node, String element,
                                    String attribute, String inPattern,
                                    String outPattern, Boolean cdata) {
        // get list of elements with specified name
        List<Node> elements = node.selectNodes(element);
        for (Node el : elements) {
            Element elm = (Element) el;
            // get specified attribute
            Attribute att = elm.attribute(attribute);
            if (att != null) {
                // replace inPattern with outPattern
                String attValue = att.getValue();
                String newAttValue = attValue.replace(inPattern, outPattern);

                if (cdata) {
                    att.setValue(CDATA_OPEN + newAttValue + CDATA_CLOSE);
                } else {
                    att.setValue(newAttValue);
                }
                Logger.info("{} attribute {} \'{}\' replaced with \'{}\'",
                        element, attribute, attValue, att.getValue());
            } else {
                Logger.error("{} attribute \'{}\' not found.",
                        element, attribute);
            }
        }
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
     *  @param  nodes  List of xml Nodes to display
     *
     */

    public static void printItems(List<Node> nodes) {
        nodeNum = 0;

        for (Node node : nodes) {
            nodeNum++;
            itemHeader(node);
        }
    }

    /**
     * Prints header line for an item
     *
     *  @param  node  xml item node
     *
     */
    public static void itemHeader(Node node) {
        String propId = "";
        Element e1 = (Element) node;
        String title = e1.selectSingleNode(TITLE).getText();
        propId = getPostmetaValue(e1, PROPID_KEY);
        System.out.printf("#%03d: Prop ID: %s; %s\n", nodeNum,
                propId, title);
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
}
