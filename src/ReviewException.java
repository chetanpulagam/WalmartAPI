

public class ReviewException extends Exception {

	public ReviewException()
	{
		super("Cannot fetch reviews for this product id");
	}
}
