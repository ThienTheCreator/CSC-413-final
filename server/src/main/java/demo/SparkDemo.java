package demo;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;
import static com.mongodb.client.model.Filters.*;


public class SparkDemo {
  static Gson gson = new Gson();
  static List<Data> dataList = new ArrayList<>();
  public static void main(String[] args) {
    port(1235);
    webSocket("/ws",WebSocketHandler.class);

    // open connection
    MongoClient mongoClient = new MongoClient("localhost", 27017);
    // get ref to database
    MongoDatabase db = mongoClient.getDatabase("MyDatabase");
    // get ref to collection
    MongoCollection<Document> myCollection = db.getCollection("MyCollection");
    // get ref to collection
    MongoCollection<Document> myInquiry = db.getCollection("MyInquiry");

    post("/submit-post", (req,res) -> {
      Data tempData = gson.fromJson(req.body(), Data.class);
      String tempEmail = tempData.email;
      String tempInfo = tempData.info;

      Document doc = new Document("email", tempEmail)
              .append("info", tempInfo);

      // insert document into collection
      myCollection.insertOne(doc);
      WebSocketHandler.broadcast(gson.toJson(dataList));
      return "Success";
    });

    get("/get-posts", (req, res) -> {
      List<Document> docs = myCollection.find().limit(100).into(new ArrayList<Document>());
      dataList = new ArrayList<Data>();
      docs.forEach(result -> {
        Data temp = gson.fromJson(result.toJson(),Data.class);
        dataList.add(temp);
      });
      WebSocketHandler.broadcast("Get");

      return gson.toJson(dataList);
    });

    post("/submit-email", (req, res) -> {
      List<Document> docs = myCollection.find().limit(100).into(new ArrayList<Document>());
      List<Data> tempDataList = new ArrayList<Data>();

      Data tempData = gson.fromJson(req.body(), Data.class);
      String tempEmail = tempData.email;
      String tempInfo = tempData.info;

      docs.forEach(result -> {
        Data temp = gson.fromJson(result.toJson(),Data.class);
        if(temp.email.equals(tempEmail))
          tempDataList.add(temp);
      });

      return gson.toJson(tempDataList);
    });

    post("/submit-inquiry", (req, res) -> {
      Inquiry temp = gson.fromJson(req.body(), Inquiry.class);
      String tempEmail = temp.email;
      String tempInquiry = temp.inquiry;

      Document doc = new Document("email", tempEmail)
              .append("inquiry", tempInquiry);

      // add an inquiry
      myInquiry.insertOne(doc);
      System.out.println(doc);

      return "post inquiry";
    });

    get("/get-inquiries", (req, res) -> {
      List<Document> docs = myInquiry.find().limit(100).into(new ArrayList<Document>());
      List<Inquiry> tempInquiryList = new ArrayList<Inquiry>();
      docs.forEach(result -> {
        Inquiry temp = gson.fromJson(result.toJson(),Inquiry.class);
        System.out.println(temp.email + " " + req.queryMap("email").value());
        if(temp.email.equals(req.queryMap("email").value()))
          tempInquiryList.add(temp);
      });
      WebSocketHandler.broadcast("Get");

      return gson.toJson(tempInquiryList);
    });

    get("/get-by-email", (req, res) -> {
      List<Document> docs = myCollection.find().limit(100).into(new ArrayList<Document>());
      List<Data> tempDataList = new ArrayList<Data>();

      docs.forEach(result -> {
        Data temp = gson.fromJson(result.toJson(),Data.class);
        if(temp.email.equals((req.queryMap("email").value())))
          tempDataList.add(temp);
      });

      WebSocketHandler.broadcast("Get");

      return gson.toJson(tempDataList);
    });

    post("/delete", (req, res) -> {
      String tempEmail = req.queryMap("email").value();
      myCollection.deleteOne(Filters.eq("email", tempEmail));
      return "Hello" + req.queryMap("email").value();
    });
  }
}
