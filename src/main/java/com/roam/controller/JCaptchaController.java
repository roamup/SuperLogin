package com.roam.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class JCaptchaController {

	//@ResponseBody
	@RequestMapping(value = "/loadCaptcha")
	public void generateCaptcha(HttpServletRequest request, HttpServletResponse response, ModelAndView mv)
			throws IOException {
		/*
		 * Subject subject = SecurityUtils.getSubject(); String captchaId =
		 * (String) subject.getSession().getId();
		 */
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		String captchaId = request.getSession().getId();
		BufferedImage challenge = (BufferedImage) CaptchaServiceSingleton.getInstance().getChallengeForID(captchaId,
				request.getLocale());
		ServletOutputStream out = response.getOutputStream();
		ImageIO.write(challenge, "jpg", out);
		out.flush();
		out.close();

		/*mv.addObject("captcha", out);
		return mv;*/
	}

}
