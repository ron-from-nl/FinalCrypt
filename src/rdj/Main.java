///*
// * Copyright (C) 2017 ron
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package rdj;
//
//import jcurses.system.CharColor;
//import jcurses.widgets.*;
//public class Main
//{
//    public static void main(String[] args) throws Exception
//    {    
//        Window w = new Window(40, 20, true, "Hello World Window");
//        DefaultLayoutManager mgr = new DefaultLayoutManager();
//        mgr.bindToContainer(w.getRootPanel());
//        mgr.addWidget(
//            new Label("Hello World!", new CharColor(CharColor.WHITE, CharColor.GREEN)),
//            0, 0, 40, 20,
//            WidgetsConstants.ALIGNMENT_CENTER,
//            WidgetsConstants.ALIGNMENT_CENTER);
//        w.show();
//        Thread.currentThread().sleep(5000);
//        w.close(); // reset the native console
//    }
//}