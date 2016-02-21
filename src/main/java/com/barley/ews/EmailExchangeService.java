package com.barley.ews;

import microsoft.exchange.webservices.data.autodiscover.IAutodiscoverRedirectionUrl;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

@Service
public class EmailExchangeService {

    private static ExchangeService exchangeService = null;
    private static boolean isLoggedIn = false;

    static class RedirectionUrlCallback implements IAutodiscoverRedirectionUrl {
        public boolean autodiscoverRedirectionUrlValidationCallback(
                String redirectionUrl) {
            return redirectionUrl.toLowerCase().startsWith("https://");
        }
    }

    public void login(Properties properties) throws Exception {
        String exchangeEmail = properties.getProperty("exchange.email");
        String exchangePassword = properties.getProperty("exchange.password");
        //String exchangeUrl = properties.getProperty("exchange.url");
        Assert.notNull(exchangeEmail, "Exchange email is null. Please provide a valid email.");
        Assert.notNull(exchangePassword, "Exchange password is null. Please provide a valid password.");
        //Assert.notNull(exchangeUrl, "Exchange url is null. Please provide a valid url to connect.");
        //exchangeService = new ExchangeService(ExchangeVersion.Exchange2007_SP1);
        exchangeService = new ExchangeService();
        ExchangeCredentials credentials = new WebCredentials(exchangeEmail, exchangePassword);
        exchangeService.setCredentials(credentials);
        //service.setUrl(URI.create(exchangeUrl));
        exchangeService.autodiscoverUrl(exchangeEmail, new RedirectionUrlCallback());
        isLoggedIn = true;
    }


    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public List<Map<String, String>> readEmails(int count, WellKnownFolderName name, boolean doDelete) {
        System.out.println("\n\n\n############### READING EMAILS for : " + name.name() + " #################");
        List<Map<String, String>> msgDataList = new ArrayList<>();
        try {
            Folder folder = Folder.bind(exchangeService, name);
            folder.delete(DeleteMode.HardDelete);
            FindItemsResults<Item> results = exchangeService.findItems(folder.getId(), new ItemView(count));
            int i = 1;
            for (Item item : results) {
                Map<String, String> messageData;
                messageData = readEmailItem(item.getId(), doDelete);
                if (!doDelete) {
                    System.out.println("\nEmails #" + (i++) + ":");
                    System.out.println("subject : " + messageData.get("subject"));
                    System.out.println("Sender : " + messageData.get("senderName").toString());
                    msgDataList.add(messageData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msgDataList;
    }

    /**
     * Reading one email at a time. Using Item ID of the email.
     * Creating a message data map as a return value.
     */
    public Map readEmailItem(ItemId itemId, boolean doDelete) {
        Map messageData = new HashMap();
        try {
            Item itm = Item.bind(exchangeService, itemId, PropertySet.FirstClassProperties);
            EmailMessage emailMessage = EmailMessage.bind(exchangeService, itm.getId());
            if (doDelete) {
                System.out.println("deleting item: " + itm.getSubject());
                //itm.delete(DeleteMode.SoftDelete);
                emailMessage.delete(DeleteMode.SoftDelete);
                //itm.delete(DeleteMode.SoftDelete);
                //emailMessage.delete(DeleteMode.HardDelete);
            } else {
                messageData.put("emailItemId", emailMessage.getId().toString());
                messageData.put("subject", emailMessage.getSubject());
                messageData.put("fromAddress", emailMessage.getFrom().getAddress().toString());
                messageData.put("senderName", emailMessage.getSender().getName().toString());
                Date dateTimeCreated = emailMessage.getDateTimeCreated();
                messageData.put("SendDate", dateTimeCreated.toString());
                Date dateTimeReceived = emailMessage.getDateTimeReceived();
                messageData.put("ReceivedDate", dateTimeReceived.toString());
                messageData.put("Size", emailMessage.getSize() + "");
                messageData.put("emailBody", emailMessage.getBody().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageData;
    }

}
