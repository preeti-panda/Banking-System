package com.banking;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/Deposit")
public class Deposit extends HttpServlet {
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
                    int num = Integer.parseInt(request.getParameter("anum").trim());
                    int amt = Integer.parseInt(request.getParameter("amt").trim());
                    String query = "SELECT * FROM account WHERE anum=?";
                    try (PreparedStatement ps = con.prepareStatement(query)) {
                        ps.setInt(1, num);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            query = "UPDATE account SET abal = abal + ? WHERE anum=?";
                            try (PreparedStatement psUpdate = con.prepareStatement(query)) {
                                psUpdate.setInt(1, amt);
                                psUpdate.setInt(2, num);
                                psUpdate.executeUpdate();
                                pw.print("<h3 align='center'>Deposit Successful</h3>");
                                RequestDispatcher rd = request.getRequestDispatcher("/user.html");
                                rd.include(request, response);
                            }
                        } else {
                            pw.print("<h3 align='center'>Invalid Account Number Given</h3>");
                            RequestDispatcher rd = request.getRequestDispatcher("/deposit.html");
                            rd.include(request, response);
                        }
                    }
                } else {
                    pw.print("<h3 align='center'>Database connection failed</h3>");
                }
            } catch (Exception e) {
                pw.print("<h3 align='center'>Invalid Account Number Given - Try Again</h3>");
                request.getRequestDispatcher("/deposit.html").include(request, response);
            }
        } else {
            pw.print("<h3>You logged out from previous Session - Please Login</h3>");
            request.getRequestDispatcher("login.html").include(request, response);
        }
    }
}
