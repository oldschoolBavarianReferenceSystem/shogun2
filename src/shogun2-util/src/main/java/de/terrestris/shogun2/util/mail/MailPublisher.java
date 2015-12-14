package de.terrestris.shogun2.util.mail;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 *
 * @author Daniel Koch
 *
 */
@Component
public class MailPublisher {

	/**
	 * The Logger.
	 */
	private static final Logger LOG = Logger.getLogger(MailPublisher.class);

	/**
	 * The autowired JavaMailSender class.
	 */
	@Autowired
	@Qualifier("mailSender")
	private JavaMailSender mailSender;

	/**
	 * The default mail sender (e.g. noreply@shogun2.de).
	 */
	@Autowired
	@Qualifier("defaultMailSender")
	private String defaultMailSender;

	/**
	 * Sends a SimpleMailMessage.
	 *
	 * @param from The mail sender address.
	 * @param replyTo The reply to address.
	 * @param to A list of mail recipient addresses.
	 * @param cc A list of carbon copy mail recipient addresses.
	 * @param bcc A list of blind carbon copy mail recipient addresses.
	 * @param subject The mail subject.
	 * @param msg The mail message text.
	 */
	public void sendMail(String from, String replyTo, String[] to, String[] cc,
			String[] bcc, String subject, String msg) {

		SimpleMailMessage simpleMailMassage = new SimpleMailMessage();

		// fallback to default mail sender
		if (from == null || from.isEmpty()) {
			from = defaultMailSender;
		}

		simpleMailMassage.setFrom(from);
		simpleMailMassage.setReplyTo(replyTo);
		simpleMailMassage.setTo(to);
		simpleMailMassage.setBcc(bcc);
		simpleMailMassage.setCc(cc);
		simpleMailMassage.setSubject(subject);
		simpleMailMassage.setText(msg);

		sendMail(simpleMailMassage);
	}

	/**
	 *
	 * Sends a MimeMessage.
	 *
	 * @param from The mail sender address.
	 * @param replyTo The reply to address.
	 * @param to A list of mail recipient addresses.
	 * @param cc A list of carbon copy mail recipient addresses.
	 * @param bcc A list of blind carbon copy mail recipient addresses.
	 * @param subject The mail subject.
	 * @param msg The mail message text.
	 * @param html Whether to apply content type "text/html" or the default
	 *             content type ("text/plain").
	 * @param attachmentFilename The attachment file name.
	 * @param attachmentFile The file resource to be applied to the mail.
	 *
	 * @throws MessagingException
	 */
	public void sendMimeMail(String from, String replyTo, String[] to, String[] cc,
			String[] bcc, String subject, String msg, Boolean html,
			String attachmentFilename, File attachmentFile) throws MessagingException {

		MimeMessage mimeMailMessage = mailSender.createMimeMessage();
		MimeMessageHelper mimeHelper = new MimeMessageHelper(mimeMailMessage);

		// fallback to default mail sender
		if (from == null || from.isEmpty()) {
			from = defaultMailSender;
		}

		mimeHelper.setFrom(from);
		mimeHelper.setReplyTo(replyTo);
		mimeHelper.setTo(to);
		mimeHelper.setBcc(bcc);
		mimeHelper.setCc(cc);
		mimeHelper.setSubject(subject);
		mimeHelper.setText(msg, html);
		mimeHelper.addAttachment(attachmentFilename, attachmentFile);

		sendMail(mimeMailMessage);
	}

	/**
	 *
	 * @param mailMessage
	 */
	public void sendMail(SimpleMailMessage mailMessage) {
		LOG.debug("Requested to send a mail");
		try {
			mailSender.send(mailMessage);
			LOG.debug("Successfully send mail.");
		} catch(MailException e) {
			LOG.error("Could not send the mail: " + e.getMessage());
		}
	}

	/**
	 *
	 * @param mimeMessage
	 */
	public void sendMail(MimeMessage mimeMessage) {
		LOG.debug("Requested to send a mail");
		try {
			mailSender.send(mimeMessage);
			LOG.debug("Successfully send mail.");
		} catch(MailException e) {
			LOG.error("Could not send the mail: " + e.getMessage());
		}
	}

	/**
	 * @param mailSender the mailSender to set
	 */
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	/**
	 * @param defaultMailSender the defaultMailSender to set
	 */
	public void setDefaultMailSender(String defaultMailSender) {
		this.defaultMailSender = defaultMailSender;
	}

}
