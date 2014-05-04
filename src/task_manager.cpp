// Andreas Dressel

#include <cstdlib>
#include <iostream>
#include <fstream>
#include <cstring>
#include <string>
#include <queue>
#include <map>
#include <set>

#include <boost/algorithm/string/split.hpp>
#include <boost/algorithm/string/classification.hpp>


using namespace std;


void populate_dictionary( string file_name, std::map<string, int> &variable_dictionary_string_int, std::map<int, string> &variable_dictionary_int_string )
{
    ifstream in( file_name.c_str() );
    
    if( !in.is_open() ) 
    {
        cout << "cannot open file containing dictionary" << endl;
        exit( 0 );
    }
    
    string line;
    
    while( in.good() )
    {
        getline( in, line );
        int len = line.find( " : " );
        
        if( len != string::npos ) {
            string s = line.substr( 0, len );
            int i = atoi( line.substr( len+3, line.size()-len ).c_str() );
            
            variable_dictionary_string_int.insert( std::pair<string, int>( s, i ) );
            variable_dictionary_int_string.insert( std::pair<int, string>( i, s ) );
            
            // cout << "s: " << s << ", i: " << i << endl;
        }
    }
    
    in.close();
}


void collect_operations_from_solution_file( std::set<string> &all_operations, std::set<string> &ops, int &number_of_operations )
{
    std::set<string>::iterator it;
    for( it = ops.begin(); it != ops.end(); ++it )
    {
        int pos1 = (*it).find( "step:" );
        int pos2 = (*it).find( " )" );
        
        int step = atoi( (*it).substr( pos1+5, pos2-pos1 ).c_str() );
        string s = *it;
        all_operations.insert( s.replace( pos1+5, pos2-pos1, to_string( step+number_of_operations ).c_str() ) );
    }
    number_of_operations += ops.size();
}


void modify_cnf_file( string file_name, std::set<string> state )
{
    ifstream in( file_name.c_str() );
    
    std::vector<string> content;
    
    if( !in.is_open() )
    {
        cout << "cannot open cnf file" << endl;
        exit( 0 );
    }
    
    string line;
    
    int number_of_clauses = 0;
    while( in.good() && number_of_clauses == 0 )
    {
        getline( in, line );
        if( line.find( "p cnf " ) != string::npos )
        {
            int pos = line.find_last_of( ' ' );
            number_of_clauses = atoi( line.substr( pos+1, line.size()-pos-1).c_str() );
        }
        
        content.push_back( line );
    }
    
    while( in.good() && ( number_of_clauses-256 ) > 0 ) 
    {
        getline( in, line );
        content.push_back( line );
        number_of_clauses -= 1;        
    }
    in.close();
    
    ofstream out( file_name.c_str() );
    
    std::vector<string>::iterator it_vec;
    for( it_vec = content.begin(); it_vec != content.end(); ++it_vec )
    {
        out << *it_vec << "\n";
    }
    
    std::set<string>::iterator it;
    for( it = state.begin(); it != state.end(); ++it )
    {
        out << *it << " 0\n";
    }
    
    out.close();
}


void calculate_new_state( std::set<string> &outcome_state_as_string, std::set<string> &input_state_as_int, std::map<string, int> &dictionary )
{    
    std::set<string>::iterator it;
    for( it = outcome_state_as_string.begin(); it != outcome_state_as_string.end(); ++it )
    {
        string tile_position = *it;
        int pos = tile_position.find( "step:" );
        
        if( tile_position.substr( pos+5, 2 ).compare( "11" ) == 0 )
        {
            tile_position.replace( pos+5, 2, "1");
            input_state_as_int.insert( to_string( dictionary[tile_position] ) );
        }
    }
}


void decode_solution( string file_name, std::set<string> &outcome_state, std::set<string> &operations, std::map<int, string> &dictionary )
{
    ifstream in( file_name.c_str() );
    
    if( !in.is_open() )
    {
        cout << "cannot open solution file" << endl;
        exit( 0 );
    }
    
    string line;

    // siege.results should only contain a single line of output    
    if( in.good() )
    {
        getline( in, line );
        line = line.substr( line.find_first_of( "{" ), line.find_first_of( "}" )-line.find_first_of( "{" ) );
        
        std::vector<string> tokens;
        split( tokens, line, boost::is_any_of( " " ) );
        
        std::vector<string>::iterator it;
        for( it = tokens.begin(); it != tokens.end(); ++it )
        {
            if( (*it).find( "-" ) == string::npos )
            {
                string variable = dictionary[atoi( (*it).c_str() )];
                if( variable.substr( 0,2 ).compare( "EO" ) == 0 )
                {
                    operations.insert( variable );
                    
                } else if( variable.substr( 0,2 ).compare( "TP" ) == 0 )
                {
                    if( variable.find( "step:11" ) != string::npos )
                    {
                        outcome_state.insert( variable );
                    }
                }
            }
        }
    }
    
    in.close();
}


