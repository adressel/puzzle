package util;

/**
 *
 * @author Andreas Dressel
 */
public class Literal
{

    long symbol;
    boolean sign;

    public Literal( long symbol, boolean sign )
    {
        this.sign = sign;
        this.symbol = symbol;
    }
    
    public boolean equals( Literal other )
    {
        return (this.symbol == other.symbol);
    }
    
    @Override
    public boolean equals( Object o ) 
    {
        if( !(o instanceof Literal ) ) {
            return false;
        } else {
            return ( this.symbol == ((Literal)o).symbol ) 
                    && ( this.sign == this.sign );
        }
        
    }
}
