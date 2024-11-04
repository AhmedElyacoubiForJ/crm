# Dokumentation: Bereitstellung einer Spring Boot-Anwendung

## Übersicht

> Diese Dokumentation beschreibt, wie eine Spring Boot-Anwendung auf verschiedenen kostenlosen Hosting-Plattformen veröffentlicht werden kann.
> Die Beispiele umfassen Heroku, AWS Elastic Beanstalk, Google App Engine und Azure App Service.

## Vorbereitung
> Bevor du mit der Bereitstellung beginnst, stelle sicher, dass dein Projekt die folgenden Anforderungen erfüllt:
   - Eine pom.xml-Datei, die die benötigten Abhängigkeiten enthält.
   - Eine Procfile oder app.yaml-Datei, um die Startbefehle zu definieren.
   - Ein gültiges Konto auf der gewählten Hosting-Plattform.

## Heroku
### Schritte
1. **Registrierung und Einrichtung**:
   - Registriere dich bei [Heroku](https://www.heroku.com/) und installiere das [Heroku CLI](https://devcenter.heroku.com/articles/heroku-cli).
2. **Projekt vorbereiten**:
   - Erstelle eine `Procfile` im Hauptverzeichnis deines Projekts:
      ```Plaintext
        web: java -jar target/crm.jar
      ```
3. **Deployment**:
   - Melde dich bei Heroku an:
     ```Sh
        heroku login
      ```
   - Erstelle eine neue Heroku-App:
      ```Sh
        git push heroku main
      ```
## AWS Elastic Beanstalk
### Schritte:
1. **Registrierung und Einrichtung:**:
   - Registriere dich bei [AWS](https://aws.amazon.com/de/) und installiere die [AWS CLI](https://aws.amazon.com/de/cli/).
2. **Anwendung erstellen**:
   - Erstelle eine Anwendung in der Elastic Beanstalk-Konsole.
3. **Deployment**:
   - Konfiguriere dein Projekt für Elastic Beanstalk und deploye es:
      ```Sh
        eb init -p java-17 deine-app
        eb create deine-app-env
        eb deploy
      ```
## Google App Engine
### Schritte:
1. **Registrierung und Einrichtung**:
   - Registriere dich bei [Google Cloud](https://cloud.google.com/) und installiere das [Google Cloud SDK](https://cloud.google.com/).
2. **Projekt vorbereiten**:
   - Erstelle eine `app.yaml` im Hauptverzeichnis deines Projekts:
      ```Yaml
        runtime: java
      ```
3. **Deployment**:
   - Deploy deine Anwendung:
      ```Sh
        gcloud app deploy
      ```
## Azure App Service
### Schritte:
1. **Registrierung und Einrichtung:**:
   - Registriere dich bei [Microsoft Azure](https://azure.microsoft.com/de-de/) und installiere die [Azure CLI](https://learn.microsoft.com/en-us/cli/azure/install-azure-cli).
2. **Anwendung erstellen**:
   - Erstelle eine Web-App in der Azure App Service-Konsole.
3. **Deployment**:
   - Deploy deine Anwendung mit der Azure CLI:
      ```Sh
        az login
        az webapp up --name deine-app --resource-group deine-resource-group --runtime "JAVA|17-java"
      ```