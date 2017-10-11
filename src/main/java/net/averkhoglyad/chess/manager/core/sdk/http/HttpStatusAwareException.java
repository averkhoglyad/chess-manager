package net.averkhoglyad.chess.manager.core.sdk.http;

public abstract class HttpStatusAwareException extends Exception
{

	private final int statusCode;
	private final String reasonPhrase;

	public HttpStatusAwareException( int statusCode, String reasonPhrase )
	{
		this.statusCode = statusCode;
		this.reasonPhrase = reasonPhrase;
	}

	@Override
	public String getMessage()
	{
		return "Status: " + statusCode + " " + reasonPhrase;
	}

	public int getStatusCode()
	{
		return statusCode;
	}

	public String getReasonPhrase()
	{
		return reasonPhrase;
	}

}
