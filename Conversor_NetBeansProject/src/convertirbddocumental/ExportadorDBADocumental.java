/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package convertirbddocumental;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import rdb.BaseDatos;

/**
 *
 * @author jmeneses
 */
public class ExportadorDBADocumental {
    
    private BaseDatos bd = new BaseDatos();
    public String rutaExportacion;
    
    public ExportadorDBADocumental(){
        bd.cargarBaseDatos();
    }
    public void escribirColeccion(String nombreColeccion,String coleccion){
        File archivoColeccion = new File(rutaExportacion+"coleccion_"+nombreColeccion+".txt"); 
        try {
            PrintWriter escritorArchivo = new PrintWriter(new FileWriter(archivoColeccion,false));
            escritorArchivo.println(coleccion);
            escritorArchivo.close();
        } catch (IOException ex) {
            System.out.println("Poblemas al escribir coleccion "+nombreColeccion+" "+ex.getMessage());
        }    
    }
    public void exportarTabla(String nombreTabla){
        String coleccion = bd.exportarTabla(nombreTabla);
        escribirColeccion(nombreTabla, coleccion);
    }
    public void exportarUnoAMuchosEmbebido(String nombreTablaPrincipal,String campoTablaPrincipal,String nombreTablaRelacionada,String campoTablaRelacionada,String identificadorTablaRelacionada){
        String coleccion = bd.exportarTablaRelacionada(nombreTablaPrincipal, campoTablaPrincipal, nombreTablaRelacionada, campoTablaRelacionada, identificadorTablaRelacionada);
        escribirColeccion(nombreTablaPrincipal, coleccion);
    }
    public void exportarUnoAUnoEmbebido(String nombreTablaPrincipal,String campoTablaPrincipal,String nombreTablaRelacionada,String campoTablaRelacionada,String identificadorTablaRelacionada){
        //String coleccion = bd.exportarUnionTablas(nombreTablaPrincipal,campoTablaPrincipal,nombreTablaPrincipal,campoTablaRelacionada);
        String coleccion = bd.exportarTablasUnoAUnoEmbebido(nombreTablaPrincipal, campoTablaPrincipal, nombreTablaRelacionada, campoTablaRelacionada, identificadorTablaRelacionada);
        escribirColeccion(nombreTablaPrincipal, coleccion);
    }    
}
