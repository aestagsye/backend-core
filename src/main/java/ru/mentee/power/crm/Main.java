package ru.mentee.power.crm;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import ru.mentee.power.crm.repository.InMemoryLeadRepository;
import ru.mentee.power.crm.repository.LeadRepositoryLegacy;
import ru.mentee.power.crm.service.LeadServiceLegacy;
import ru.mentee.power.crm.servlet.LeadListServlet;

public class Main {
  public static void main(String[] args) throws Exception {
    LeadRepositoryLegacy repository = new InMemoryLeadRepository();
    LeadServiceLegacy leadService = new LeadServiceLegacy(repository);

    Tomcat tomcat = new Tomcat();
    tomcat.setPort(8080);
    tomcat.getConnector();

    Context context = tomcat.addContext("", new File(".").getAbsolutePath());
    context.getServletContext().setAttribute("leadService", leadService);

    tomcat.addServlet(context, "LeadListServlet", new LeadListServlet());
    context.addServletMappingDecoded("/leads", "LeadListServlet");
    tomcat.start();

    System.out.println("Tomcat started on port 8080");
    System.out.println("Open http://localhost:8080/leads in browser");

    tomcat.getServer().await();
  }
}