package com.harsha.account.web;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.harsha.account.model.User;
import com.harsha.account.service.SecurityService;
import com.harsha.account.service.UserService;
import com.harsha.account.u2f.Resource;
import com.harsha.account.validator.UserValidator;
import com.harsha.account.view.AuthenticationView;
import com.harsha.account.view.RegistrationView;
import com.yubico.u2f.U2F;
import com.yubico.u2f.data.DeviceRegistration;
import com.yubico.u2f.data.messages.RegisterRequestData;
import com.yubico.u2f.exceptions.U2fBadConfigurationException;
import com.yubico.u2f.exceptions.U2fBadInputException;

import io.dropwizard.views.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;
    
    private Resource resource;

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model) {
        model.addAttribute("userForm", new User());
        model.addAttribute("username", new String());

        return "registration";
    }
    
    private final U2F u2f = new U2F();
    public static final String APP_ID = "https://localhost:8080";
    private final Map<String, String> requestStorage = new HashMap<String, String>();
    
    private final LoadingCache<String, Map<String, String>> userStorage = CacheBuilder.newBuilder().build(new CacheLoader<String, Map<String, String>>() {
        @Override
        public Map<String, String> load(String key) throws Exception {
            return new HashMap<String, String>();
        }
    });
    
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        userService.save(userForm);
        
        System.out.println(userForm.getUsername());

        securityService.autologin(userForm.getUsername(), userForm.getPasswordConfirm());

        return "redirect:/welcome";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(@ModelAttribute("userForm") User userForm, Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");
        
        System.out.println(userForm.getUsername());

        return "login";
    }

    @RequestMapping(value = {"/", "/welcome"}, method = RequestMethod.GET)
    public String welcome(Model model) {
        return "welcome";
    }
    
    @RequestMapping(value = "/u2fRegister", method = RequestMethod.GET)
    public View u2fRegister(@ModelAttribute("username") User userForm, BindingResult bindingResult, Model model) throws U2fBadConfigurationException, U2fBadInputException{
    	System.out.println("inside u2fRegister");
    	System.out.println("username is" + userForm.getUsername());
    	String username = userForm.getUsername();
    	RegisterRequestData registerRequestData = u2f.startRegistration(APP_ID, getRegistrations(username));
        requestStorage.put(registerRequestData.getRequestId(), registerRequestData.toJson());
    	return new RegistrationView(registerRequestData.toJson(), username);
    }
    
    @GET
    public View startRegistration(String username) throws U2fBadConfigurationException, U2fBadInputException {
    	System.out.println("username inside startRegistration " +username);
    	RegisterRequestData registerRequestData = u2f.startRegistration(APP_ID, getRegistrations(username));
        requestStorage.put(registerRequestData.getRequestId(), registerRequestData.toJson());
    	return new RegistrationView(registerRequestData.toJson(), username);
    }
    
    private Iterable<DeviceRegistration> getRegistrations(String username) throws U2fBadInputException {
        List<DeviceRegistration> registrations = new ArrayList<DeviceRegistration>();
        for (String serialized : userStorage.getUnchecked(username).values()) {
            registrations.add(DeviceRegistration.fromJson(serialized));
        }
        return registrations;
    }
       
}
