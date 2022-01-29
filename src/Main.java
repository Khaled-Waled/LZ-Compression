import java.util.Scanner;

public class Main {

    public static void terminal()
    {
        LZ comp;
        Scanner scanner = new Scanner(System.in);
        String sourceDir="";
        String targetDir="";

        //Terminal Start
        System.out.println("************************************");
        System.out.println("** Program made by Khaled Waleed  **");
        System.out.println("************************************");


        System.out.println("Welcome to LZ compressions terminal\n");
        System.out.println("Do you want to compress or uncompress ?");
        System.out.println("Compress:    0");
        System.out.println("Decompress:  1");
        int option=scanner.nextInt();

        if (option==1) //Uncompress
        {
            System.out.println("Which file you want to decompress?\nPlease enter the directory below: ");
            sourceDir=scanner.next();

            System.out.println("Where do you want to put the uncompressed file ?");
            targetDir=scanner.next();

            System.out.println("Which algorithm to use?");
            System.out.println("LZ77:   0");
            System.out.println("LZ78:   1");
            System.out.println("LZ7w:   2");

            option=scanner.nextInt();
            if (option == 1)
                comp = new LZ78();
            else if (option ==2)
                comp =  new LZW();
            else comp = new LZ77();

            comp.uncompressFile(sourceDir,targetDir);
        }
        else //Compress
        {
            System.out.println("Which file you want to compress?");
            System.out.println("Please enter the directory below:");
            sourceDir =  scanner.next();

            System.out.println("Choose the new compressed file directory:");
            targetDir = scanner.next();

            System.out.println("Which algorithm to use?");
            System.out.println("LZ77:   0");
            System.out.println("LZ78:   1");
            System.out.println("LZ7w:   2");

            option=scanner.nextInt();
            if (option == 1)
                comp = new LZ78();
            else if (option ==2)
                comp =  new LZW();
            else comp = new LZ77();

            comp.compressFile(sourceDir,targetDir);
        }
        System.out.println("Program Ended...");

    }

    public static void test()
    {
        LZW comp = new LZW();

        comp.compressFile("src/Sample.txt","src/Target.txt");
        comp.uncompressFile("src/Target.txt","src/aaa.txt");
    }

    public static void main(String[] args)
    {
        terminal();
        //test();

    }
}
