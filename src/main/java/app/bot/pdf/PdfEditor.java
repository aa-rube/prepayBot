package app.bot.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;

@Service
public class PdfEditor {

    public static File addTextToPdf(String fullName, String sum) {
        try {
            String data = LocalDate.now().toString();
            File tempFile = Files.createTempFile("reseipt", ".pdf").toFile();
            PdfReader reader = new PdfReader("input.pdf");
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(tempFile));
            BaseFont font = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            int pages = reader.getNumberOfPages();
            for (int i = 1; i <= pages; i++) {
                PdfContentByte cb = stamper.getOverContent(i);
                cb.beginText();

                cb.setFontAndSize(font, 12);
                cb.setTextMatrix(85, reader.getPageSize(i).getHeight() - 440);
                cb.showText(fullName);

                cb.setTextMatrix(480, reader.getPageSize(i).getHeight() - 540);
                cb.showText(sum);

                cb.setTextMatrix(480, reader.getPageSize(i).getHeight() - 664);
                cb.showText(sum);

                cb.setTextMatrix(450, reader.getPageSize(i).getHeight() - 380);
                cb.showText(data);
                cb.endText();
            }
            stamper.close();
            reader.close();
            return tempFile;

        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
