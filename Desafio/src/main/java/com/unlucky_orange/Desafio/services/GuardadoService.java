package com.unlucky_orange.Desafio.services;

import com.unlucky_orange.Desafio.entity.AutorEntity;
import com.unlucky_orange.Desafio.entity.LibroEntity;
import com.unlucky_orange.Desafio.model.DatosLibros;
import com.unlucky_orange.Desafio.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuardadoService {

    @Autowired
    private LibroRepository libroRepository;

    public void guardarLibro(DatosLibros datosLibro) {
        // Verificar si el libro ya está en la base de datos (por título exacto)
        boolean existe = libroRepository.findByTituloContainingIgnoreCase(datosLibro.titulo()).stream()
                .anyMatch(libro -> libro.getTitulo().equalsIgnoreCase(datosLibro.titulo()));

        if (existe) {
            System.out.println("⚠️ El libro ya existe en la base de datos. No se guardará otra vez.");
            return;
        }

        // Crear y poblar la entidad Libro
        LibroEntity libro = new LibroEntity();
        libro.setTitulo(datosLibro.titulo());
        libro.setNumeroDeDescargas(datosLibro.numeroDeDescargas());
        libro.setIdioma(datosLibro.idiomas().isEmpty() ? "desconocido" : datosLibro.idiomas().get(0));
        libro.setIlustrado(false); // Podrías mejorar esta lógica si hay forma de detectarlo

        // Convertir autores
        List<AutorEntity> autores = datosLibro.autor().stream().map(a -> {
            AutorEntity autor = new AutorEntity();
            autor.setNombre(a.Nombre());
            autor.setFechaDeNacimiento(a.fechaDeNacimiento());
            autor.setFechaDeMuerte(a.fechaDeMuerte());
            autor.setLibros(List.of(libro)); // Relación inversa
            return autor;
        }).collect(Collectors.toList());

        // Asociar autores al libro
        libro.setAutores(autores);

        // Guardar libro y autores
        libroRepository.save(libro);

        // Confirmación
        System.out.println("✅ Libro guardado exitosamente en la base de datos:");
        System.out.println("📚 Título: " + libro.getTitulo());
        System.out.println("👥 Autores:");
        libro.getAutores().forEach(a -> System.out.println("   - " + a.getNombre()));
    }
}
