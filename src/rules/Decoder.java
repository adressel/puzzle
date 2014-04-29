package rules;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Andreas
 */
public class Decoder
{
    public enum SolverType {
        ZCHAFF, SIEGE
    }
    
    private void decodeSolution( String solution_file_name, String output_file_name, String map_file_name, SolverType solver_type ) 
    {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        
        try {
            reader = new BufferedReader( new InputStreamReader( new FileInputStream( new File( solution_file_name ) ) ) );
            writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File( output_file_name ) ) ) );
            
            String line = null;
            
            switch( solver_type ) {
                case ZCHAFF:
                    while( (line = reader.readLine() ) != null ) {
                
                        if( line.startsWith( "Instance Satisfiable" ) ) { // works for zChaff solver output
                            break;
                        }
                    }
                    line = reader.readLine();
                    break;
                case SIEGE:
                    line = reader.readLine();
                    break;
                default:
                    System.out.println( "Solver type not supported" );
                    System.exit( 0 );
            }
            
            if( line == null ) {
                System.out.println( "No solution found" );
                System.exit( 0 );
            } else {
            
                Map<Long, String> map = getMapping( map_file_name );            
                SortedSet<String> results = new TreeSet<String>( new Comparator<String>() {

                    @Override
                    public int compare( String o1, String o2 )
                    {
                        if( o1 == null || o2 == null ) {
                            return 1;
                        }
                        return o1.compareTo( o2 );
                    }
                });

                String[] l = line.split( " " );
                for( String s : l ) {
                    if( s.matches( "[0-9]+" ) ) {

                        results.add( map.get( Long.parseLong( s ) ) );

                    }
                }

                for( String result : results ) {

                    writer.write( result );
                    writer.newLine();

                }
                writer.flush();
            
            }
            
        } catch( FileNotFoundException ex) {
            ex.printStackTrace();
        } catch( IOException ex ) {
            ex.printStackTrace();
        } finally {
            if( reader != null) {
                try {
                    reader.close();
                } catch( IOException ex ) {
                    ex.printStackTrace();
                }
            }
            if( writer != null ) {
                try {
                    writer.close();
                } catch( IOException ex ) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    private Map<Long, String> getMapping( String map_file_name )
    {
        Map<Long, String> map = new HashMap<Long, String>();
        
        
        BufferedReader reader = null;
        
        try {
            reader = new BufferedReader( new InputStreamReader( new FileInputStream( new File( map_file_name ) ) ) );
            
            String line = reader.readLine();
            while( line != null ) {
                
                // must have format <variable_name> : <variable_symbol>
                String[] l = line.split( " : ");
                map.put( Long.parseLong( l[1] ), l[0] );
                
                line = reader.readLine();
            }
            
            
        } catch( FileNotFoundException ex ) {
            ex.printStackTrace();
        } catch( IOException ex ) {
            ex.printStackTrace();
        } finally {
            if( reader != null ) {
                try {
                    reader.close();
                } catch( IOException ex ) {
                    ex.printStackTrace();
                }
            }
        }
        
        return map;
    }
    
    public static void main( String[] args ) 
    {
        Decoder decoder = new Decoder();
        
        SolverType solver = null;
        if( args[3].equals( "zchaff" ) ) {
            solver = SolverType.ZCHAFF;
        } else if ( args[3].equals( "siege" ) ) {
            solver = SolverType.SIEGE;
        } else {
            System.out.println( "Solver type not supported" );
            System.exit( 0 );
        }
        
        decoder.decodeSolution( args[0], args[1], args[2], solver );
    }
    
}
