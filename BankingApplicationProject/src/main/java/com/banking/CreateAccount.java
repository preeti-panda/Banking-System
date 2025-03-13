package com.banking;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/CreateAccount")
public class CreateAccount extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter pw = response.getWriter();
        response.setContentType("text/html");
        HttpSession session = request.getSession(false);
        if (session != null) {
            String user = (String) session.getAttribute("name");
            pw.print("<h1 align='center'>Welcome, " + user + " Continue with your transactions</h1>");
            try (Connection con = DBConnection.get()) {
                if (con != null) {
                    int num = Integer.parseInt(request.getParameter("anum"));
                    String name = request.getParameter("aname");
                    int balance = Integer.parseInt(request.getParameter("abal"));
                    String query = "INSERT INTO account (num,name, balance) VALUES (?, ?, ?)";
                    try (PreparedStatement ps = con.prepareStatement(query)) {
                        ps.setInt(1, num);
                        ps.setString(2, name);
                        ps.setInt(3, balance);
                        int count = ps.executeUpdate();
                        if (count > 0) {
                            pw.print("<h3 align='center'>Account Created Successfully</h3>");
                        } else {
                            pw.print("<h3 align='center'>Record already existed - Try Again</h3>");
                        }
                    }
                } else {
                    pw.print("<h3 align='center'>Database connection failed</h3>");
                }
            } catch (Exception e) {
                pw.print("<h3 align='center'>Error: " + e.getMessage() + "</h3>");
            }
        } else {
            pw.print("<h3>You logged out from the previous session - Please Login</h3>");
            request.getRequestDispatcher("login.html").include(request, response);
        }
    }
}
