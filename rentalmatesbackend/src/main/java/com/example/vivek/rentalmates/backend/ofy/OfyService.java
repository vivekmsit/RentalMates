package com.example.vivek.rentalmates.backend.ofy;

import com.example.vivek.rentalmates.backend.entities.ExpenseData;
import com.example.vivek.rentalmates.backend.entities.FlatInfo;
import com.example.vivek.rentalmates.backend.entities.RegistrationRecord;
import com.example.vivek.rentalmates.backend.entities.UserProfile;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

/**
 * Objectify service wrapper so we can statically register our persistence classes
 * More on Objectify here : https://code.google.com/p/objectify-appengine/
 */
public class OfyService {

    static {
        ObjectifyService.register(RegistrationRecord.class);
        ObjectifyService.register(UserProfile.class);
        ObjectifyService.register(FlatInfo.class);
        ObjectifyService.register(ExpenseData.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}