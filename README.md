## Introducción

Este repositorio contiene el código del Parcial #1 de la materia TDSE de la
Escuela Colombiana de Ingeniería. El trabajo fue realizado por Marianella Polo
Peña bajo la dirección del profesor Daniel Benavides. Es una versión simple y
didáctica pensada para demostrar conceptos vistos en clase.

## Requisitos

- Java 8 o superior
- Apache Maven

## Cómo clonar

```bash
git clone https://github.com/AREP-Polo/Parcial1
cd Parcial1/parcial
```

## Ejecutar servidor

Inicia la aplicación principal (servidor HTTP):

```bash
mvn exec:java -D"exec.mainClass"="com.parcial.app.HttpServer"
```

## Ejecutar facade

Ejecuta la clase facade para pruebas o llamadas alternativas:

```bash
mvn exec:java -D"exec.mainClass"="com.parcial.app.HttpFacade"
```

## Probar

Abre un navegador y visita:

```
http://localhost:8081/
```

La página incluida en `src/resources/index.html` permite interactuar con el
servidor de ejemplo.

## Notas rápidas

- Puedes compilar todo con `mvn package` antes de ejecutar las clases.
- Si el puerto 8081 está ocupado, libera el puerto o modifica la configuración
	en la clase `HttpServer`.

## Estructura mínima

- `src/main/java` — código fuente Java
- `src/resources` — recursos estáticos (HTML)

## Video

Puedes ver una demostración en el siguiente video:

<video src="res/video.mp4" controls width="600">
	Tu navegador no soporta la reproducción de video.
</video>

---

Autor: Marianella Polo Peña

Curso: TDSE — Parcial #1

Profesor: Daniel Benavides

Escuela Colombiana de Ingeniería

