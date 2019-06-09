package filter;

import connection.ConnectionCreator;
import utils.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.sql.*;

@WebFilter(urlPatterns = {"/welcome.jsp"}, filterName = "FilterAuth")
public class FilterAuth implements Filter {

    private static String name;
    private static String password;
    private ConnectionCreator connectionCreator;
    private Connection connection;

    public ResultSet makeQuary(String sql) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = connection.createStatement();
            rs = st.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

        boolean isOk = false;

        //File file = new File("notes3.txt");

        //BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            connection = connectionCreator.createConnection();
            ResultSet resS = makeQuary("SELECT * FROM users");
            User us = new User();
            us.setPassword(req.getParameter("pass"));
            us.setLogin(req.getParameter("name"));

            while (resS.next()) {
                User type = new User();

                type.setLogin(resS.getString(1));

                type.setPassword(resS.getString(2));

                if (type.getLogin().equals(us.getLogin()) && type.getPassword().equals(us.getPassword())) {
                    isOk = true;
                    break;
                }
            }
        } catch (SQLException e) {
            resp.getWriter().println(e.getMessage());
            try {
                connection.close();
            } catch (SQLException ex) {
                resp.getWriter().println(ex.getMessage());
            }
        } catch (Exception e) {
            resp.getWriter().println(e.getLocalizedMessage());
        }

        if (!isOk) {
            resp.getWriter().println("You were wrong.........");
            req.getRequestDispatcher("/register").forward(req,resp);
        } else {
            chain.doFilter(req, resp);
        }
    }

    public void init(FilterConfig fConfig) throws ServletException {
        name = fConfig.getInitParameter("login");
        password = fConfig.getInitParameter("password");
    }
}
