package nearsoft.academy.bigdata.recommendation;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.nio.*;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;

public class MovieRecommender {
    int reviews;
    HashMap<String, Integer> productsID;
    HashMap<Integer, String> idProducts;
    HashMap<String, Integer> usersID;
    
    public MovieRecommender(String path) {
        reviews = 0;
        productsID = new HashMap<String, Integer>();
        idProducts = new HashMap<Integer, String>(); 
        usersID = new HashMap<String, Integer>();
        readFile(path);
    }
    
    private void readFile(String path) { 
        BufferedReader objReader = null;
        try {
            String strCurrentLine;
            objReader = new BufferedReader(new FileReader(path));
           
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter("dataset.csv"));
                String score = "";
                String user = "";
                String product = "";

                while ((strCurrentLine = objReader.readLine()) != null) {
                    if (strCurrentLine.contains("review/score")) {
                        score = strCurrentLine.split(" ")[1];
                        reviews++;
                    }
                    
                    else if (strCurrentLine.contains("review/userId")) {
                        user = strCurrentLine.split(" ")[1];
                        if (!usersID.containsKey(user)) {
                            usersID.put(user, usersID.size());
                        }
                        user = usersID.get(user).toString();
                    }
                    
                    else if (strCurrentLine.contains("product/productId")) {
                        product = strCurrentLine.split(" ")[1];
                        if (!productsID.containsKey(product)) {
                            productsID.put(product, productsID.size());
                            idProducts.put(idProducts.size(), product);
                        }
                        product = productsID.get(product).toString();
                    }

                    else if (strCurrentLine.contains("review/text")) {
                        out.write(user + "," + product + "," + score +  '\n');
                    }
 
                }
                out.close();

            }
            catch (IOException e) {
            }
            
            
            System.out.println(reviews);
            System.out.println(productsID.size());
            System.out.println(usersID.size());
        } 
        catch (IOException e) {
            
            e.printStackTrace();
            
        } 
        finally {
            
            try {
                if (objReader != null)
                objReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } 
    }
    
    
    public int getTotalReviews() {
        return reviews;
    }
    
    public int getTotalProducts() {
        return productsID.size();
    }
    
    public int getTotalUsers() {
        return usersID.size();
    }
    
    public List<String> getRecommendationsForUser(String user) throws IOException, TasteException {
        DataModel model = new FileDataModel(new File("dataset.csv"));
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
        UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);

        List<String> recommendations = new ArrayList<String>();
        for (RecommendedItem recommendation : recommender.recommend(usersID.get(user), 3)) {
            recommendations.add(idProducts.get((int)(recommendation.getItemID())));
        }

        return recommendations;
    }
}