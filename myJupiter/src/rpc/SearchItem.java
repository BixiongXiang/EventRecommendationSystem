package rpc;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class SearchItem
 * http://localhost:8080/myJupiter/search?username=james&pwd=123
 * http://localhost:8080/Jupiter/search?lat=37.38&lon=-122.08
 */
@WebServlet(description = "handle search request from front end", urlPatterns = { "/search" })
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		//check login before login
		// allow access only if login session exists
//		HttpSession session = request.getSession(false);
//		if (session == null) {
//			response.setStatus(403);
//			return;
//		}
		
		
		// optional
//		String userId = session.getAttribute("user_id").toString();
		
		// get search parameters. Term can be empty or null. this is the search key word
		String term = request.getParameter("term");
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		
		// we let db program to search the tm and store to db
		DBConnection connection = DBConnectionFactory.getConnection("mysql");
		
		//try to get data from tmAPI and store it in DB
		try {
			List<Item> items = connection.searchItems(lat, lon, term);

			JSONArray array = new JSONArray();
			for (Item item : items) {
				array.put(item.toJSONObject());
			}
			//after we get data from tmAPI write it into response,
			RpcHelper.writeJsonArray(response, array);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
