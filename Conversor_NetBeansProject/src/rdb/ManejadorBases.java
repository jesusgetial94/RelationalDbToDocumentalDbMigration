package rdb;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.*;

public class ManejadorBases implements java.io.Serializable{
    private Connection cn;
    //private Statement smt;
    String url,user,pwd,drv,mensaje,maximo,base,gestor,host;

    private String ip="";
    private String puerto = "";
    int nr=0;
    int coderr;
    int numeroColumnas;
    ColumnaTabla[] columnas =null; 
    ResultSet resultado = null;
    ResultSetMetaData MetaDatoResultado = null;
    boolean estado,control;
    private static ManejadorBases manejadorBasesSingleton;
    //base principal              

    public static ManejadorBases getSingletonInstance(String tipoBase) {
        //manejadorBases manejadorBasesProv = new manejadorBases(tipoBase);
       if (manejadorBasesSingleton == null){
            manejadorBasesSingleton = new ManejadorBases(tipoBase);
        }
        else{
            //System.out.println("No se puede crear el objeto "+ tipoBase + " porque ya existe un objeto de la clase SoyUnico");
            
        }
        return manejadorBasesSingleton;
        //return manejadorBasesProv;
    }

    public static ManejadorBases getSingletonInstance() {
        if (manejadorBasesSingleton == null){
            manejadorBasesSingleton = new ManejadorBases();
        }
        else{
            //System.out.println("No se puede crear el objeto  porque ya existe un objeto de la clase SoyUnico");
        }
        return manejadorBasesSingleton;
    }

    public static ManejadorBases actualizarSingletonInstance(String base){
        System.out.println("Actualizando Singleton");
        /*if(manejadorBasesSingleton!=null){
            manejadorBases mb = getSingletonInstance(base);
            mb.eliminarConexion();
            manejadorBasesSingleton = null;
        }*/
        return getSingletonInstance(base);
    }

    private ManejadorBases(){

    }
    /**
     * Método utilizado para extraer los parámetros de conexión a una base de datos del archivo bases.xls
     * @param tipobase String:
     * Identificador de la base de datos.
     */
    private ManejadorBases(String tipobase){
      //System.out.println("entre a constructor manejador de bases"+tipobase);
      try{
          DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
          DocumentBuilder builder =factory.newDocumentBuilder();

          //System.out.println("antes del builder");
          Document document = builder.parse("FILE:./src/Configuracion/bases.xml");
          /*Document document = builder.parse(new DBSettings()
                     .getClass()
                     .getResourceAsStream("bases.xml"));*/
          //System.out.println("despues del bull");
          Node rootNode = document.getDocumentElement();
          //se crea un listado de todos los metodos de insercion
          //System.out.println("Esta es la base a buscar "+tipobase);
          NodeList listaBases = document.getElementsByTagName("basedatos");
          boolean encontrada=false;
          String etiqueta="",gestor="";
          for(int i=0; i< listaBases.getLength() && !encontrada;i++){
                 Node nodoBase = listaBases.item(i);
                 NodeList listaParametros = nodoBase.getChildNodes();
                 for(int j=0; j<listaParametros.getLength();j++){
                     Node parametro = listaParametros.item(j);
                     etiqueta=parametro.getNodeName();
                     if(etiqueta.equals("tipo")){
                        Node valorParametro=parametro.getFirstChild();
                        //se escoje el metodo de insercion deacuerdo al nombre
                        //System.out.println("base en archivo "+valorParametro.getNodeValue());
                        if(valorParametro.getNodeValue().equals(tipobase))encontrada=true;
                     }
                     if(encontrada){
                        if(etiqueta.equals("gestor")){
                              gestor=parametro.getFirstChild().getNodeValue();
                             // System.out.println("el gestor es "+gestor);
                        }
                        else if(etiqueta.equals("nombre")){//nombre de la bse de datos
                               base=parametro.getFirstChild().getNodeValue();
                               //System.out.println("la base es "+base);
                        }
                        else if(etiqueta.equals("ip")){//nombre de la bse de datos
                               ip=parametro.getFirstChild().getNodeValue();
                               //System.out.println("la ip es "+ip);
                        }
                        else if(etiqueta.equals("puerto")){//nombre de la bse de datos
                               puerto=parametro.getFirstChild().getNodeValue();
                               //System.out.println("el puerto es "+puerto);
                        }
                        else if(etiqueta.equals("usuario")){
                               user=parametro.getFirstChild().getNodeValue();
                               if(user.equals("null"))user="";
                               //System.out.println("el usuario es"+user);
                        }
                        else if(etiqueta.equals("psw")){
                               pwd=parametro.getFirstChild().getNodeValue();
                               if(pwd.equals("null"))pwd="";
                               //System.out.println("el pasword es "+pwd);
                        }

                    }
                }
                this.definirParametros(gestor);
                //System.out.println("defini parametros gestor base es ");
            }
        }
      catch(IOException ioException){
        System.out.println("aqui hay un error"+ioException.getMessage());
        lanzarDialog("Error en constructor: ", ioException.getMessage());
      } 
      catch(DOMException domException){
        System.out.println("aqui hay un error");
        lanzarDialog("Error en constructor: ", domException.getMessage());
      }
      catch(SAXException saxException){
        System.out.println("aqui hay un error");
        lanzarDialog("Error en constructor: ", saxException.getMessage());
      }
      catch(ParserConfigurationException e){
        System.out.println("aqui va otro error");
        lanzarDialog("Error en constructor: ", e.getMessage());
      }

    }   
    
