package com.ereinecke.cvtpropxml;

/**
 * Initiates a static array of PropertyFeatures for cvtprop
 * <p>
 *
 * @author  Erik Reinecke  <erik@ereinecke.com>
 * @version 0.1
 * @since   1/21/2018
 *
 * */

public class InitPropertyFeatures {

    public static PropertyFeature[] initPropertyFeatures() {
        int arraySize = 47;

        PropertyFeature[] propertyFeatureArray = new PropertyFeature[arraySize];

        propertyFeatureArray[0] = new PropertyFeature("Air Conditioning",
                "air-conditioning","2");
        propertyFeatureArray[1] = new PropertyFeature( "Appliances Included",
                "appliances-included","57");
        propertyFeatureArray[2] = new PropertyFeature("Appliances Not Included",
                "appliances-not-included","58");
        propertyFeatureArray[3] = new PropertyFeature("Balcony",
                "balcony","4");
        propertyFeatureArray[4] = new PropertyFeature("Breakfast Area",
                "breakfast-area","39");
        propertyFeatureArray[5] = new PropertyFeature("Built-In Kitchen",
                "built-in-kitchen","73");
        propertyFeatureArray[6] = new PropertyFeature("Cable TV",
                "cable-tv","47");
        propertyFeatureArray[7] = new PropertyFeature("Cistern",
                "cistern","60");
        propertyFeatureArray[8] = new PropertyFeature("City Electric",
                "city-electric","65");
        propertyFeatureArray[9] = new PropertyFeature("City Sewer Connection",
                "city-sewer-connection","66");
        propertyFeatureArray[10] = new PropertyFeature( "City Water",
                "city-water","64");
        propertyFeatureArray[11] = new PropertyFeature("Den",
                "den","42");
        propertyFeatureArray[12] = new PropertyFeature("Dining Room",
                "dining-room","40");
        propertyFeatureArray[13] = new PropertyFeature("Dryer",
                "dryer","6");
        propertyFeatureArray[14] = new PropertyFeature("Fireplace",
                "fireplace","7");
        propertyFeatureArray[15] = new PropertyFeature("Fountain",
                "fountain","53");
        propertyFeatureArray[16] = new PropertyFeature("Fully Furnished",
                "fully-furnished","74");
        propertyFeatureArray[17] = new PropertyFeature("Furniture Included",
                "furniture-included","10");
        propertyFeatureArray[18] = new PropertyFeature("Furniture Negotiable",
                "furniture-negotiable","56");
        propertyFeatureArray[19] = new PropertyFeature( "Garden",
                "garden","52");
        propertyFeatureArray[20] = new PropertyFeature("Gray Water System",
                "gray-water-system","62");
        propertyFeatureArray[21] = new PropertyFeature("Gym",
                "gym","11");
        propertyFeatureArray[22] = new PropertyFeature("Heating",
                "heating","12");
        propertyFeatureArray[23] = new PropertyFeature("Internet Ready",
                "internet-ready","49");
        propertyFeatureArray[24] = new PropertyFeature("Jacuzzi",
                "jacuzzi","54");
        propertyFeatureArray[25] = new PropertyFeature("Kitchen",
                "kitchen","5");
        propertyFeatureArray[26] = new PropertyFeature("Laundry",
                "laundry","44");
        propertyFeatureArray[27] = new PropertyFeature("Library",
                "library", "43");
        propertyFeatureArray[28] = new PropertyFeature( "Living Room",
                "living-room","41");
        propertyFeatureArray[29] = new PropertyFeature("Patio",
                "patio","50");
        propertyFeatureArray[30] = new PropertyFeature("Pool",
                "pool","78");
        propertyFeatureArray[31] = new PropertyFeature("Satellite TV",
                "satellite-tv","48");
        propertyFeatureArray[32] = new PropertyFeature("Septic Tank",
                "septic-tank","68");
        propertyFeatureArray[33] = new PropertyFeature("Service Quarters",
                "service-quarters","38");
        propertyFeatureArray[34] = new PropertyFeature("Solar",
                "solar","63");
        propertyFeatureArray[35] = new PropertyFeature("Some Furniture Included",
                "some-furniture-included","55");
        propertyFeatureArray[36] = new PropertyFeature("Storage",
                "storage","19");
        propertyFeatureArray[37] = new PropertyFeature("Studio",
                "studio","37");
        propertyFeatureArray[38] = new PropertyFeature("Swimming Pool",
                "swimming-pool","16");
        propertyFeatureArray[39] = new PropertyFeature("Telephone Line",
                "telephone-line","45");
        propertyFeatureArray[40] = new PropertyFeature("Number of Phone Lines",
                "phone-lines","46");
        propertyFeatureArray[41] = new PropertyFeature("Terrace",
                "terrace","51");
        propertyFeatureArray[42] = new PropertyFeature("Tinaco",
                "tinaco","59");
        propertyFeatureArray[43] = new PropertyFeature( "Washer",
                "washer","21");
        propertyFeatureArray[44] = new PropertyFeature("Water Purification System",
                "water-purification-system","61");
        propertyFeatureArray[45] = new PropertyFeature("Water Well",
                "water-well","67");
        propertyFeatureArray[46] = new PropertyFeature("Yard",
                "yard","23");

        return propertyFeatureArray;
    }

}