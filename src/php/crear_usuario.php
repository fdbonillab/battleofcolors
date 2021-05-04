<?php
/**
 * Insertar un nuevo usuario en la base de datos
 */

require 'Usuario.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    // Decodificando formato Json
    $body = json_decode(file_get_contents("php://input"), true);
     print json_encode($body);
	 print json_encode(array('mensaje' => 'En crear_usuario'));
    // Insertar meta
    $retorno = Usuario::insert(
        $body['idgoogle'],
        $body['pasos'],
        $body['distance']);

    if ($retorno) {
        // Código de éxito
        print json_encode(
            array(
                'estado' => '1',
                'mensaje' => 'Creación exitosa')
        );
    } else {
        // Código de falla
        print json_encode(
            array(
                'estado' => '2',
                'mensaje' => 'Creación fallida')
        );
    }
}
