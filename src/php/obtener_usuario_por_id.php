<?php
/**
 * Obtiene el detalle de una meta especificada por
 * su identificador "idMeta"
 */
//echo 'hola que mas ot ';
require 'Usuario.php' ;
//echo 'hola que mas';
if ($_SERVER['REQUEST_METHOD'] == 'GET') {

    if (isset($_GET['idgoogle'])) {

        // Obtener parámetro idgoogle
        $parametro = $_GET['idgoogle'];

        // Tratar retorno
        $retorno = Usuario::getById($parametro);


        if ($retorno) {

            $team["estado"] = "1";
			$team["mensaje"] = "usuario consultado con exito";
            $team["usuario"] = $retorno;
            // Enviar objeto json de la meta
            print json_encode($team);
        } else {
            // Enviar respuesta de error general
            print json_encode(
                array(
                    'estado' => '2',
                    'mensaje' => 'No se obtuvo el registro'
                )
            );
        }

    } else {
        // Enviar respuesta de error
        print json_encode(
            array(
                'estado' => '3',
                'mensaje' => 'Se necesita un identificador'
            )
        );
    }
}
