package ltu.course.mobile.project.greenerfootballcup;


import org.junit.Test;

import ltu.course.mobile.project.greenerfootballcup.utilities.Field;

import static org.junit.Assert.*;

public class FieldTest {
    @Test
    public void Field_isCorrect(){
        Field[] fields = new Field[5];
        fields[0] = new Field("A%2011-manna%20(Gstad)");
        fields[1] = new Field("B%2011-manna%20(Gstad)");
        fields[2] = new Field("C%2011-manna%20(Byavallen)");
        fields[3] = new Field("C2%207-manna%20(Gstad)");
        fields[4] = new Field("C1%207-manna%20(Gstad)");

        assertEquals("A%2011-manna%20(Gstad)", fields[0].getUrlArgument());
        assertEquals("B%2011-manna%20(Gstad)", fields[1].getUrlArgument());
        assertEquals("C%2011-manna%20(Byavallen)", fields[2].getUrlArgument());
        assertEquals("C2%207-manna%20(Gstad)", fields[3].getUrlArgument());
        assertEquals("C1%207-manna%20(Gstad)", fields[4].getUrlArgument());

        assertEquals("A 11-manna (Gstad)", fields[0].getFullName());
        assertEquals("B 11-manna (Gstad)", fields[1].getFullName());
        assertEquals("C 11-manna (Byavallen)", fields[2].getFullName());
        assertEquals("C2 7-manna (Gstad)", fields[3].getFullName());
        assertEquals("C1 7-manna (Gstad)", fields[4].getFullName());

        assertEquals("A 11", fields[0].getDisplayName());
        assertEquals("B 11", fields[1].getDisplayName());
        assertEquals("C 11", fields[2].getDisplayName());
        assertEquals("C2 7", fields[3].getDisplayName());
        assertEquals("C1 7", fields[4].getDisplayName());
    }
}
