Brief Description of the Implementation:
1.) Accepted the user search string and validated the input.
2.) Constructed the url and invoked the search api using the search query.
3.) Parsed the json response using the GSON jar and fetched the first item id.
4.) Using this item id invoked the product recommendation api and fetched the first 10 products.
5.) The products are captured in a ProductResponse objects.
6.) For each product, the averageOverallRating is captured and set in the ProductResponse objects.
7.) If there is no averageOverallRating for any product I have set it 0 so that the code will not break and continue.
8.) The ProductResponse objects are sorted according to the averageOverallRating and displayed to the user.

Note: User defined exceptions are thrown which make sense to the user for any unexpected behaviour throughout the application.
      Testing is done for various inputs.

Instructions to run:
1.) Switch to root directory of the project - WalmartAPI
2.) Run the below command
	javac -cp ".;./jars/gson-2.3.1.jar" src/*.java
3.) Change the directory to src - (cd src)
4.) Run the below command
	java -cp ".;../jars/gson-2.3.1.jar" HomeworkApi
5.) Enter the search query to see the results.