package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.naming.java.javaURLContextFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

/*
 * in TM API class we try to do these things:
 * 1, get data from the api , receive as an JSONArray, each JSONObject in the array is a event
 * 2, try to parse the data from a JSONObject, like address, categories to a strring
 * 3, try to use the parsed info to form a ItemList
 * 4, test api as a local java program
 * 
 * */
public class TicketMasterAPI {
	// define constants for requesting the api throught http link
	// detail:
	// https://developer.ticketmaster.com/products-and-docs/apis/discovery-api/v2/#search-events-v2

	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = ""; // no restriction
	private static final String API_KEY = "KrPh4M1oa48SFYGxPAGrAL3lR97G9hR8"; // this is my ticket master Consumer Key

	public List<Item> search(double lat, double lon, String keyword) {
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		try {
			keyword = java.net.URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// "apikey=qqPuP6n3ivMUoT9fPgLepkRMreBcbrjV&latlong=37,-120&keyword=event&radius=50"
		String query = String.format("apikey=%s&latlong=%s,%s&keyword=%s&radius=%s", API_KEY, lat, lon, keyword, 100);
		String url = URL + "?" + query;

		try {
			// use an URL object to oprn the url and returns a connection
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");// default is get

			// here we actually connect the server. to execute the request we can use:
			// .connect(); getResponseCode(); getInputStream(); getOutputStream();
			System.out.println("Sending request to url: " + url);

			int responseCode = connection.getResponseCode();

			System.out.println("Response code: " + responseCode);

			if (responseCode != 200) {
				return new ArrayList<>();
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuilder response = new StringBuilder();

			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

			JSONObject obj = new JSONObject(response.toString());

			// check if the field we need exists
			//_embedded-> events(array) -> event(object)
			if (!obj.isNull("_embedded")) {
				JSONObject embedded = obj.getJSONObject("_embedded");
				return getItemList(embedded.getJSONArray("events"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// if fail return an empty list
		return new ArrayList<>();
	}

	// this function is only used to test the tmAPI's response
	private void queryAPI(double lat, double lon) {
		List<Item> events = search(lat, lon, null);

		for (Item event : events) {
			System.out.println(event.toJSONObject());
		}
	}

	/**
	 * Helper methods: to get a field from an event JSONObject
	 * Address, ImgUrls, Categories, 
	 */
	
	//returns a string of one of the venues of a event
	private String getAddress(JSONObject event) throws JSONException {
		//every object still have a _embeded field, below that the venues is an array of events' venues
		//for one event may have many venues, this mathod check all the venues and return the first nonempty venue 
		if (!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");
			if (!embedded.isNull("venues")) {
				JSONArray venues = embedded.getJSONArray("venues");
				
				//now each obj in venues is a event's venue
				for (int i = 0; i < venues.length(); ++i) {
					JSONObject venue = venues.getJSONObject(i);
					StringBuilder builder = new StringBuilder();
					if (!venue.isNull("address")) {
						JSONObject address = venue.getJSONObject("address");
						if (!address.isNull("line1")) {
							builder.append(address.getString("line1"));
						}

						if (!address.isNull("line2")) {
							builder.append(",");
							builder.append(address.getString("line2"));
						}

						if (!address.isNull("line3")) {
							builder.append(",");
							builder.append(address.getString("line3"));
						}
					}

					if (!venue.isNull("city")) {
						JSONObject city = venue.getJSONObject("city");
						builder.append(",");
						builder.append(city.getString("name"));
					}

					String result = builder.toString();
					if (!result.isEmpty()) {
						return result;
					}
				}
			}
		}
		return "";

	}

	private String getImageUrl(JSONObject event) throws JSONException {
		if (!event.isNull("images")) {
			JSONArray array = event.getJSONArray("images");
			for (int i = 0; i < array.length(); i++) {
				JSONObject image = array.getJSONObject(i);
				if (!image.isNull("url")) {
					return image.getString("url");
				}
			}
		}
		return "";
	}

	private Set<String> getCategories(JSONObject event) throws JSONException {

		Set<String> categories = new HashSet<>();
		if (!event.isNull("classifications")) {
			JSONArray classifications = event.getJSONArray("classifications");
			for (int i = 0; i < classifications.length(); ++i) {
				JSONObject classification = classifications.getJSONObject(i);
				if (!classification.isNull("segment")) {
					JSONObject segment = classification.getJSONObject("segment");
					if (!segment.isNull("name")) {
						categories.add(segment.getString("name"));
					}
				}
			}
		}
		return categories;
	}

//Convert JSONArray to a list of item objects.
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();
		Set<String> visited = new HashSet<String>(); // dedup
		
		for (int i = 0; i < events.length(); ++i) {
			JSONObject event = events.getJSONObject(i);
			
			//event with same name may hold on multple days, so get only one of the event
			if (visited.contains(event.getString("name"))) {
				continue;
			} else {
				visited.add(event.getString("name"));
			}
			
			ItemBuilder builder = new ItemBuilder();
			if (!event.isNull("id")) {
				builder.setItemId(event.getString("id"));
			}
			if (!event.isNull("name")) {
				builder.setName(event.getString("name"));
			}
			if (!event.isNull("url")) {
				builder.setUrl(event.getString("url"));
			}
			if (!event.isNull("distance")) {
				builder.setDistance(event.getDouble("distance"));
			}
			
//			builder.setAddress(getAddress(event));
//			builder.setCategories(getCategories(event));
//			builder.setImageUrl(getImageUrl(event));
			// we make the builder's setter returns ItemBuilder, so we can use . to call multiple functions
			// or we need to call it seperately
			builder.setAddress(getAddress(event))
				   .setCategories(getCategories(event))
				   .setImageUrl(getImageUrl(event));
			
			// build() returns an Item, we add it to the list
			itemList.add(builder.build());
		}
		return itemList;
	}

	// this main function is used to test the api
	public static void main(String[] args) {
		TicketMasterAPI tmApi = new TicketMasterAPI();
		// Mountain View, CA
		// tmApi.queryAPI(37.38, -122.08);
		// London, UK
		// tmApi.queryAPI(51.503364, -0.12);
		// Houston, TX

		// test function try to print response of tm
		tmApi.queryAPI(29.682684, -95.295410);
	}
}
