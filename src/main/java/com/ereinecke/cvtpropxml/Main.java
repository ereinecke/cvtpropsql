package com.ereinecke.cvtpropxml;

import com.sun.istack.internal.NotNull;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.writers.ConsoleWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.ereinecke.cvtpropxml.Constants.*;
import static com.ereinecke.cvtpropxml.InitPropertyFeatures.initPropertyFeatures;
import static com.ereinecke.cvtpropxml.InitPropertyStatus.initPropertyStatus;
import static com.ereinecke.cvtpropxml.InitPropertyTypes.initPropertyTypes;
import static com.ereinecke.cvtpropxml.InitUser.initUser;
import static java.lang.System.exit;

/**
 * Reads and parses property files (export xml format), generates sql
 * <p>
 * Input are export xml file from the WordPress theme WP Pro Real Estate,
 *    preprocessed with cvtprop
 * Writes out sql files for post and post_meta tables
 *
 * @author  Erik Reinecke  <erik@ereinecke.com>
 * @version 0.5
 * @since   3/4/2018
 *
 * @param  listingsInputXML  file name of preprocessed listings export file
 * @param  dumpOnly          if '-d', just print out listing headers
 * @param  numRecords        if '-nX', stop after X records
 *
 *
 *    Test files:  full sized: data/resm-listings.properties.2018-02-27-cvt.xml
 *
 * */

public class Main {

    static int maxRecords = Integer.MAX_VALUE;
    static boolean dumpOnly = false;
    static String listingsFile = "";
    static Document propertyDoc;
    static FileWriter postmetaSQL;
    static ArrayList<Wp_Post> wp_Posts;
    static ArrayList<Wp_Postmeta> wp_Postmetas;

    static Configurator logConfig;

    public static void main(String[] args) {

        // Set log level INFO < WARNING < ERROR < OFF
        logConfig = Configurator.defaultConfig();
        logConfig.writer(new ConsoleWriter(), Level.WARNING)
                .addWriter(new org.pmw.tinylog.writers.FileWriter("data/debug.txt",
                                true, false),
                        Level.DEBUG, MIN_LOG_FMT)
                .activate();

        // input file from command line unless hardcoded above
        if (args.length > 0) {
            listingsFile = args[0];
            // Check for flags - only -d and -nX supported
            if (args.length > 1) {
                for (int i = 1; i < args.length && i < 3; i++) {
                    String flag = args[i].substring(0,2);
                    // dumpOnly means dump certain data structures only
                    if (flag.equals(DUMP))
                        dumpOnly = true;
                    if (flag.equals(MAX_RECORDS)) {
                        try {
                            String maxRecordsStr = args[i].substring(2);
                            maxRecords = Integer.parseInt(maxRecordsStr);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Logger.error("Error parsing flag {}", args[i]);
                        }

                        Logger.info("Will only process {} records", maxRecords);
                    }
                }
            }
        } else {
            listingsFile = DEFAULT_IMPORT;
            Logger.error("No input files specified.\n  Importing {}", DEFAULT_IMPORT);
            exit(1);
        }

        //  read listing xml
        try {
            propertyDoc = parse(listingsFile);
        } catch (DocumentException e) {
            Logger.error(e, "File {} not found.\n", listingsFile);
            System.exit(1);
        }

        postmetaSQL = openOutputFile(listingsFile);
        sqlWrite(postmetaSQL, POSTMETA_HEADER);
        processProperties(propertyDoc);

        // Close SQL output file
        sqlWrite(postmetaSQL,";");
        try {
            postmetaSQL.flush();
            postmetaSQL.close();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.error("Error closing output file");
        }

    }

