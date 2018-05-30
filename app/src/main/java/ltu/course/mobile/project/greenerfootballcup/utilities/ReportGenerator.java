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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Field;
import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Match;
import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Team;

public abstract class ReportGenerator {
    public static boolean generate(File outputFile, File resultPicture, File fairplayPicture,
                                   Match match, Field field,
                                   Team teamA, Team teamB,
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
        Date today = Calendar.getInstance().getTime();
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String dateStr = format.format(today);
        String subtitle = "Match "+match.getNumber()+ " of group "+match.getGroup();
        String dateSubtitle = "Date: "+dateStr+" at "+match.getTime();
        String fieldSubtitle ="Field: "+field.getFullName();

        Label titleLabel = new Label(title, 0, 0, width, 30,
                                     Font.getHelvetica(), 24, TextAlign.CENTER);
        Label subtitleLabel = new Label(subtitle, 0, 30, width, 30,
                                        Font.getHelvetica(), 20, TextAlign.CENTER);
        Label subtitleDateLabel = new Label(dateSubtitle, 0, 60, width/2, 20,
                                        Font.getHelvetica(), 18, TextAlign.LEFT);
        Label subtitleFieldLabel = new Label(fieldSubtitle, width/2, 60, width/2, 20,
                                            Font.getHelvetica(), 18, TextAlign.RIGHT);

        // Add labels to page
        page.getElements().add(titleLabel);
        page.getElements().add(subtitleLabel);
        page.getElements().add(subtitleDateLabel);
        page.getElements().add(subtitleFieldLabel);

        float scale = 0.2f;
        Label resultImageLabel = new Label("Results: ", 0, 90, width / 2, 20,
                                           Font.getHelvetica(), 16, TextAlign.LEFT);
        Image resultImage = new Image(resultPicture.getAbsolutePath(), 0, 110, scale);

        Label fairPlayImageLabel = new Label("Fairplay results: ", width / 2 + 30f, 90, width / 2, 20,
                                             Font.getHelvetica(), 16, TextAlign.LEFT);
        Image fairPlayImage = new Image(fairplayPicture.getAbsolutePath(), width / 2 + 30f, 110,
                                        scale);

        page.getElements().add(resultImageLabel);
        page.getElements().add(resultImage);
        page.getElements().add(fairPlayImageLabel);
        page.getElements().add(fairPlayImage);


        //Create Table2 object.
        float yTable = 270;
        Table2 table = new Table2(0, yTable, width, height-yTable);

        // Add columns to the table
        table.getColumns().add(width/2-40);
        table.getColumns().add(40);
        table.getColumns().add(width/2-40);
        table.getColumns().add(40);

        // Add rows to the table and add cells to the rows
        Row2 row1 = table.getRows().add(30, Font.getHelveticaBold(), 16,
                                         Grayscale.getBlack(), Grayscale.getGray());

        row1.getCellDefault().setAlign(TextAlign.CENTER);
        row1.getCellDefault().setVAlign(VAlign.CENTER);
        row1.getCells().add(match.getFirstTeam()).setColumnSpan(2);
        row1.getCells().add(match.getSecondTeam()).setColumnSpan(2);

        int maxRow = Math.max(teamA.getNumberPlayer(), teamB.getNumberPlayer());
        int maxFirstPage = Math.min(maxRow, 18);
        int nbSecondPage = maxRow - maxFirstPage;
        for(int i = 0; i < maxFirstPage; i++)
        {
            Row2 row2 = table.getRows().add(20);
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
        page.getElements().add(table);

        float ySignature = 300+20*maxFirstPage+20;

        float scaleSignature = 0.1f;
        String signatureStr = "Coach's signature: ";
        Label signatureALabel = new Label(signatureStr, 0, ySignature, width / 2, 20,
                                           Font.getHelvetica(), 16, TextAlign.LEFT);
        Image signatureAImage = new Image(signatureTeamA.getAbsolutePath(), 0, ySignature + 40, scaleSignature);

        Label signatureBLabel = new Label(signatureStr, width / 2 + 30f, ySignature, width / 2, 20,
                                             Font.getHelvetica(), 16, TextAlign.LEFT);
        Image signatureBImage = new Image(signatureTeamB.getAbsolutePath(), width / 2 + 30f, ySignature + 40,
                                          scaleSignature);

        page.getElements().add(signatureALabel);
        page.getElements().add(signatureAImage);
        page.getElements().add(signatureBLabel);
        page.getElements().add(signatureBImage);

        // Add page to document
        objDocument.getPages().add(page);

        if(nbSecondPage > 0)
        {
            Page page2 = new Page(PageSize.A4, PageOrientation.PORTRAIT, 54.0f);
            //Create Table2 object.
            Table2 table2 = new Table2(0, 0, width, height);

            // Add columns to the table
            table2.getColumns().add(width/2-40);
            table2.getColumns().add(40);
            table2.getColumns().add(width/2-40);
            table2.getColumns().add(40);

            // Add rows to the table and add cells to the rows
            Row2 row21 = table2.getRows().add(30, Font.getHelveticaBold(), 16,
                                             Grayscale.getBlack(), Grayscale.getGray());

            row21.getCellDefault().setAlign(TextAlign.CENTER);
            row21.getCellDefault().setVAlign(VAlign.CENTER);
            row21.getCells().add(match.getFirstTeam()).setColumnSpan(2);
            row21.getCells().add(match.getSecondTeam()).setColumnSpan(2);

            for(int i = maxFirstPage; i < maxRow; i++)
            {
                Row2 row22 = table2.getRows().add(20);
                if(teamA.getNumberPlayer() > i)
                {
                    row22.getCells().add(teamA.getPlayers().get(i).getName());
                    row22.getCells().add(teamA.getPlayers().get(i).getAge());
                }
                else
                {
                    row22.getCells().add("");
                    row22.getCells().add("");
                }

                if(teamB.getNumberPlayer() > i)
                {
                    row22.getCells().add(teamB.getPlayers().get(i).getName());
                    row22.getCells().add(teamB.getPlayers().get(i).getAge());
                }
                else
                {
                    row22.getCells().add("");
                    row22.getCells().add("");
                }
            }

            // Add the table to the page
            page2.getElements().add(table2);

            float ySignature2 = 30+20*nbSecondPage+20;

            Label signatureALabel2 = new Label(signatureStr, 0, ySignature2, width / 2, 20,
                                              Font.getHelvetica(), 16, TextAlign.LEFT);
            Image signatureAImage2 = new Image(signatureTeamA.getAbsolutePath(), 0, ySignature2 + 40, scaleSignature);

            Label signatureBLabel2 = new Label(signatureStr, width / 2 + 30f, ySignature2, width / 2, 20,
                                              Font.getHelvetica(), 16, TextAlign.LEFT);
            Image signatureBImage2 = new Image(signatureTeamB.getAbsolutePath(), width / 2 + 30f, ySignature2 + 40,
                                              scaleSignature);

            page2.getElements().add(signatureALabel2);
            page2.getElements().add(signatureAImage2);
            page2.getElements().add(signatureBLabel2);
            page2.getElements().add(signatureBImage2);
            objDocument.getPages().add(page2);
        }

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
