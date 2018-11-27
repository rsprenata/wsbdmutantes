/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rsprenata.servlet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rsprenata.bean.Mutante;
import com.rsprenata.bean.Usuario;
import com.rsprenata.dao.LoginDao;
import com.rsprenata.dao.MutanteDao;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 *
 * @author rsprenata
 */
@WebServlet(name = "MutanteServlet", urlPatterns = {"/Mutante"})
@MultipartConfig(location = "/",fileSizeThreshold=1024*1024*10, 	// 10 MB 
                 maxFileSize=1024*1024*50,      	// 50 MB
                 maxRequestSize=1024*1024*100)
public class MutanteServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String op = request.getParameter("op");
        
        switch(op) {
            case "cadastrar":
                cadastrar(request, response);
                break;
            case "carregarPor":
                carregarPor(request, response);
                break;
            case "carregarTodos":
                carregarTodos(request, response);
                break;
            case "carregar":
                carregar(request, response);
                break;
            case "excluir":
                excluir(request, response);
                break;
            case "editar":
                editar(request, response);
                break;
        }
    }
    
    public void cadastrar(HttpServletRequest request, HttpServletResponse response) {
        try {
            Mutante mutante = new Mutante();
            mutante.setNome(request.getParameter("nome"));
            mutante.setUsuario(request.getParameter("usuario"));
            List<Part> parts = request.getParts().stream().collect(Collectors.toList());
            Type listType = new TypeToken<List<String>>() {}.getType();
            mutante.setHabilidades(new Gson().fromJson(request.getParameter("habilidades"), listType));
            Part foto = request.getPart("foto");
            
            MutanteDao mDao = new MutanteDao();
            mDao.cadastrar(mutante, foto, getServletContext().getInitParameter("upload.location"));

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write("{\"status\":200, \"message\":\"Sucesso\"}");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            try {
                response.getWriter().write("{\"status\":500, \"message\":\"Erro no servidor\"}");
            } catch (IOException ex) {
                Logger.getLogger(MutanteServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    public void carregarPor(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pesquisa = request.getParameter("pesquisa");
        String por = request.getParameter("por");
        String exato = request.getParameter("exato");
        
        MutanteDao mDao = new MutanteDao();
        List<Mutante> mutantes = mDao.carregarPor(pesquisa, por, exato);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        response.getWriter().write(new Gson().toJson(mutantes));
    }
    
    
    public void carregarTodos(HttpServletRequest request, HttpServletResponse response) throws IOException {
        MutanteDao mDao = new MutanteDao();
        List<Mutante> mutantes = mDao.carregarTodos();
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        response.getWriter().write(new Gson().toJson(mutantes));
    }
    
    
    public void carregar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nome = request.getParameter("nome");
        MutanteDao mDao = new MutanteDao();
        Mutante mutante = mDao.carregar(nome);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        response.getWriter().write(new Gson().toJson(mutante));
    }
    
    
    public void excluir(HttpServletRequest request, HttpServletResponse response) {
        try {
            String nome = request.getParameter("nome");
            MutanteDao mDao = new MutanteDao();
            mDao.excluir(nome, getServletContext().getInitParameter("upload.location"));
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            response.getWriter().write("{\"status\":200, \"message\":\"Sucesso\"}");
        } catch (IOException ex) {
            try {
                response.getWriter().write("{\"status\":500, \"message\":\"Erro no servidor\"}");
            } catch (IOException ex1) {
                Logger.getLogger(MutanteServlet.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(MutanteServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void editar(HttpServletRequest request, HttpServletResponse response) {
        try {
            Mutante mutante = new Mutante();
            mutante.setNome(request.getParameter("nome"));
            mutante.setUsuario(request.getParameter("usuario"));
            List<Part> parts = request.getParts().stream().collect(Collectors.toList());
            Type listType = new TypeToken<List<String>>() {}.getType();
            mutante.setHabilidades(new Gson().fromJson(request.getParameter("habilidades"), listType));
            Part foto = request.getPart("foto");
            String nomeAntigo = request.getParameter("nomeAntigo");
            
            MutanteDao mDao = new MutanteDao();
            mDao.editar(mutante, nomeAntigo, foto, getServletContext().getInitParameter("upload.location"));

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write("{\"status\":200, \"message\":\"Sucesso\"}");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            try {
                response.getWriter().write("{\"status\":500, \"message\":\"Erro no servidor\"}");
            } catch (IOException ex) {
                Logger.getLogger(MutanteServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
