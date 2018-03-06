package com.ereinecke.cvtpropxml;

import org.pmw.tinylog.Logger;

import static com.ereinecke.cvtpropxml.Main.sqlString;

/**
 * Definition and constructor of Wp_Postmeta class for cvtpropsql
 * <p>
 *
 * @author  Erik Reinecke  <erik@ereinecke.com>
 * @version 1.0
 * @since   3/4/2018
 *
 * */

public class Wp_Postmeta {

    int meta_id;
    int post_id;
    String meta_key;
    String meta_value;

    public Wp_Postmeta(
            int meta_id,
            int post_id,
            String meta_key,
            String meta_value) {

        this.meta_id = meta_id;
        this.post_id = post_id;
        this.meta_key = meta_key;
        this.meta_value = meta_value;
    }

    public String get_Wp_Postmeta_SQL(){
        String outStr = "(" + meta_id + ", " + post_id + ", " +
                sqlString(meta_key) + ", " + sqlString(meta_value) + "),";
        Logger.info("PostmetaSQL : {}", outStr);
        return outStr;
    };

  /* SQL table definition for reference
    CREATE TABLE IF NOT EXISTS `wp_postmeta` (
    `meta_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `post_id` bigint(20) unsigned NOT NULL DEFAULT '0',
    `meta_key` varchar(255) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
    `meta_value` longtext COLLATE utf8mb4_unicode_520_ci,
    PRIMARY KEY (`meta_id`),
    KEY `post_id` (`post_id`),
    KEY `meta_key` (`meta_key`(191))
    ) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci AUTO_INCREMENT=12530 ;
  */

}
