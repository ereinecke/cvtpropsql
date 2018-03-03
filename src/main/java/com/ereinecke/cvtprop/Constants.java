package com.ereinecke.cvtprop;

/**
 * Utility class containing constant definitions for cvtprop
 * <p>
 *
 * @author  Erik Reinecke  <erik@ereinecke.com>
 * @version 0.1
 * @since   1/21/2018
 *
 * */

public class Constants {

    // Export and import base site urls (not sure if needed)
    public static String EXPORT_DOMAIN   = "realestateinsanmiguel.com";
    public static String IMPORT_DOMAIN   = "vivirensanmiguel.com";

    // Substitution patterns
    public static String LINK_IN         = "/listings/";
    public static String LINK_OUT        = "/property/";
    public static String POST_TYPE_IN    = "listings";
    public static String POST_TYPE_OUT   = "property";
    public static String FEATURES_IN     = "additional_features";
    public static String FEATURES_OUT    = "property-features";
    public static String STATUS_OUT      = "property-status";
    public static String TYPE_OUT        = "property-type";

    // xPaths
    public static String CHAN_ITEM       = "/rss/channel/item";
    public static String CHAN_LINK       = "/rss/channel/link";
    public static String CHAN_SITE       = "/rss/channel/wp:base_site_url";
    public static String CHAN_BLOG       = "/rss/channel/wp:base_blog_url";
    public static String POSTMETA        = "wp:postmeta";
    public static String POST_TYPE       = "wp:post_type";
    public static String POST_ID         = "wp:post_id";
    public static String POST_PARENT     = "wp:post_parent";
    public static String POST_NAME       = "wp:post_name";
    public static String METAKEY         = "wp:meta_key";
    public static String METAVALUE       = "wp:meta_value";
    public static String STATUS          = "wp:status";
    public static String ATTACH_URL      = "wp:attachment_url";
    public static String ATTACHMENT      = "attachment";
    public static String PROPID_KEY      = "_ct_mls";
    public static String CONST_SIZE_KEY  = "_ct_sqft";
    public static String LOT_SIZE_KEY    = "_ct_lotsize";
    public static String PRICE_KEY       = "_ct_price";
    public static String BROKER_KEY      = "_ct_broker";
    public static String VIDEO_KEY       = "_ct_video";
    public static String THUMBNAIL_KEY   = "_thumbnail_id";
    public static String EDITLAST_KEY    = "_edit_last";
    public static String LATLNG_KEY      = "_ct_latlng";
    public static String ZIPCODE_KEY     = "zipcode";
    public static String TITLE           = "title";
    public static String LINK            = "link";
    public static String GUID            = "guid";
    public static String CATEGORY        = "category";
    public static String NICENAME        = "nicename";
    public static String DOMAIN          = "domain";
    public static String BEDS            = "beds";
    public static String BATHS           = "baths";
    public static String CITY            = "city";
    public static String STATE           = "state";
    public static String PROP_TYPE       = "property_type";
    public static String PROP_STATUS     = "ct_status";

    // CDATA manipulation
    public static String CDATA_FMT       = "<![CDATA[%s]]>";
    public static String CDATA_OPEN      = "<![CDATA[";
    public static String CDATA_CLOSE     = "]]>";

    // Post status
    public static String DRAFT           = "draft";
    public static String PUBLISH         = "publish";

    // Data files
    public static String SHORT_LISTINGS = "data/resm-listings.small.2018-02-05.xml";
    public static String FULL_LISTINGS  = "data/resm-properties.2018-02-27.xml";
    public static String FULL_MEDIA  = "data/resm-media.2018-02-27.xml";

