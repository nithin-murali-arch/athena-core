package org.bnb.athena.restapis;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.bnb.athena.pojos.TestPojo;

@Path("/test")
public class TestApi {
	
	@Path("/inout")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public TestPojo testIn(TestPojo pojo){
		return pojo;
	}
}
