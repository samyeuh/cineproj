@echo off
title Démarrage projet React TS

echo =============================
echo   Initialisation du projet
echo =============================

echo.
echo [1/2] Installation des dépendances...
call npm install
if %errorlevel% neq 0 (
    echo.
    echo ! ERREUR lors de npm install.
)

echo.
echo [2/2] Lancement du serveur de développement...
echo (Tu verras l'URL ici s'il démarre bien)

call npm run dev

echo.
echo =============================
echo Le processus s'est terminé.
echo Appuie sur une touche pour quitter...
pause >nul