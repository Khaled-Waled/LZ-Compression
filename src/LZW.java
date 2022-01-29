import java.io.IOException;

public class LZW extends LZ{

    private static int searchInDictionary(String[] dic,int len ,String target)
    {
        int idx=-1;

        for (int i=0; i<=len; i++)
        {
            if (dic[i].equals(target))
            {
                idx = i;
                break;
            }
        }
        return idx;
    }
    public void compressFile(String sourceDir, String targetDir)
    {
        String ogData = fileToString(sourceDir);
        int[] tags =new int[ogData.length()];
        int numOfTags=0;


        //build initial dictionary
        String[] dictionary = new String[256];
        int dictionaryEntries=-1;
        for (int i=0; i<128;i++)
        {
            dictionary[i]= String.valueOf((char)i);
            dictionaryEntries++;
        }


        //main compression algorithm
        int idx=-1;
        int idx_old=0;
        String buffer="";

        for (int ptr=0; ptr<ogData.length(); ptr++)
        {
            buffer+=ogData.charAt(ptr);
            idx_old = idx;
            idx = searchInDictionary(dictionary,dictionaryEntries,buffer);

            if (idx==-1)
            {
                dictionary[++dictionaryEntries] = buffer;
                tags[numOfTags++]=idx_old;
                ptr--;
                buffer="";
            }
        }
        if (idx != idx_old) //for loop ends before adding the last tag
        {
            dictionary[++dictionaryEntries] = buffer;
            tags[numOfTags++]=idx;
        }

        String comData="";  //construct comData
        for (int i=0; i<numOfTags; i++)
            comData+= toBin(tags[i],8);

        //Send comData
        comData = binaryStringToBits(comData,0);

        writeToFile(comData,targetDir);
    }

    public void uncompressFile(String sourceDir, String targetDir)
    {
        String comData = fileToString(sourceDir);
        String uncomData="";
        comData = bitsToBinaryString(comData,0);

        //build initial dictionary
        String[] dictionary = new String[256];
        int dictionaryEntries=-1;
        for (int i=0; i<128;i++)
        {
            dictionary[i]= String.valueOf((char)i);
            dictionaryEntries++;
        }

        //re-extract tags
        int[] tags = new int [comData.length()/8];
        for (int i=0; i<tags.length; i++)
            tags[i] = Integer.parseInt(comData.substring(8*i,8*i+8),2);

        //add first symbol
        uncomData+=dictionary[tags[0]];

        for (int i=1; i<tags.length; i++)
        {
            if (tags[i]>dictionaryEntries)
                dictionary[++dictionaryEntries]= dictionary[tags[i-1]]+dictionary[tags[i-1]].charAt(0);
            else dictionary[++dictionaryEntries]=dictionary[tags[i-1]]+dictionary[tags[i]].charAt(0);

            uncomData+= dictionary[tags[i]];
        }

        writeToFile(uncomData,targetDir);

        /*//DEBUG -------- print dictionary
        for (int i=128; i<dictionaryEntries+1;i++)
            System.out.println(i+"-  "+dictionary[i]);*/
    }
}
