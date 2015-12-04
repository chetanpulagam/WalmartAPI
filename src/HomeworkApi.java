
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;





public class HomeworkApi {

	private final String apiKey = "rbg8rx55bkr9ey5nkcpvwsrj";


	public static void main(String[] args) throws InvalidSearchQueryException, NoItemsFoundException, InternalServerException, UnsupportedEncodingException {

		HomeworkApi hw = new HomeworkApi();
		hw.search();
	}

	public List<ProductResponse> search() throws InvalidSearchQueryException, NoItemsFoundException, InternalServerException, UnsupportedEncodingException
	{
		System.out.println("Enter product name");
		Scanner sc = new Scanner(System.in);
		String searchQuery = sc.nextLine();
	   
		if(searchQuery==null||searchQuery.isEmpty())
		{
			if(sc!=null)
			{
				sc.close();
			}
				throw new InvalidSearchQueryException();
		}
		
		String searchUrl = "http://api.walmartlabs.com/v1/search?apiKey="+this.apiKey+"&query="+searchQuery;
		
		searchUrl = searchUrl.trim();
		searchUrl = searchUrl.replaceAll("\\s", "%20");
		HttpURLConnection request = null;
		JsonParser jp = new JsonParser(); //from gson
		JsonElement root = null;
		JsonObject rootObject = null;
		JsonElement items = null;
		JsonArray jArr = null;
		JsonObject responseObject=null;
		int itemId = 0;
		try 
		{
			URL url = new URL(searchUrl);
			request = (HttpURLConnection) url.openConnection();
			request.connect();
		
			root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
			rootObject=root.getAsJsonObject();
			items = rootObject.get("items");
			jArr = items.getAsJsonArray();
			for(JsonElement j:jArr)
			{
				responseObject=j.getAsJsonObject();
				itemId = Integer.parseInt(responseObject.get("itemId").toString());
				break;
			}
		
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			//throw new InternalServerException();
		}
		
		finally
		{
			if(sc!=null)
			{
				sc.close();
			}
		}
		
		return productRecommendation(itemId);
	
	}

	public List<ProductResponse> productRecommendation(int itemId) throws InternalServerException
	{
		String productRecUrl = "http://api.walmartlabs.com/v1/nbp?apiKey="+this.apiKey+"&itemId="+itemId;
		HttpURLConnection request = null;
		Gson gson = new Gson();
		JsonParser jp = new JsonParser(); //from gson
		JsonElement root = null;
		JsonArray jArr = null;
		try 
		{
			URL url = new URL(productRecUrl);
			request = (HttpURLConnection) url.openConnection();
			request.connect();
			
			root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
			jArr = root.getAsJsonArray();
		} 
		catch (Exception e)
		{
			throw new InternalServerException();
		}
		
		int count=0;
		List<ProductResponse> products = new ArrayList<ProductResponse>();
		for(JsonElement j:jArr)
		{
			if(count==10)
			{
				break;
			}
			else
			{
				count++;
				JsonObject jObj=null;
				JsonElement productId = null;
				try
				{
					jObj=j.getAsJsonObject();
					products.add(gson.fromJson(jObj, ProductResponse.class));
					productId = jObj.get("itemId");
				}
				catch(Exception e)
				{
					if(count==10)
					{
						throw new InternalServerException();
					}
					else
					{
						continue;
					}
				}
			
				try
				{
					review(Integer.parseInt(productId.toString()),products);
				}
				catch(ReviewException r)
				{
					continue;
				}
			}
		}
		
		Collections.sort(products);
		System.out.println("products");
		for(ProductResponse pr:products)
		{
			System.out.println("Item Id:"+pr.getItemId()+" Item Name: "+pr.getName()+" Item Rating:"+pr.getAverageReviewsRating());
		}
		return products;
	}

	public void review(Integer productId,List<ProductResponse> products) throws ReviewException
	{
		String reviewUrl = "http://api.walmartlabs.com/v1/reviews/"+productId+"?apiKey="+this.apiKey;
		HttpURLConnection request = null;
		JsonParser jp = new JsonParser(); //from gson
		JsonElement root = null;
		JsonElement avgRatingValue = null;
		try 
		{
			URL url = new URL(reviewUrl);
			request = (HttpURLConnection) url.openConnection();
			request.connect();
		
			root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
			JsonObject jObj=root.getAsJsonObject();
			JsonElement reviewStatistics=jObj.get("reviewStatistics");
			JsonObject averageOverallRating = reviewStatistics.getAsJsonObject();
			avgRatingValue = averageOverallRating.get("averageOverallRating");
		}
		catch(Exception e)
		{
			products.get(products.size()-1).setAverageReviewsRating(0.00);
			throw new ReviewException();
		}

		String sub = avgRatingValue.toString();
		sub = sub.substring(1, sub.length()-1);
		
		double rating = Double.parseDouble(sub);
		
		products.get(products.size()-1).setAverageReviewsRating(rating);

	}
}