    /**
     * Lanza Ventana emergente notificando un error.
     * @param typeEr Tipo de error 
     * @param msmEr Mensaje del error.
     */
    public static void lanzarDialog(String typeEr, String msmEr) {        
        JTextArea text   = new JTextArea(msmEr,10,50);                
        JScrollPane pane = new JScrollPane(text);               
        
        JOptionPane.showMessageDialog(null, pane, typeEr, JOptionPane.ERROR_MESSAGE);

    }

    public void setUrl(String purl){
       url = purl;
    }

    public String getUrl() {return this.url;}

    public void setUser(String puser){
       user = puser;
    }

    public String getUser() {return  this.user;}

    public void setPwd(String ppwd){
       pwd = ppwd;
    }

    public String getPwd() {return  this.pwd;}

    public void setDrv(String pdrv){
       drv = pdrv;
    }

   public void setBase(String pbase){
      base = pbase;
   }

   public void setGestor(String pgestor){
      gestor = pgestor;
      this.definirParametros(gestor);
   }

   public void setHost(String phost){
      host = phost;
   }
   /**
    * Método utilizado para definir los parámetros de conexión a una base de datos.
    */
   public void definirParametros(){
      url="jdbc:odbc:";
      drv="sun.jdbc.odbc.JdbcOdbcDriver";
      //user="";
      //pwd="";
      mensaje="";
      coderr=0;
      control=false;
      nr=0;
   }
   
   /**
    * Método utilizado para definir los parámetros de conexión a una base de datos.
    * @param manejador String:
    * Tipo de base de datos para definición de los parametros.
    */
   public void definirParametros(String manejador){
       String filename="";//solo para que compile
      //define los parametros de coneccion deacuerdo a la base de datos
      if(manejador.equals("postgresql")){    
          //url="jdbc:postgresql://127.0.0.1:5432/";
          url="jdbc:postgresql://"+ip+":"+puerto+"/";
          url=url.trim();

          drv="org.postgresql.Driver";    
      }
      else if(manejador.equals("mysql")){
          url = "jdbc:mysql://"+ip+"/";
          drv = "org.gjt.mm.mysql.Driver";
      }        
      else if(manejador.equals("acces")){
        filename = "c:/jonny/prueba.mdb";
        url = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
        url+= filename.trim() + ";DriverID=22;READONLY=true}"; 
      }
      else if(manejador.equals("odbc")){
        url="jdbc:odbc:";
        drv="sun.jdbc.odbc.JdbcOdbcDriver";
      }
      mensaje="";
      coderr=0;
      control=false;
      nr=0;
   }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPuerto() {
        return puerto;
    }
    public void setPuerto(String puerto) {
        this.puerto = puerto;
    }

       public int getCoderr(){
	   		return coderr;
  	   } 	
       public void setCoderr(int cod){
       		coderr=cod;
       }
	   public String getMensaje(){
	   		return mensaje;
  	   } 	
       public void setMensaje(String men){
       		mensaje=men;
       }
       public boolean getEstado(){
       		return estado;
       } 	
       public void setEstado(boolean est){       	
       		estado=est;
       }
       
