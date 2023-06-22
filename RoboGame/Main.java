import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Main{
    public static String longestString(List<String> ss){
        int index = 0;
        int longest = ss.get(0).;

        for(int i = 1; i < ss.size(); i++){
            if(ss.get(i).length() > longest){
                index = i;
                longest = ss.get(i).length();
            }
        }

        return ss.get(index);
    }
    public static void main(String[] arg){
        List<String>ss1 = List.of("aa","a","","abc");
        assert longestString(ss1).equals("abc"):longestString(ss1);
        List<String>ss2 = List.of("aa","a","abcd","ac");
        assert longestString(ss2).equals("abcd"):longestString(ss2);
        try{ longestString(List.of()); assert false; }
        catch(IllegalArgumentException iae){}
    }
}
