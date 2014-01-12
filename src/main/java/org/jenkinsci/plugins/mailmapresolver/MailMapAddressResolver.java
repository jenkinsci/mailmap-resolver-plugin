/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.mailmapresolver;

import hudson.Extension;
import hudson.model.User;
import hudson.tasks.MailAddressResolver;
import java.io.IOException;
import java.util.Properties;
import jenkins.model.Jenkins;
/**
 *
 * @author acearl
 */
@Extension
public class MailMapAddressResolver extends MailAddressResolver {

    @Override
    public String findMailAddressFor(User user) {
        String address = null;
        MailMapResolver resolver = Jenkins.getInstance().getDescriptorByType(MailMapResolver.class);
        if(resolver != null) {
            try {
                Properties props = resolver.getUsermap();
                for(Object k : props.keySet()) {
                    String key = (String)k;
                    if(key.contains(",")) { // multiple usernames
                        String[] usernames = key.split(",");
                        for(String username : usernames) {
                            if(username.trim().compareToIgnoreCase(user.getId()) == 0) {
                                address = props.getProperty(key);
                                break;
                            }
                        }
                        
                        if(address != null) {
                            break;
                        }
                    } else if(key.trim().compareToIgnoreCase(user.getId()) == 0) {
                        address = props.getProperty(key);
                        break;
                    }
                }
            } catch (IOException e) {
                // do nothing, we'll fall through and return null
            }
        }
        return address;
    }    
}
