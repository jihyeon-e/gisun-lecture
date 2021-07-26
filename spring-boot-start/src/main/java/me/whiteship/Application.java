package me.whiteship;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.io.IOException;
import java.io.PrintWriter;


@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
//        Tomcat tomcat = new Tomcat();
//        tomcat.setPort(8080);
//
//        Context context = tomcat.addContext("/", "/");
//
//        HttpServlet servlet = new HttpServlet() {
//            @Override
//            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//                PrintWriter writer = resp.getWriter();
//                writer.println("<html><head><title>");
//                writer.println("Hey, Tomcat");
//                writer.println("</title></head>");
//                writer.println("<body><h1>Hello Tomcat</h1></body>");
//                writer.println("</html>");
//            }
//        };
//
//        String servletName = "helloServlet";
//        tomcat.addServlet("/", servletName, servlet);
//        context.addServletMappingDecoded("/hello", servletName);
//
//        tomcat.start();
//        tomcat.getServer().await();
    }

}

