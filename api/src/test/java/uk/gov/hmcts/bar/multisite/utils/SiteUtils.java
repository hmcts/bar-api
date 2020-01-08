package uk.gov.hmcts.bar.multisite.utils;

import uk.gov.hmcts.bar.multisite.model.SiteUserDto;

import java.util.ArrayList;
import java.util.List;

public class SiteUtils {

    public static List<SiteUserDto> createUsers() {
        List<SiteUserDto> users = new ArrayList<>();
        users.add(new SiteUserDto() {
            @Override
            public String getEmail() {
                return "a@a.com";
            }

            @Override
            public String getForename() {
                return "A";
            }

            @Override
            public String getSurname() {
                return "User";
            }

            @Override
            public String getRoles() { return "bar-delivery-manager,bar-senior-clerk,IDAM_ADMIN_USER";};

        });

        users.add(new SiteUserDto() {
            @Override
            public String getEmail() {
                return "b@b.com";
            }

            @Override
            public String getForename() {
                return "B";
            }

            @Override
            public String getSurname() {
                return "User";
            }

            @Override
            public String getRoles() { return "bar-senior-clerk,bar-post-clerk"; }
        });

        users.add(new SiteUserDto() {
            @Override
            public String getEmail() {
                return "c@c.com";
            }

            @Override
            public String getForename() {
                return "C";
            }

            @Override
            public String getSurname() {
                return "User";
            }

            @Override
            public String getRoles() { return "bar-fee-clerk"; }
        });

        return users;
    }
}
