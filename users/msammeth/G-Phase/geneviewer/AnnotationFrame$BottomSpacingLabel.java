// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 12/05/2006 07:41:04 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) fieldsfirst nonlb 
// Source File Name:   AnnotationFrame.java

import javax.swing.JLabel;

class AnnotationFrame$BottomSpacingLabel extends JLabel {

    AnnotationFrame$BottomSpacingLabel() {
        super();
        setBorder(new order(AnnotationFrame.this));
    }

    AnnotationFrame$BottomSpacingLabel(String s) {
        this();
        setText(s);
    }
}