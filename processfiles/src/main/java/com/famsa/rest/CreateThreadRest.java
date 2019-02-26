package com.famsa.rest;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.famsa.exceptions.CreateThreadCtrlExc;
import com.famsa.fabricas.CreateThreadFactory;
import com.famsa.interfaces.ICreateThread;

@Path("/createthread")
public class CreateThreadRest {
	
	private static final String MYRESULTADOEXC = "[{\"idException\": %d, \"msgException\": \"%s\"}]";

	@GET
	@Path("/buscaIds")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getIDs() {
		
		String strJSON;
		ICreateThread creThread = CreateThreadFactory.createJsonIds();
		try {
			strJSON = creThread.generaJsonIds();
		} catch (CreateThreadCtrlExc e) {
			String resultado = String.format(MYRESULTADOEXC, 7000, 
					e.toString().substring((e.toString().indexOf('#') + 1), (e.toString().length())));
			return Response.status(200).entity(resultado).build();
		}
		
		return Response.status(200).entity(strJSON).build();
		
	}

	@GET
	@Path("/buscaDetalle/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getDetalle(
			@DefaultValue("id") @PathParam("id") int paramId) {
		
		String strJSON;
		ICreateThread creThread = CreateThreadFactory.createJsonDetalle();
		try {
			strJSON = creThread.generaJsonDetalle(
					paramId);
		} catch (CreateThreadCtrlExc e) {
			String resultado = String.format(MYRESULTADOEXC, 7000, 
					e.toString().substring((e.toString().indexOf('#') + 1), (e.toString().length())));
			return Response.status(200).entity(resultado).build();
		}
		
		return Response.status(200).entity(strJSON).build();
		
	}
	
}
