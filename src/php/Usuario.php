<?php

/**
 * Representa el la estructura de las metas
 * almacenadas en la base de datos
 */
require 'Database.php';

class RecordGame
{
    function __construct()
    {
    }

    /**
     * Retorna en la fila especificada de la tabla 'meta'
     *
     * @param $idMeta Identificador del registro
     * @return array Datos del registro
     */
    public static function getAll()
    {
        $consulta = "SELECT * FROM record_game";
        try {
            // Preparar sentencia
            $comando = Database::getInstance()->getDb()->prepare($consulta);
            // Ejecutar sentencia preparada
            $comando->execute();

            return $comando->fetchAll(PDO::FETCH_ASSOC);

        } catch (PDOException $e) {
            echo 'Error',$e->getMessage(),"kk";
            return false;
        }
    }

    /**
     * Obtiene los campos de una meta con un identificador
     * determinado
     *
     * @param $idMeta Identificador de la meta
     * @return mixed
     */
    public static function getById($idgoogle)
    {
        // Consulta de la meta
        $consulta = "SELECT idgoogle,
                             pasos,
                             distance
                             FROM usuario
                             WHERE idgoogle = ?";

        try {
            // Preparar sentencia
            $comando = Database::getInstance()->getDb()->prepare($consulta);
            // Ejecutar sentencia preparada
            $comando->execute(array($idgoogle));
            // Capturar primera fila del resultado
            $row = $comando->fetch(PDO::FETCH_ASSOC);
            return $row;

        } catch (PDOException $e) {
            // Aquí puedes clasificar el error dependiendo de la excepción
            // para presentarlo en la respuesta Json
            return -1;
        }
    }

    /**
     * Obtiene la suma de las distancias de los usuarios asociados a un equipo o team
     *
     * @param $idTeam Identificador del team
     * @return mixed
     */
    public static function getSumDistanceByTeam($idTeam)
    {
        // Consulta de la meta
        $consulta = "SELECT t.idTeam, sum(u.distance) distance FROM usuario_team ut,
		usuario u, team t where u.idgoogle = ut.id_user and ut.id_team = t.idTeam and t.idTeam =  ?";

        try {
            // Preparar sentencia
            $comando = Database::getInstance()->getDb()->prepare($consulta);
            // Ejecutar sentencia preparada
            $comando->execute(array($idTeam));
            // Capturar primera fila del resultado
            $row = $comando->fetch(PDO::FETCH_ASSOC);
            return $row;

        } catch (PDOException $e) {
            // Aquí puedes clasificar el error dependiendo de la excepción
            // para presentarlo en la respuesta Json
			print json_encode(array('mensaje' => $e->getMessage()));
			echo 'Excepción capturada: ',  $e->getMessage(), "\n";
            return -1;
        }
    }

    /**
     * Actualiza un registro de la bases de datos basado
     * en los nuevos valores relacionados con un identificador
     *
     * @param $idMeta      identificador
     * @param $titulo      nuevo titulo
     * @param $descripcion nueva descripcion
     * @param $fechaLim    nueva fecha limite de cumplimiento
     * @param $categoria   nueva categoria
     * @param $prioridad   nueva prioridad
     */
    public static function update(
        $idgoogle,
        $distance
    )
    {
        // Creando consulta UPDATE
        $consulta = "UPDATE usuario " .
            " SET distance=? " .
            " WHERE idgoogle=? ";
        try{
			// Preparar la sentencia
			$cmd = Database::getInstance()->getDb()->prepare($consulta);

			// Relacionar y ejecutar la sentencia
			$cmd->execute(array($distance, $idgoogle));

			return $cmd;
		} catch (Exception $e) {
			print json_encode(array('mensaje' => $e->getMessage()));
			echo 'Excepción capturada: ',  $e->getMessage(), "\n";
		}
    }

    /**
     * Insertar una nueva meta
     *
     * @param $titulo      titulo del nuevo registro
     * @param $descripcion descripción del nuevo registro
     * @param $fechaLim    fecha limite del nuevo registro
     * @param $categoria   categoria del nuevo registro
     * @param $prioridad   prioridad del nuevo registro
     * @return PDOStatement
     */
    public static function insert(
        $idgoogle,
        $pasos,
        $distance
    )
    {
        // Sentencia INSERT
		print json_encode(array('mensaje' => 'En insert_usuario'));
        $comando = "INSERT INTO usuario( " .
            "idgoogle," .
            " pasos," .
            "  distance) " .
            " VALUES( ?,?,?)";

        // Preparar la sentencia
        $sentencia = Database::getInstance()->getDb()->prepare($comando);

		try {
			 return $sentencia->execute(
            array(
                $idgoogle,
                $pasos,
                $distance
            )
        );
		} catch (Exception $e) {
			print json_encode(array('mensaje' => $e->getMessage()));
			echo 'Excepción capturada: ',  $e->getMessage(), "\n";
		}

    }

    /**
     * Eliminar el registro con el identificador especificado
     *
     * @param $idMeta identificador de la meta
     * @return bool Respuesta de la eliminación
     */
    public static function delete($idTeam)
    {
        // Sentencia DELETE
        $comando = "DELETE FROM usuario WHERE idgoogle=?";

        // Preparar la sentencia
        $sentencia = Database::getInstance()->getDb()->prepare($comando);

        return $sentencia->execute(array($idgoogle));
    }
}

?>
