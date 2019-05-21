package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * This function used to check if the session exist, if yes no need to login again
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		DBConnection conn = DBConnectionFactory.getConnection();

		try {
			// if not exist, do not creat new
			HttpSession session = request.getSession(false);
			
			JSONObject obj = new JSONObject();
			if (session != null) {
				String userId = session.getAttribute("user_id").toString();
				obj.put("status", "OK").put("user_id", userId).put("name", conn.getFullname(userId));
			} else {
				obj.put("status", "Invalid Session");
				response.setStatus(403);
			}			
			RpcHelper.writeJsonObject(response, obj);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

	/**
	 * This function call db to verify the encoded pwd and creat a sessionS
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		DBConnection conn = DBConnectionFactory.getConnection();

		try {
			JSONObject objin = RpcHelper.readJSONObject(request);

			// we assume front end have these field
			String userId = objin.getString("user_id");
			// pwd encode at front end, here is not a explicit pwd same as in db
			String password = objin.getString("password");

			JSONObject obj = new JSONObject();

			if (conn.verifyLogin(userId, password)) {
				HttpSession session = request.getSession(); // default is true, means if not exist will creat one
				session.setAttribute("user_id", userId);
				session.setMaxInactiveInterval(600);

				obj.put("status", "OK").put("user_id", userId).put("name", conn.getFullname(userId));
//				response.setStatus(200); no need to write, auto respond
			}	else if (!conn.isConnected()) {
				obj.put("status", "DB Connection Error");
				response.setStatus(500);
			}  else {
				obj.put("status", "User Doesn't Exist");
				response.setStatus(401);
			}

			RpcHelper.writeJsonObject(response, obj);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

}
