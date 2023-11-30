package app.bot.pdf;

import app.bot.model.Project;
import com.itextpdf.text.pdf.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class PdfEditor {

    public static File addTextToPdf(String fullName, String sum, String project) {
        try {
            File tempFile = Files.createTempFile("receipt", ".pdf").toFile();
            PdfReader reader = null;

            try {
                reader = new PdfReader("/root/prepayBot/input_new.pdf");
            } catch (Exception e) {
                reader = new PdfReader("input_new.pdf");
            }

            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(tempFile));
            BaseFont font = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

            int pages = reader.getNumberOfPages();
            for (int i = 1; i <= pages; i++) {
                PdfContentByte cb = stamper.getOverContent(i);
                cb.beginText();
                cb.setFontAndSize(font, 12);

                cb.setTextMatrix(85, reader.getPageSize(i).getHeight() - 330);
                cb.showText(fullName);

                cb.setTextMatrix(490, reader.getPageSize(i).getHeight() - 330);
                cb.showText(sum);

                cb.setTextMatrix(450, reader.getPageSize(i).getHeight() - 260);
                cb.showText(bangkokTime());

                cb.setTextMatrix(55, reader.getPageSize(i).getHeight() - 260);
                cb.showText(project);

                cb.endText();
            }
            stamper.close();
            reader.close();
            return tempFile;

        } catch (Exception ex) {
            return null;
        }
    }

    private static String bangkokTime() {
        Instant utcInstant = Instant.now();
        ZoneId bangkokZone = ZoneId.of("Asia/Bangkok");
        ZonedDateTime bangkokTime = ZonedDateTime.ofInstant(utcInstant, bangkokZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return bangkokTime.format(formatter);
    }
}