    /**
     * Process property items document
     *
     *  @param  doc   Document containing property listings
     *
     */
    public static void processProperties(Document doc) {

        
        // select all items (properties)
        List<Node> propertyNodes = propertyDoc.selectNodes(CHAN_ITEM);
        if (propertyNodes == null) {
            Logger.error("No property nodes found in {}.", listingsFile);
            exit(1);
        }
        else {
            wp_Posts = new ArrayList<>(propertyNodes.size());
            Logger.info("Number of nodes found for {}: {}", CHAN_ITEM, propertyNodes.size());
            // log list of items
            if (dumpOnly) {
                printItems(propertyNodes);
                System.exit(0);
            }
        }

        // Convert wp:postmeta xml to wp_postmeta sql
        int i = 0;  // property counter
        int j = 0;  // postmeta counter
        for (Node node : propertyNodes) {
            i++;
            j++;

            if (i > maxRecords) {
                Logger.warn("Processed {} properties and {} postmetas", i-1, j-1);
                return;
            }

            System.out.println(">===========================================================================<");
            itemHeader(node, i);
            Element item = (Element)node;

            writePostMetaSql(node, AP_LOCATIONS_KEY, AP_LOCATIONS_VALUE);
            writePostMetaSql(node, _AP_LOCATIONS_KEY, _AP_LOCATIONS_VALUE);
            
            writePostMetaSql(node, AP_TYPE_KEY, AP_TYPE_VALUE);
            writePostMetaSql(node, _AP_TYPE_KEY, _AP_TYPE_VALUE);
            
            writePostMetaSql(node, AP_STATUS_KEY, AP_STATUS_VALUE);
            writePostMetaSql(node, _AP_STATUS_KEY, _AP_STATUS_VALUE);
            
            writePostMetaSql(node, AP_FEATURES_KEY, AP_FEATURES_VALUE);
            writePostMetaSql(node, _AP_FEATURES_KEY,_AP_FEATURES_VALUE);
            
            writePostMetaSql(node, THUMBNAIL_ID_KEY, THUMBNAIL_ID_VALUE);
            writePostMetaSql(node, _THUMBNAIL_ID_KEY, _THUMBNAIL_ID_VALUE);
            
            writePostMetaSql(node, EP_GALLERY_KEY, EP_GALLERY_VALUE);
            writePostMetaSql(node, _EP_GALLERY_KEY, _EP_GALLERY_VALUE);
            
            writePostMetaSql(node, EP_GOOGLE_MAPS_KEY, EP_GOOGLE_MAPS_VALUE);
            writePostMetaSql(node, _EP_GOOGLE_MAPS_KEY, _EP_GOOGLE_MAPS_VALUE);
            
            writePostMetaSql(node, EP_POSTAL_CODE_KEY, EP_POSTAL_CODE_VALUE);
            writePostMetaSql(node, _EP_POSTAL_CODE_KEY, _EP_POSTAL_CODE_VALUE);
            
            writePostMetaSql(node, EP_ID_KEY, EP_ID_VALUE);
            writePostMetaSql(node, _EP_ID_KEY, _EP_ID_VALUE);
            
            writePostMetaSql(node, EP_FEATURED_KEY, EP_FEATURED_VALUE);
            writePostMetaSql(node, _EP_FEATURED_KEY, _EP_FEATURED_VALUE);
            
            writePostMetaSql(node, EP_LAYOUT_KEY, EP_LAYOUT_VALUE);
            writePostMetaSql(node, _EP_LAYOUT_KEY, _EP_LAYOUT_VALUE);
            
            writePostMetaSql(node, EP_FEATURED_KEY, EP_FEATURED_VALUE);
            writePostMetaSql(node, _EP_FEATURED_KEY, _EP_FEATURED_VALUE);

            writePostMetaSql(node, EP_VIDEO_PROVIDER_KEY, EP_VIDEO_PROVIDER_VALUE);
            writePostMetaSql(node, _EP_VIDEO_PROVIDER_KEY, _EP_VIDEO_PROVIDER_VALUE);
            
            writePostMetaSql(node, EP_VIDEO_ID_KEY, EP_VIDEO_ID_VALUE);
            writePostMetaSql(node, _EP_VIDEO_ID_KEY, _EP_VIDEO_ID_VALUE);
            
            writePostMetaSql(node, EP_STATUS_UPDATE_KEY, EP_STATUS_UPDATE_VALUE);
            writePostMetaSql(node, _EP_STATUS_UPDATE_KEY, _EP_STATUS_UPDATE_VALUE);
            
            writePostMetaSql(node, EP_AVAILABLE_FROM_KEY, EP_AVAILABLE_FROM_VALUE);
            writePostMetaSql(node, _EP_AVAILABLE_FROM_KEY, _EP_AVAILABLE_FROM_VALUE);

            writePostMetaSql(node, EP_PRICE_PREFIX_KEY, EP_PRICE_PREFIX_VALUE);
            writePostMetaSql(node, _EP_PRICE_PREFIX_KEY, _EP_PRICE_PREFIX_VALUE);
            
            writePostMetaSql(node, EP_PRICE_KEY, EP_PRICE_VALUE);
            writePostMetaSql(node, _EP_PRICE_KEY, _EP_PRICE_VALUE);

            writePostMetaSql(node, EP_PRICE_SUFFIX_KEY, EP_PRICE_SUFFIX_VALUE);
            writePostMetaSql(node, _EP_PRICE_SUFFIX_KEY, _EP_PRICE_SUFFIX_VALUE);

            writePostMetaSql(node, EP_LOT_SIZE_KEY, EP_LOT_SIZE_VALUE);
            writePostMetaSql(node, _EP_LOT_SIZE_KEY, _EP_LOT_SIZE_VALUE);
            
            writePostMetaSql(node, EP_CONST_SIZE_KEY, EP_CONST_SIZE_VALUE);
            writePostMetaSql(node, _EP_CONST_SIZE_KEY, _EP_CONST_SIZE_VALUE);
            
            writePostMetaSql(node, EP_ROOMS_KEY, EP_ROOMS_VALUE);
            writePostMetaSql(node, _EP_ROOMS_KEY, _EP_ROOMS_VALUE);
            
            writePostMetaSql(node, EP_BEDROOMS_KEY, EP_BEDROOMS_VALUE);
            writePostMetaSql(node, _EP_BEDROOMS_KEY, _EP_BEDROOMS_VALUE);
            
            writePostMetaSql(node, EP_BATHROOMS_KEY, EP_BATHROOMS_VALUE);
            writePostMetaSql(node, _EP_BATHROOMS_KEY, _EP_BATHROOMS_VALUE);
            
            writePostMetaSql(node, EP_GARAGES_KEY, EP_GARAGES_VALUE);
            writePostMetaSql(node, _EP_GARAGES_KEY, _EP_GARAGES_VALUE);
            
            writePostMetaSql(node, EP_CUSTOM_AGENT_KEY, EP_CUSTOM_AGENT_VALUE);
            writePostMetaSql(node, _EP_CUSTOM_AGENT_KEY, _EP_CUSTOM_AGENT_VALUE);
            
            writePostMetaSql(node, EP_INTERNAL_NOTE_KEY, EP_INTERNAL_NOTE_VALUE);
            writePostMetaSql(node, _EP_INTERNAL_NOTE_KEY, _EP_INTERNAL_NOTE_VALUE);
            
            writePostMetaSql(node, EP_BATHROOMS_KEY, EP_BATHROOMS_VALUE);
            writePostMetaSql(node, _EP_BATHROOMS_KEY, _EP_BATHROOMS_VALUE);
            
            writePostMetaSql(node, EP_BATHROOMS_KEY, EP_BATHROOMS_VALUE);
            writePostMetaSql(node, _EP_BATHROOMS_KEY, _EP_BATHROOMS_VALUE);
            
            /*  Not yet included
             EP_CONTACT_INFORMATION_KEY = "estate_property_contact_information";
             EP_CONTACT_INFORMATION_VALUE = "all";
             _EP_CONTACT_INFORMATION_KEY = "_estate_property_contact_information";
             _EP_CONTACT_INFORMATION_VALUE = "field_55366bb455cb9";
                 EP_ATTACHMENTS_REPEATER_KEY = "estate_property_attachments_repeater";
             EP_ATTACHMENTS_REPEATER_VALUE = "1";
             _EP_ATTACHMENTS_REPEATER_KEY = "_estate_property_attachments_repeater";
             _EP_ATTACHMENTS_REPEATER_VALUE = "field_55366c6a55cbc";
             EP_FLOOR_PLANS_KEY = "estate_property_floor_plans";
             EP_FLOOR_PLANS_VALUE = "1";
             _EP_FLOOR_PLANS_KEY = "_estate_property_floor_plans";
             _EP_FLOOR_PLANS_VALUE = "field_5537396ce3c14";
             ADDITIONAL_PUBLIC_TRANSPORTATION_KEY = "additional_public_transportation";
             ADDITIONAL_PUBLIC_TRANSPORTATION_VALUE = "";
             _ADDITIONAL_PUBLIC_TRANSPORTATION_KEY = "_additional_public_transportation";
             _ADDITIONAL_PUBLIC_TRANSPORTATION_VALUE = "field_557e6ad8f5975";
             _ADDITIONAL_NEIGHBORHOOD_KEY = "_additional_neighborhood";
             _ADDITIONAL_NEIGHBORHOOD_VALUE = "";
             __ADDITIONAL_NEIGHBORHOOD_KEY = "__additional_neighborhood";
             __ADDITIONAL_NEIGHBORHOOD_VALUE = "field_557e6b71f5977";
             _ADDITIONAL_AIR_QUALITY_KEY = "_additional_air_quality";
             _ADDITIONAL_AIR_QUALITY_VALUE = "";
             __ADDITIONAL_AIR_QUALITY_KEY = "__additional_air_quality";
             __ADDITIONAL_AIR_QUALITY_VALUE = "field_557e6b5df5976";
             _ESTATE_PAGE_HIDE_SIDEBAR_KEY = "_estate_page_hide_sidebar";
             _ESTATE_PAGE_HIDE_SIDEBAR_VALUE = "0";
             _ESTATE_PAGE_HIDE_FOOTER_WIDGETS_KEY = "_estate_page_hide_footer_widgets";
             _ESTATE_PAGE_HIDE_FOOTER_WIDGETS_VALUE = "0";
             _ESTATE_INTRO_FULLSCREEN_BACKGROUND_VIDEO_PROVIDER_KEY = "_estate_intro_fullscreen_background_video_provider";
             _ESTATE_INTRO_FULLSCREEN_BACKGROUND_VIDEO_PROVIDER_VALUE = "none";
             _ESTATE_INTRO_FULLSCREEN_BACKGROUND_VIDEO_AUDIO_KEY = "_estate_intro_fullscreen_background_video_audio";
             _ESTATE_INTRO_FULLSCREEN_BACKGROUND_VIDEO_AUDIO_VALUE = "0";
             _EP_VIEWS_COUNT_KEY = "_estate_property_views_count";
             _EP_VIEWS_COUNT_VALUE = "0";
             EP_ATTACHMENTS_REPEATER_0_EP_ATTACHMENT_KEY = "estate_property_attachments_repeater_0_estate_property_attachment";
             EP_ATTACHMENTS_REPEATER_0_EP_ATTACHMENT_VALUE = "";
             _EP_ATTACHMENTS_REPEATER_0_EP_ATTACHMENT_KEY = "_estate_property_attachments_repeater_0_estate_property_attachment";
             _EP_ATTACHMENTS_REPEATER_0_EP_ATTACHMENT_VALUE = "field_55366e2dd72fc";
             EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_TITLE_KEY = "estate_property_floor_plans_0_acf_estate_floor_plan_title";
             EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_TITLE_VALUE = "";
             _EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_TITLE_KEY = "_estate_property_floor_plans_0_acf_estate_floor_plan_title";
             _EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_TITLE_VALUE = "field_553739f9e3c15";
             EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_BATHROOMS_KEY = "estate_property_floor_plans_0_acf_estate_floor_plan_bathrooms";
             EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_BATHROOMS_VALUE = "";
             _EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_BATHROOMS_KEY = "_estate_property_floor_plans_0_acf_estate_floor_plan_bathrooms";
             _EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_BATHROOMS_VALUE = "field_55373a8ce3c1a";
             EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_DESCRIPTION_KEY = "estate_property_floor_plans_0_acf_estate_floor_plan_description";
             EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_DESCRIPTION_VALUE = "";
             _EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_DESCRIPTION_KEY = "_estate_property_floor_plans_0_acf_estate_floor_plan_description";
             _EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_DESCRIPTION_VALUE = "field_556c450013df8";
             EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_IMAGE_KEY = "estate_property_floor_plans_0_acf_estate_floor_plan_image";
             EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_IMAGE_VALUE = "";
             _EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_IMAGE_KEY = "_estate_property_floor_plans_0_acf_estate_floor_plan_image";
             _EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_IMAGE_VALUE = "field_55373ae8e3c1c";
             ESTATE_PAGE_HIDE_SITE_HEADER_KEY = "estate_page_hide_site_header";
             ESTATE_PAGE_HIDE_SITE_HEADER_VALUE = "0";
             _EDIT_LAST_KEY = "_edit_last";
             _EDIT_LAST_VALUE = "";

            */
        }
    }

