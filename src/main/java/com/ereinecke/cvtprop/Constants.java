package com.ereinecke.cvtprop;

public class Constants {

    // xPaths
    public static String ITEM = "/rss/channel/item";
    public static String POSTMETA = "wp:postmeta";
    public static String METAKEY = "wp:meta_key";
    public static String METAVALUE = "wp:meta_value";
    public static String PROPID_KEY = "_ct_mls";
    public static String LOTSIZE_KEY = "_ct_lotsize";
    public static String SQFT_KEY = "_ct_sqft";
    public static String PRICE_KEY = "_ct_price";
    public static String THUMBNAIL_KEY = "_thumbnail_id";
    public static String EDITLAST_KEY = "_edit_last";
    public static String LATLNG_KEY = "_ct_latlng";
    public static String TITLE = "title";

    // CDATA manipulation
    public static String CDATA_FMT = "<![CDATA[%s]]>";
    public static String CDATA_OPEN = "<![CDATA[";
    public static String CDATA_CLOSE = "]]>";

    // Data files
    public static String SHORT_INPUT = "data/2427-prod.xml";
    public static String FULL_INPUT = "data/resm-listings-modified.wordpress.2017-12-07.xml";

}
