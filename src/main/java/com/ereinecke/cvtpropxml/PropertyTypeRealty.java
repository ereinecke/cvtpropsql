package com.ereinecke.cvtpropxml;

/**
 * Definition and contstructor of PropertyTypeRealty class for cvtprop
 * <p>
 *
 * @author  Erik Reinecke  <erik@ereinecke.com>
 * @version 0.1
 * @since   1/21/2018
 *
 * */

public class PropertyTypeRealty {
    String niceName;
    String slug;
    String propTypeNum;
    String propTypeNumSerialized;

    public PropertyTypeRealty(String niceName, String slug, String propTypeNum,
                        String propTypeNumSerialized) {
        this.niceName = niceName;
        this.slug = slug;
        this.propTypeNum = propTypeNum;
        this.propTypeNumSerialized = propTypeNumSerialized;
    }

}
