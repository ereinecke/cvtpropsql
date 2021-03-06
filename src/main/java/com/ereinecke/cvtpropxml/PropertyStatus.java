package com.ereinecke.cvtpropxml;

/**
 * Definition and constructor of PropertyStatus class for cvtprop
 * <p>
 *
 * @author  Erik Reinecke  <erik@ereinecke.com>
 * @version 1.0
 * @since   1/21/2018
 *
 * */

public class PropertyStatus {
        String niceName;
        String slug;
        String propStatusNum;
        String propStatusNumSerialized;

        public PropertyStatus(String niceName, String slug, String propStatusNum,
                               String propStatusNumSerialized) {
            this.niceName = niceName;
            this.slug = slug;
            this.propStatusNum = propStatusNum;
            this.propStatusNumSerialized = propStatusNumSerialized;
        }
}