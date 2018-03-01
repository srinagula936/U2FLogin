package com.harsha.account.web;

import com.harsha.account.model.User;
import com.harsha.account.service.SecurityService;
import com.harsha.account.service.UserService;
import com.harsha.account.u2f.Resource;
import com.harsha.account.validator.UserValidator;

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
    
/*    @RequestMapping(value = "/u2fRegister", method = RequestMethod.GET)
    public String u2fRegister(Model model) {
        model.addAttribute("username", new String());

        return "u2fRegister";
    }*/
    
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
    public String u2fRegister(@ModelAttribute("username") User userForm, BindingResult bindingResult, Model model){
    	System.out.println("inside u2fRegister");
    	System.out.println("username is" + userForm.getUsername());
    	String username = userForm.getUsername();
    	try {
    		resource.startRegistration(username);
    	}
    	catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
