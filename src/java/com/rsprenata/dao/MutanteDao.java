package com.rsprenata.dao;

import com.rsprenata.bean.Mutante;
import com.rsprenata.bean.Usuario;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.Part;
import sun.misc.IOUtils;

public class MutanteDao {
    public void cadastrar(Mutante mutante, Part foto, String applicationPath) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        Connection connection = connectionFactory.getConnection();
        PreparedStatement stmt = null;
        File uploadFolder = null;
        try {
            connection.setAutoCommit(false);
            stmt = connection.prepareStatement("INSERT INTO Mutante (nome, usuario) VALUES (?, ?)");
            stmt.setString(1, mutante.getNome());
            stmt.setString(2, mutante.getUsuario());
            stmt.executeUpdate();

            // constructs path of the directory to save uploaded file
            String uploadFilePath = applicationPath + File.separator + mutante.getNome();
            // creates upload folder if it does not exists
            uploadFolder = new File(uploadFilePath);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            // write all files in upload folder
            String fileName = foto.getSubmittedFileName();
            String contentType = foto.getContentType();
            
            stmt = connection.prepareStatement("UPDATE Mutante SET foto_path = ? WHERE nome = ?");
            stmt.setString(1, uploadFilePath + File.separator + fileName);
            stmt.setString(2, mutante.getNome());
            stmt.executeUpdate();

            foto.write(uploadFilePath + File.separator + fileName);
            
            for (String s : mutante.getHabilidades()) {
                stmt = connection.prepareStatement("INSERT INTO Habilidades (mutante_nome, habilidade) VALUES (?, ?)");
                stmt.setString(1, mutante.getNome());
                stmt.setString(2, s);
                stmt.executeUpdate();
            }
            connection.commit();
        } catch (Exception exception) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                Logger.getLogger(MutanteDao.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (uploadFolder != null) {
                uploadFolder.delete();
            }
            throw new RuntimeException("Erro. Origem="+exception.getMessage());
        } finally {
            if (stmt != null)
                try { stmt.close(); }
                catch (SQLException exception) { System.out.println("Erro ao fechar stmt. Ex="+exception.getMessage()); }
            if (connection != null)
                try { connection.close(); }
                catch (SQLException exception) { System.out.println("Erro ao fechar conexão. Ex="+exception.getMessage()); }
        }
    }
    
    public void editar(Mutante mutante, String nomeAntigo, Part foto, String applicationPath) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        Connection connection = connectionFactory.getConnection();
        PreparedStatement stmt = null;
        File uploadFolder = null;
        try {
            connection.setAutoCommit(false);

            stmt = connection.prepareStatement("DELETE FROM Habilidades WHERE mutante_nome = ?");
            stmt.setString(1, nomeAntigo);
            stmt.executeUpdate();

            stmt = connection.prepareStatement("UPDATE Mutante SET nome = ?, usuario = ? WHERE nome = ?");
            stmt.setString(1, mutante.getNome());
            stmt.setString(2, mutante.getUsuario());
            stmt.setString(3, nomeAntigo);
            stmt.executeUpdate();
            
            //apaga pasta nome antigo
            String uploadFilePath = applicationPath + File.separator + nomeAntigo;
            uploadFolder = new File(uploadFilePath);
            if (uploadFolder.exists()) {
                uploadFolder.delete();
            }
            

            // constructs path of the directory to save uploaded file
            uploadFilePath = applicationPath + File.separator + mutante.getNome();
            // creates upload folder if it does not exists
            uploadFolder = new File(uploadFilePath);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            // write all files in upload folder
            String fileName = foto.getSubmittedFileName();
            String contentType = foto.getContentType();
            
            stmt = connection.prepareStatement("UPDATE Mutante SET foto_path = ? WHERE nome = ?");
            stmt.setString(1, uploadFilePath + File.separator + fileName);
            stmt.setString(2, mutante.getNome());
            stmt.executeUpdate();

            foto.write(uploadFilePath + File.separator + fileName);
            
            for (String s : mutante.getHabilidades()) {
                stmt = connection.prepareStatement("INSERT INTO Habilidades (mutante_nome, habilidade) VALUES (?, ?)");
                stmt.setString(1, mutante.getNome());
                stmt.setString(2, s);
                stmt.executeUpdate();
            }

            connection.commit();
        } catch (Exception exception) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                Logger.getLogger(MutanteDao.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (uploadFolder != null) {
                uploadFolder.delete();
            }
            throw new RuntimeException("Erro. Origem="+exception.getMessage());
        } finally {
            if (stmt != null)
                try { stmt.close(); }
                catch (SQLException exception) { System.out.println("Erro ao fechar stmt. Ex="+exception.getMessage()); }
            if (connection != null)
                try { connection.close(); }
                catch (SQLException exception) { System.out.println("Erro ao fechar conexão. Ex="+exception.getMessage()); }
        }
    }
    
    public void excluir(String nome, String applicationPath) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        Connection connection = connectionFactory.getConnection();
        PreparedStatement stmt = null;
        try {
            connection.setAutoCommit(false);
            
            String uploadFilePath = applicationPath + File.separator + nome;
            File uploadFolder = new File(uploadFilePath);
            if (uploadFolder.exists()) {
                uploadFolder.delete();
            }

            stmt = connection.prepareStatement("DELETE FROM Habilidades WHERE mutante_nome = ?");
            stmt.setString(1, nome);
            stmt.executeUpdate();

            stmt = connection.prepareStatement("DELETE FROM Mutante WHERE nome = ?");
            stmt.setString(1, nome);
            stmt.executeUpdate();

            connection.commit();
        } catch (Exception exception) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                Logger.getLogger(MutanteDao.class.getName()).log(Level.SEVERE, null, ex);
            }
            throw new RuntimeException("Erro. Origem="+exception.getMessage());
        } finally {
            if (stmt != null)
                try { stmt.close(); }
                catch (SQLException exception) { System.out.println("Erro ao fechar stmt. Ex="+exception.getMessage()); }
            if (connection != null)
                try { connection.close(); }
                catch (SQLException exception) { System.out.println("Erro ao fechar conexão. Ex="+exception.getMessage()); }
        }
    }
    
    public List<Mutante> carregarPor(String pesquisa, String por, String exato) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        Connection connection = connectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Mutante> mutantes = new ArrayList<Mutante>();
        
        try {
            if ("nome".equals(por))
                stmt = connection.prepareStatement("SELECT DISTINCT nome FROM Mutante WHERE nome LIKE ?");
            else
                stmt = connection.prepareStatement("SELECT DISTINCT m.nome FROM Mutante m JOIN Habilidades h ON h.mutante_nome = m.nome WHERE h.habilidade LIKE ?");
            
            if ("true".equals(exato))
                stmt.setString(1, pesquisa);
            else
                stmt.setString(1, "%"+pesquisa+"%");
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Mutante mutante = new Mutante();
                mutante.setNome(rs.getString("nome"));
                mutantes.add(mutante);
            }
        } catch (Exception exception) {
            throw new RuntimeException("Erro. Origem="+exception.getMessage());
        } finally {
            if (rs != null)
                try { rs.close(); }
                catch (SQLException exception) { System.out.println("Erro ao fechar rs. Ex="+exception.getMessage()); }
            if (stmt != null)
                try { stmt.close(); }
                catch (SQLException exception) { System.out.println("Erro ao fechar stmt. Ex="+exception.getMessage()); }
            if (connection != null)
                try { connection.close(); }
                catch (SQLException exception) { System.out.println("Erro ao fechar conexão. Ex="+exception.getMessage()); }
        }
        
        return mutantes;
    }
    
    public List<Mutante> carregarTodos() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        Connection connection = connectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Mutante> mutantes = new ArrayList<Mutante>();
        
        try {
            stmt = connection.prepareStatement("SELECT * FROM Mutante");
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Mutante mutante = new Mutante();
                mutante.setNome(rs.getString("nome"));
                File f = new File(rs.getString("foto_path"));
                mutante.setImagem(Files.readAllBytes(new File(rs.getString("foto_path")).toPath()));
                mutantes.add(mutante);
            }
        } catch (Exception exception) {
            throw new RuntimeException("Erro. Origem="+exception.getMessage());
        } finally {
            if (rs != null)
                try { rs.close(); }
                catch (SQLException exception) { System.out.println("Erro ao fechar rs. Ex="+exception.getMessage()); }
            if (stmt != null)
                try { stmt.close(); }
                catch (SQLException exception) { System.out.println("Erro ao fechar stmt. Ex="+exception.getMessage()); }
            if (connection != null)
                try { connection.close(); }
                catch (SQLException exception) { System.out.println("Erro ao fechar conexão. Ex="+exception.getMessage()); }
        }
        
        return mutantes;
    }
    
    public Mutante carregar(String nome) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        Connection connection = connectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Mutante mutante = new Mutante();
        
        try {
            stmt = connection.prepareStatement("SELECT * FROM Mutante WHERE nome = ?");
            stmt.setString(1, nome);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                mutante.setNome(rs.getString("nome"));
                File f = new File(rs.getString("foto_path"));
                mutante.setImagem(Files.readAllBytes(new File(rs.getString("foto_path")).toPath()));
                mutante.setUsuario(rs.getString("usuario"));
                
                stmt = connection.prepareStatement("SELECT * FROM Habilidades WHERE mutante_nome = ?");
                stmt.setString(1, nome);
                rs = stmt.executeQuery();
                
                List<String> habilidades = new ArrayList<String>();
                while (rs.next()) {
                    habilidades.add(rs.getString("habilidade"));
                }
                mutante.setHabilidades(habilidades);
            }
        } catch (Exception exception) {
            throw new RuntimeException("Erro. Origem="+exception.getMessage());
        } finally {
            if (rs != null)
                try { rs.close(); }
                catch (SQLException exception) { System.out.println("Erro ao fechar rs. Ex="+exception.getMessage()); }
            if (stmt != null)
                try { stmt.close(); }
                catch (SQLException exception) { System.out.println("Erro ao fechar stmt. Ex="+exception.getMessage()); }
            if (connection != null)
                try { connection.close(); }
                catch (SQLException exception) { System.out.println("Erro ao fechar conexão. Ex="+exception.getMessage()); }
        }
        
        return mutante;
    }
}
