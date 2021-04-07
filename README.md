# BlockIfDrunk
Block If Drunk App
App Movil desarrollada para Android con Java, Firebase y Google Geolocation API, que reconoce y actua en base a los niveles de alcohol del usuario a traves de un prototipo 
de un carro hecho en arduino que cuenta con un sensor mq3 conectado a la aplicacion mediante bluetooth. Si el prototipo detecta que el usuario supera los limites
de alcohol permitidos para manejar no le deja conducir el carro y se le envia un sms con la ubicacion actual a un contacto previamente designado en la pantalla de registro
de usuario. Ademas cuenta con un background service que nos permite conocer la aplicacion que esta usando el usuario y si la aplicacion es la contactos la cierra
, esto para la implementacion de una funcion que permite al usuario llamar dentro de la aplicacion y bloquear contactos, evitando asi con este background service 
que el usuario burle esta funcion.
