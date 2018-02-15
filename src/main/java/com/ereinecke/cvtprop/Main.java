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
import java.util.Arrays;
import java.util.List;

import static com.ereinecke.cvtprop.Constants.*;
import static com.ereinecke.cvtprop.InitPropertyFeatures.initPropertyFeatures;
import static com.ereinecke.cvtprop.InitPropertyStatus.initPropertyStatus;
import static com.ereinecke.cvtprop.InitPropertyTypes.initPropertyTypes;
import static com.ereinecke.cvtprop.InitUser.initUser;
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

    static int mediaIx = 0;
    static String listingsFile = "";
    static String mediaFile = "";
    // static String listingsFile = FULL_INPUT;
    static Document propertyDoc;
    static Document mediaDoc;
    static PropertyStatus[] propertyStatuses;
    static PropertyTypeRealty[] propertyTypes;
    static PropertyFeature[] propertyFeatures;
    static User[] users;
    static MediaItem[] mediaItems;
    static List<Node> mediaNodes;

    static boolean PROCESS_PROPERTIES = true;


    public static void main(String[] args) {

        // Set log level INFO < WARNING < ERROR < OFF
        Configurator.defaultConfig()
                .level(Level.INFO)
                .activate();

        propertyStatuses = initPropertyStatus();
        propertyTypes = initPropertyTypes();
        propertyFeatures = initPropertyFeatures();
        users = initUser();

        // input file from command line unless hardcoded above
        if (listingsFile.length() == 0 && args.length == 2) {
            listingsFile = args[0];
            mediaFile = args[1];
        } else {
            Logger.error("No input files specified.\n  Usage: cvtprop listingXML mediaXML");
            exit(1);
        }

        //  read listing xml
        try {
            propertyDoc = parse(listingsFile);
        } catch (DocumentException e) {
            Logger.error(e, "File {} not found.\n", listingsFile);
        }

        //  read media xml
        try {
            mediaDoc = parse(mediaFile);
        } catch (DocumentException e) {
            Logger.error(e, "File {} not found.\n", mediaFile);
        }

        // Initialize and sort media items array, change links in media XML
        processMedia(mediaDoc);

        // set PROCESS_PROPERTIES to false to skip this step
        if (!PROCESS_PROPERTIES) {
            Logger.error("Not processing properties.");
            exit(0);
        } else {
            // The bulk of the work is done in this function
            processProperties(propertyDoc);
        }

        writeDOM(propertyDoc, listingsFile);
        writeDOM(mediaDoc, mediaFile);

    }

    /**
     * Process property items document
     *
     *  @param  mediaDoc   Document containing
     *
     */
    public static void processProperties(Document mediaDoc) {
        // Process property listings

        // modify channel links
        elementSubst(mediaDoc, CHAN_LINK, EXPORT_DOMAIN, IMPORT_DOMAIN, false);
        elementSubst(mediaDoc, CHAN_SITE, EXPORT_DOMAIN, IMPORT_DOMAIN, false);
        elementSubst(mediaDoc, CHAN_BLOG, EXPORT_DOMAIN, IMPORT_DOMAIN, false);

        // select all items (properties)
        List<Node> propertyNodes = propertyDoc.selectNodes(CHAN_ITEM);
        if (propertyNodes == null) {
            Logger.error("No property nodes found in {}.", listingsFile);
            exit(1);
        }
        else {
            Logger.info("Number of nodes found for {}: {}", CHAN_ITEM, propertyNodes.size());
            // log list of items
            // printItems(nodes);
        }

        // Property Listings Conversion
        int i = 0;
        for (Node node : propertyNodes) {
            i++;
            System.out.println(">===========================================================================<");
            itemHeader(node, i);
            Element item = (Element)node;
            // Change link
            elementSubst(item, LINK, LINK_IN, LINK_OUT, false);
            // Change guid
            elementSubst(item, GUID, EXPORT_DOMAIN, IMPORT_DOMAIN, false);
            // Change post_type
            elementSubst(item, POST_TYPE, POST_TYPE_IN, POST_TYPE_OUT, true);

            // Process category nodes
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
            wpPostmeta(item, _EP_BATHROOMS_KEY, _EP_BATHROOMS_VALUE);
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
            if (propertyType != null ) writePropertyType(item, propertyType);
            // TODO: check to see what happens if domain="property_type" doesn't change

            // Property status
            Element propertyStatus = getCategoryNode(item, PROP_STATUS);
            if (propertyStatus != null ) writePropertyStatus(item, propertyStatus);
            domainSubst(item, CATEGORY, DOMAIN, PROP_STATUS, STATUS_OUT, false);

            // Property features
            // Change features attribute domain first
            domainSubst(item, CATEGORY, DOMAIN,
                    FEATURES_IN, FEATURES_OUT, false);
            writePropertyFeatures(item);

            // Process metadata nodes
            // Broker
            writeBrokerNum((Element)node);

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
                writeAddress(item, latLongValues);
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

            // Process mediaItems to write serialized array of photos for each
            // property
            writeMediaGallery(item, propIdString);

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

            // The remaining postmeta items are written with default values
            wpPostmeta(item, _VC_POST_SETTINGS_KEY, _VC_POST_SETTINGS_VALUE);


            // Remove original property ID last so that it can be used for logging
            Element propId = getPostmetaElement(item, PROPID_KEY);
            propId.getParent().detach();
        }
    }

    /**
     * Process media items document, initializing MediaItem array
     * Sort array by wp:post_parent to speed up processing
     *
     *  @param  mediaDoc   Document containing
     *
     */
    public static void processMedia(Document mediaDoc) {

        // modify channel links
        elementSubst(mediaDoc, CHAN_LINK, EXPORT_DOMAIN, IMPORT_DOMAIN, false);
        elementSubst(mediaDoc, CHAN_SITE, EXPORT_DOMAIN, IMPORT_DOMAIN, false);
        elementSubst(mediaDoc, CHAN_BLOG, EXPORT_DOMAIN, IMPORT_DOMAIN, false);

        // select all media items
        mediaNodes = mediaDoc.selectNodes(CHAN_ITEM);
        if (mediaNodes == null) {
            Logger.error("No media nodes found in {}.", mediaFile);
            exit(1);
        }
        else {
            Logger.warn("Number of nodes found for {}: {}", CHAN_ITEM, mediaNodes.size());
            // log list of items
            // printMediaItems(mediaNodes);
        }

        mediaItems = new MediaItem[mediaNodes.size()];
        int i = 0;
        for (Node node : mediaNodes) {
             System.out.println(">===========================================================================<");
             mediaItemHeader(node, i);

            Element pic = (Element) node;
            if (!pic.selectSingleNode(POST_TYPE).getText().equals(ATTACHMENT)) {
                Logger.error("Item #{} not an attachment.", i);
                break;
            }

            // Change links
            elementSubst(pic, LINK, EXPORT_DOMAIN, IMPORT_DOMAIN, false);
            elementSubst(pic, GUID, EXPORT_DOMAIN, IMPORT_DOMAIN, false);
            elementSubst(pic, ATTACH_URL, EXPORT_DOMAIN, IMPORT_DOMAIN, false);

            // Generate an array of MediaItem
            String post_id = pic.selectSingleNode(POST_ID).getText();
            String post_parent = pic.selectSingleNode(POST_PARENT).getText();
            String post_name = pic.selectSingleNode(POST_NAME).getText();
            String link = pic.selectSingleNode(LINK).getText();
            mediaItems[i] = new MediaItem(Integer.parseInt(post_id),
                    Integer.parseInt(post_parent), post_name, link);

            i++;
        }
        // sort MediaItem array by post_parent
        Arrays.sort(mediaItems);
        // printMediaItems(mediaItems);

    }

    /**
     * Write modified DOM4J tree to disk
     *
     *  @param  doc              DOM4J document
     *  @param  inputFileName    input XML file
     *
     */
    public static void writeDOM(Document doc, String inputFileName) {

        String outputFileName = (outputFile(inputFileName));
        try {
            XMLWriter writer =
                    new XMLWriter(new FileWriter(new File(outputFileName)),
                            OutputFormat.createPrettyPrint());
            writer.write(doc);
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
     * Process mediaItems to write serialized array of photos for each
     * property
     *
     *  @param  item            element containing categories
     *  @param  propID          string containing property ID
     *
     */

    public static void writeMediaGallery(Element item, String propIdStr) {

        String mediaItemsTemp = "{";
        int propId = 0;
        // Some IDs have L appended; remove L for int comparison
        try {
            propId = Integer.parseInt(propIdStr);
        } catch (NumberFormatException e) {
            if (propIdStr.contains("L")) {
                propId = Integer.parseInt(propIdStr.substring(0, propIdStr.length() - 1));
            } else {
                e.printStackTrace();
            }
        }
        // get array of mediaItems that cite this property as a parent
        // mediaIx is a global index to take advantage of the fact that
        // mediaItems array is sorted.
        int i = 0;  // counts number of items in gallery
        int maxMediaItems = mediaItems.length;
        int postParent;

        // step through mediaItems until finding the propId
        do {
            postParent = mediaItems[mediaIx].getPost_parent();
            mediaIx++;
        } while (mediaIx < maxMediaItems && postParent < propId);

        if (mediaIx == maxMediaItems) {
            Logger.error("End of media items at #: {}", mediaIx);
            return;
        }

        // TODO: need to make this a for/while propId doesn't change
        //for (i = mediaIx; i < mediaItems.length; mediaIx++) {
        do {
            // get mediaItem post_id
            int mediaItemId = mediaItems[mediaIx].getPost_id();
            String mediaItemIdStr = Integer.toString(mediaItemId);
            mediaItemsTemp += "i:" + i + ";s:" + mediaItemIdStr.length() +
                    ":\"" + mediaItemId + "\";";
             Logger.debug("mediaItemsTemp: {}", mediaItemsTemp);
             mediaIx++;
             i++;
        } while (mediaIx < maxMediaItems &&
                mediaItems[mediaIx].getPost_parent() == postParent);
        mediaItemsTemp += "}";
        // prepend array length
        String numItems = Integer.toString(i);
        String mediaItemSer = "a:" + numItems + ":" + mediaItemsTemp;
        Logger.info("MediaItemsSer for propID({}): {}", propId, mediaItemSer);


        try {
            wpPostmeta(item, EP_GALLERY_KEY, mediaItemSer);
            wpPostmeta(item, _EP_GALLERY_KEY, _EP_GALLERY_VALUE);
        } catch (Exception e) {
            Logger.error("Exception writing gallery postmeta for propId {} ", propIdStr);
            e.printStackTrace();
        }

    }

    /**
     * Write propertytype to proper wp:postmeta entry
     *
     *  @param  item            element containing categories
     *  @param  propertyType    element containing property type
     *
     */

    public static void writePropertyType(Element item, Element propertyType) {
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

    public static void writeAddress(Element item, String latLongString) {
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
     * Write broker assigment to proper wp:postmeta entry
     *
     *  @param  item              Node containing listing
     *
     */

    public static void writeBrokerNum(Element item) {

        // default is Lane.
        String brokerNum = "2";
        Element broker = getPostmetaElement(item, BROKER_KEY);
        // TODO: this if statement may be removed if Lane is always agent
        if (broker != null) {
            String brokerName = getPostmetaValue(item, BROKER_KEY);
            for (int i = 0; i < users.length; i++) {
                if (brokerName.contains(users[i].niceName)) {
                    Logger.error("Broker found: {} ", brokerName);
                    brokerNum = users[i].userNum;
                    break;
                }
            }
            broker.getParent().detach();
        } else {
            Logger.error("Broker not found.");
        }
        wpPostmeta(item, EP_CUSTOM_AGENT_KEY, brokerNum);
        wpPostmeta(item, _EP_CUSTOM_AGENT_KEY, _EP_CUSTOM_AGENT_VALUE);
        wpPostmeta(item, EP_CONTACT_INFORMATION_KEY, EP_CONTACT_INFORMATION_VALUE);
        wpPostmeta(item, _EP_CONTACT_INFORMATION_KEY, _EP_CONTACT_INFORMATION_VALUE);
    }


    /**
     * Write property status to proper wp:postmeta entry
     *
     *  @param  item              element containing categories
     *
     */

    public static void writePropertyFeatures(Element item) {
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

    public static void writePropertyStatus(Element item, Element propertyStatus) {
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
     * Simple text substition in specified elements of supplied node
     *
     *  @param  node        xml node containing elements to change
     *  @param  element     xml element needing change
     *  @param  inPattern   text string to change
     *  @param  outPattern  substitution string
     *  @param  cdata       output string as CDATA
     *
     */

    public static void elementSubst(Node node, String element,
                                    String inPattern, String outPattern,
                                    Boolean cdata) {

        List<Node> elements = node.selectNodes(element);
        if (elements.size() == 0) Logger.warn("No nodes found for \'{}\'.");

        for (Node el : elements) {
            String inString = el.getText();
            String outString = inString.replace(inPattern, outPattern);
//            Logger.error("{}, {}", inString, outString);
            if (!inString.equals(outString)) {
                if (cdata) {
                    el.setText(CDATA_OPEN + outString +
                            CDATA_CLOSE);
                } else {
                    el.setText(outString);
                }
                Logger.info("In {}, \'{}\' replaced with \'{}\'",
                        element, inString,
                        node.selectSingleNode(element).getText());
            } else {
                Logger.info("In {}, no replacement made for {}.",
                        element, inString);
            }
        }
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
        int i = 0;

        for (Node node : nodes) {
            i++;
            itemHeader(node, i);
        }
    }

    /**
     * Prints header line for an item
     *
     *  @param  node  xml item node
     *
     */
    public static void itemHeader(Node node, int nodeNum) {
        String propId;
        Element e1 = (Element) node;
        String title = e1.selectSingleNode(TITLE).getText();
        propId = getPostmetaValue(e1, PROPID_KEY);
        System.out.printf("#%03d: Prop ID: %s; %s\n", nodeNum,
                propId, title);
    }

    /**
     * Prints out array of media items (photos) found
     *
     *  @param  mediaItems  array of MediaItem to display
     *
     */

    public static void printMediaItems(MediaItem[] mediaItems) {

        for (int i = 0; i < mediaItems.length; i++) {
            System.out.printf("MediaItem %s: post_id: %s; post_parent: %s; \n" +
                            "    file name: %s, \n    link: %s.\n", i,
                    mediaItems[i].getPost_id(),
                    mediaItems[i].getPost_parent(),
                    mediaItems[i].getPost_name(),
                    mediaItems[i].getLink());
        }
    }
    
    
    /**
     * Prints out list of media items (photos) found
     *
     *  @param  nodes  List of xml Nodes to display
     *
     */

    public static void printMediaItems(List<Node> nodes) {
        int i = 0;

        for (Node node : nodes) {
            i++;
            mediaItemHeader(node, i);
        }
    }

    /**
     * Prints header line for an item
     *
     *  @param  node  xml item node
     *
     */
    public static void mediaItemHeader(Node node, int nodeNum) {
        String mediaId = "";
        Element e1 = (Element) node;
        String title = e1.selectSingleNode(TITLE).getText();
        mediaId = e1.selectSingleNode(POST_ID).getText();
        System.out.printf("#%03d: Picture ID: %s; %s\n", nodeNum,
                mediaId, title);
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
