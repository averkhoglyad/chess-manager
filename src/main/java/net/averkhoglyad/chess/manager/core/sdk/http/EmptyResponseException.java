package net.averkhoglyad.chess.manager.core.sdk.http;

public class EmptyResponseException extends HttpStatusAwareException
{

	public EmptyResponseException( int statusCode, String reasonPhrase )
	{
		super( statusCode, reasonPhrase );
	}

	@Override
	public String getMessage()
	{
		return "Unexpected empty response." + super.getMessage();
	}

}
