package com.ereinecke.cvtpropxml;

import org.pmw.tinylog.Logger;

import static com.ereinecke.cvtpropxml.Main.sqlString;

/**
 * Definition and constructor of wp_Posts class for cvtpropsql
 * <p>
 *
 * @author  Erik Reinecke  <erik@ereinecke.com>
 * @version 1.0
 * @since   3/4/2018
 *
 * */

public class Wp_Post {

    int    post_id;
    int    post_author;
    String post_date;
    String post_date_gmt;
    String post_content;
    String post_title;
    String text;
    String post_excerpt;
    String post_status;
    String comment_status;
    String ping_status;
    String post_password;
    String post_name;
    String to_ping;
    String pinged;
    String post_modified;
    String post_modified_gmt;
    String post_content_filtered;
    int    post_parent;
    String guid;
    int    menu_order;
    String post_type;
    String post_mime_type;
    int    comment_count;
    
    public Wp_Post(
            int    post_id,
            int    post_author,
            String post_date,
            String post_date_gmt,
            String post_content,
            String post_title,
            String text,
            String post_excerpt,
            String post_status,
            String comment_status,
            String ping_status,
            String post_password,
            String post_name,
            String to_ping,
            String pinged,
            String post_modified,
            String post_modified_gmt,
            String post_content_filtered,
            int    post_parent,
            String guid,
            int    menu_order,
            String post_type,
            String post_mime_type,
            int    comment_count) {

        this.post_id = post_id;
        this.post_author = post_author;
        this.post_date = post_date;
        this.post_date_gmt = post_date_gmt;
        this.post_content = post_content;
        this.post_title = post_title;
        this.text = text;
        this.post_excerpt = post_excerpt;
        this.post_status = post_status;
        this.comment_status = comment_status;
        this.ping_status = ping_status;
        this.post_password = post_password;
        this.post_name = post_name;
        this.to_ping = to_ping;
        this.pinged = pinged;
        this.post_modified = post_modified;
        this.post_modified_gmt = post_modified_gmt;
        this.post_content_filtered = post_content_filtered;
        this.post_parent = post_parent;
        this.guid = guid;
        this.menu_order = menu_order;
        this.post_type = post_type;
        this.post_mime_type = post_mime_type;
        this.comment_count = comment_count;
    }

    public String get_Wp_Postmeta_SQL(){
        String outStr = "(" + post_id + ", " + post_author + ", " +
                sqlString(post_date) + ", " + sqlString(post_date_gmt)  + ", " +
                sqlString(post_content) + ", " + sqlString(post_title) + ", " +
                sqlString(text) + ", " + sqlString(post_excerpt) + ", " +
                sqlString(post_status) + ", " + sqlString(comment_status)  + ", " +
                sqlString(ping_status) + ", " + sqlString(post_password)  + ", " +
                sqlString(post_name) + ", " + sqlString(to_ping) + ", " +
                sqlString(pinged) + ", " + sqlString(post_modified) + ", " +
                sqlString(post_modified_gmt) + ", " +
                sqlString(post_content_filtered) + ", " + post_parent + ", " +
                sqlString(guid) + ", " + menu_order + ", " +
                sqlString(post_type) + ", " + sqlString(post_mime_type) + ", " +
                comment_count + ", " + "),";
        Logger.info("PostSQL : {}", outStr);
        return outStr;
    }
    
  /*  SQL table definition for reference
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `post_author` bigint(20) unsigned NOT NULL DEFAULT '0',
  `post_date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `post_date_gmt` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `post_content` longtext COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `post_title` text COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `post_excerpt` text COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `post_status` varchar(20) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT 'publish',
  `comment_status` varchar(20) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT 'open',
  `ping_status` varchar(20) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT 'open',
  `post_password` varchar(255) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT '',
  `post_name` varchar(200) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT '',
  `to_ping` text COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `pinged` text COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `post_modified` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `post_modified_gmt` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `post_content_filtered` longtext COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `post_parent` bigint(20) unsigned NOT NULL DEFAULT '0',
  `guid` varchar(255) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT '',
  `menu_order` int(11) NOT NULL DEFAULT '0',
  `post_type` varchar(20) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT 'post',
  `post_mime_type` varchar(100) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT '',
  `comment_count` bigint(20) NOT NULL DEFAULT '0',
  */
  
}
