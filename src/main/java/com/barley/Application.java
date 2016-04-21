package com.barley;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.barley")
public class Application {
    public static void main(String[] args) throws Exception {
        System.out.println("$$$$$$$$$ Arguments received $$$$$$$$$$$$");

        for (String arg : args) {
            System.out.println(" Argument : " + arg);
        }
        if (args.length > 0) {
            System.setProperty("email.directory.name", args[0]);
        }
        System.out.println("######## Starting the application now ########");
        ConfigurableApplicationContext cac = SpringApplication.run(Application.class, args);
        System.out.println("######## Application load finished ########");
        cac.start();
        System.out.println("######## Application load finished ########");
        System.out.println("######## Application load finished ########");
        System.out.println("######## Application load finished ########");
    }


    /*private void readEmails() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("exchange.url", "exchange.url");
        properties.setProperty("exchange.email", "exchange.email");
        properties.setProperty("exchange.password", "exchange.password");

        EmailExchangeService service = new EmailExchangeService();

        service.login(properties);
        if (service.isLoggedIn()) {
            service.readEmails(100, WellKnownFolderName.RecoverableItemsPurges, true);
        }



    //get hold of actor system
    ActorSystem system = cac.getBean(ActorSystem.class);
    ActorRef helloWorld = system.actorOf(SpringExtension.SpringExtProvider.get(system).props("HelloWorld"), "helloWorld");
    helloWorld.tell(Greeter.Msg.START, helloWorld);
    }*/
}
