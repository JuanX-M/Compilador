# Compilador - Diseño de Compiladores (2025)

Este proyecto consiste en el desarrollo de un compilador de un lenguaje de programación propio, capaz de traducir código de alto nivel a lenguaje ensamblador x86 (Pentium) ejecutable mediante **MASM32**. El desarrollo cubre todas las etapas del proceso de compilación, desde el análisis léxico hasta la generación de código objeto.

Trabajo grupal realizado para la materia **Diseño de Compiladores** (4to año) de la carrera **Ingeniería de Sistemas**, **UNICEN**.

## Integrantes
* **Juan Cruz Muñoz**
* **Nicolás Ortiz**
* **Lautaro García**

---

## Características Principales del Lenguaje
El compilador está diseñado para manejar un lenguaje con las siguientes capacidades técnicas:
* **Tipos de datos:** Soporte para `INT` (enteros de 16 bits) y `FLOAT` (punto flotante de 32 bits bajo estándar IEEE 754).
* **Estructuras de Control:** Sentencias `IF-ELSE` y ciclos `FOR` con rangos definidos e incremento/decremento automático.
* **Funciones:**
    * Declaración e invocación con gestión de ámbitos (scopes) anidados.
    * Pasaje de parámetros con semántica de **Copia Valor (CV)** y **Copia Resultado (CR)**.
    * Soporte para **Retorno Múltiple**.
* **Expresiones Lambda:** Definición de funciones anónimas en línea.
* **Comentarios:** Soporte para comentarios multilínea delimitados por `## ... ##`.

---

## Arquitectura del Sistema

### 1. Análisis Léxico (TP1)
Implementado mediante un Autómata Finito Determinista (AFD) basado en una matriz de transiciones y acciones semánticas.
* **Componentes:** `AnalizadorLexico.java`, `MatrizTransicion.java`.
* **Funcionalidad:** Tokenización, validación de rangos de constantes y manejo de errores léxicos mediante un sistema de Logger centralizado.

### 2. Análisis Sintáctico y Semántico (TP2 y TP3)
Utiliza **BYACC/J** para procesar la gramática y validar la estructura semántica del programa.
* **Código Intermedio:** Generación de **Tercetos** almacenados en una estructura dinámica.
* **Control Semántico:** Chequeo de tipos, verificación de declaraciones previas y gestión de visibilidad de variables mediante búsqueda ascendente en el árbol de ámbitos.

### 3. Generación de Código Assembler (TP4)
Traducción directa de los tercetos a instrucciones de bajo nivel para procesadores Pentium.
* **Aritmética:** Uso de registros de 16 bits (`AX`, `CX`) para enteros y el **coprocesador matemático 80X87** para operaciones de punto flotante.
* **Gestión de Memoria:** Implementación mediante el mecanismo de variables auxiliares para resultados intermedios.

---

## Controles en Tiempo de Ejecución (Grupo 1)
El compilador incluye rutinas de seguridad que detienen la ejecución ante errores críticos:
1.  **División por cero:** Se verifica tanto para datos enteros como de punto flotante antes de realizar la operación.
2.  **Overflow en productos Float:** Detección de desbordamiento mediante el registro de estado del coprocesador tras operaciones `FMUL`.
3.  **Pérdida de información (TOI):** Durante la conversión de `FLOAT` a `INT`, se chequea si el valor original posee decimales que se perderían mediante una comparación de reversa.

---

## Cómo Ejecutar
1.  **Compilación del Proyecto:**
    Se puede utilizar el archivo `make.bat` incluido para compilar el código Java y generar el parser.
    ```bash
    ./make.bat
    ```
2.  **Ejecución del Compilador:**
    Inicie el JAR del compilador e indique el archivo de prueba deseado (debe estar en la carpeta `/data`).
3.  **Ensamblado:**
    El archivo generado `programa.asm` puede ser procesado con MASM32 para obtener el ejecutable final.

---
*Este proyecto cumple con los lineamientos del Trabajo Práctico Especial de la cursada 2025.*
