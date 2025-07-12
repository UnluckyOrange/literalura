package com.unlucky_orange.Desafio.principal;

import com.unlucky_orange.Desafio.entity.AutorEntity;
import com.unlucky_orange.Desafio.entity.LibroEntity;
import com.unlucky_orange.Desafio.model.Datos;
import com.unlucky_orange.Desafio.model.DatosLibros;
import com.unlucky_orange.Desafio.repository.AutorRepository;
import com.unlucky_orange.Desafio.repository.LibroRepository;
import com.unlucky_orange.Desafio.services.ConsumoAPI;
import com.unlucky_orange.Desafio.services.ConvierteDatos;
import com.unlucky_orange.Desafio.services.GuardadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Scanner;

@Component
public class Principal {

    private static final String URL_BASE = "https://gutendex.com/books/";

    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConvierteDatos conversor = new ConvierteDatos();
    private final Scanner teclado = new Scanner(System.in);

    @Autowired
    private GuardadoService guardadoService;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private AutorRepository autorRepository;

    public void muestraElMenu() {
        int opcion = -1;

        while (opcion != 6) {
            System.out.println("""
                    
                    ====== MENÚ BIBLIOTECA ======
                    1. Buscar libro por título
                    2. Listar libros registrados
                    3. Listar autores registrados
                    4. Listar autores vivos en un año
                    5. Listar libros por idioma
                    6. Salir
                    =============================
                    Elige una opción:
                    """);

            try {
                opcion = Integer.parseInt(teclado.nextLine());
                switch (opcion) {
                    case 1 -> buscarLibroPorTitulo();
                    case 2 -> listarLibrosRegistrados(); // ✅ cambio aquí
                    case 3 -> listarAutoresRegistrados();
                    case 4 -> listarAutoresVivosEnAnio();
                    case 5 -> listarLibrosPorIdioma();
                    case 6 -> System.out.println("👋 Hasta luego.");
                    default -> System.out.println("❌ Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Ingrese un número válido.");
            }
        }
    }

    private void buscarLibroPorTitulo() {
        System.out.println("🔎 Ingrese el nombre del libro:");
        String titulo = teclado.nextLine();

        String json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + titulo.replace(" ", "+"));
        Datos datos = conversor.obtenerDatos(json, Datos.class);

        Optional<DatosLibros> libroBuscado = datos.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(titulo.toUpperCase()))
                .findFirst();

        if (libroBuscado.isPresent()) {
            System.out.println("✅ Libro encontrado:");
            System.out.println(libroBuscado.get());
            guardadoService.guardarLibro(libroBuscado.get());
        } else {
            System.out.println("❌ Libro no encontrado.");
        }
    }

    private void listarLibrosRegistrados() {
        var libros = libroRepository.findAll();
        if (libros.isEmpty()) {
            System.out.println("❌ No hay libros registrados en la base de datos.");
        } else {
            System.out.println("📚 Libros registrados:");
            libros.forEach(libro -> {
                System.out.println("📖 Título: " + libro.getTitulo());
                System.out.println("   Idioma: " + libro.getIdioma());
                System.out.println("   Descargas: " + libro.getNumeroDeDescargas());
                System.out.println("   Ilustrado: " + (libro.isIlustrado() ? "Sí" : "No"));
                System.out.println("   Autores:");
                libro.getAutores().forEach(autor -> System.out.println("     - " + autor.getNombre()));
                System.out.println();
            });
        }
    }

    private void listarAutoresRegistrados() {
        var autores = autorRepository.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
        } else {
            System.out.println("👨‍🏫 Autores registrados:");
            autores.forEach(autor ->
                    System.out.printf("👤 %s (Nac: %s, Muerte: %s)%n",
                            autor.getNombre(),
                            autor.getFechaDeNacimiento(),
                            autor.getFechaDeMuerte()));
        }
    }

    private void listarAutoresVivosEnAnio() {
        System.out.println("📅 Ingrese el año para buscar autores vivos:");
        int anio = Integer.parseInt(teclado.nextLine());

        var autores = autorRepository.findAll().stream()
                .filter(autor -> {
                    try {
                        int nacimiento = Integer.parseInt(autor.getFechaDeNacimiento());
                        String muerteStr = autor.getFechaDeMuerte();
                        boolean estaVivo = muerteStr == null || muerteStr.isBlank() || Integer.parseInt(muerteStr) > anio;
                        return nacimiento <= anio && estaVivo;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .toList();

        if (autores.isEmpty()) {
            System.out.println("No se encontraron autores vivos ese año.");
        } else {
            System.out.println("🟢 Autores vivos en " + anio + ":");
            autores.forEach(autor -> System.out.println("🟢 " + autor.getNombre()));
        }
    }

    private void listarLibrosPorIdioma() {
        System.out.println("🌐 Ingrese el código del idioma (ej. en, es, fr):");
        String idioma = teclado.nextLine();

        var libros = libroRepository.findByIdiomaIgnoreCase(idioma);
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros en ese idioma.");
        } else {
            System.out.println("📘 Libros en idioma '" + idioma + "':");
            libros.forEach(libro -> System.out.println("📘 " + libro.getTitulo()));
        }
    }
}
