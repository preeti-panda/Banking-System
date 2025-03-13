package com.banking;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/Transfer")
public class Transfer extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter pw = response.getWriter();
        HttpSession session = request.getSession(false);
        if (session != null) {
            response.setContentType("text/html");
            Connection con = null;
            try {
                con = DBConnection.get();
                double amount = Double.parseDouble(request.getParameter("amount"));
                int acc1 = Integer.parseInt(request.getParameter("acc1"));
                int acc2 = Integer.parseInt(request.getParameter("acc2"));
                String query = "UPDATE account SET balance = balance - ? WHERE num = ?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setDouble(1, amount);
                ps.setInt(2, acc1);
                int count1 = ps.executeUpdate();

                query = "UPDATE account SET balance = balance  + ? WHERE num = ?";
                ps = con.prepareStatement(query);
                ps.setDouble(1, amount);
                ps.setInt(2, acc2);
                int count2 = ps.executeUpdate();

                if (count1 > 0 && count2 > 0) {
                    pw.print("<h3 align='center'>Amount transferred</h3>");
                    RequestDispatcher rd = request.getRequestDispatcher("/user.html");
                    rd.include(request, response);
                } else {
                    pw.print("<h3 align='center'>Amount not transferred</h3>");
                    RequestDispatcher rd = request.getRequestDispatcher("/transfer.html");
                    rd.include(request, response);
                }
            } catch (Exception e) {
                pw.print("<h3 align='center'>-" + e + "</h3>");
                request.getRequestDispatcher("/transfer.html").include(request, response);
            } finally {
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                    }
                }
            }
        }
    }
}
