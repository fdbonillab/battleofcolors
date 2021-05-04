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
    public static function getById($id_user)
    {
        // Consulta de la meta
        $consulta = "SELECT id_user,
                             matrix
                             FROM record_game
                             WHERE id_user = ?";

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
        $id_user,
        $distance
    )
    {
        // Creando consulta UPDATE
        $consulta = "UPDATE record_game " .
            " SET matrix=? " .
            " WHERE id_user=? ";
        try{
			// Preparar la sentencia
			$cmd = Database::getInstance()->getDb()->prepare($consulta);

			// Relacionar y ejecutar la sentencia
			$cmd->execute(array($matrix, $id_user));

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
        $id_user,
        $matrix
    )
    {
        // Sentencia INSERT
		print json_encode(array('mensaje' => 'En insert record_game'));
        $comando = "INSERT INTO record_game( " .
            "id_user," .
            " matrix ) " .
            " VALUES( ?,?)";

        // Preparar la sentencia
        $sentencia = Database::getInstance()->getDb()->prepare($comando);

		try {
			 return $sentencia->execute(
            array(
                $id_user,
                $matrix
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
    public static function delete($id_user)
    {
        // Sentencia DELETE
        $comando = "DELETE FROM record_game WHERE id_user=?";

        // Preparar la sentencia
        $sentencia = Database::getInstance()->getDb()->prepare($comando);

        return $sentencia->execute(array($id_user));
    }
}

?>
