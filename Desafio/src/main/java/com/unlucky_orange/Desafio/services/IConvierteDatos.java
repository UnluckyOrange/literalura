package com.unlucky_orange.Desafio.services;


public interface IConvierteDatos {
    <T> T obtenerDatos(String json,Class<T> clase);
}
