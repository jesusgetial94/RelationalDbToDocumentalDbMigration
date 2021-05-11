package rdb;

public class ColumnaTabla{
    private String nombre="";
    private String tipo="";

    public ColumnaTabla(){
        nombre="";
        tipo="";
    }

    public void setNombre(String pnombre){
        nombre = pnombre;
    }

    public void setTipo(String ptipo){
        tipo = ptipo;
        
    }
//______________________________________________________________________________
    public String getNombre(){
    	return nombre;
    }		
    public String getTipo(){
        return tipo;
    }
}	