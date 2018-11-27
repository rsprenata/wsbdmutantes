/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rsprenata.bean;

import java.util.List;

/**
 *
 * @author rsprenata
 */
public class Mutante {
    private Integer id;
    private String nome;
    private List<String> habilidades;
    private byte[] imagem;
    private String usuario;

    public Mutante() {}

    public Integer getId() {
            return this.id;
    }

    public void setId(Integer id) {
            this.id = id;
    }

    public String getNome() {
            return this.nome;
    }

    public void setNome(String nome) {
            this.nome = nome;
    }

    public List<String> getHabilidades() {
            return this.habilidades;
    }

    public void setHabilidades(List<String> habilidades) {
            this.habilidades = habilidades;
    }

    public byte[] getImagem() {
            return imagem;
    }

    public void setImagem(byte[] imagem) {
            this.imagem = imagem;
    }

    public String getUsuario() {
            return usuario;
    }

    public void setUsuario(String usuario) {
            this.usuario = usuario;
    }
}
