/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jenkinsci.plugins.mailmapresolver;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import hudson.model.User;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import static org.mockito.Mockito.*;
import org.xml.sax.SAXException;

/**
 *
 * @author acearl
 */
public class MailMapAddressResolverTest {
    
    @Rule
    public JenkinsRule j = new JenkinsRule();
    
    private MailMapResolver resolverConfig;
    
    @Before
    public void before() {
        resolverConfig = j.jenkins.getDescriptorByType(MailMapResolver.class);
    }
    
    @Test
    public void testSimpleMapping() {
        resolverConfig.mailMap = "mickey=mickey@disney.com";
        
        MailMapAddressResolver resolver = new MailMapAddressResolver();
        
        User user = mock(User.class);
        when(user.getId()).thenReturn("mickey");
        
        String email = resolver.findMailAddressFor(user);
        assertEquals("mickey@disney.com", email);                
    }    
    
    @Test
    public void testReturnsNullOnMissingEntry() {
        resolverConfig.mailMap = "donald=donald@disney.com";
        
        MailMapAddressResolver resolver = new MailMapAddressResolver();
        
        User user = mock(User.class);
        when(user.getId()).thenReturn("mickey");
        
        String email = resolver.findMailAddressFor(user);
        assertEquals(null, email);    
    }
    
    @Test
    public void testMultipleUsernames() {
        resolverConfig.mailMap = "mickey,mouse=mickey@disney.com";
        MailMapAddressResolver resolver = new MailMapAddressResolver();
        
        User mickey = mock(User.class);
        when(mickey.getId()).thenReturn("mickey");
        
        User mouse = mock(User.class);
        when(mouse.getId()).thenReturn("mouse");
        
        String email = resolver.findMailAddressFor(mickey);
        assertEquals("mickey@disney.com", email);    
        email = resolver.findMailAddressFor(mouse);
        assertEquals("mickey@disney.com", email);    
    }
    
    @Test
    public void testConfigRoundtrip() throws IOException, SAXException, Exception {
        MailMapResolver descriptor = j.jenkins.getDescriptorByType(MailMapResolver.class);        
        HtmlPage page = j.createWebClient().goTo("configure");
        HtmlTextArea mapInput = page.getElementByName("mailMapResolver_Map");
        mapInput.setText("mickey=mickey@disney.com");
        j.submit(page.getFormByName("config"));       
        
        assertEquals("mickey=mickey@disney.com", descriptor.mailMap); 
    }
    
    @Test
    public void testConfigRoundtripReloadsValue() throws IOException, SAXException, Exception {
        MailMapResolver descriptor = j.jenkins.getDescriptorByType(MailMapResolver.class);   
        WebClient client = j.createWebClient();
        HtmlPage page = client.goTo("configure");
        HtmlTextArea mapInput = page.getElementByName("mailMapResolver_Map");
        mapInput.setText("mickey=mickey@disney.com");
        j.submit(page.getFormByName("config"));       
        
        assertEquals("mickey=mickey@disney.com", descriptor.mailMap);
        
        page = client.goTo("configure");
        mapInput = page.getElementByName("mailMapResolver_Map");
        assertEquals("micket=mickey@disney.com", mapInput.getText());
    }
}
