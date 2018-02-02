package com.ereinecke.cvtprop;

/**
 * Definition and constructor of PropertyFeature class for cvtprop
 * <p>
 *
 * @author  Erik Reinecke  <erik@ereinecke.com>
 * @version 0.1
 * @since   1/21/2018
 *
 * */

public class PropertyFeature {
    String niceName;
    String slug;
    String propTypeNum;
    String propTypeNumSerialized;

    public PropertyFeature(String niceName, String slug, String propTypeNum,
                        String propTypeNumSerialized) {
        this.niceName = niceName;
        this.slug = slug;
        this.propTypeNum = propTypeNum;
        this.propTypeNumSerialized = propTypeNumSerialized;
    }

}