package rules;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Andreas Dressel
 */
public class VariableDictionary
{
    private static VariableDictionary _instance = null;
    private long count;
    private final Map<String, Long> variables;
    
    private VariableDictionary() 
    {
        this.count = 0;
        this.variables = new HashMap<String, Long>();
    }
    
    public static synchronized VariableDictionary getInstance() 
    {
        if( _instance == null ) {
            _instance = new VariableDictionary();
        }
        return _instance;
    }
    
    private long getNextVariableSymbol() 
    {
        count += 1;
        return count;
    }
    
    public boolean containsKey( String key ) 
    {
        return this.variables.containsKey( key );
    }
    
    public Long get( String key ) 
    {
        return this.variables.get( key );
    }
    
    public long getVariableCount()
    {
        return this.count;
    }
    
    public void print() 
    {
        int i = 1;
        for( String key : this.variables.keySet() ) {
            System.out.print( key + " => " + this.variables.get( key ) + " " );
            if( i%5 == 0 ) {
                System.out.println();
            }
            i++;
        }
        System.out.println();
    }
    
    public long getSymbolForVariable( String variable_name )
    {
        if( this.variables.keySet().contains( variable_name ) ) {
            return this.variables.get( variable_name );
        } else {
            long symbol = getNextVariableSymbol();
            this.variables.put( variable_name , symbol );
            return symbol;
        }
    }
    
    public Map<Long, String> getMapping()
    {
        Map<Long, String> map = new HashMap<Long, String>();
        
        for( Map.Entry<String, Long> entry : variables.entrySet() ) {
            map.put( entry.getValue(), entry.getKey() );
        }
        
        return map;
    }
    
    public String getVariableName( long symbol )
    {
        for ( Map.Entry<String, Long> entry : variables.entrySet() ) {
            
            if( entry.getValue().equals( symbol ) ) {
                return entry.getKey();
            }
        }
        
        return "";
    }
    
    public void writeToFile( String file_name)
    {
        BufferedWriter writer = null;
        
        try {
            writer = new BufferedWriter(new OutputStreamWriter( new FileOutputStream( new File( file_name ) ) ) );
            
            
            Object[] keys = variables.keySet().toArray();
            Arrays.sort( keys, new Comparator<Object>() {

                @Override
                public int compare( Object o1, Object o2 )
                {
                    return o1.toString().compareTo( o2.toString() );
                }
            });
            
            
            for( Object key : keys ) {
                
                writer.write( key.toString() + " : " + variables.get( key.toString() ) );
                writer.newLine();
            }
            writer.flush();
            
            
        } catch( FileNotFoundException ex ) {
            ex.printStackTrace();
        } catch( IOException ex ) {
            ex.printStackTrace();
        } finally {
            
            if( writer != null ) {
                try {
                    writer.close();
                } catch( IOException ex ) {
                    ex.printStackTrace();
                }
            }
        } 
    }
}
