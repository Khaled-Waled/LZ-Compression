public class LZ78 extends LZ
{
    public void compressFile(String sourceDir, String targetDir)
    {
        String ogData = fileToString(sourceDir);
        String comData="";
        String[] dictionary = new String[ogData.length()];
        dictionary[0]="";
        String buffer="";
        int dictionaryEntries=0,ptr=0;
        int pos=0;
        int tags [][] = new int[ogData.length()][2];
        int numOfTags=0;

        while (ptr<ogData.length())
        {
            boolean found=false;
            buffer += ogData.charAt(ptr);

            //search for the item in the dictionary
            for (int i=dictionaryEntries;i>0;i--)
            {
                if (buffer.equals(dictionary[i]))
                {
                    found = true;
                    pos = i;
                    break;
                }
            }
            if (!found)
            {
                dictionaryEntries++;
                dictionary[dictionaryEntries] = buffer;
                //System.out.println(dictionary[dictionaryEntries]);
                tags[numOfTags][0] = pos;
                tags[numOfTags++][1] = ogData.charAt(ptr);
                buffer ="";
                pos=0;
            }
            ptr++;
        }
        /*
        //old Data representation
        String s="";
        for (int i=0;i<=numOfTags;i++)
            s += ("<"+ tags[i][0]+",\""+(char) tags[i][1]+"\"> ");
        System.out.println(s);*/

        if (tags[numOfTags][1]==0)
            tags[numOfTags][1]= ogData.charAt(ogData.length()-1);
        for (int i=0;i<=numOfTags;i++)
            comData+= (toBin(tags[i][0],log2(dictionaryEntries))  + toBin(tags[i][1],7) );

        int extraLen= 8-(comData.length()%8);
        if (extraLen==8)
            extraLen=0;
        comData = binaryStringToBits(comData);
        comData = String.valueOf((byte) log2(dictionaryEntries)) +(byte) extraLen + comData;

        writeToFile(comData,targetDir);
    }

    public void uncompressFile(String sourceDir, String targetDir)
    {
        //Remove Headers
        String comData = fileToString(sourceDir);
        int maxPos = comData.charAt(0) - '0';
        comData = comData.substring(1);

        int extraLen = comData.charAt(0) - '0';
        comData = comData.substring(1);
        comData = bitsToBinaryString(comData, extraLen);
        //**************************


        String uncomData="";
        String[] dictionary = new String[1+(comData.length()/(maxPos+7))];
        dictionary[0]="";
        int dictionaryEntries=1;
        for (int i=0; i<comData.length(); i+=maxPos+7)
        {
            int  pos = Integer.parseInt(comData.substring(i,i+maxPos),2);
            char nex = (char) Integer.parseInt(comData.substring(i+maxPos,i+maxPos+7),2);

            String buffer = dictionary[pos]+nex;
            uncomData+=buffer;
            dictionary[dictionaryEntries++] = buffer;
        }
        writeToFile(uncomData,targetDir);

    }
}
