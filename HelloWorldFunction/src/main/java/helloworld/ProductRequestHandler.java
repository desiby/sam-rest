package helloworld;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

/**
 * Handler for requests to Lambda function.
 */
public class ProductRequestHandler{

    Logger logger = Logger.getLogger("Mylogs");
    
    //dependency injection
    private ProductDao dao;


    public void setDao(ProductDao dao) {
        this.dao = dao;
    }

    private ProductDao getProductDao(){
        if (this.dao == null){
            this.dao = new ProductDaoImpl();
        }
        return this.dao;
    }

    //GET /products
    public APIGatewayProxyResponseEvent getAllProducts(APIGatewayProxyRequestEvent input, Context context) {
        List<Product> products = this.getProductDao().listAllProducts(); 
        LambdaLogger lambdalogger = context.getLogger();
        
        try {   
              Gson gson = new GsonBuilder().setPrettyPrinting().create();
              String json = gson.toJson(products);
              
              Map<String, String> headers = new HashMap<String, String>();
              headers.put("Content-Type", "application/json");
              return new APIGatewayProxyResponseEvent().withStatusCode(200).withHeaders(headers).withBody(json);
		} catch(JsonParseException ex) {
            lambdalogger.log(ex.getMessage());
			  return new APIGatewayProxyResponseEvent().withStatusCode(500);
		}
    }

    //POST /product
    public APIGatewayProxyResponseEvent createProduct(APIGatewayProxyRequestEvent request, Context context) {
		LambdaLogger lambdalogger = context.getLogger();
        String body = request.getBody();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Product product;
		
		try {
			  product = gson.fromJson(body, Product.class);
			  this.getProductDao().saveProduct(product);
              Map<String, String> headers = new HashMap<String, String>();
              headers.put("Content-Type", "application/json");
			  return new APIGatewayProxyResponseEvent().withStatusCode(201).withHeaders(headers).withBody(gson.toJson(product));
		} catch ( JsonParseException ex ) {
            lambdalogger.log(ex.getMessage());
			  return new APIGatewayProxyResponseEvent().withStatusCode(500);
		}
	}

    //PUT /product
    public APIGatewayProxyResponseEvent updateProduct(APIGatewayProxyRequestEvent request, Context context) throws JsonParseException {
        LambdaLogger lambdalogger = context.getLogger(); 
        
        try {
              String body = request.getBody(); 
              Gson gson = new GsonBuilder().setPrettyPrinting().create();
              
              Product product = new Product();
              product = gson.fromJson(body, Product.class);
              
              this.getProductDao().saveProduct(product);
              Map<String, String> headers = new HashMap<String, String>();
              headers.put("Content-Type", "application/json");
              return new APIGatewayProxyResponseEvent().withStatusCode(200).withHeaders(headers).withBody(gson.toJson(product));
		} catch(Exception ex) {
            lambdalogger.log(ex.getMessage());;
			  return new APIGatewayProxyResponseEvent().withStatusCode(500);
		}
    }

    //GET /product/{id}
    public APIGatewayProxyResponseEvent getProductById(APIGatewayProxyRequestEvent request, Context context) {
        LambdaLogger lambdalogger = context.getLogger(); 
        String id = request.getPathParameters().get("id"); 
        Product product = this.getProductDao().getProductById(id);
        
        if (product == null){
            logger.severe("NOT FOUND!");
            return new APIGatewayProxyResponseEvent().withStatusCode(404);
        }
        
        try {
			  Gson gson = new GsonBuilder().setPrettyPrinting().create();
			  String json = gson.toJson(product);
             
               Map<String, String> headers = new HashMap<String, String>();
			   headers.put("Content-Type", "application/json");
			  return new APIGatewayProxyResponseEvent().withStatusCode(200).withHeaders(headers).withBody(json);
		} catch(JsonParseException ex) {
			lambdalogger.log(ex.getMessage());;
			return new APIGatewayProxyResponseEvent().withStatusCode(500);
		}
    }

    //DELETE /product
    public APIGatewayProxyResponseEvent deleteProduct(APIGatewayProxyRequestEvent request, Context context) {
        LambdaLogger lambdalogger = context.getLogger(); 

     try{   
        String id = request.getPathParameters().get("id");
        
        this.getProductDao().deleteProduct(id);
        logger.info(id + " succesfully deleted!");
             
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        return new APIGatewayProxyResponseEvent().withStatusCode(200);
     }catch(JsonParseException e){
        lambdalogger.log(e.getMessage());
         return new APIGatewayProxyResponseEvent().withStatusCode(502); 
     }
    }

     
}
