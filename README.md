# Anleitung zum Starten des Servers und Clients

1. Laden Sie den Server und den Client herunter.
2. Starten Sie zuerst den Server. Dazu kann es erforderlich sein, den Port 3141 durch eine eingehende Firewall-Regel freizugeben.
3. Nach erfolgreicher Initialisierung liefert der Server eine Bestätigung zurück.
   
   ![image](https://github.com/JulHirsch/ChatApp/assets/69107704/584e3137-c182-4f04-9b31-9f7d2b97cd2b)
    
4. Starten Sie nun einen Client. Beachten Sie, dass pro Gerät (oder IP-Adresse) nur ein Client gestartet werden kann. Ein Client kann auf der gleichen IP wie der Server laufen.
5. Geben Sie die IP-Adresse des Servers ein.
    
   ![image](https://github.com/JulHirsch/ChatApp/assets/69107704/3890b191-2be5-418e-affd-e731d26f59e7)

6. Tragen Sie einen Namen ein, der für andere Clients sichtbar sein wird.
    
   ![image](https://github.com/JulHirsch/ChatApp/assets/69107704/2647f92a-c7a9-41a3-954f-47eb14af58fa)
    
7. Die Chat-Oberfläche sollte sich öffnen (dies sollte ohne Verzögerung passieren). Falls das Fenster weiß bleibt, versucht der Client noch, den Server zu finden und wird nach einer gewissen Zeit eine Fehlermeldung zurückgeben, falls dies nicht erfolgreich war.
    
    ![image](https://github.com/JulHirsch/ChatApp/assets/69107704/4fac448b-003e-478d-8ad7-f712ef557e46)
    
8. Zuerst ist der globale Chat zu sehen. Eine Nachricht kann in diesen geschickt werden, indem sie unten ins Feld eingegeben und danach Enter gedrückt wird.

    ![image](https://github.com/JulHirsch/ChatApp/assets/69107704/65252a71-6001-497d-9e17-6881d34df332)

9. Über das Plus unten rechts oder 'Options' oben links kann ein neuer privater Chat erstellt werden.
10. Geben Sie zuerst die IP des Clients ein, mit dem Sie kommunizieren möchten.
    
    ![image](https://github.com/JulHirsch/ChatApp/assets/69107704/6ef40029-9710-45c3-8ef0-730cbca01dfc)

11. Wählen Sie dann die Verschlüsselung aus, entweder 'None' (Nachrichten werden unverschlüsselt an den jeweiligen anderen geschickt), 'Caesar' (es muss eingeben werden, um wie viel die Buchstaben verschoben werden sollen, dieses Feld erscheint auch das erste Mal, wenn man von einem neuen Client eine mit Caesar verschlüsselte Nachricht erhält) oder 'RSA' (der Schlüsselaustausch funktioniert automatisch und man kann wie gewohnt chatten. Beachten Sie, dass die Verschlüsselung mit RSA nur zwischen zwei Clients möglich ist, es funktioniert nicht, einen privaten Chat mit sich selbst zu starten und mit RSA zu sichern).
    
    ![image](https://github.com/JulHirsch/ChatApp/assets/69107704/185979ec-13f2-4f13-bf41-c5ed7ee0c135)

12. Wenn der angeschriebene Client nicht online ist, kommt eine Antwort vom Server.
    
    ![image](https://github.com/JulHirsch/ChatApp/assets/69107704/ba2cd52c-fcb6-4876-9deb-6415e5dfaca4)

13. Wenn Sie eine Nachricht empfangen, wird bei Bedarf oben automatisch ein neuer Tab hinzugefügt. Wenn Sie auf diesen klicken, können Sie wie gewohnt mit dieser Person chatten.
