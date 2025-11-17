@echo off
REM --- Limpiar archivos viejos para asegurar que lo que vemos es nuevo ---
del programa.obj
del programa.exe

REM --- Paso 1: Ensamblar (ASM -> OBJ) ---
echo Ensamblando...
C:\masm32\bin\ml /c /Zd /coff programa.asm
IF %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Fallo al ensamblar. Revisa tu codigo ASM.
    pause
    exit /b
)

REM --- Paso 2: Enlazar (OBJ -> EXE) ---
echo Linkeando...
C:\masm32\bin\Link /SUBSYSTEM:CONSOLE programa.obj
IF %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Fallo al linkear. Faltan librerias o hay simbolos duplicados.
    pause
    exit /b
)

REM --- Paso 3: Ejecutar ---
echo.
echo ==========================================
echo EJECUTANDO PROGRAMA:
echo ==========================================
programa.exe
echo.
echo ==========================================
echo Fin de la ejecucion.
pause