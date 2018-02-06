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
import java.util.ArrayList;
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

        // Set log level INFO < WARNING < ERROR < OFF
        Configurator.defaultConfig()
                .level(Level.INFO)
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

            // Convert beds and baths to wp:metadata entries
            String numRooms = "";
            Element Rooms = getCategoryNode(item, BEDS);
            if (Rooms != null) numRooms = Rooms.attributeValue(NICENAME);
            wpPostmeta(item, EP_BEDROOMS_KEY, numRooms );
            wpPostmeta(item, _EP_BEDROOMS_KEY,
                    _EP_BEDROOMS_VALUE);
            if (Rooms != null) Rooms.detach();
            numRooms = "";
            Rooms = getCategoryNode(item, BATHS);
            if (Rooms != null) numRooms = Rooms.attributeValue(NICENAME);
            if (numRooms == null) numRooms = "";
            wpPostmeta(item, EP_BATHROOMS_KEY, numRooms );
            wpPostmeta(item, _EP_BATHROOMS_KEY,
                    _EP_BATHROOMS_VALUE);
            if (Rooms != null) Rooms.detach();

            // Property ID
            String propIdString = getPostmetaValue(item, PROPID_KEY);
            if (propIdString != null) {
                wpPostmeta(item, EP_ID_KEY, propIdString);
                wpPostmeta(item, _EP_ID_KEY, _EP_ID_VALUE);
            }

            // City & state
            Element city = getCategoryNode(item, CITY);
            citySubst(item, city);
            if (city != null) city.detach();
            city = getCategoryNode(item, STATE);
            if (city != null) city.detach();

            // Property type
            Element propertyType = getCategoryNode(item, PROP_TYPE);
            if (propertyType != null ) propertyTypeWrite(item, propertyType);
            // TODO: check to see what happens if domain="property_type" doesn't change

            // Property status
            Element propertyStatus = getCategoryNode(item, PROP_STATUS);
            if (propertyStatus != null ) propertyStatusWrite(item, propertyStatus);
            domainSubst(item, CATEGORY, DOMAIN, PROP_STATUS, STATUS_OUT, false);

            // Property features
            // Change features attribute domain first
            domainSubst(item, CATEGORY, DOMAIN,
                    FEATURES_IN, FEATURES_OUT, false);
            propertyFeaturesWrite(item);

            // Property and construction size
            Element propSize = getPostmetaElement(item, CONST_SIZE_KEY);
            if (propSize != null) {
                String houseArea = getPostmetaValue(item, CONST_SIZE_KEY);
                wpPostmeta(item,  EP_CONST_SIZE_KEY, houseArea);
                wpPostmeta(item, _EP_CONST_SIZE_KEY, _EP_CONST_SIZE_VALUE);
                propSize.getParent().detach();
            }
            propSize = getPostmetaElement(item, LOT_SIZE_KEY);
            if (propSize != null) {
                String lotArea = getPostmetaValue(item, LOT_SIZE_KEY);
                wpPostmeta(item,  EP_LOT_SIZE_KEY, lotArea);
                wpPostmeta(item, _EP_LOT_SIZE_KEY, _EP_LOT_SIZE_VALUE);
                propSize.getParent().detach();
            }
            
            // Latitude and Longitude
            Element latLong = getPostmetaElement(item, LATLNG_KEY);
            if (latLong != null) {
                String latLongValues = getPostmetaValue(item, LATLNG_KEY);
                addressWrite(item, latLongValues);
                latLong.getParent().detach();
            }

            // Postal code
            Element postCode = getCategoryNode(item, ZIPCODE_KEY);
            if (postCode != null) {
                String postCodeString = postCode.attributeValue(NICENAME);
                wpPostmeta(item, EP_POSTAL_CODE_KEY, postCodeString);
                wpPostmeta(item,_EP_POSTAL_CODE_KEY, _EP_POSTAL_CODE_VALUE);
                postCode.getParent().detach();
            }

            // write __thumbnail_id postmeta (field value)
            Element thumbnail = getPostmetaElement(item, THUMBNAIL_KEY);
            String thumbnailId = "";
            if (thumbnail != null) {
                thumbnailId = getPostmetaValue(item, THUMBNAIL_ID_KEY);
                thumbnail.getParent().detach();
            }
            wpPostmeta(item, THUMBNAIL_ID_KEY,thumbnailId);
            wpPostmeta(item, _THUMBNAIL_ID_KEY, _THUMBNAIL_ID_VALUE);

            // convert price postmeta and add price prefix & suffix
            String postmetaString = "";
            Element postmeta = getPostmetaElement(item, PRICE_KEY);
            if (postmeta != null) postmetaString = postmeta.getText();
            wpPostmeta(item, EP_PRICE_KEY, postmetaString);
            wpPostmeta(item, _EP_PRICE_KEY, _EP_PRICE_VALUE);
            if (postmeta != null) postmeta.getParent().detach();
            wpPostmeta(item, EP_PRICE_PREFIX_KEY, EP_PRICE_PREFIX_VALUE);
            wpPostmeta(item, _EP_PRICE_PREFIX_KEY, _EP_PRICE_PREFIX_VALUE);
            wpPostmeta(item, EP_PRICE_SUFFIX_KEY, EP_PRICE_SUFFIX_VALUE);
            wpPostmeta(item, _EP_PRICE_SUFFIX_KEY, _EP_PRICE_SUFFIX_VALUE);


            // Remove original property ID last so that it can be used for logging
            Element propId = getPostmetaElement(item, PROPID_KEY);
            propId.getParent().detach();
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

        // Fix &gt; and &lt; in output
        try {
            Runtime.getRuntime().exec("data/fixcvt.sh " + outputFileName);
        } catch (IOException e) {
            Logger.error("Unable to run fixcvt.sh on {}", outputFileName);
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

    public static Element getPostmetaElement(Element item, String meta_key) {

        List<Node> postmetas = item.selectNodes(POSTMETA);
        for (Node postmeta : postmetas) {
            Element e2 = (Element) postmeta.selectSingleNode(METAKEY);
            if (e2.getText().equals(meta_key)) {
                return (Element)postmeta.selectSingleNode(METAVALUE);
            }
        }
        Logger.warn("{} with {} \'{}\' not found for PropID: {}", POSTMETA,
                METAKEY, meta_key, getPostmetaValue(item, PROPID_KEY));
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

        Element postmeta = getPostmetaElement(item, meta_key);
        if (postmeta != null) {
            return postmeta.getText();
        }

        // Logger.error(POSTMETA + " with " + METAKEY + "\'" + meta_key + "\' not found.");
        Logger.warn("{} with {} \'{}\' not found for PropID: {}", POSTMETA,
                METAKEY, meta_key, getPostmetaValue(item, PROPID_KEY));
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
     * Find  single category node with specified domain attribute
     * if there are multiple category nodes with that domain, only first
     * is returned
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
        Logger.warn(CATEGORY + "\'" + domain +
                "\' not found for PropID: " + getPostmetaValue(item, PROPID_KEY));

        return null;
    }

    /**
     * Find multiple category nodes with specified domain attribute
     *
     *  @param  item            element containing categories
     *  @param  domain          category domain attribute
     *
     */
    public static List<Element> getCategoryNodes(Element item, String domain) {

        List<Element> categories = item.selectNodes(CATEGORY);
        List<Element> domainCategories = new ArrayList<>();
        for (Element category : categories) {
            if (category.attributeValue(DOMAIN).equals(domain)) {
                domainCategories.add(category);
            }
        }
        if (domainCategories.size() < 1) {
            domainCategories = null;
            Logger.error(CATEGORY + "\'" + domain +
                    "\' not found for PropID: " + getPostmetaValue(item, PROPID_KEY));
        }

        return domainCategories;
    }

    /**
     * Write propertytype to proper wp:postmeta entry
     *
     *  @param  item            element containing categories
     *  @param  propertyType    element containing property type
     *
     */

    public static void propertyTypeWrite(Element item, Element propertyType) {
        String propType = null;
        if (propertyType == null) {
            propType = "unknown";
            Logger.error("Property Type not found.");
        } else {
            propType = propertyType.attributeValue("nicename");
            // Haciendas become country homes
            if (propType == "haciendas") propType = "countryhomes";
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
     * Write property status to proper wp:postmeta entry
     *
     *  @param  item            element containing categories
     *  @param  latLongString   latitude and longitude, comma-separated
     *
     */

    public static void addressWrite(Element item, String latLongString) {
        // parse latitude and longitude
        String[] latLong = latLongString.split(",");
        if (latLong == null || latLong.length != 2) {
            Logger.error("Error parsing latlong string \'{}\'", latLongString);
            return;
        }

        // Write address postmeta
        String latLongSer =
                "a:3:{s:3:\"lat\";s:" + latLong[0].length() + ":\"" + latLong[0] +
                "\";s:3:\"lng\";s:" + latLong[1].length() + ":\"" + latLong[1] +
                "\";}";
        Logger.info("LatLong converted from {} to {}", latLongString, latLongSer);
        wpPostmeta(item, EP_GOOGLE_MAPS_KEY, latLongSer);
        wpPostmeta(item, _EP_GOOGLE_MAPS_KEY, _EP_GOOGLE_MAPS_VALUE);
    }

    /**
     * Write property status to proper wp:postmeta entry
     *
     *  @param  item              element containing categories
     *
     */

    public static void propertyFeaturesWrite(Element item) {
        List<Element> propFeatures = getCategoryNodes(item, FEATURES_OUT);
        if (propFeatures == null) {
            Logger.error("No property features found for propID: {}",
                    getPostmetaValue(item, PROPID_KEY));
            return;
        }
        String propFeatureSer = "a:" + propFeatures.size() + ":{";

        // propFeatures is the list of features found in this listing;
        // propertyFeatures is the full list of features
        // TODO: outer loop has to be changed to .hasNext()
        int i = 0;
        for (Element feature : propFeatures) {
            propFeatureSer += "i:" + i;
            i++;
            for (int j = 0; j < propertyFeatures.length; j++) {
                if (feature.attributeValue(NICENAME).
                        equals(propertyFeatures[j].slug)) {
                    propFeatureSer += ";i:" + propertyFeatures[j].propTypeNum +
                    ";";
                }
            }
        }
        propFeatureSer += "}";
        wpPostmeta(item, AP_FEATURES_KEY, propFeatureSer);
        wpPostmeta(item, _AP_FEATURES_KEY, _AP_FEATURES_VALUE);
        Logger.warn("Set Property Features to \'{}\' propID: {}",
                propFeatureSer, getPostmetaValue(item, PROPID_KEY));
    }

    /**
     * Write property status to proper wp:postmeta entry
     *
     *  @param  item            element containing categories
     *  @param  propertyStatus  element containing property status
     *
     */

    public static void propertyStatusWrite(Element item, Element propertyStatus) {
        String propStatus = null;
        String propStatusCode = null;

        // check ct_status for featured, reduced, sold
        propStatus = propertyStatus.attributeValue("nicename");
        if (propStatus == null) {
            Logger.warn("Property status not found for PropID: {}.",
                    getPostmetaValue(item, PROPID_KEY));
            return;
        }
        int found = 0;

        for (int i = 0; i < propertyStatuses.length; i++) {
            if (propertyStatuses[i].slug.equals(propStatus)) {
                propStatusCode = propertyStatuses[i].propStatusNumSerialized;
                wpPostmeta(item, AP_STATUS_KEY, propStatusCode);
                wpPostmeta(item, _AP_STATUS_KEY, _AP_STATUS_VALUE);
                found++;
                Logger.info("Set Property Status to {}", propStatus,
                        propertyStatuses[i].slug);
                if (found > 1)
                    Logger.error("Multiple property statuses found for PropID: {}.",
                            getPostmetaValue(item, PROPID_KEY));
            }
        }

        // Write featured postmeta
        if (propStatus.equals("featured")) {
            wpPostmeta(item, EP_FEATURED_KEY, "a:1:{i:0;s:1:\"1\";}");
        } else {
            wpPostmeta(item, EP_FEATURED_KEY, "");
        }
        wpPostmeta(item, _EP_FEATURED_KEY, _EP_FEATURED_VALUE);
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
            if (att != null && att.getData().equals(inPattern)) {
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
