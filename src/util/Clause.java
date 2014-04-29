package util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A clause represents a set of unique literals (unique by their symbol
 * property) that are connected by the logic OR operator.
 *
 * @author Andreas Dressel
 */
public class Clause
{

    private final Set<Literal> literals; //maybe use set?

    public Clause()
    {
        this.literals = new HashSet<Literal>();
    }

    public Clause( Literal l )
    {
        this.literals = new HashSet<Literal>();
        this.literals.add( l );
    }

    public void addLiteral( Literal l )
    {
        if ( !this.literals.contains( l ) ) {
            this.literals.add( l );
        }
    }
    
    public void addAll( Collection<Literal> literals )
    {
        this.literals.addAll( literals );
    }

    public Set<Literal> getLiterals()
    {
        return this.literals;
    }
    
    // Set uses this method primarily to find out, if two Clauses are the same.
    // @Todo: find a solution for implementing hashCode(); calling equlas is less
    //        efficient.
    @Override
    public int hashCode() 
    {
        return 0;
    }
    
    @Override
    public boolean equals( Object o )
    {
        if( !( o instanceof Clause ) ) {
            return false;
        } else {
            return this.literals.containsAll( ( (Clause)o).literals );
        }

    }
    
    public void print() 
    {
        System.out.print( " Literals " );
        for( Literal l : literals ) {
            if( !l.sign ) {
                System.out.print( "~" );
            }
            System.out.print( l.symbol );
        }
        //System.out.println( "" );
    }
}
