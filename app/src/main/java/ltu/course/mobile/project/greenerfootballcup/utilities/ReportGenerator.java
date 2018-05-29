package ltu.course.mobile.project.greenerfootballcup.utilities;


import com.cete.dynamicpdf.Document;
import com.cete.dynamicpdf.Font;
import com.cete.dynamicpdf.Page;
import com.cete.dynamicpdf.PageOrientation;
import com.cete.dynamicpdf.PageSize;
import com.cete.dynamicpdf.TextAlign;
import com.cete.dynamicpdf.pageelements.Label;

import java.io.File;

import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Match;
import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Team;

public abstract class ReportGenerator {
    public static boolean generate(File outputFile, File resultPicture, File fairplayPicture,
                                   File signatureFile, Match match, Team team){
        // Create a document and set it's properties
        Document objDocument = new Document();
        objDocument.setCreator("DynamicPDFHelloWorld.java");
        objDocument.setAuthor("Your Name");
        objDocument.setTitle("Hello World");

        // Create a page to add to the document
        Page objPage = new Page(PageSize.LETTER, PageOrientation.PORTRAIT,
                                54.0f);

        // Create a Label to add to the page
        String strText = "Hello World...\nFrom DynamicPDF Generator "
                         + "for Java\nDynamicPDF.com";
        Label objLabel = new Label(strText, 0, 0, 504, 100,
                                   Font.getHelvetica(), 18, TextAlign.CENTER);

        // Add label to page
        objPage.getElements().add(objLabel);

        // Add page to document
        objDocument.getPages().add(objPage);

        try {
            // Outputs the document to file
            objDocument.draw(outputFile.getAbsolutePath());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false; // TODO
    }
}
