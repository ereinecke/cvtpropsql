package com.ereinecke.cvtprop;

/**
 * Definition and constructor of PropertyStatus class for cvtprop
 * <p>
 *
 * @author  Erik Reinecke  <erik@ereinecke.com>
 * @version 0.1
 * @since   1/21/2018
 *
 * */

public class PropertyStatus {
        String niceName;
        String slug;
        String propTypeNum;
        String propTypeNumSerialized;

        public PropertyStatus(String niceName, String slug, String propTypeNum,
                               String propTypeNumSerialized) {
            this.niceName = niceName;
            this.slug = slug;
            this.propTypeNum = propTypeNum;
            this.propTypeNumSerialized = propTypeNumSerialized;
        }
}