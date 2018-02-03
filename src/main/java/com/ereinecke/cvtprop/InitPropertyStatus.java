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
        int arraySize = 5;

        PropertyStatus[] propertyStatusArray = new PropertyStatus[arraySize];

        propertyStatusArray[0] = new PropertyStatus("For Rent",
                "rent","8",
                "a:1:{i:0;i:8;}");
        propertyStatusArray[1] = new PropertyStatus( "For Sale",
                "sale","9",
                "a:1:{i:0;i:9;}");
        propertyStatusArray[0] = new PropertyStatus("Featured",
                "featured","8",
                "a:1:{i:0;i:106;}");
        propertyStatusArray[1] = new PropertyStatus( "Reduced",
                "reduced","9",
                "a:1:{i:0;i:107;}");
        propertyStatusArray[1] = new PropertyStatus( "For Sale",
                "sold","105",
                "a:1:{i:0;i:105;}");

        return propertyStatusArray;
    }

}
