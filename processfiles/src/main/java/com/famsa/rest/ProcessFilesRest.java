package com.famsa.rest;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.famsa.exceptions.ProcessFileCtrlExc;
import com.famsa.fabricas.ProcessFileFactory;
import com.famsa.interfaces.IProcessFile;

@Path("/processfiles")
public class ProcessFilesRest {

	private static final String MYRESULTADOEXC = "[{\"idException\": %d, \"msgException\": \"%s\"}]";
	
	@GET
	@Path("/buscaArchivos/{filePath : .*}/{uuid}/{creationTime}/{xmlFileName : .*}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getBuscaArchivos(
			@DefaultValue("filePath") @PathParam("filePath") String paramFilePath,
			@DefaultValue("uuid") @PathParam("uuid") String paramUuid,
			@DefaultValue("creationTime") @PathParam("creationTime") String paramCreationTime,
			@DefaultValue("xmlFileName") @PathParam("xmlFileName") String paramXMLFileName) throws ProcessFileCtrlExc {
		
		String strJSON;
		IProcessFile proFile = ProcessFileFactory.createJson();
		try {
			strJSON = proFile.generaJson( 
					paramFilePath, paramUuid, paramCreationTime, paramXMLFileName);
		} catch (ProcessFileCtrlExc e) {
			String resultado = String.format(MYRESULTADOEXC, 7000, 
					e.toString().substring((e.toString().indexOf('#') + 1), (e.toString().length())));
			return Response.status(200).entity(resultado).build();
		}
		
		return Response.status(200).entity(strJSON).build();

	}

}
