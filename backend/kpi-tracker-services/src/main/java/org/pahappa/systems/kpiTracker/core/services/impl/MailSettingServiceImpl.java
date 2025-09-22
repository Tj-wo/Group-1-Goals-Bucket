package org.pahappa.systems.kpiTracker.core.services.impl;

import org.apache.commons.lang.StringUtils;
import org.pahappa.systems.kpiTracker.core.services.MailSettingService;
import org.pahappa.systems.kpiTracker.models.MailSetting;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.dao.impl.BaseDAOImpl;
import org.sers.webutils.server.core.utils.MailUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MailSettingServiceImpl extends BaseDAOImpl<MailSetting> implements MailSettingService {

	@Override
	public MailSetting saveMailSetting(MailSetting mailSetting) throws ValidationFailedException {
		if (StringUtils.isBlank(mailSetting.getSmtpHost()))
			throw new ValidationFailedException("Missing SMTP host");

		if (StringUtils.isBlank(mailSetting.getSmtpPort()))
			throw new ValidationFailedException("Missing SMTP port");

		if (StringUtils.isBlank(mailSetting.getSenderEmail()))
			throw new ValidationFailedException("Missing sender email");

		if (!MailUtils.Util.getInstance().isValidEmail(mailSetting.getSenderEmail()))
			throw new ValidationFailedException("Invalid sender email");

		// Validate SMTP port is numeric
		try {
			Integer.parseInt(mailSetting.getSmtpPort());
		} catch (NumberFormatException e) {
			throw new ValidationFailedException("SMTP port must be a valid number");
		}

		// If authentication is enabled, validate credentials
		if (mailSetting.isSmtpAuth()) {
			if (StringUtils.isBlank(mailSetting.getSmtpUsername()))
				throw new ValidationFailedException("SMTP authentication is enabled but username is missing");

			if (StringUtils.isBlank(mailSetting.getSmtpPassword()))
				throw new ValidationFailedException("SMTP authentication is enabled but password is missing");
		}

		return super.save(mailSetting);
	}

	@Override
	public MailSetting getMailSetting() {
		if (super.findAll().size() > 0) {
			return super.findAll().get(0);
		}
		return null;
	}

}