    // wp:postmetas
    public static String _VC_POST_SETTINGS_KEY = "_vc_post_settings";
    public static String _VC_POST_SETTINGS_VALUE = "a:1:{s:10:\"vc_grid_id\";a:0:{}}";
    public static String AP_LOCATIONS_KEY = "acf-property-location";
    public static String AP_LOCATIONS_VALUE = "";
    public static String _AP_LOCATIONS_KEY = "_acf-property-location";
    public static String _AP_LOCATIONS_VALUE = "field_5562dee6753f7";
    public static String AP_TYPE_KEY = "acf-property-type";
    public static String AP_TYPE_VALUE = "";
    public static String _AP_TYPE_KEY = "_acf-property-type";
    public static String _AP_TYPE_VALUE = "field_55681a8db9d18";
    public static String AP_STATUS_KEY = "acf-property-status";
    public static String AP_STATUS_VALUE = "";
    public static String _AP_STATUS_KEY = "_acf-property-status";
    public static String _AP_STATUS_VALUE = "field_55681ad3b9d19";
    public static String AP_FEATURES_KEY = "acf-property-features";
    public static String AP_FEATURES_VALUE = "";
    public static String _AP_FEATURES_KEY = "_acf-property-features";
    public static String _AP_FEATURES_VALUE = "field_55681b1ab9d1a";
    public static String THUMBNAIL_ID_KEY = "_thumbnail_id";
    public static String THUMBNAIL_ID_VALUE = "";
    public static String _THUMBNAIL_ID_KEY = "__thumbnail_id";
    public static String _THUMBNAIL_ID_VALUE = "field_556c69038f2fa";
    public static String EP_GALLERY_KEY = "estate_property_gallery";
    public static String EP_GALLERY_VALUE = "";
    public static String _EP_GALLERY_KEY = "_estate_property_gallery";
    public static String _EP_GALLERY_VALUE = "field_553624c8bfe23";
    public static String EP_GOOGLE_MAPS_KEY = "estate_property_google_maps";
    public static String EP_GOOGLE_MAPS_VALUE = "";
    public static String _EP_GOOGLE_MAPS_KEY = "_estate_property_google_maps";
    public static String _EP_GOOGLE_MAPS_VALUE = "field_55362588bfe26";
    public static String EP_POSTAL_CODE_KEY = "estate_property_postal_code";
    public static String EP_POSTAL_CODE_VALUE = "";
    public static String _EP_POSTAL_CODE_KEY = "_estate_property_postal_code";
    public static String _EP_POSTAL_CODE_VALUE = "field_5a78c2a609e20";
    public static String EP_ID_KEY = "estate_property_id";
    public static String EP_ID_VALUE = "";
    public static String _EP_ID_KEY = "_estate_property_id";
    public static String _EP_ID_VALUE = "field_55362547bfe24";
    public static String EP_FEATURED_KEY = "estate_property_featured";
    public static String EP_FEATURED_VALUE = "";
    public static String _EP_FEATURED_KEY = "_estate_property_featured";
    public static String _EP_FEATURED_VALUE = "field_553623e61c243";
    public static String EP_LAYOUT_KEY = "estate_property_layout";
    public static String EP_LAYOUT_VALUE = "theme_option_setting";
    public static String _EP_LAYOUT_KEY = "_estate_property_layout";
    public static String _EP_LAYOUT_VALUE = "field_5536231605e57";
    public static String EP_VIDEO_PROVIDER_KEY = "estate_property_video_provider";
    public static String EP_VIDEO_PROVIDER_VALUE = "vimeo";
    public static String _EP_VIDEO_PROVIDER_KEY = "_estate_property_video_provider";
    public static String _EP_VIDEO_PROVIDER_VALUE = "field_5536246ebfe21";
    public static String EP_VIDEO_ID_KEY = "estate_property_video_id";
    public static String EP_VIDEO_ID_VALUE = "";
    public static String _EP_VIDEO_ID_KEY = "_estate_property_video_id";
    public static String _EP_VIDEO_ID_VALUE = "field_553624b2bfe22";
    public static String EP_STATUS_UPDATE_KEY = "estate_property_status_update";
    public static String EP_STATUS_UPDATE_VALUE = "";
    public static String _EP_STATUS_UPDATE_KEY = "_estate_property_status_update";
    public static String _EP_STATUS_UPDATE_VALUE = "field_553623b71c242";
    public static String EP_AVAILABLE_FROM_KEY = "estate_property_available_from";
    public static String EP_AVAILABLE_FROM_VALUE = "";
    public static String _EP_AVAILABLE_FROM_KEY = "_estate_property_available_from";
    public static String _EP_AVAILABLE_FROM_VALUE = "field_553669acf0fb1";
    public static String EP_PRICE_PREFIX_KEY = "estate_property_price_prefix";
    public static String EP_PRICE_PREFIX_VALUE = "USD";
    public static String _EP_PRICE_PREFIX_KEY = "_estate_property_price_prefix";
    public static String _EP_PRICE_PREFIX_VALUE = "field_55366a5655cb0";
    public static String EP_PRICE_KEY = "estate_property_price";
    public static String EP_PRICE_VALUE = "";
    public static String _EP_PRICE_KEY = "_estate_property_price";
    public static String _EP_PRICE_VALUE = "field_55366a9855cb1";
    public static String EP_PRICE_SUFFIX_KEY = "estate_property_price_suffix";
    public static String EP_PRICE_SUFFIX_VALUE = "";
    public static String _EP_PRICE_SUFFIX_KEY = "_estate_property_price_suffix";
    public static String _EP_PRICE_SUFFIX_VALUE = "field_55366afc55cb2";
    public static String EP_LOT_SIZE_KEY = "estate_property_lot_size";
    public static String EP_SIZE_VALUE = "";
    public static String _EP_LOT_SIZE_KEY = "_estate_property_lot_size";
    public static String _EP_LOT_SIZE_VALUE = "field_55366b2555cb3";
    public static String EP_CONST_SIZE_KEY = "estate_property_const_size";
    public static String EP_CONST_SIZE_VALUE = "";
    public static String _EP_CONST_SIZE_KEY = "_estate_property_const_size";
    public static String _EP_CONST_SIZE_VALUE = "field_55366b3a55cb4";
    public static String EP_ROOMS_KEY = "estate_property_rooms";
    public static String EP_ROOMS_VALUE = "";
    public static String _EP_ROOMS_KEY = "_estate_property_rooms";
    public static String _EP_ROOMS_VALUE = "field_55366b6755cb5";
    public static String EP_BEDROOMS_KEY = "estate_property_bedrooms";
    public static String EP_BEDROOMS_VALUE = "";
    public static String _EP_BEDROOMS_KEY = "_estate_property_bedrooms";
    public static String _EP_BEDROOMS_VALUE = "field_55366b8555cb6";
    public static String EP_BATHROOMS_KEY = "estate_property_bathrooms";
    public static String EP_BATHROOMS_VALUE = "";
    public static String _EP_BATHROOMS_KEY = "_estate_property_bathrooms";
    public static String _EP_BATHROOMS_VALUE = "field_55366b9255cb7";
    public static String EP_GARAGES_KEY = "estate_property_garages";
    public static String EP_GARAGES_VALUE = "";
    public static String _EP_GARAGES_KEY = "_estate_property_garages";
    public static String _EP_GARAGES_VALUE = "field_55366ba055cb8";
    public static String EP_CONTACT_INFORMATION_KEY = "estate_property_contact_information";
    public static String EP_CONTACT_INFORMATION_VALUE = "all";
    public static String _EP_CONTACT_INFORMATION_KEY = "_estate_property_contact_information";
    public static String _EP_CONTACT_INFORMATION_VALUE = "field_55366bb455cb9";
    public static String EP_CUSTOM_AGENT_KEY = "estate_property_custom_agent";
    public static String EP_CUSTOM_AGENT_VALUE = "";
    public static String _EP_CUSTOM_AGENT_KEY = "_estate_property_custom_agent";
    public static String _EP_CUSTOM_AGENT_VALUE = "field_55366be755cba";
    public static String EP_INTERNAL_NOTE_KEY = "estate_property_internal_note";
    public static String EP_INTERNAL_NOTE_VALUE = "";
    public static String _EP_INTERNAL_NOTE_KEY = "_estate_property_internal_note";
    public static String _EP_INTERNAL_NOTE_VALUE = "field_55366c3455cbb";
    public static String EP_ATTACHMENTS_REPEATER_KEY = "estate_property_attachments_repeater";
    public static String EP_ATTACHMENTS_REPEATER_VALUE = "1";
    public static String _EP_ATTACHMENTS_REPEATER_KEY = "_estate_property_attachments_repeater";
    public static String _EP_ATTACHMENTS_REPEATER_VALUE = "field_55366c6a55cbc";
    public static String EP_FLOOR_PLANS_KEY = "estate_property_floor_plans";
    public static String EP_FLOOR_PLANS_VALUE = "1";
    public static String _EP_FLOOR_PLANS_KEY = "_estate_property_floor_plans";
    public static String _EP_FLOOR_PLANS_VALUE = "field_5537396ce3c14";
    public static String ADDITIONAL_PUBLIC_TRANSPORTATION_KEY = "additional_public_transportation";
    public static String ADDITIONAL_PUBLIC_TRANSPORTATION_VALUE = "";
    public static String _ADDITIONAL_PUBLIC_TRANSPORTATION_KEY = "_additional_public_transportation";
    public static String _ADDITIONAL_PUBLIC_TRANSPORTATION_VALUE = "field_557e6ad8f5975";
    public static String _ADDITIONAL_NEIGHBORHOOD_KEY = "_additional_neighborhood";
    public static String _ADDITIONAL_NEIGHBORHOOD_VALUE = "";
    public static String __ADDITIONAL_NEIGHBORHOOD_KEY = "__additional_neighborhood";
    public static String __ADDITIONAL_NEIGHBORHOOD_VALUE = "field_557e6b71f5977";
    public static String _ADDITIONAL_AIR_QUALITY_KEY = "_additional_air_quality";
    public static String _ADDITIONAL_AIR_QUALITY_VALUE = "";
    public static String __ADDITIONAL_AIR_QUALITY_KEY = "__additional_air_quality";
    public static String __ADDITIONAL_AIR_QUALITY_VALUE = "field_557e6b5df5976";
    public static String _ESTATE_PAGE_HIDE_SIDEBAR_KEY = "_estate_page_hide_sidebar";
    public static String _ESTATE_PAGE_HIDE_SIDEBAR_VALUE = "0";
    public static String _ESTATE_PAGE_HIDE_FOOTER_WIDGETS_KEY = "_estate_page_hide_footer_widgets";
    public static String _ESTATE_PAGE_HIDE_FOOTER_WIDGETS_VALUE = "0";
    public static String _ESTATE_INTRO_FULLSCREEN_BACKGROUND_VIDEO_PROVIDER_KEY = "_estate_intro_fullscreen_background_video_provider";
    public static String _ESTATE_INTRO_FULLSCREEN_BACKGROUND_VIDEO_PROVIDER_VALUE = "none";
    public static String _ESTATE_INTRO_FULLSCREEN_BACKGROUND_VIDEO_AUDIO_KEY = "_estate_intro_fullscreen_background_video_audio";
    public static String _ESTATE_INTRO_FULLSCREEN_BACKGROUND_VIDEO_AUDIO_VALUE = "0";
    public static String _EP_VIEWS_COUNT_KEY = "_estate_property_views_count";
    public static String _EP_VIEWS_COUNT_VALUE = "0";
    public static String EP_ATTACHMENTS_REPEATER_0_EP_ATTACHMENT_KEY = "estate_property_attachments_repeater_0_estate_property_attachment";
    public static String EP_ATTACHMENTS_REPEATER_0_EP_ATTACHMENT_VALUE = "";
    public static String _EP_ATTACHMENTS_REPEATER_0_EP_ATTACHMENT_KEY = "_estate_property_attachments_repeater_0_estate_property_attachment";
    public static String _EP_ATTACHMENTS_REPEATER_0_EP_ATTACHMENT_VALUE = "field_55366e2dd72fc";
    public static String EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_TITLE_KEY = "estate_property_floor_plans_0_acf_estate_floor_plan_title";
    public static String EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_TITLE_VALUE = "";
    public static String _EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_TITLE_KEY = "_estate_property_floor_plans_0_acf_estate_floor_plan_title";
    public static String _EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_TITLE_VALUE = "field_553739f9e3c15";
    public static String EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_BATHROOMS_KEY = "estate_property_floor_plans_0_acf_estate_floor_plan_bathrooms";
    public static String EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_BATHROOMS_VALUE = "";
    public static String _EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_BATHROOMS_KEY = "_estate_property_floor_plans_0_acf_estate_floor_plan_bathrooms";
    public static String _EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_BATHROOMS_VALUE = "field_55373a8ce3c1a";
    public static String EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_DESCRIPTION_KEY = "estate_property_floor_plans_0_acf_estate_floor_plan_description";
    public static String EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_DESCRIPTION_VALUE = "";
    public static String _EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_DESCRIPTION_KEY = "_estate_property_floor_plans_0_acf_estate_floor_plan_description";
    public static String _EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_DESCRIPTION_VALUE = "field_556c450013df8";
    public static String EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_IMAGE_KEY = "estate_property_floor_plans_0_acf_estate_floor_plan_image";
    public static String EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_IMAGE_VALUE = "";
    public static String _EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_IMAGE_KEY = "_estate_property_floor_plans_0_acf_estate_floor_plan_image";
    public static String _EP_FLOOR_PLANS_0_ACF_ESTATE_FLOOR_PLAN_IMAGE_VALUE = "field_55373ae8e3c1c";
    public static String ESTATE_PAGE_HIDE_SITE_HEADER_KEY = "estate_page_hide_site_header";
    public static String ESTATE_PAGE_HIDE_SITE_HEADER_VALUE = "0";
    public static String _EDIT_LAST_KEY = "_edit_last";
    public static String _EDIT_LAST_VALUE = "8";

    // Logging formats
    public static String DEF_LOG_FMT = "{date:yyyy-MM-dd HH:mm:ss} [{thread}] {class}.{method}()\n{level}: {message}";
    public static String MIN_LOG_FMT = "{message}";

    
    
}
