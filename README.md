# Parcial1

Para correr este proyecto iniclamente ejecute el comando 
```
mvn clean package
```
Luego de esto, para ejecutar la clase main use el siguiente comando desde el directorio "parcial":
```
mvn exec:java
```
Esto gracias a la adición de un plugin a el POM.xml

Así, el puerto del computador quedará habilitado para la recepción de datos (en este caso el puerto 8080).

Seguido a esto, abra desde su Browser:
```
http://localhost:8080/
```