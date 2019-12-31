package com.dangdang.cymbal.service.util.service.impl;

import com.dangdang.cymbal.common.exception.CymbalException;
import com.dangdang.cymbal.service.util.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Implement of {@link MailService} by spring mail starter.
 *
 * @author GeZhen
 */
@Slf4j
@Service
public class MailServiceImpl implements MailService {

    @Resource
    private JavaMailSender sender;

    @Value("${alarm.mail.from}")
    private String from;

    @Value("${alarm.mail.to}")
    private String[] adminMails;

    private RetryTemplate retryTemplate = new RetryTemplate();

    private static final int MAIL_MAX_ATTEMPTS = 3;

    private static final int MAIL_BACKOFF_PERIOD = 100;


    public MailServiceImpl() {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(MAIL_MAX_ATTEMPTS);
        retryTemplate.setRetryPolicy(retryPolicy);

        // 失败后补偿策略
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(MAIL_BACKOFF_PERIOD);
        retryTemplate.setBackOffPolicy(backOffPolicy);
    }

    @Override
    public void sendMail(final String title, final String content, final String[] to, final String[] cc,
            final boolean isHtml) {
        retryTemplate.execute(retryContext -> {
            try {
                MimeMessage message = sender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, CharEncoding.UTF_8);
                helper.setFrom(from);
                helper.setTo(to);
                if (cc != null && cc.length > 0) {
                    helper.setCc(cc);
                }
                helper.setSubject(title);
                helper.setText(content, isHtml);
                sender.send(message);
                return null;
            } catch (MessagingException e) {
                throw new CymbalException(e);
            }
        }, retryContext -> {
            log.error("Send mail fail", retryContext.getLastThrowable());
            throw (CymbalException) retryContext.getLastThrowable();
        });
    }

    @Override
    public void sendMailToAdmin(final String title, final String content) {
        sendMail(title, content, adminMails, null, false);
    }

    @Override
    public void sendHtmlMailToAdmin(final String title, final String content) {
        sendMail(title, content, adminMails, null, true);
    }

    @Override
    public void sendHtmlMail(final String title, final String content, final String[] recivers) {
        sendMail(title, content, recivers, adminMails, true);
    }
}