       public int getNr(){       		
       		return nr;
       }
       
       public int getNumeroColumnas(){
       	   return numeroColumnas;
       } 
       
       public ColumnaTabla[] getColumnas(){
       	  return columnas;
       }
       	
       public void setNr(int num){
       		nr=num;
       }
    
    /**
      * Método para obtener el MetaDato de la base se datos.
      * @return DatabaseMetaData:
      * MetaDato de la base de datos.
    */
    public DatabaseMetaData getmetadata(){
        try{
            return cn.getMetaData();
        }
        catch(java.sql.SQLException e){
            System.out.println("Un error en metadata"+e.getMessage());
            lanzarDialog("Error en DatabaseMetaData: ", e.getMessage());
            return null;	
        }
    }
              
    public ResultSet getResultado(){
        return resultado;
    }

    /**
    * Método para iniciar una conexión con la base de datos.
    * @return boolean:
    * Retorna verdadero si la conexión se realizo correctamente.
    */	   
    public boolean conectar(){
        boolean intentarConeccion = false;
        boolean conexionRealizada = true;
        if(cn==null){
            intentarConeccion = true;
        }
        else{
            try {
                if(cn.isClosed()){
                    intentarConeccion = true;
                }
            } catch (SQLException e) {                
                mensaje="Error al conectarse a la Base de datos "+e.getMessage();
                estado=false;
                conexionRealizada = false;
                lanzarDialog("Error al conectarse a la Base de datos ", e.getMessage());
            }  
        }
        if(intentarConeccion){

              System.out.println("Voy a conectar a "+base+" en "+ip);
              try{
                  Class.forName(drv);
                  estado=true;
                  conexionRealizada = true;
                  //System.out.println("Conectado a base");
              }catch(ClassNotFoundException e){
                  estado=false;
                  conexionRealizada = false;
                  mensaje="Error al conectarse a la Base de datos "+e.getMessage();                    
                  lanzarDialog("Error al conectarse a la Base de datos ", e.getMessage());
              }
              catch (Exception e){
                  mensaje=e.getMessage();
                  System.out.println(mensaje);                  
                  lanzarDialog("Error al conectarse a la Base de datos ", e.getMessage());
                  conexionRealizada = false;
                  //control=false;
              }
              try{
                  cn=DriverManager.getConnection(url+base,user,pwd);
                  conexionRealizada = true;
                  estado=true;
                  //System.out.println("Se acaba de conectar a "+base);
              }

              catch(SQLException e){
                  mensaje="Error al conectarse a la Base de datos "+e.getMessage();
                  estado=false;
                  conexionRealizada = false;
                  System.out.println(mensaje);
                  lanzarDialog("Error al conectarse a la Base de datos ", e.getMessage());
              }
              catch (Exception e){
                  mensaje=e.getMessage();
                  System.out.println(mensaje);
                  lanzarDialog("Error al conectarse a la Base de datos ", e.getMessage());
                  conexionRealizada = false;
                  //control=false;
              }
              return control=estado;                      
        }
        
        else
            //System.out.println("conexion ya establecida "+(new java.sql.Timestamp(System.currentTimeMillis())).toString());      
        
        return conexionRealizada;
    }
    

       /**
        * Método utilizado para eliminar una conexión con la base de datos.
        * @return boolean:
        * Retorna verdadero si la conexión fue eliminada exitosamente.
        */
       public boolean eliminarConexion(){
            control=false;              
            try{
                 cn.close();
                 estado=false;                   
                 control=true;
            }catch(SQLException e){
               mensaje="Error al desconectarse de la Base de datos "+e.getMessage();	
               System.out.println(mensaje);
               lanzarDialog("Error al conectarse a la Base de datos ", e.getMessage());
            }
            return control;
         
       }
       public boolean desconectar(){
          // eliminarConexion();
       	/*	  control=false;              
              try{
                   cn.close();
                   estado=false;                   
                   control=true;
              }catch(SQLException e){
              		
                 mensaje="Error al desconectarse de la Base de datos "+e.getMessage();	
                 System.out.println(mensaje);
              }
              return control;*/
         return true;
       }
       
