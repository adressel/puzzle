package rules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Variable State Independent Decaying Sum (decision heuristic in zChaff)
 * 
 * @author Andreas Dressel
 */
public class VSIDS
{
    public void analyzeFile( String file_name )
    {
        // key: variable name (Long), value: number of appearances (Integer)
        HashMap<Long, Integer> temp = new HashMap<Long, Integer>();
        SortedMap<Long, Integer> vsids = new TreeMap<Long, Integer>( new ValueComparator( temp ) );
        
        BufferedReader reader = null;
        
        try {
            reader = new BufferedReader( new InputStreamReader( new FileInputStream( new File( file_name ) ) ) );
            
            String line = null;
            
            while( (line = reader.readLine() ) != null ) {
                
                if( line.startsWith( "c" ) || line.startsWith( "p" ) ) {
                    continue;
                }
                
                String[] literals = line.split( " " );
                
                for( String literal : literals ) {
                    if( !literal.equals( "0" ) ) {
                        
                        if( temp.containsKey( Long.parseLong( literal ) ) ) {
                            temp.put( Long.parseLong( literal ), temp.get( Long.parseLong( literal ) ) + 1 );
                        } else {
                            temp.put( Long.parseLong( literal ), 1 );
                        }
                    }
                }
            }
            vsids.putAll( temp );
            
            for ( Map.Entry<Long, Integer> entry : vsids.entrySet() ) {
                
                System.out.println( "Variable: " + VariableDictionary.getInstance().getVariableName( Math.abs( entry.getKey() ) ) + ", count: " + entry.getValue() );
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
    }
    
    private class ValueComparator implements Comparator<Long>
    {

        Map<Long, Integer> map;
        
        public ValueComparator( Map<Long, Integer> map )
        {
            this.map = map;
        }

        @Override
        public int compare( Long o1, Long o2 )
        {
            return map.get( o1 ).compareTo( map.get( o2 ) ) > 0 ? 1 : -1;
        }
        
    }
}
