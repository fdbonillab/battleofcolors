<?php
/**
 * Insertar una matrix con la jugada de un usuario en la base de datos
 */

require 'RecordGame.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    // Decodificando formato Json
    $body = json_decode(file_get_contents("php://input"), true);
     print json_encode($body);
	 print json_encode(array('mensaje' => 'En crear_record game'));
    // Insertar meta
    $retorno = Usuario::insert(
        $body['id_user'],
        $body['matrix']);

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
