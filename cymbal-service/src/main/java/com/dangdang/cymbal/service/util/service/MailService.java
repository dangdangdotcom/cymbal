package com.dangdang.cymbal.service.util.service;

/**
 * Mail service in util.
 *
 * @auther GeZhen
 */
public interface MailService {

    /**
     * Send mail and cc to target mail address.
     *
     * @param title
     * @param content
     * @param to
     * @param cc
     * @param isHtml
     */
    void sendMail(String title, String content, String[] to, String[] cc, boolean isHtml);

    /**
     * Send mail to admin user.
     *
     * @param title
     * @param content
     */
    void sendMailToAdmin(String title, String content);

    /**
     * Send mail in HTML format to admin user.
     *
     * @param title
     * @param content
     */
    void sendHtmlMailToAdmin(String title, String content);

    /**
     * Send mail to target users.
     *
     * @param title
     * @param content
     * @param receivers
     */
    void sendHtmlMail(String title, String content, String[] receivers);
}