       /**
        * Metodo Utilizado para realizar una operacion con la base de datos, la operación retorna un resultado. 
        * @param sql String:
        * Sentencia SQL para realizar la operación.
        * @return boolean:
        * Retorna verdadero si la operación se realizo corectamente.
        */
       public ResultadoConsultaBase outDb(String sql) {
            Statement smt;           
            //System.out.printf("%s\n",sql);
            //resultado = new ResultSet();
           // System.out.println(sql);
            control=false; 	
            int ls=0;
            ResultadoConsultaBase resultadoConsultaBase = new ResultadoConsultaBase();
            try {
                smt = cn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY); 
                //System.out.println("Antes de llenar resulset");
                resultadoConsultaBase.resultado = smt.executeQuery(sql);
                //System.out.println("Despues de llenar resulset "+resultadoConsultaBase.resultado);
                resultadoConsultaBase.MetaDatoResultado = resultadoConsultaBase.resultado.getMetaData();
                resultadoConsultaBase.consultaRealizada = true; 
                resultado = resultadoConsultaBase.resultado; //sin sincronizacion
                MetaDatoResultado = resultadoConsultaBase.MetaDatoResultado;                                    
                control=true;              	   	
            }catch (SQLException e) {
                   mensaje=e.getMessage(); 
                   System.out.println(e.getMessage());	                    
                   resultadoConsultaBase.consultaRealizada = false;
                   control=false;
                   e.printStackTrace(System.out);
                   //lanzarDialog("Error en OytDB: ", e.getMessage());                   
            }catch (Exception e){
                   mensaje=e.getMessage(); 
                   System.out.println(e.getMessage());	          
                   resultadoConsultaBase.consultaRealizada = false;
                   control=false;
                   //lanzarDialog("Error en OutDB: ", e.getMessage());
            }
            if(resultadoConsultaBase.consultaRealizada){
                //System.out.println("La consulta si se realizo");
                try{
                    resultadoConsultaBase.resultado.last();
                    ls=resultadoConsultaBase.resultado.getRow();	
                    resultadoConsultaBase.resultado.beforeFirst();
                    resultadoConsultaBase.nr = ls;
                   // System.out.println("numero de registros "+ls);
                    nr=ls;
                }catch(SQLException e){	            	
                    System.out.println(e.getMessage());		
                    //lanzarDialog("Error en OutDB: ", e.getMessage());                   
                }  	
            }
              //System.out.println("tenemos "+nr+" registros");
            //return control;
            return resultadoConsultaBase;
      }
      /**
       * Metodo Utilizado para realizar una operacion con la base de datos, la operación no retorna un resultado. 
       * @param sql String:
       * Sentencia SQL para realizar la operación.
       * @return boolean:
       * Retorna verdadero si la operación se realizo corectamente.
       */      
      public boolean inDb(String sql){ 
            Statement smt;
            boolean controlInterno =false;
      	  	 //System.out.println(sql);     
      		 control=false;	      		       		 	
             try{
                 smt=cn.createStatement();
                 smt.executeUpdate(sql);
                 smt.close();
                 control=true;  
                 controlInterno = true;
             }catch(SQLException e){
                 setCoderr(Integer.parseInt(e.getSQLState()));
                 System.out.println("Codigo del Error -> "+e.getSQLState());
                 mensaje=e.getMessage();
                 control=false;
                 controlInterno = false;
                 //lanzarDialog("Error en inDB: ", e.getMessage());                   
             }
             //return control;
             return controlInterno;
       }
       public boolean getMetaDatos(){
       	   boolean realizado = true;
       	   try{
       	   	numeroColumnas = MetaDatoResultado.getColumnCount();
                columnas = new ColumnaTabla[numeroColumnas];
                for(int i=1;i<=numeroColumnas;i++){
                    columnas[i-1] = new ColumnaTabla();
                    columnas[i-1].setNombre(MetaDatoResultado.getColumnName(i));
                    columnas[i-1].setTipo(MetaDatoResultado.getColumnTypeName(i));
                }
       	    }
       	    catch(java.sql.SQLException e){
                System.out.println("Error al consultar Datos de columnas"+e.getMessage());
                mensaje="Error al consultar Datos de columnas"+e.getMessage();
                lanzarDialog("Error al consultar Datos de columnas: ", e.getMessage());                   
                return false;	
            }
       	   return realizado;
       }
}