/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdb;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jmeneses
 */
public class BaseDatos {
    LinkedList<Tabla> tablas = new LinkedList();
    String base = "rdb";
    ManejadorBases mb = ManejadorBases.getSingletonInstance("rdb");
    private Tabla buscarTabla(String nombreTabla){
        Tabla tablaEncontrada = null;
        for(int i=0;i<tablas.size();i++){
            if(tablas.get(i).nombreTabla.equals(nombreTabla)){
                System.out.println("Encontre tabla");
                tablaEncontrada = tablas.get(i);
                break;
            }
        }
        return tablaEncontrada;
    }
    public String exportarTablaRelacionada(String nombreTablaPrincipal,String campoTablaPrincipal,String nombreTablaRelacionada,String campoTablaRelacionada,String identificadorTablaRelacionada){
       Tabla tablaPrincipal = buscarTabla(nombreTablaPrincipal);
        System.out.println("Tabla principal "+tablaPrincipal.nombreTabla);
       ColumnaTabla columnaTablaPrincipal = tablaPrincipal.buscarCampo(campoTablaPrincipal);
        System.out.println("Columna tabla principal "+columnaTablaPrincipal.getNombre());
       Tabla tablaRelacionada = buscarTabla(nombreTablaRelacionada);
       System.out.println("Tabla Relacionada "+tablaRelacionada.nombreTabla);
       ColumnaTabla columnaTablaRelacionada = tablaRelacionada.buscarCampo(campoTablaRelacionada);
        System.out.println("Columna tabla relacionada "+columnaTablaRelacionada.getNombre());
       Tabla tablaExportacion= new Tabla();
       String json = tablaExportacion.exportarJsonUnoAMuchos(tablaPrincipal, tablaRelacionada, columnaTablaPrincipal, columnaTablaRelacionada, identificadorTablaRelacionada);
       System.out.println(json);
       return json;
    }
     public String exportarTablasUnoAUnoEmbebido(String nombreTablaPrincipal,String campoTablaPrincipal,String nombreTablaRelacionada,String campoTablaRelacionada,String identificadorTablaRelacionada){
       Tabla tablaPrincipal = buscarTabla(nombreTablaPrincipal);
        System.out.println("Tabla principal "+tablaPrincipal.nombreTabla);
       ColumnaTabla columnaTablaPrincipal = tablaPrincipal.buscarCampo(campoTablaPrincipal);
        System.out.println("Columna tabla principal "+columnaTablaPrincipal.getNombre());
       Tabla tablaRelacionada = buscarTabla(nombreTablaRelacionada);
       System.out.println("Tabla Relacionada "+tablaRelacionada.nombreTabla);
       ColumnaTabla columnaTablaRelacionada = tablaRelacionada.buscarCampo(campoTablaRelacionada);
        System.out.println("Columna tabla relacionada "+columnaTablaRelacionada.getNombre());
       Tabla tablaExportacion= new Tabla();
       String json = tablaExportacion.exportarJsonUnoAUnoEmbebido(tablaPrincipal, tablaRelacionada, columnaTablaPrincipal, columnaTablaRelacionada, identificadorTablaRelacionada);
       System.out.println(json);
       return json;
    }
    public String exportarUnionTablas(String nombreTablaPrincipal,String campoTablaPrincipal,String nombreTablaRelacionada,String campoTablaRelacionada){
        Tabla tablaPrincipal = buscarTabla(nombreTablaPrincipal);
        System.out.println("Tabla principal "+tablaPrincipal.nombreTabla);
       ColumnaTabla columnaTablaPrincipal = tablaPrincipal.buscarCampo(campoTablaPrincipal);
        System.out.println("Columna tabla principal "+columnaTablaPrincipal.getNombre());
       Tabla tablaRelacionada = buscarTabla(nombreTablaRelacionada);
       System.out.println("Tabla Relacionada "+tablaRelacionada.nombreTabla);
       ColumnaTabla columnaTablaRelacionada = tablaRelacionada.buscarCampo(campoTablaRelacionada);
        System.out.println("Columna tabla relacionada "+columnaTablaRelacionada.getNombre());
        Tabla tablaExportacion= new Tabla();
        String json = tablaExportacion.exportarJsonUnionTablas(tablaPrincipal, tablaRelacionada ,columnaTablaPrincipal, columnaTablaRelacionada);
        System.out.println(json);
        return json;
    }
    public String exportarTabla(String nombreTabla){
        System.out.println("A exportar tabla "+nombreTabla);
        String coleccion="";
        for(int i=0;i<tablas.size();i++){
            if(tablas.get(i).nombreTabla.equals(nombreTabla)){
                System.out.println("Encontre tabla");
                coleccion = tablas.get(i).exportarJsonTabla();
                System.out.println(coleccion);
                break;
            }
        }
        return coleccion;
    }
    public boolean cargarBaseDatos(){
        ResultadoConsultaBase resultadoConsultaBase;
        ResultSet resultado;
        boolean registrado=false;   
        tablas = new LinkedList();
        // mb = manejadorBases.getSingletonInstance(base);
        String sentenciaSql = "select * from pg_tables where schemaname='public'";
        if(mb.conectar()){
            resultadoConsultaBase = mb.outDb(sentenciaSql);
            if(resultadoConsultaBase.consultaRealizada){
                try{
                 resultado=resultadoConsultaBase.resultado;
                 while(resultado.next()){
                     String nombreTabla = resultado.getString("tablename");
                     System.out.println(nombreTabla);
                     Tabla nuevaTabla = new Tabla();
                     nuevaTabla.nombreTabla = nombreTabla;
                     nuevaTabla.cargarDatosTabla();
                     tablas.add(nuevaTabla);
                     registrado = true;
                 }
               }
               catch(java.sql.SQLException e){
                     System.out.println("problemas al recorrer el resultado "+e.getMessage());
                     mb.desconectar();                     
                     return false;
               }
            }
            else{
                System.out.println("Problemas al consultar las tablas "+mb.getMensaje());
                registrado = false;
            }

           mb.desconectar();
        }
        else{
            System.out.println("Problemas al conectarce a la base de datos  "+ mb.getMensaje());
            
            registrado = false;
        }
        return registrado;
    }
    
}
