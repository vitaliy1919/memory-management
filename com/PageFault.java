package com;/* It is in this file, specifically the replacePage function that will
   be called by com.MemoryManagement when there is a page fault.  The
   users of this program should rewrite com.PageFault to implement the
   page replacement algorithm.
*/

  // This com.PageFault file is an example of the FIFO com.Page Replacement
  // Algorithm as described in the Memory Management section.

import java.util.*;

public class PageFault {

    int clockPosition = -1;
    Vector<Page> mappedPages = null;


  public static void bringMemoryFromDisk(Vector mem, int number) {

      new Thread(()->{
          try {
              Thread.sleep(1);
              ((Page)mem.elementAt(number)).M = 0;

          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }).start();

  }

  public static void replacePage(Page oldPage, Page newPage, ControlPanel controlPanel, Vector<Page> mappedPages, int position) {
      controlPanel.removePhysicalPage( oldPage.physical );
      newPage.physical = oldPage.physical;
      controlPanel.addPhysicalPage( newPage.physical , newPage.id );
      oldPage.inMemTime = 0;
      oldPage.lastTouchTime = 0;
      oldPage.R = 0;
      oldPage.M = 0;
      oldPage.physical = -1;
      mappedPages.set(position, newPage);
  }

  // WSClock algo
    /**
     * The page replacement algorithm for the memory management sumulator.
     * This method gets called whenever a page needs to be replaced.
     * <p>
     * The page replacement algorithm included with the simulator is
     * FIFO (first-in first-out).  A while or for loop should be used
     * to search through the current memory contents for a canidate
     * replacement page.  In the case of FIFO the while loop is used
     * to find the proper page while making sure that virtPageNum is
     * not exceeded.
     * <pre>
     *   com.Page page = ( com.Page ) mem.elementAt( oldestPage )
     * </pre>
     * This line brings the contents of the com.Page at oldestPage (a
     * specified integer) from the mem vector into the page object.
     * Next recall the contents of the target page, replacePageNum.
     * Set the physical memory address of the page to be added equal
     * to the page to be removed.
     * <pre>
     *   controlPanel.removePhysicalPage( oldestPage )
     * </pre>
     * Once a page is removed from memory it must also be reflected
     * graphically.  This line does so by removing the physical page
     * at the oldestPage value.  The page which will be added into
     * memory must also be displayed through the addPhysicalPage
     * function call.  One must also remember to reset the values of
     * the page which has just been removed from memory.
     *
     * @param mem is the vector which contains the contents of the pages
     *   in memory being simulated.  mem should be searched to find the
     *   proper page to remove, and modified to reflect any changes.
     * @param virtPageNum is the number of virtual pages in the
     *   simulator (set in com.Kernel.java).
     * @param replacePageNum is the requested page which caused the
     *   page fault.
     * @param controlPanel represents the graphical element of the
     *   simulator, and allows one to modify the current display.
     */
  public  void replacePage (
          Vector mem,
          int virtPageNum,
          int replacePageNum,
          ControlPanel controlPanel,
          long workingSetTime,
          long maxDisksWrites)
  {
      if (clockPosition == -1) {
          // firstly we need to create a circular list with all mapped virtual addresses
          mappedPages = new Vector<>();
          for (int i = 0; i < mem.size(); i++) {
              Page currentPage = (Page) mem.elementAt(i);
              if (currentPage.physical != -1)
                  mappedPages.add(currentPage);
          }
          clockPosition = 0;
      }
      boolean pageReplaced = false;
      int diskWrites = 0;
      while (!pageReplaced) {
          for (; clockPosition < mappedPages.size(); clockPosition++) {
              Page currentPage = mappedPages.elementAt(clockPosition);
              System.out.println(currentPage);
              // if page was recently referenced
              // that we set the bit to 0 and advance
              if (currentPage.R == 1) {
                  currentPage.R = 0;
                  continue;
              }

              // if page is not recently referenced
              // and it is not in the working set
              if (currentPage.R == 0 && currentPage.lastTouchTime > workingSetTime) {

                  // if the page is clean then
                  // simply remove it
                  if (currentPage.M == 0) {
                      Page newPage = (Page)mem.elementAt(replacePageNum);
                      replacePage(currentPage, newPage, controlPanel, mappedPages, clockPosition);
                      mappedPages.set(clockPosition, newPage);
                  }

                  // otherwise start dist write
                  if (diskWrites < maxDisksWrites) {
                      diskWrites++;
                      bringMemoryFromDisk(mem, currentPage.id);
                  }
              }
          }
          clockPosition = 0;
          if (diskWrites == 0) {
              Page newPage = (Page)mem.elementAt(replacePageNum);
              replacePage(mappedPages.elementAt(0), newPage, controlPanel, mappedPages, 0);
              pageReplaced = true;
          }
      }
//    int count = 0;
//    int oldestPage = -1;
//    int oldestTime = 0;
//    int firstPage = -1;
//    int map_count = 0;
//    boolean mapped = false;
//
//    while ( ! (mapped) || count != virtPageNum ) {
//      Page page = ( Page ) mem.elementAt( count );
//      if ( page.physical != -1 ) {
//        if (firstPage == -1) {
//          firstPage = count;
//        }
//        if (page.inMemTime > oldestTime) {
//          oldestTime = page.inMemTime;
//          oldestPage = count;
//          mapped = true;
//        }
//      }
//      count++;
//      if ( count == virtPageNum ) {
//        mapped = true;
//      }
//    }
//    if (oldestPage == -1) {
//      oldestPage = firstPage;
//    }
//    Page page = ( Page ) mem.elementAt( oldestPage );
//    Page nextpage = ( Page ) mem.elementAt( replacePageNum );
//    controlPanel.removePhysicalPage( oldestPage );
//    nextpage.physical = page.physical;
//    controlPanel.addPhysicalPage( nextpage.physical , replacePageNum );
//    page.inMemTime = 0;
//    page.lastTouchTime = 0;
//    page.R = 0;
//    page.M = 0;
//    page.physical = -1;
  }
}
