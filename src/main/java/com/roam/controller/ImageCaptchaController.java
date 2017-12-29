package com.roam.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.octo.captcha.service.CaptchaServiceException;

@Controller
public class ImageCaptchaController {

	@RequestMapping("/getCaptcha")
	public void genernateCaptchaImage(final HttpServletRequest request, final HttpServletResponse response)
			throws IOException {
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		ServletOutputStream out = response.getOutputStream();
		try {
			String captchaId = request.getSession(true).getId();
			BufferedImage challenge = (BufferedImage) CaptchaServiceSingleton.getInstance().getChallengeForID(captchaId,
					request.getLocale());
			ImageIO.write(challenge, "jpg", out);
			out.flush();
		} catch (CaptchaServiceException e) {
		} finally {
			out.close();
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/checkCaptcha", method = RequestMethod.POST)
	public Map checkCaptcha(HttpServletRequest request, String checkCaptcha) {
		Map resultMap = new HashMap<>();
		String captchaId = request.getSession().getId();
		try {
			Boolean isResponseCorrect = CaptchaServiceSingleton.getInstance().validateResponseForID(captchaId, checkCaptcha);
			resultMap.put("result", isResponseCorrect? "正确":"错误");
		} catch (Exception e) {
			resultMap.put("result", "验证码已失效，请点击图片刷新~");
		}
		return resultMap;
	}

}
