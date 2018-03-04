package com.ereinecke.cvtpropxml;

public class InitUser {


    public static User[] initUser() {
        int arraySize = 5;

        User[] usersArray = new User[arraySize];

        usersArray[0] = new User("Lane","2");
        usersArray[1] = new User( "Gordon","4}");
        usersArray[2] = new User("Enrique","6");
        usersArray[3] = new User( "Ruth", "5");
        usersArray[4] = new User( "Javier","16");

        return usersArray;
    }

}
