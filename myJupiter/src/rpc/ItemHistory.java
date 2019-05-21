package rpc;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import db.mysql.MySQLConnection;
import entity.Item;

/**
 * Servlet implementation class ItemHistory
 */
@WebServlet("/history")
public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ItemHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * use user_id to retrieve the favorite items from history table response is a
	 * JSONArray show the result of the operation, itemList
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		JSONArray array = new JSONArray(); // result

		String userId = request.getParameter("user_id");

		DBConnection conn = DBConnectionFactory.getConnection();
		
		try {
			// get item set from db
			Set<Item> items = conn.getFavoriteItems(userId);
			
			// transfer items to JSONObject
			for (Item item : items) {
				JSONObject obj = item.toJSONObject();
				obj.append("favorite", true); // agree with front end, need to have a fav field
				array.put(obj);
			}
			//write JSONArray to response
			RpcHelper.writeJsonArray(response, array);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			conn.close();
		}

	}

	/**
	 * use user_id and item_id to add a favorite item in history table response is a
	 * JSONObject show the result of the operation
	 * front end will pass me a JSONObject to show what should be favorite in the request
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	/* form of request: 
		{
			'user_id'
			'favorite':
				['item_id1', ...]
		}
	*/
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		DBConnection conn = DBConnectionFactory.getConnection();

		try {
			//transfer request to JSONObject
			JSONObject obj = RpcHelper.readJSONObject(request);
			
			//every time will only be one item in the request
			String userId = obj.getString("user_id");
			JSONArray array = obj.getJSONArray("favorite");
			String itemId = array.getString(0); // get the only item in the array
			
			conn.setFavoriteItems(userId, itemId); // set in db
			
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS")); // return an object with 1 key
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

	/**
	 * use user_id and item_id to delete a favorite item in history table response
	 * is a JSONObject show the result of the operation
	 * 
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub		
		DBConnection conn = DBConnectionFactory.getConnection();
		
		try {
			JSONObject obj = RpcHelper.readJSONObject(request);
			
			String userId = obj.getString("user_id");
			
			JSONArray array = obj.getJSONArray("favorite");
			String itemId = array.getString(0);
			
			conn.unsetFavoriteItems(userId, itemId);
			
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
			
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			conn.close();
		}
	}

}
