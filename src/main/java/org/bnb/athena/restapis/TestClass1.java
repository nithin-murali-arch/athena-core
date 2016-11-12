package org.bnb.athena.restapis;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/testing123")
public class TestClass1 {
	@GET
	@Path("/test123")
	public String test(){
		return "Success";
	}
}
