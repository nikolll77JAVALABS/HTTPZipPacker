package nikolll77.com;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@WebServlet(name = "SendZipServlet")
@MultipartConfig
public class SendZipServlet extends HttpServlet {
    public static String header = "<html><head><title>nikolll77.com welcome</title></head><body><html>";
    public static String footer = "</body></html>";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String s="";
        File folder = new File("e://forzip//");
        String tmpFileName;
        Collection<Part> parts = request.getParts();
        File zipFile=new File(folder,"myzip.zip");
        FileOutputStream fos = new FileOutputStream(zipFile);
        ZipOutputStream zos = new ZipOutputStream(fos);

        for (Part filePart:parts) {
            tmpFileName = getFileName(filePart);
            s += tmpFileName + "; ";
            File file = new File(folder, tmpFileName);

            //------save file
            //filePart.write("e://forzip//"+getFileName(filePart)); //-badway
            //File file = File.createTempFile(tmpFileName, "", folder);
            try (InputStream input = filePart.getInputStream()) {
                Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            //---zipfile
            ZipEntry forZip = new ZipEntry(tmpFileName);
            zos.putNextEntry(forZip);

            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            Integer len;
            while ((len=fis.read(buffer))>=0){
                zos.write(buffer,0,len);
            }
            zos.closeEntry();
            fis.close();

        }

        zos.close();

        //----send zip
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition","attachment;filename="+zipFile.getName());
        OutputStream ros = response.getOutputStream();
        FileInputStream fis =new FileInputStream(zipFile);
        byte[] buffer = new byte[1024];
        Integer len;
        while((len=fis.read(buffer))>=0) {
            ros.write(buffer,0,len);
        }
        fis.close();
        ros.flush();

/*        PrintWriter pw = response.getWriter();
        pw.write(header);
        pw.write("You must have zip here "+s);
        pw.write(footer);*/

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private static String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1); // MSIE fix.
            }
        }
        return null;
    }
}