void encode_initial_state( int _11, int _12, int _13, int _14,
                           int _21, int _22, int _23, int _24,
                           int _31, int _32, int _33, int _34,
                           int _41, int _42, int _43, int _44,
                           std::map<string, int> &dictionary,
                           string cnf_file_name )
{
    std::set<string> state;
    state.insert( to_string( dictionary["TP( position:1 step:1 tile:" + to_string( _11 ) + " )"] ) );
    state.insert( to_string( dictionary["TP( position:2 step:1 tile:" + to_string( _12 ) + " )"] ) ) ;
    state.insert( to_string( dictionary["TP( position:3 step:1 tile:" + to_string( _13 ) + " )"] ) ) ;
    state.insert( to_string( dictionary["TP( position:4 step:1 tile:" + to_string( _14 ) + " )"] ) );
    state.insert( to_string( dictionary["TP( position:5 step:1 tile:" + to_string( _21 ) + " )"] ) );
    state.insert( to_string( dictionary["TP( position:6 step:1 tile:" + to_string( _22 ) + " )"] ) );
    state.insert( to_string( dictionary["TP( position:7 step:1 tile:" + to_string( _23 ) + " )"] ) );
    state.insert( to_string( dictionary["TP( position:8 step:1 tile:" + to_string( _24 ) + " )"] ) );
    state.insert( to_string( dictionary["TP( position:9 step:1 tile:" + to_string( _31 ) + " )"] ) );
    state.insert( to_string( dictionary["TP( position:10 step:1 tile:" + to_string( _32 ) + " )"] ) );
    state.insert( to_string( dictionary["TP( position:11 step:1 tile:" + to_string( _33 ) + " )"] ) );
    state.insert( to_string( dictionary["TP( position:12 step:1 tile:" + to_string( _34 ) + " )"] ) );
    state.insert( to_string( dictionary["TP( position:13 step:1 tile:" + to_string( _41 ) + " )"] ) );
    state.insert( to_string( dictionary["TP( position:14 step:1 tile:" + to_string( _42 ) + " )"] ) );
    state.insert( to_string( dictionary["TP( position:15 step:1 tile:" + to_string( _43 ) + " )"] ) );
    state.insert( to_string( dictionary["TP( position:16 step:1 tile:" + to_string( _44 ) + " )"] ) );
    
    modify_cnf_file( cnf_file_name, state );
}


bool final_state_reached( std::set<string> state )
{
    
    return true;
}


int main( int argc, char** argv )
{
    if( argc != 5 )
    {
        cout << "Usage: ./task_manager <cnf_file> <dictionary_file> <solver_binary> <solver_output_file>" << endl;
        exit( 0 );
    }
    
    string cnf_file = *(argv+1);
    string dictionary_file = *(argv+2);
    string solver_binary = *(argv+3);
    
    
    // dictionary
    std::map<string, int> variable_dictionary_string_int;
    std::map<int, string> variable_dictionary_int_string;
    populate_dictionary( dictionary_file, variable_dictionary_string_int, variable_dictionary_int_string);
    
    
    // operations
    std::set<int, string> operations;
    //int number_of_operations = 0;


    
    // 1. write initial state to end of file
    encode_initial_state( 1,  2,  3,  4,
                          5,  6,  7,  8,
                          9,  10, 11, 12,
                          13, 14, 15, 16,
            
                          variable_dictionary_string_int,
                          cnf_file );
    
    // while final state is not reached:
    while( !final_state_reached( state ) )
    {
        
        // 2. run sat solver
        
        char *solver_args[4];
        pid_t pid;
  
        solver_args[0] = *(argv+3);
        solver_args[1] = *(argv+1);
        solver_args[2] = NULL;
    
        switch ( ( pid = fork() ) )
        {
            case -1:
                cout << "Fork() has failed" << endl;
                break;
            case 0:
                execv ( *(argv+3), solver_args );
                
                exit( EXIT_FAILURE );
                break;
            default:
                cout << "This is a message from the parent" << endl;
                break;
        }
  
        waitpid( pid, NULL, 0 );
        
        
            
        // 3. decode solution
        
        // operations for this iteration
        std::set<string> ops;
        
        // state
        std::set<string> state;
        
        decode_solution( *(argv+4), state, ops, variable_dictionary_int_string );
        
        
        std::set<string>::iterator it_1;
        for( it_1 = ops.begin(); it_1 != ops.end(); ++it_1 )
        {
            cout << *it_1 << endl;
        }
    
        std::set<string>::iterator it_2;
        for( it_2 = state.begin(); it_2 != state.end(); ++it_2 )
        {
            cout << *it_2 << endl;
        }
        
        // 4. add operations to priority queue
    
        // 4. write new state to end of file
    
        
    }
    
        
    
}
