package helloworld;

import java.util.List;
import java.util.UUID;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;

public class ProductDaoImpl implements ProductDao {

    private AmazonDynamoDB client;
	private DynamoDBMapper mapper;

    private static final boolean RUN_LOCALLY = false;
    
    public ProductDaoImpl(){
        
       if(RUN_LOCALLY){
           this.client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
             new AwsClientBuilder.EndpointConfiguration("http://dynamodb:8000", "us-east-1"))
              .build();
       }else{
          this.client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
       }
        
        this.mapper = new DynamoDBMapper(this.client);
    }
    
    @Override
    public List<Product> listAllProducts() {
        return this.mapper.scan(Product.class, new DynamoDBScanExpression());
    }

    @Override
    public Product getProductById(String id) {
        return this.mapper.load(Product.class, id);
    }

    @Override
    public void saveProduct(Product product) {
        if(product.getId() == null) {
			product.setId(UUID.randomUUID().toString());
		}
        this.mapper.save(product);
    }

    @Override
    public void deleteProduct(String id) {
        Product product = getProductById(id);
        if (product != null){
            this.mapper.delete(product);
        }
    }
    
    
}
