package ltu.course.mobile.project.greenerfootballcup;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ltu.course.mobile.project.greenerfootballcup.Activities.FieldActivity;
import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Field;

public class FieldActivityTest {
    @Test
    public void SortedFieldList_isCorrect()
    {
        Field[] fields = new Field[5];
        fields[0] = new Field("A%2011-manna%20(Gstad)");
        fields[1] = new Field("C%2011-manna%20(Byavallen)");
        fields[2] = new Field("C1%207-manna%20(Gstad)");
        fields[3] = new Field("B%2011-manna%20(Gstad)");
        fields[4] = new Field("C2%207-manna%20(Gstad)");

        List<Field> listA = new ArrayList<>(Collections.singletonList(fields[0]));
        List<Field> listB = new ArrayList<>(Collections.singletonList(fields[3]));
        List<Field> listC = new ArrayList<>(Arrays.asList(fields[1], fields[2], fields[4]));

        List<List<Field>> correctList = new ArrayList<>();
        correctList.add(listA);
        correctList.add(listB);
        correctList.add(listC);

        List<List<Field>> generatedList = FieldActivity.sortAndSplitFieldList(fields);

        Assert.assertEquals(correctList, generatedList);
    }
}