    /**
     * Get single integer element value
     *
     *  @param  node            element to process
     *  @param  xPath           element xPath
     *
     *  @return intValue               
     *
     */
    public static int getIntVal(Node node, String xPath) {
        return (int)node.numberValueOf(xPath);
    }

    /**
     * Get single integer element value
     *
     *  @param  node            element to process
     *  @param  xPath           element xPath
     *
     *  @return intValue
     *
     */
    public static String getStringVal(Node node, String xPath) {
        Node n = node.selectSingleNode(xPath);
        if (n == null) {
            Logger.error("{} not found in propId {}", 
                    xPath, getPropID(node));
            return null;
        } else 
            return n.getText();
    }

    /**
     * Get specified wp:postmeta element with key metakey in specified item
     *
     *  @param  node            element to contain wp:postmeta
     *  @param  meta_key        wp:meta_key

     *  @return                 wp:postmeta element
     *
     */

    public static Element getPostmetaElement(Node node, String meta_key) {

        List<Node> postmetas = node.selectNodes(POSTMETA);
        for (Node postmeta : postmetas) {
            Element e2 = (Element) postmeta.selectSingleNode(METAKEY);
            if (e2.getText().equals(meta_key)) {
                Node value = postmeta.selectSingleNode(METAVALUE);
                return (Element)value;
            }
        }
        // Logger.warn("{} with {} \'{}\' not found.", POSTMETA,
        //         METAKEY, meta_key);
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

        // Logger.warn("{} with {} \'{}\' not found.", POSTMETA,
        //         METAKEY, meta_key);
        return null;
    }

