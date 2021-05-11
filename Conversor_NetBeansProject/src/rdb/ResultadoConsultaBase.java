/*
 * Servicio Geologico Colombiano
 * Observatorio Sismologico y Vulcanologico de Pasto
 */
package rdb;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 *
 * @author jmeneses
 */
public class ResultadoConsultaBase {
    public boolean consultaRealizada = false;
    public ResultSet resultado = null;
    public ResultSetMetaData MetaDatoResultado = null;
    public int nr=0;

    public ResultadoConsultaBase(boolean consultaRealizada, ResultSet resultSet,
            ResultSetMetaData resultSetMetaData, int nr)
    {
        this.consultaRealizada = consultaRealizada;
        this.resultado = resultSet;
        this.MetaDatoResultado = resultSetMetaData;
        this.nr = nr;
    }
    
    public ResultadoConsultaBase() {}
}
