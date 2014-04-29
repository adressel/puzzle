package util;

import rules.VariableDictionary;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * A CNF represents a set of unique clauses (unique by their contained literals)
 * that are combined by the logic AND operator.
 *
 * @author Andreas Dressel
 */
public class Cnf
{

    private Set<Clause> clauses; //use set instead?
    private final VariableDictionary dictionary;
    private int helper_variable_counter;

    public Cnf( final VariableDictionary dictionary )
    {
        this.dictionary = dictionary;
        this.clauses = new HashSet<Clause>(); // size is 16 by default, should be twice my expectation though
        this.helper_variable_counter = 0;
    }
    
    public Cnf( final VariableDictionary dictionary, int size )
    {
        this.dictionary = dictionary;
        this.clauses = new HashSet<Clause>(size);
        this.helper_variable_counter = 0;
    }
    
    private int getNextHelperVarialbeNumber() 
    {
        this.helper_variable_counter += 1;
        return this.helper_variable_counter;
    }

    public Cnf combineCNF( Cnf other, String operator )
    {        
        switch ( operator ) {
            case "AND":
                this.clauses.addAll( other.getClauses() );
                break;
            case "OR":
                Set<Clause> temp = new HashSet<Clause>();
                
                if( this.clauses.isEmpty() ) {
                    this.clauses = other.getClauses();
                } else if( this.clauses.size() == 1 || other.getClauses().size() == 1 ) {
                    
                    for( Clause c1 : this.clauses ) {
                        for( Clause c2 : other.getClauses() ) {
                            for( Literal l1 : c1.getLiterals() ) {
                                for( Literal l2 : c2.getLiterals() ) {
                                    if( l1.symbol != l2.symbol ) {
                                        Clause clause = new Clause();
                                        clause.addLiteral( l1 );
                                        clause.addLiteral( l2 );
                                        temp.add( clause );
                                    } 
                                    // - (A  OR  A) -> A
                                    // - (~A OR ~A) -> ~A
                                    else if( l1.sign == l2.sign ) {
                                        temp.add( new Clause( l1 ) );
                                    }
                                    // - (~A OR  A) -> 1, remove clause
                                }
                            }
                        }
                    }
                    this.clauses = temp;
                } else if( this.clauses.size() > 1 && other.clauses.size() > 1 ) {
                    
                    long symbol = this.dictionary.getSymbolForVariable( "helper_var" + getNextHelperVarialbeNumber() );
                    
                    Cnf zCnf = new Cnf( this.dictionary );
                    zCnf.addClause( new Clause( new Literal( symbol, true ) ), "AND" );
                    zCnf = zCnf.combineCNF( this, "OR" );
                    
                    Cnf notZCnf = new Cnf( this.dictionary );
                    notZCnf.addClause( new Clause( new Literal( symbol, false ) ), "AND" );
                    notZCnf = notZCnf.combineCNF( other, "OR" );
                    
                    Cnf cnf = zCnf.combineCNF( notZCnf, "AND" );
                    this.clauses = cnf.getClauses();
                }
                
                break;
            default:
                System.err.println( "Unknown operator: " + operator );
                break;
        }

        return this;
    }

    public void addClause( Clause clause, String operator )
    {
        switch ( operator ) {
            case "AND":
                this.clauses.add( clause );
                break;
            case "OR":
                Set<Clause> temp = new HashSet<Clause>();
                for ( Clause c : this.clauses ) {
                    for ( Literal l1 : c.getLiterals() ) {
                        for ( Literal l2 : clause.getLiterals() ) { //  unused l2, maybe there's something todo...
                            Clause cl = new Clause();
                            cl.addLiteral( l1 );
                            temp.add( cl );
                        }
                    }
                }
                this.clauses = temp;
                break;
            default:
                System.err.println( "Unknown operator: " + operator );
                break;
        }
    }
    
    public int getNumberOfClauses() 
    {
        return this.clauses.size();
    }
    
    public void print() 
    {
        System.out.println( "Number of clauses: " + getNumberOfClauses());
        int i = 0;
        for( Clause c : clauses ) {
            c.print();
            if( i%5 == 0 ) {
                System.out.println( "" );
            }
            i++;
        }
    }

    public Set<Clause> getClauses()
    {
        return this.clauses;
    }
    
    public void encodeInDIMACS( String file_name, long number_of_variables )
    {
        
        BufferedWriter writer = null;
        
        try {
            writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File( file_name ) ) ) );
            
            // write comment lines including the file name and one empty line
            writer.write( "c " + file_name );
            writer.newLine();
            writer.write( "c" );
            writer.newLine();
            
            // write the problem form cnf, the number of variables and the number of clauses
            writer.write( "p cnf " + this.dictionary.getVariableCount() + " " + this.clauses.size() );
            writer.newLine();
            
            // write clauses to file, each of them followed by 0
            for( Clause c : this.clauses ) {
                for( Literal l : c.getLiterals() ) {
                    if( !l.sign ) {
                        writer.write( "-" );
                    }
                    writer.write( Long.toString( l.symbol ) + " " );
                }
                writer.write( "0");
                writer.newLine();
                writer.flush();
            }
                        
        } catch( FileNotFoundException ex ) {
            ex.printStackTrace();
        } catch( IOException ex ) {
            ex.printStackTrace();
        } finally {
            if(writer != null) {
                try {
                    writer.close();
                } catch(IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
