/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdb;

import java.sql.ResultSet;

/**
 *
 * @author jmeneses
 */
public class Tabla {
    public String nombreTabla;
    public ColumnaTabla[] columnasTabla;
    public String base;
    private boolean relacionarTabla= false;
    private boolean unoAMuchos = false;
    private boolean registrosTablaRelacionada = false;
    private Tabla tablaPrincipal;
    private Tabla tablaRelacionada;
    private ColumnaTabla campoTablaPrincipal;
    private ColumnaTabla campoTablaRecionada;
    private String identificadorTablaRelacionada;
    public ColumnaTabla buscarCampo(String nombreCampo){
        ColumnaTabla campoTabla= null;
        for(int i =0;i<columnasTabla.length;i++){
            if(columnasTabla[i].getNombre().equals(nombreCampo)){
                campoTabla = columnasTabla[i];
                break;
            }    
        }
        return campoTabla;
    }
    public boolean cargarDatosTabla(){
        boolean cargado = false;
        String sentenciaSql = "select * from "+nombreTabla;
        ManejadorBases mb = ManejadorBases.getSingletonInstance(base);
        if(mb.conectar()){
            mb.outDb(sentenciaSql);
            mb.getMetaDatos();
            columnasTabla = mb.getColumnas();
            for(int i=0;i<columnasTabla.length;i++){
                System.out.println("      "+columnasTabla[i].getNombre()+" "+columnasTabla[i].getTipo());
            }
        }
        return cargado;
    }
    public String generarSqlExportacionTablaRelacionada(String valorCampoTablaPrincipal){
        String sentenciaSql = "select * from "+tablaRelacionada.nombreTabla +" where "+campoTablaRecionada.getNombre() +"=";
        if(campoTablaPrincipal.getTipo().equals("varchar"))
            sentenciaSql+="'"+valorCampoTablaPrincipal+"'";
        else
            sentenciaSql+=valorCampoTablaPrincipal;
        return sentenciaSql;
    }
    public String exportarJsonUnoAMuchos(Tabla tablaPrincipal,Tabla tablaRelacionada,ColumnaTabla campoTablaPrincipal,ColumnaTabla campoTablaRecionada,String identificadorTablaRelacionada){
        this.tablaPrincipal = tablaPrincipal;
        this.tablaRelacionada = tablaRelacionada;
        this.campoTablaPrincipal = campoTablaPrincipal;
        this.campoTablaRecionada = campoTablaRecionada;
        this.identificadorTablaRelacionada = identificadorTablaRelacionada;
        String json="";
        String sentenciaSql = "select * from "+tablaPrincipal.nombreTabla;
        relacionarTabla = true;
        unoAMuchos = true;
        json = exportarJsonSentenciaSql(tablaPrincipal,sentenciaSql,"");
        return json;
    }
    
    public String exportarJsonUnoAUnoEmbebido(Tabla tablaPrincipal,Tabla tablaRelacionada,ColumnaTabla campoTablaPrincipal,ColumnaTabla campoTablaRecionada,String identificadorTablaRelacionada){
        this.tablaPrincipal = tablaPrincipal;
        this.tablaRelacionada = tablaRelacionada;
        this.campoTablaPrincipal = campoTablaPrincipal;
        this.campoTablaRecionada = campoTablaRecionada;
        this.identificadorTablaRelacionada = identificadorTablaRelacionada;
        String json="";
        String sentenciaSql = "select * from "+tablaPrincipal.nombreTabla;
        relacionarTabla = true;
        unoAMuchos = false;
        json = exportarJsonSentenciaSql(tablaPrincipal,sentenciaSql,"");
        return json;
    }
    
    public String exportarJsonUnionTablas(Tabla tablaPrincipal,Tabla tablaRelacionada,ColumnaTabla campoTablaPrincipal,ColumnaTabla campoTablaRecionada){
        this.tablaPrincipal = tablaPrincipal;
        this.tablaRelacionada = tablaRelacionada;
        this.campoTablaPrincipal = campoTablaPrincipal;
        this.campoTablaRecionada = campoTablaRecionada;
        String sentenciaSql = "select * from "+tablaPrincipal.nombreTabla+" T1 LEFT JOIN"
                + " "+tablaRelacionada.nombreTabla+" T2 ON T1."+campoTablaPrincipal.getNombre()+"=T2."+campoTablaRecionada.getNombre();
        String json = exportarJsonSentenciaSql(tablaPrincipal,sentenciaSql,"");
        return json;
    }
    public String exportarJsonSentenciaSql(Tabla tablaExportacion,String sentenciaSql,String nombreCampoAExcluir){
        String json="";
        ManejadorBases mb = ManejadorBases.getSingletonInstance(base);
        ResultadoConsultaBase resultadoConsultaBase;
        ResultSet resultado;
        ColumnaTabla[] columnasConsulta;
        if(mb.conectar()){
            resultadoConsultaBase = mb.outDb(sentenciaSql);
            if(resultadoConsultaBase.consultaRealizada){
                try{
                    resultado=resultadoConsultaBase.resultado;
                    mb.getMetaDatos();
                    columnasConsulta =mb.getColumnas();
                    while(resultado.next()){
                       if(registrosTablaRelacionada)
                           json+="\n";
                       else json+="db."+tablaExportacion.nombreTabla+".insert(";
                       json+="{";
                       boolean incluirComa = false;
                       for(int i=0;i<columnasConsulta.length;i++){
                          if(!columnasConsulta[i].getNombre().equals(nombreCampoAExcluir)){ 
                                String valorCampo =  resultado.getString(columnasConsulta[i].getNombre());
                                if(valorCampo!=null){
                                      if(incluirComa)
                                          json=json+",";
                                      else
                                          incluirComa = true;
                                      json=json+"\""+columnasConsulta[i].getNombre()+"\":";
                                      if(columnasConsulta[i].getTipo().equals("varchar"))
                                          json = json+"\""+valorCampo+"\"";
                                      else
                                          json = json+valorCampo;
                                }
                          }
                          if(i+1==columnasConsulta.length&&relacionarTabla){
                                
                                String sentenciaSQLRelacionda = generarSqlExportacionTablaRelacionada(resultado.getString(campoTablaPrincipal.getNombre()));
                                System.out.println(sentenciaSQLRelacionda);
                                if(unoAMuchos)
                                    registrosTablaRelacionada=true;
                                else
                                    registrosTablaRelacionada=true;
                                relacionarTabla= false;
                                String jsonTablaRelacionada = exportarJsonSentenciaSql(tablaExportacion,sentenciaSQLRelacionda,campoTablaRecionada.getNombre());
                                if(!jsonTablaRelacionada.trim().isEmpty()){
                                  json+=",\""+identificadorTablaRelacionada+"\":\n";
                                  if(unoAMuchos)
                                     json+="[";
                                  json = json + jsonTablaRelacionada;
                                  if(unoAMuchos)
                                    json+="]\n";
                                  
                                }
                                relacionarTabla=true;
                                registrosTablaRelacionada = false;
                          }
                       }
                       if(registrosTablaRelacionada)
                           json+="},";
                       else
                           json+="})\n";
                    }
                    if(registrosTablaRelacionada&&json.trim().length()>0){
                        json = json.substring(0,json.length()-1);
                        json +="\n";
                    }    
                }
                catch(java.sql.SQLException e){
                     System.out.println("problemas al recorrer el resultado "+e.getMessage());
                
                }
            }
        }
        
        return json;
    }
    
    public String exportarJsonTabla(){
        String sentenciaSql = "select * from "+nombreTabla;
        return exportarJsonSentenciaSql(this,sentenciaSql,"");
    }
}
