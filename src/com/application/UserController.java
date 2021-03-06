package com.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.mvc.Viewable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


@Path("/")
public class UserController {

	@Context
	//static
	HttpServletRequest request;

	@GET
	@Path("/")
	@Produces(MediaType.TEXT_HTML)
	public Response loginPage() {
		return Response.ok(new Viewable("/Login.jsp")).build();
	}

	@GET
	@Path("/signUp")
	@Produces(MediaType.TEXT_HTML)
	public Response signUpPage() {
		return Response.ok(new Viewable("/Signup.jsp")).build();
	}
	
	@GET
	@Path("/showLocation")
	@Produces(MediaType.TEXT_HTML)
	public Response showLocationPage(){
		return Response.ok(new Viewable("/ShowLocation.jsp")).build();
	}

	@POST
	@Path("/updateMyLocation")
	@Produces(MediaType.TEXT_PLAIN)
	public String updateLocation(@FormParam("lat") String lat, @FormParam("long") String lon){
		HttpSession session = request.getSession();
		Long id = (Long) session.getAttribute("id");
		//String serviceUrl = "http://se2firstapp-softwareeng2.rhcloud.com/FCISquare/rest/updatePosition";
		String serviceUrl = "http://localhost:8080/FCISquare/rest/updatePosition";

		String urlParameters = "id=" + id + "&lat=" + lat + "&long="+ lon;
		// System.out.println(urlParameters);
		String retJson = Connection.connect(serviceUrl, urlParameters, "POST",
				"application/x-www-form-urlencoded;charset=UTF-8");
		JSONParser parser = new JSONParser();
		JSONObject obj;
		try {
			obj = (JSONObject)parser.parse(retJson);
			Long status = (Long) obj.get("status");
			if(status == 1)
				return "Your location is updated";
			else
				return "A problem occured";
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "A problem occured";
		
	}
	
	@POST
	@Path("/doLogin")
	@Produces(MediaType.TEXT_HTML)
	public Response showHomePage(@FormParam("email") String email,
			@FormParam("pass") String pass) {
		//String serviceUrl = "http://se2firstapp-softwareeng2.rhcloud.com/FCISquare/rest/login";
		System.out.println("In do log in function");
		String serviceUrl = "http://localhost:8080/FCISquare/rest/login";

		String urlParameters = "email=" + email + "&pass=" + pass;
		// System.out.println(urlParameters);
		String retJson = Connection.connect(serviceUrl, urlParameters, "POST",
				"application/x-www-form-urlencoded;charset=UTF-8");
		System.out.println("back from the server");
		HttpSession session = request.getSession();
		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		try {
			System.out.println("going to parse the return values");
			obj = (JSONObject) parser.parse(retJson);
			session.setAttribute("email", obj.get("email"));
			session.setAttribute("name", obj.get("name"));
			session.setAttribute("id", obj.get("id"));
			session.setAttribute("lat", obj.get("lat"));
			session.setAttribute("long", obj.get("long"));
			session.setAttribute("pass", obj.get("pass"));
			Map<String, String> map = new HashMap<String, String>();

			map.put("name", (String) obj.get("name"));
			map.put("email", (String) obj.get("email"));

			return Response.ok(new Viewable("/home.jsp", map)).build();

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	@POST
	@Path("/doSignUp")
	@Produces(MediaType.TEXT_HTML)
	public Response showHomePage(@FormParam("name") String name,
			@FormParam("email") String email, @FormParam("pass") String pass) {
		//String serviceUrl = "http://se2firstapp-softwareeng2.rhcloud.com/FCISquare/rest/signup";
		String serviceUrl = "http://localhost:8080/FCISquare/rest/signup";

		String urlParameters = "name=" + name + "&email=" + email + "&pass="
				+ pass;
		// System.out.println(urlParameters);
		String retJson = Connection.connect(serviceUrl, urlParameters, "POST",
				"application/x-www-form-urlencoded;charset=UTF-8");
		HttpSession session = request.getSession();
		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		try {
			obj = (JSONObject) parser.parse(retJson);
			session.setAttribute("email", obj.get("email"));
			session.setAttribute("name", obj.get("name"));
			session.setAttribute("id", obj.get("id"));
			session.setAttribute("lat", obj.get("lat"));
			session.setAttribute("long", obj.get("long"));
			session.setAttribute("pass", obj.get("pass"));
			Map<String, String> map = new HashMap<String, String>();

			map.put("name", (String) obj.get("name"));
			map.put("email", (String) obj.get("email"));

			return Response.ok(new Viewable("/home.jsp", map)).build();

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	
	@POST
	@Path("/doGetFollowers")
	@Produces(MediaType.TEXT_HTML)
	public Response getFollowers(){
		HttpSession session = request.getSession();
		String email = (String) session.getAttribute("email");
		String ServerUrl = "http://localhost:8080/FCISquare/rest/followers";
		System.out.println("email : " + email);
		String parameters = "email="+email;
		String retJson = Connection.connect(ServerUrl, parameters, "POST", 
				"application/x-www-form-urlencoded;charset=UTF-8");
		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		Map<String , ArrayList<String>> map = new HashMap<>();
		ArrayList<String> list=new ArrayList<>();
		try {
			obj = (JSONObject)parser.parse(retJson);
			for(int i = 0 ; i<obj.size();i++){
				list.add((String) obj.get("follower #"+(i+1)));
				System.out.println((String) obj.get("follower #"+(i+1)));
			}
			map.put("followers", list);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return Response.ok(new Viewable("/getFollowers.jsp", map)).build();
	}
	
	@POST
	@Path("/follow")
	@Produces(MediaType.TEXT_HTML)
	public Response call_follow(){
		return Response.ok(new Viewable("/follow.jsp")).build();
	}
	
	
	@POST
	@Path("/doFollow")
	@Produces(MediaType.TEXT_HTML)
	public String follow(@FormParam("tofollow") String tofollow){
		HttpSession session = request.getSession();
		String email = (String) session.getAttribute("email");
		String pass = (String) session.getAttribute("Pass");
		String ServerUrl = "http://localhost:8080/FCISquare/rest/follow";
		String parameters = "Follower_email="+email+"&pass="+pass+"&Followed_email="+tofollow;
		String retJson = Connection.connect(ServerUrl, parameters, "POST", 
				"application/x-www-form-urlencoded;charset=UTF-8");
		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		try {
			obj = (JSONObject) parser.parse(retJson);
			Long status = (Long) obj.get("status");
			if(status == 1)
				return "now you follow -> "+tofollow;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "there is a problem";
	}
	
	@POST
	@Path("/unfollow")
	@Produces(MediaType.TEXT_HTML)
	public Response call_unfollow(){
		return Response.ok(new Viewable("/unfollow.jsp")).build();
	}
	
	
	@POST
	@Path("/dounFollow")
	@Produces(MediaType.TEXT_HTML)
	public String unfollow(@FormParam("tounfollow") String tounfollow){
		HttpSession session = request.getSession();
		String email = (String) session.getAttribute("email");
		String pass = (String) session.getAttribute("Pass");
		String ServerUrl = "http://localhost:8080/FCISquare/rest/unfollow";
		String parameters = "Follower_email="+email+"&pass="+pass+"&Followed_email="+tounfollow;
		String retJson = Connection.connect(ServerUrl, parameters, "POST", 
				"application/x-www-form-urlencoded;charset=UTF-8");
		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		try {
			obj = (JSONObject) parser.parse(retJson);
			Long status = (Long) obj.get("status");
			if(status == 1)
				return "unfollow is done";
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "there is a problem";
	}
}



















