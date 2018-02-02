package com.ereinecke.cvtprop;

/**
 * Initiates a static array of PropertyStatus for cvtprop
 * <p>
 *
 * @author  Erik Reinecke  <erik@ereinecke.com>
 * @version 0.1
 * @since   1/21/2018
 *
 * */

public class InitPropertyStatus {

    public static PropertyStatus[] initPropertyStatus() {
        int arraySize = 2;

        PropertyStatus[] propertyStatusArray = new PropertyStatus[arraySize];

        propertyStatusArray[0] = new PropertyStatus("For Rent",
                "rent","8",
                "a:1:{i:0;i:8;}");
        propertyStatusArray[1] = new PropertyStatus( "For Sale",
                "sale","9",
                "a:1:{i:0;i:9;}");

        return propertyStatusArray;
    }

}
