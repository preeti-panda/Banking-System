package com.banking;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/NameUpdate")
public class NameUpdate extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter pw = response.getWriter();
        response.setContentType("text/html");
        HttpSession session = request.getSession(false);
        if (session != null) {
            String user = (String) session.getAttribute("name");
            pw.print("<h1 align='center'>Welcome, " + user + " Continue with your transactions</h1>");
            Connection con = null;
            try {
                con = DBConnection.get();
                int num = Integer.parseInt(request.getParameter("anum"));
                String query = "SELECT * FROM account WHERE anum = ?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setInt(1, num);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    request.getRequestDispatcher("/user.html").include(request, response);
                    pw.print("<h3 align='center'>Account Details are</h3>");
                    pw.print("<form action='NameUpdate' method='post'>");
                    pw.print("<table align='center'>"
                            + "<tr> "
                            + "<td> <label> Account Number = </label> </td>"
                            + "<td> <input type='text' readonly name='anum' value='" + rs.getInt("anum") + "' /> </td>"
                            + "</tr>"
                            + "<tr> "
                            + "<td> <label> Account Name = </label> </td>"
                            + "<td> <input type='text' name='aname' value='" + rs.getString("aname") + "' /> </td>"
                            + "</tr>"
                            + "<tr> "
                            + "<td> <label> Balance = </label> </td>"
                            + "<td> <input type='text' readonly name='abal' value='" + rs.getInt("abal") + "' /> </td>"
                            + "</tr>"
                            + "<tr>"
                            + "<td colspan='2'><input type='submit' value='Update'/> </td>"
                            + "</tr>"
                            + "</table>");
                    pw.print("</form>");
                } else {
                    pw.print("<h3 align='center'>No Such Record - Try Again</h3>");
                    request.getRequestDispatcher("/nameupdate.html").include(request, response);
                }
            } catch (Exception e) {
                pw.print("<h3 align='center'>Invalid Account Number - Try Again</h3>");
                request.getRequestDispatcher("/nameupdate.html").include(request, response);
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter pw = response.getWriter();
        response.setContentType("text/html");
        HttpSession session = request.getSession(false);
        if (session != null) {
            String user = (String) session.getAttribute("name");
            pw.print("<h1 align='center'>Welcome, " + user + " Continue with your transactions</h1>");
            Connection con = null;
            try {
                con = DBConnection.get();
                int num = Integer.parseInt(request.getParameter("anum"));
                String name = request.getParameter("aname");
                String query = "UPDATE account SET aname=? WHERE anum=?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, name);
                ps.setInt(2, num);
                int count = ps.executeUpdate();
                if (count > 0) {
                    pw.print("<h3 align='center'>Account Updated Successfully</h3>");
                    RequestDispatcher rd = request.getRequestDispatcher("/user.html");
                    rd.include(request, response);
                } else {
                    pw.print("<h3 align='center'>Failed in updation - Try Again</h3>");
                    request.getRequestDispatcher("/nameupdate.html").include(request, response);
                }
            } catch (Exception e) {
                pw.print("<h3 align='center'>Failed in Updation - Try Again</h3>");
                request.getRequestDispatcher("/nameupdate.html").include(request, response);
            } finally {
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                    }
                }
            }
        } else {
            pw.print("<h3>You logged out from previous Session - Please Login</h3>");
            request.getRequestDispatcher("login.html").include(request, response);
        }
    }
}
