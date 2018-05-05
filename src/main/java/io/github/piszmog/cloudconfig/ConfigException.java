package io.github.piszmog.cloudconfig;

/**
 * Exception thrown when an error occurs when operating on the config server.
 * <p>
 * Created by Piszmog on 5/5/2018
 */
public class ConfigException extends Exception
{
    /**
     * Creates a new config exception.
     */
    public ConfigException()
    {
    }

    /**
     * Creates a new config exception.
     *
     * @param message the message associated with the exception
     */
    public ConfigException( final String message )
    {
        super( message );
    }

    /**
     * Creates a new config exception.
     *
     * @param message the message associated with the exception
     * @param cause   the cause of th exception
     */
    public ConfigException( final String message, final Throwable cause )
    {
        super( message, cause );
    }

    /**
     * Creates a new config exception.
     *
     * @param cause the cause of th exception
     */
    public ConfigException( final Throwable cause )
    {
        super( cause );
    }

    /**
     * Creates a new config exception.
     *
     * @param message            the message associated with the exception
     * @param cause              the cause of th exception
     * @param enableSuppression  determines if suppression is enabled
     * @param writableStackTrace determines if the stack is writeable
     */
    public ConfigException( final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace )
    {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
