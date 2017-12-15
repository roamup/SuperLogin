package com.roam.jcaptcha;

import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;

/**
 * <p>
 * Version: 1.0
 */
public class MyManageableImageCaptchaService extends DefaultManageableImageCaptchaService {

	public MyManageableImageCaptchaService(com.octo.captcha.service.captchastore.CaptchaStore captchaStore,
			com.octo.captcha.engine.CaptchaEngine captchaEngine, int minGuarantedStorageDelayInSeconds,
			int maxCaptchaStoreSize, int captchaStoreLoadBeforeGarbageCollection) {
		super(captchaStore, captchaEngine, minGuarantedStorageDelayInSeconds, maxCaptchaStoreSize,
				captchaStoreLoadBeforeGarbageCollection);
	}

	public boolean hasCapcha(String id, String userCaptchaResponse) {
		return store.getCaptcha(id).validateResponse(userCaptchaResponse);
	}
}