   /**
     * Gets meta_value associated with meta_key and writes sql statement for it
     *
     *  @param  node            xml node containing <wp:postmeta>
     *  @param  meta_key        wp:meta_key
     *
     */

    public static void writePostMetaSql(Node node, String meta_key, 
                                        String default_meta_value) {

        Element postmeta = getPostmetaElement(node, meta_key);
        if (postmeta != null) {
            String metaValue = getPostmetaValue((Element)node, meta_key);
            if (metaValue == null || metaValue.length() == 0) {
                metaValue = default_meta_value;
            }
            Wp_Postmeta pm = new Wp_Postmeta(0,
                    getPropPostID(node), meta_key, metaValue);
            sqlWrite(postmetaSQL, pm.get_Wp_Postmeta_SQL());
            return;       
        }

        Logger.warn("{} with {} \'{}\' not found.", POSTMETA,
                METAKEY, meta_key);
        return;
    }

    /**
     * Return Post ID for the specified <item> node
     *
     *  @param  propItem
     *
     */

    public static int getPropPostID(Node propItem) {
        return Integer.valueOf(propItem.selectSingleNode(POST_ID).getText());
    }
    
    /**
     * Return Property ID for the specified <item> node
     *
     *  @param  propItem
     *
     */

    public static String getPropID(Node propItem) {
        String propId = getPostmetaValue((Element)propItem, PROPID_KEY);
        return getPostmetaValue((Element)propItem, PROPID_KEY);
    }

    /**
     * Opens output file name, using input file name and replacing .xml with .sql
     *
     *  @param  inputFileName
     */
    public static FileWriter openOutputFile(String inputFileName) {

        String outputFileName = inputFileName.replace(".xml", ".sql");
        FileWriter sqlFile;

        try {
            sqlFile = new FileWriter(outputFileName);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.error("Error opening file {}", outputFileName);
            return(null);
        }
        Logger.info("Output file name: {}", outputFileName);

        return sqlFile;
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
     * Return an SQL-safe version of input string
     *      Surrounded by single quotes and any internal single quotes are doubled
     *
     *  @param  inString  string to convert
     *
     */
    public static String sqlString(String inString) {
        String outString = "'" +
                inString.replace("'", "''") + "'";

        return outString;
    }

    /**
     * Writes a string to the specified writer
     *
     *  @param  writer    FileWriter to write with
     *  @param  inString  string to convert
     *
     */
    public static void sqlWrite(FileWriter writer, String inString) {

        try {
            Logger.debug("{}", inString);
            writer.write(inString + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            Logger.error("Exception writing {}", inString);
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
}
