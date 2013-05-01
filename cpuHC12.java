/**2013
 * Taller de Programacion de Sistemas
 * @author Edwin Alberto Castañeda García
 */
import java.io.*;
import java.util.*;
import java.lang.*;
public class cpuHC12 {
	static final int SI=1,NO=0;
	static final int ETIQUETA=0,CODOP=1,OPERANDO=2,MODOSDIR=3,limiteTokens=4;
	String original,log;
	String[] linea2=new String[limiteTokens];
	int nLineas,end,tieneCodop,tieneEtiqueta;
	Scanner Lectora=new Scanner (System.in);
	Stack<String> lineaxlinea=new Stack<String>();
	Stack<String> modosdir=new Stack<String>();
/*************************/
	public String modos_direccionamiento(String nombre){
		Stack<String> modosdir=new Stack<String>();
		String linea,ruta="TABOP.txt",sToken="";
		int i,l,s,existe=NO,encontrado=NO;
		String[] tokensLinea=new String[6];
		File archivo = new File(ruta);
		if(!archivo.exists()){
		}
		try {
			FileReader leerArchivo = new FileReader(archivo);
			BufferedReader buffer = new BufferedReader(leerArchivo);
			existe=SI;
			while ((linea = buffer.readLine()) != null){
				encontrado=i=NO;
				StringTokenizer st=new StringTokenizer(linea.toString());
				do{
					sToken=st.nextToken();//Tomando token de la linea
					if(sToken.contains(nombre)&&nombre.length()==sToken.length())
						encontrado=SI;
					if(encontrado==SI){
						tokensLinea[i]=sToken;
						i++;
					}
				}while(st.hasMoreTokens()&&encontrado==SI);
				if(encontrado==SI){
					modosdir.add(tokensLinea[1]);
					/*
					tokensLinea[4]=tokensLinea[3];//pasa pos3 a pos4
					l=tokensLinea[2].length();//calcula ancho de pos2
					s=Integer.parseInt(tokensLinea[4])+l;
					tokensLinea[3]=Integer.toString(l);//guardar "l" en pos3
					tokensLinea[5]=Integer.toString(s);//guardar "s" en pos5
					*/
				}
			}
			buffer.close();
		}
		catch (Exception ex){
			if(existe==NO)
				System.out.println("NO EXISTE ARCHIVO "+ruta);
		}
		return modosdir.toString();
	}
/*************************/
	public void mostrar(String ruta,String retorno,String log){
		System.out.println("\n\t---> Original de "+ruta+"\n"+original);
		System.out.println("\n\t---> Retorno en .INST \n"+retorno);
		if(log.length()>0)
			System.out.println("\n\t---> ERRORES! \n"+log);
		else
			System.out.println(log="\n\t Completado Sin Errores");
	}
/*************************/
	public String QuitarComentarios(String linea){
		if(linea.contains(";")){
			int p=linea.indexOf(';');
			linea=linea.substring(0,p);
		}
		return linea;
    }
/*************************/
	public String retornoINST(){
		String retorno;
		retorno=("#linea Etiqueta  CODOP  Operando  ");
		for(int j=0;j<nLineas;j++)
			retorno+=("\n"+lineaxlinea.get(j).toString());
		return retorno;
	}
/*************************/
	public String defineLinea(){
		int i;
		String nuevaLinea=(" "+nLineas+"\t"+linea2[ETIQUETA]);
		for(i=13-nuevaLinea.length();i>0;i--)
			nuevaLinea+=" ";
		nuevaLinea+=linea2[CODOP];
		for(i=20-nuevaLinea.length();i>0;i--)
			nuevaLinea+=" ";
		nuevaLinea+=linea2[OPERANDO];
		linea2[MODOSDIR]=modos_direccionamiento(linea2[CODOP]);
		nuevaLinea+="\t ";
		nuevaLinea+=linea2[MODOSDIR];
		return nuevaLinea;
	}
/*************************/
	public int esEtiqueta(String sToken){
		char c,pCt=sToken.charAt(0);
		if(Character.isLetter(pCt) && sToken.length()<=8 && tieneCodop==NO){
			for(int i=1;i<sToken.length();i++){
				c=sToken.charAt(i);
				if(Character.isLetterOrDigit(c)|c=='_'){
					tieneEtiqueta=SI;
				}else
					log+="\n Caracter '"+c+"' no valido en linea "+nLineas;
			}
			linea2[ETIQUETA]=sToken;
		}
		return tieneEtiqueta;
	}
/*************************/
	public int esCODOP(String sToken){
		sToken=sToken.toUpperCase();
		char pCt=sToken.charAt(0);
		if(Character.isLetter(pCt) && tieneCodop==NO){
			if(sToken.length()<=5 && sToken.indexOf('.')==sToken.lastIndexOf('.') ){
				if(sToken.contains("END")){
					end=SI;
					linea2[OPERANDO]=" ";
				}
				linea2[CODOP]=sToken.toUpperCase();
				if(tieneEtiqueta==NO)
					linea2[ETIQUETA]=("NULL");
				return SI;
			}
		}else if(esOperando(sToken)==NO)
			log+="\n Token "+sToken+" NO reconocido de linea "+nLineas;
		return NO;
	}
/*************************/
	public int esOperando(String sToken){
		linea2[OPERANDO]=sToken; //es Operando
		if(tieneCodop==NO)
			linea2[CODOP]=("NULL");
		return NO;
	}
/*************************/
	public void diferenciarTokens(String linea){
		String sToken,nuevaLinea;
		char pCt,pcl=linea.charAt(0);
		StringTokenizer st=new StringTokenizer(linea);
		tieneCodop=tieneEtiqueta=NO;
		for(int i=0;i<limiteTokens;i++)
			linea2[i]="NULL";
		while(st.hasMoreTokens()){
			sToken=st.nextToken();//Tomando token de la linea
			pCt=sToken.charAt(0);//Primer caracter del token
			if(Character.isLetter(pCt) && sToken.length()<=8 && tieneCodop==NO){
				if(tieneEtiqueta==NO && pcl!=' '){
					tieneEtiqueta=esEtiqueta(sToken);
				}else if(tieneCodop==NO){
					tieneCodop=esCODOP(sToken);
				}else
					log+="\n Token "+sToken+" NO reconocido de linea "+nLineas;
			}else if(end==NO && tieneCodop==SI){
				esOperando(sToken);
			}
		};
		if(tieneCodop==NO)
			log+="\n No tiene CODOP en linea "+nLineas;
    }
/*************************/
	public String leer(String ruta){
		String linea,extension;
		int p=ruta.lastIndexOf('.');
		linea=original=log="";
		lineaxlinea.clear();
		extension=ruta.substring(p,ruta.length());
		nLineas=end=0;
		File archivo = new File(ruta);
		if(!archivo.exists()){
			if(ruta.contains(extension.toUpperCase()) ){
				ruta=ruta.substring(0,p)+extension.toLowerCase();
				archivo = new File(ruta);
			}else if(ruta.contains(ruta.toLowerCase()) ){
				ruta=ruta.substring(0,p)+extension.toUpperCase();
				archivo = new File(ruta);
			}
		}
		if(!archivo.exists()){
			System.out.println("NO EXISTE ARCHIVO "+ruta);
		}try{
			FileReader leerArchivo = new FileReader(archivo);
			BufferedReader buffer = new BufferedReader(leerArchivo);
			while ((linea = buffer.readLine()) != null){
				original+=linea+"\n";
				linea=QuitarComentarios(linea); //Quitar comentarios
				if(linea.length()>0 && end==NO){
					diferenciarTokens(linea.toString());
					lineaxlinea.add(defineLinea());
					nLineas++;
				}
			}
			buffer.close();
			if(end==NO)
				log+="\n No finaliza con End";
		}
		catch (Exception ex){}
		return ruta;
	}
/*************************/
	public void escribir(String nuevaRuta,String contenido){
		File archivo = new File(nuevaRuta);
		if(archivo.exists()){
			if(archivo.delete()){
			    System.out.println("\n Reescribiendo "+nuevaRuta+"...");
			}else
			    System.out.println("\n Error al reescribir "+nuevaRuta+"...");
		}else
			System.out.println("\n Creando "+nuevaRuta+"...");
		try {
			FileWriter escribirArchivo = new FileWriter(archivo, true);
			BufferedWriter buffer = new BufferedWriter(escribirArchivo);
			buffer.write(contenido);
			buffer.newLine();
			buffer.close();
		}
		catch (Exception ex) {}
	}
/*************************/
    public static void main(String a[]){
    	Scanner Lectora=new Scanner(System.in);
		cpuHC12 archivo=new cpuHC12();
    	String retorno,ruta,extension=".asm";
		int opcion,existe;
		do{
			opcion=existe=0;
		    System.out.print("\n Por Favor, escriba ruta con archivo:   ");
		    ruta=Lectora.next();
			if(!ruta.contains(".")){
				System.out.println("\n No hay punto \".\" en el nombre, se abrira "+ruta+extension);
				ruta+=extension;
			}
			if(ruta.contains(extension.toUpperCase())|ruta.contains(extension.toLowerCase()) ){
				File ASM = new File(archivo.leer(ruta));
				if(ASM.exists()){
					retorno=archivo.retornoINST();
					archivo.mostrar(ruta,retorno,archivo.log);
					archivo.escribir(ruta.substring(0,ruta.lastIndexOf('.'))+".INST",retorno);
					archivo.escribir("errores.log",archivo.log);
				}
			}else
				System.out.println("\n ERROR! Solo se admiten archivos "+extension);
			//
			System.out.print("\n Desea reutilizar el programa? (0=NO,1=SI): ");
			opcion=Lectora.nextInt();
		}while(opcion==SI);
    }
}