package entity;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item.ItemBuilder;

/**
 * purpose: use item class to store the info from the TM API, so the data can be more clean for us to use
 * we only store the needed info in our item class and ignore those redundant info from the response JSONObject
 * 
 * 
 * when constructing this class, we can set field later
 * using itembulder.set()...
 * Item it = itembuilder.build();
 * 
 * */

public class Item {
	
	private String itemId;
	private String name;
	private double rating;
	private String address;
	private Set<String> categories;  // may have multiple types.
	private String imageUrl;
	private String url;
	private double distance;
	
	// transfer to a JSONObject
	
	public JSONObject toJSONObject(){
		// obj is a hashmap, so we can put item.fields into this object
		JSONObject obj = new JSONObject();
		
		//JSON operation may have jason exception
		try {
			obj.put("item_id", itemId);
			obj.put("name", name);
			obj.put("rating", rating);
			obj.put("address", address);
			obj.put("categories", new JSONArray(categories)); // can receive a Collection object to construct array
			obj.put("image_url", imageUrl);
			obj.put("url", url);
			obj.put("distance", distance);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return obj;
	}

	//public getters for getting Item fields, can not set in here. set in builder
	public String getItemId() {
		return itemId;
	}
	public String getName() {
		return name;
	}
	public double getRating() {
		return rating;
	}
	public String getAddress() {
		return address;
	}
	public Set<String> getCategories() {
		return categories;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public String getUrl() {
		return url;
	}
	public double getDistance() {
		return distance;
	}
	
	
	// private constructor for builder pattern	
	private Item(ItemBuilder builder) {
		//put tmp info in builder to the current Item object
		this.itemId = builder.itemId;
		this.name = builder.name;
		this.rating = builder.rating;
		this.address = builder.address;
		this.categories = builder.categories;
		this.imageUrl = builder.imageUrl;
		this.url = builder.url;
		this.distance = builder.distance;
	}
	
	/**
	 * This is a builder pattern in Java.
	 * 
	 * how to use:
	 * new a builder object, use setter to set fields in builder
	 * use build to build an Item object
	 * 
	 */
	//static: if no Item instance we can still have a builder, Item class exist then ItemBuilder exist
	//public: class in other package need to use this class to build Item. default can only be accessed in package
	public static class ItemBuilder {
		private String itemId;
		private String name;
		private double rating;
		private String address;
		private Set<String> categories;  // may have multiple types.
		private String imageUrl;
		private String url;
		private double distance;
		
		//builder
		public Item build() {
			return new Item(this);  //pass a builder 
		}
		
		// auto generated setters
		// change return to return this, so can call set easier: builder.set1().set2()...
		public ItemBuilder setItemId(String itemId) {
			this.itemId = itemId;
			return this;
		}
		public ItemBuilder setName(String name) {
			this.name = name;
			return this;
		}
		public ItemBuilder setRating(double rating) {
			this.rating = rating;
			return this;
		}
		public ItemBuilder setAddress(String address) {
			this.address = address;
			return this;
		}
		public ItemBuilder setCategories(Set<String> categories) {
			this.categories = categories;
			return this;
		}
		public ItemBuilder setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
			return this;
		}
		public ItemBuilder setUrl(String url) {
			this.url = url;
			return this;
		}
		public ItemBuilder setDistance(double distance) {
			this.distance = distance;
			return this;
		}
	}


}
