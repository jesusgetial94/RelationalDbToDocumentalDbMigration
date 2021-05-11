/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package convertirbddocumental;

import rdb.BaseDatos;

/**
 *
 * @author jmeneses
 */
public class ConvertirBDdocumental {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ExportadorDBADocumental exportadorBase = new ExportadorDBADocumental();
        exportadorBase.rutaExportacion = "C:/Maestria/articulo_bases_datos/taller2/colecciones/";
        exportadorBase.exportarTabla("observatorio");
        exportadorBase.exportarTabla("volcan");
        exportadorBase.exportarTabla("volcan_estacionsismica");
        exportadorBase.exportarUnoAUnoEmbebido("estacion_sismica","ess_codigo","respuesta_estacionsismica","ess_codigo","respuesta");
        exportadorBase.exportarUnoAMuchosEmbebido("sismo","sis_codigo","localizacion","sis_codigo","localizaciones");
        exportadorBase.exportarTabla("estacion_inclinometria");
        exportadorBase.exportarUnoAUnoEmbebido("medida_inclinometro","min_codigo","cambio_tendencia_inclinometro","min_codigo","cambio_tendencia");
        exportadorBase.exportarTabla("estacion_so2");
        exportadorBase.exportarTabla("medida_so2");
    }
}
