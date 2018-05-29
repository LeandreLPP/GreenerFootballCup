package ltu.course.mobile.project.greenerfootballcup.utilities;

import com.cete.dynamicpdf.Document;
import com.cete.dynamicpdf.Font;
import com.cete.dynamicpdf.Grayscale;
import com.cete.dynamicpdf.Page;
import com.cete.dynamicpdf.PageOrientation;
import com.cete.dynamicpdf.PageSize;
import com.cete.dynamicpdf.TextAlign;
import com.cete.dynamicpdf.VAlign;
import com.cete.dynamicpdf.pageelements.Cell2;
import com.cete.dynamicpdf.pageelements.Image;
import com.cete.dynamicpdf.pageelements.Label;
import com.cete.dynamicpdf.pageelements.Row2;
import com.cete.dynamicpdf.pageelements.Table2;

import java.io.File;
import java.io.FileNotFoundException;

import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Match;
import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Team;

public abstract class ReportGenerator {
    public static boolean generate(File outputFile, File resultPicture, File fairplayPicture,
                                   Match match, Team teamA, Team teamB,
                                   File signatureTeamA, File signatureTeamB) throws FileNotFoundException {
        // Create a document and set it's properties
        Document objDocument = new Document();
        objDocument.setCreator("ltu.course.mobile.project.greenerfootballcup");
        objDocument.setAuthor("Greener Football Cup");
        objDocument.setTitle("Match report: "
                             + match.getFirstTeam()
                             + " vs. "
                             + match.getSecondTeam());

        // Create a page to add to the document
        Page page = new Page(PageSize.A4, PageOrientation.PORTRAIT, 54.0f);
        // 8.27 in × 11.7 in
        // 72 points = 1 inch
        // 28.35 points = 1 centimeter

        float width = 487.44f;
        float height = 734.4f;

        // Create labels to add to the page
        String title = match.getFirstTeam() + " VS. " + match.getSecondTeam();
        String subtitle = "Match report";

        Label titleLabel = new Label(title, 0, 0, width, 50,
                                     Font.getHelvetica(), 24, TextAlign.CENTER);
        Label subtitleLabel = new Label(subtitle, 0, 50, width, 50,
                                        Font.getHelvetica(), 20, TextAlign.CENTER);

        // Add labels to page
        page.getElements().add(titleLabel);
        page.getElements().add(subtitleLabel);

        float scale = 0.1f;
        Label resultImageLabel = new Label("Results: ", 0, 120, width / 2, 20,
                                           Font.getHelvetica(), 16, TextAlign.LEFT);
        Image resultImage = new Image(resultPicture.getAbsolutePath(), 0, 140, scale);

        Label fairPlayImageLabel = new Label("Fairplay results: ", width / 2 + 30f, 120, width / 2, 20,
                                             Font.getHelvetica(), 16, TextAlign.LEFT);
        Image fairPlayImage = new Image(fairplayPicture.getAbsolutePath(), width / 2 + 30f, 140,
                                        scale);

        page.getElements().add(resultImageLabel);
        page.getElements().add(resultImage);
        page.getElements().add(fairPlayImageLabel);
        page.getElements().add(fairPlayImage);


        //Create Table2 object.
        Table2 table2 = new Table2(0, 320, width, 600);

        // Add columns to the table
        table2.getColumns().add(width/2-40);
        table2.getColumns().add(40);
        table2.getColumns().add(width/2-40);
        table2.getColumns().add(40);

        // Add rows to the table and add cells to the rows
        Row2 row1 = table2.getRows().add(30, Font.getHelveticaBold(), 16,
                                         Grayscale.getBlack(), Grayscale.getGray());

        row1.getCellDefault().setAlign(TextAlign.CENTER);
        row1.getCellDefault().setVAlign(VAlign.CENTER);
        row1.getCells().add(match.getFirstTeam()).setColumnSpan(2);
        row1.getCells().add(match.getSecondTeam()).setColumnSpan(2);

        int maxRow = Math.max(teamA.getNumberPlayer(), teamB.getNumberPlayer());
        for(int i = 0; i < maxRow; i++)
        {
            Row2 row2 = table2.getRows().add(20);
            if(teamA.getNumberPlayer() > i)
            {
                row2.getCells().add(teamA.getPlayers().get(i).getName());
                row2.getCells().add(teamA.getPlayers().get(i).getAge());
            }
            else
            {
                row2.getCells().add("");
                row2.getCells().add("");
            }

            if(teamB.getNumberPlayer() > i)
            {
                row2.getCells().add(teamB.getPlayers().get(i).getName());
                row2.getCells().add(teamB.getPlayers().get(i).getAge());
            }
            else
            {
                row2.getCells().add("");
                row2.getCells().add("");
            }
        }

        // Add the table to the page
        page.getElements().add(table2);

        float scaleSignature = 0.1f;
        Label signatureALabel = new Label("First team coach's signature: ", 0, height - 100, width / 2, 20,
                                           Font.getHelvetica(), 16, TextAlign.LEFT);
        Image signatureAImage = new Image(signatureTeamA.getAbsolutePath(), 0, height - 60, scaleSignature);

        Label signatureBLabel = new Label("First team coach's signature: ", width / 2 + 30f, height - 100, width / 2, 20,
                                             Font.getHelvetica(), 16, TextAlign.LEFT);
        Image signatureBImage = new Image(signatureTeamB.getAbsolutePath(), width / 2 + 30f, height - 60,
                                          scaleSignature);

        page.getElements().add(signatureALabel);
        page.getElements().add(signatureAImage);
        page.getElements().add(signatureBLabel);
        page.getElements().add(signatureBImage);

        // Add page to document
        objDocument.getPages().add(page);

        try
        {
            // Outputs the document to file
            objDocument.draw(outputFile.getAbsolutePath());
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }
}
