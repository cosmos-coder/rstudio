package org.rstudio.studio.client.common.cran;

import org.rstudio.core.client.widget.FocusHelper;
import org.rstudio.core.client.widget.ModalDialog;
import org.rstudio.core.client.widget.OperationWithInput;
import org.rstudio.core.client.widget.SimplePanelWithProgress;
import org.rstudio.core.client.widget.images.ProgressImages;
import org.rstudio.studio.client.common.GlobalDisplay;
import org.rstudio.studio.client.common.SimpleRequestCallback;
import org.rstudio.studio.client.common.cran.model.CRANMirror;
import org.rstudio.studio.client.server.ServerDataSource;
import org.rstudio.studio.client.server.ServerError;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

class ChooseCRANMirrorDialog extends ModalDialog<CRANMirror>
{
   public ChooseCRANMirrorDialog(GlobalDisplay globalDisplay,
                                 ServerDataSource<JsArray<CRANMirror>> mirrorDS,
                                 OperationWithInput<CRANMirror> inputOperation)
   {
      super("Choose CRAN Mirror", inputOperation);
      globalDisplay_ = globalDisplay;
      mirrorDS_ = mirrorDS;
   }

   @Override
   protected CRANMirror collectInput()
   {
      if (listBox_ != null && listBox_.getSelectedIndex() >= 0)
      {
         return mirrors_.get(listBox_.getSelectedIndex());
      }
      else
      {
         return null;
      }
   }

   @Override
   protected boolean validate(CRANMirror input)
   {
      if (input == null)
      {
         globalDisplay_.showErrorMessage("Error", 
                                         "Please select a CRAN Mirror");
         return false;
      }
      else
      {
         return true;
      }
   }

   @Override
   protected Widget createMainWidget()
   {
      // create progress container
      final SimplePanelWithProgress panel = new SimplePanelWithProgress(
                                          ProgressImages.createLargeGray());
      final int defaultPanelHeight = 285;
      panel.setSize("420px", defaultPanelHeight + "px");
      
      // show progress (with delay)
      panel.showProgress(200);
      
      // query data source for packages
      mirrorDS_.requestData(new SimpleRequestCallback<JsArray<CRANMirror>>() {

         @Override 
         public void onResponseReceived(JsArray<CRANMirror> mirrors)
         {   
            // save mirrors
            mirrors_ = mirrors;
            
            // create list box and select default item
            listBox_ = new ListBox(false);
            listBox_.setVisibleItemCount(18); // all
            listBox_.setWidth("100%");
            int usRows = 0;
            if (mirrors_.length() > 0)
            {
               for(int i=0; i<mirrors_.length(); i++)
               {
                  CRANMirror mirror = mirrors_.get(i);
                  String item = mirror.getName() + " - " + mirror.getHost();
                  String value = mirror.getURL();
                  
                  if ("us".equals(mirror.getCountry()))
                     listBox_.insertItem(item, value, usRows++);
                  else
                     listBox_.addItem(item, value);
               }
               listBox_.setSelectedIndex(0);
            }
            
            // set it into the panel
            panel.setWidget(listBox_);
            
            // if the list box is larger than the space we initially allocated
            // then increase the panel height
            if (listBox_.getOffsetHeight() > defaultPanelHeight)
               panel.setHeight(listBox_.getOffsetHeight() + "px");
            
            // set focus   
            FocusHelper.setFocusDeferred(listBox_);
         }
         
         @Override
         public void onError(ServerError error)
         {
            closeDialog();
            super.onError(error);
         }
      });
      
      return panel;
   }
   
   private final GlobalDisplay globalDisplay_ ;
   private final ServerDataSource<JsArray<CRANMirror>> mirrorDS_;
   private JsArray<CRANMirror> mirrors_ = null;
   private ListBox listBox_ = null;

}
