package net.averkhoglyad.chess.manager.core.sdk.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ErrorResponseException extends HttpStatusAwareException
{

	private final Map<String, Object> meta;

	public ErrorResponseException( int statusCode, String reasonPhrase )
	{
		this( statusCode, reasonPhrase, Collections.emptyMap() );
	}

	public ErrorResponseException( int statusCode, String reasonPhrase, Map<String, Object> meta )
	{
		super( statusCode, reasonPhrase );
		this.meta = Collections.unmodifiableMap( new HashMap<>( meta ) );
	}

	public Map<String, Object> getMeta()
	{
		return meta;
	}

}
