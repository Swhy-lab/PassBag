package com.passbag.home.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.common.home.dao.CommonDAO;



/**
 * Handles requests for the application home page.
 */
@Controller
@ResponseBody
@RequestMapping(value="/PassBag/auth", method=RequestMethod.POST)
public class AuthRestController {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthRestController.class);

	@Autowired
	CommonDAO cdao;
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@ResponseBody
	@RequestMapping(value = "/pass-check", method = RequestMethod.POST)
	public String passCheck(Locale locale, Model model) throws Exception {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		try {
		
		System.out.println(cdao.getTimestamp());
		}catch(Exception e) {
			e.printStackTrace();
		}
		return "home/home";
	}
	
	@ResponseBody
	@RequestMapping(value = "/error", method = RequestMethod.GET)
	public String error(Locale locale, Model model) {
		return "error";
	}
	
}
