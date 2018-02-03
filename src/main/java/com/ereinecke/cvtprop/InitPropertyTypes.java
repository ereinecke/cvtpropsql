package com.ereinecke.cvtprop;

/**
 * Initiates a static array of PropertyTypes for cvtprop
 * <p>
 *
 * @author  Erik Reinecke  <erik@ereinecke.com>
 * @version 0.1
 * @since   1/21/2018
 *
 * */

public class InitPropertyTypes {

    public static PropertyTypeRealty[] initPropertyTypes() {
        int arraySize = 10;

        PropertyTypeRealty[] propertyTypeRealtyArray = new PropertyTypeRealty[arraySize];

        propertyTypeRealtyArray[0] = new PropertyTypeRealty("Apartments",
                "apartments","72",
                "a:1:{i:0;i:72;}");
        propertyTypeRealtyArray[1] = new PropertyTypeRealty( "Commercial",
                "commercial","15",
                "a:1:{i:0;i:15;}");
        propertyTypeRealtyArray[2] = new PropertyTypeRealty("Condominiums",
                "condominiums","3",
                "a:1:{i:0;i:3;}");
        propertyTypeRealtyArray[3] = new PropertyTypeRealty("Country Homes",
                "country homes","20",
                "a:1:{i:0;i:20;}");
        propertyTypeRealtyArray[4] = new PropertyTypeRealty("Haciendas",
                "haciendas","70",
                "a:1:{i:0;i:70;}");
        propertyTypeRealtyArray[5] = new PropertyTypeRealty("Homes",
                "homes","13",
                "a:1:{i:0;i:13;}");
        propertyTypeRealtyArray[6] = new PropertyTypeRealty("Land",
                "land","35",
                "a:1:{i:0;i:35;}");
        propertyTypeRealtyArray[7] = new PropertyTypeRealty("Lots",
                "lots","34",
                "a:1:{i:0;i:34;}");
        propertyTypeRealtyArray[8] = new PropertyTypeRealty("Ranches",
                "ranches","36",
                "a:1:{i:0;i:36;}");
        propertyTypeRealtyArray[9] = new PropertyTypeRealty("Unknown",
                "unknown", "108", "\"a:1:{i:0;i:108;}\"");

        return propertyTypeRealtyArray;
    }

}
