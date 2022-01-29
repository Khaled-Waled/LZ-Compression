import java.io.FileNotFoundException;

public class LZ77 extends LZ
{
    public void compressFile(String sourceDir, String targetDir)
    {
        String ogData= fileToString(sourceDir);
        int searchWindowSize=13;
        String comData="";
        int ptr = 0;
        int [][] tags= new int[ogData.length()][3];
        int numOfTags=0;
        while (ptr < ogData.length()-1)
        {
            //find the start of the all repeated sequences
            int bufferPtr;
            int l=0;
            int[][] sequences = new int[searchWindowSize][2];
            //The case of not finding any
            sequences[0][0] = ptr;
            sequences[0][1] = l;
            int sequencesCount=1;
            //other places of that sequence
            for(bufferPtr= Math.max(0,ptr-searchWindowSize); bufferPtr<ptr;bufferPtr++)
            {
                if (ogData.charAt(bufferPtr) == ogData.charAt(ptr))
                {
                    for (l = 1; ptr + l < ogData.length() && bufferPtr+l < ptr; l++)
                    {
                        if (ogData.charAt(bufferPtr + l) != ogData.charAt(ptr + l)) break;
                    }
                    sequences[sequencesCount][0] = bufferPtr;
                    sequences[sequencesCount][1] = l;
                    sequencesCount++;
                }
            }

            //find the largest length of the repeated sequence
            int targetEntry=0;
            for (int i=sequencesCount;i>=0;i--)
            {
                if (sequences[i][1]>sequences[targetEntry][1])
                {
                    targetEntry = i;
                }
            }
            /*//DEBUG
            for (int i=0; i<sequencesCount+1;i++)
                System.out.print(sequences[i][0]+"-"+sequences[i][1]+" || ");
            System.out.println(" ");
            //------*/
            bufferPtr = sequences[targetEntry][0];
            l         = sequences[targetEntry][1];

            //store the tag
            tags[numOfTags][0]=ptr-bufferPtr;
            tags[numOfTags][1]=l;
            try {
                tags[numOfTags][2]=ogData.charAt(ptr+l);
            }
            catch (StringIndexOutOfBoundsException e){
                tags[numOfTags][2] = '$';
            }

            ptr = ptr + l + 1;
            numOfTags++;
        }

        //get the num of bits to represent pos and length
        int max_pos=0,max_l=0;
        for (int i=0;i<numOfTags;i++)
        {
            if (tags[i][0]>max_pos)
                max_pos = tags[i][0];
            if (tags[i][1]>max_l)
                max_l = tags[i][1];
        }
        max_pos = log2(max_pos);
        max_l   = log2(max_l);


        for (int i=0;i<numOfTags;i++)
            comData+= (toBin(tags[i][0],max_pos) + toBin(tags[i][1],max_l) + toBin(tags[i][2],7) );



        int extraLen= 8-(comData.length()%8);
        if (extraLen==8)
            extraLen=0;

        comData = binaryStringToBits(comData,extraLen);
        /*//old data representation
        String allTags="";
        for (int i=0;i<numOfTags;i++)
            allTags+= ( "<" + tags[i][0]+","+ tags[i][1] +",\""+ (char) tags[i][2] +"\"> "  );
        System.out.println(allTags);*/


        boolean debugging=false;
        if(debugging)
        {
            System.out.println("Compression complete !");
            System.out.println("Tag Format:\n" + max_pos + "-> pos || " + max_l + " -> length || " + "7 -> next char");
            System.out.println("Total Tag length = " + (max_l+max_pos+7)+" bits");
            System.out.println("Compressed data size = " + (max_l+max_pos+7) + " X " + numOfTags + " = " + (max_l+max_pos+7) * numOfTags + "Bits");
            System.out.println("Original data size = " + ogData.length() * 8 + " Bits");
            System.out.println("Compression ratio = %" + (((max_l+max_pos+7) * numOfTags *100) / (ogData.length() * 8) ));
        }

        comData = String.valueOf((byte)extraLen) + String.valueOf((byte)max_pos)+ String.valueOf((byte)max_l) + comData;
        writeToFile(comData,targetDir);
    }

    public void uncompressFile(String sourceDir, String targetDir)
    {
        String comData = fileToString(sourceDir);
        //remove headers
        int extraLen = Integer.parseInt(String.valueOf(comData.charAt(0)));
        comData = comData.substring(1);

        int max_pos = Integer.parseInt(String.valueOf(comData.charAt(0)));
        comData = comData.substring(1);

        int max_l = Integer.parseInt(String.valueOf(comData.charAt(0)));
        comData = comData.substring(1);
        //*************

        //System.out.println("extra len= "+extraLen+"\nmax pos= "+ max_pos+"\nMax l= "+max_l);

        comData = bitsToBinaryString(comData,extraLen);
        String uncomData="";
        int ptr;
        for (ptr=0; ptr<comData.length();ptr= ptr+max_pos+max_l+7)
        {
            int pos = Integer.parseInt(comData.substring(ptr,ptr+max_pos),2);
            int l   = Integer.parseInt(comData.substring(ptr+max_pos,ptr+max_pos+max_l),2);
            char nex= (char) Integer.parseInt(comData.substring(ptr+max_pos+max_l,ptr+max_pos+max_l+7),2);

            try{
                for (int i=0;i<l;i++)
                    uncomData+=uncomData.charAt(uncomData.length()-pos);
                uncomData+=nex;
            }
            catch (StringIndexOutOfBoundsException e)
            {
                /*System.out.println(uncomData.length()-pos + "   " + nex);
                System.out.println(uncomData);*/
            }
        }
        writeToFile(uncomData,targetDir);
    }
}
