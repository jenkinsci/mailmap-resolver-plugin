/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.mailmapresolver;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 *
 * @author acearl
 */
@Extension
public class MailMapResolver extends Descriptor<MailMapResolver> implements Describable<MailMapResolver> {

    public String mailMap = "";

    public MailMapResolver() {
        super(self());
    }

    public Descriptor<MailMapResolver> getDescriptor() {
        return this;
    }

    @Override
    public String getDisplayName() {
        return Messages.MailMapResolver_Name();
    }
    
    private Properties getUsermap(String input) throws IOException {
        Properties prop = new Properties();
        InputStream is = new ByteArrayInputStream(input.getBytes());
        prop.load(is);
        return prop;
    }
    
    public Properties getUsermap() throws IOException {
        return getUsermap(mailMap);
    }
    
    @Override
    public boolean configure(StaplerRequest req, JSONObject formData)
            throws FormException {
        mailMap = formData.getString("mailMapResolver_Map");
        return super.configure(req, formData);
    }
    
    public FormValidation doCheckSyntax(@QueryParameter String value) {        
        try {
            getUsermap(value);            
        } catch(IOException e) {
            return FormValidation.error("Syntax error in user map file");
        }
        
        return FormValidation.ok();
    }
}
