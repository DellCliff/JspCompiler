import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public final class JspCompiler implements ServletContextListener {

    private static Stream<String> walkResourcePaths(ServletContext servletContext, String resourcePath) {
        return resourcePath.endsWith("/")
            ? Objects.requireNonNullElseGet(servletContext.getResourcePaths(resourcePath), Set::<String>of).stream().flatMap(subPath -> walkResourcePaths(servletContext, subPath))
            : Stream.of(resourcePath);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        var servletContext = sce.getServletContext();
        walkResourcePaths(servletContext, "/")
            .filter(resourcePath -> resourcePath.endsWith(".jsp"))
            .forEach(jspPath -> {
                var dynamicServletRegistration = servletContext.addServlet(jspPath, "org.apache.jasper.servlet.JspServlet");
                dynamicServletRegistration.setInitParameter("development", "false");
                dynamicServletRegistration.setInitParameter("jspFile", jspPath);
                dynamicServletRegistration.addMapping(jspPath);
            });
    }
}
