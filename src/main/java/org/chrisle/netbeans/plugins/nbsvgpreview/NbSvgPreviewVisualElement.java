/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chrisle.netbeans.plugins.nbsvgpreview;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.StyledDocument;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.StatusDisplayer;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;

@MultiViewElement.Registration(
        displayName = "#LBL_NbSvgPreview_VISUAL",
        iconBase = "org/chrisle/netbeans/plugins/nbsvgpreview/svg-icon.png",
        mimeType = "text/svg+xml",
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "NbSvgPreviewVisual",
        position = 2000
)
@Messages("LBL_NbSvgPreview_VISUAL=Preview")
public final class NbSvgPreviewVisualElement extends JPanel implements MultiViewElement {

    private static final RequestProcessor RP = new RequestProcessor(NbSvgPreviewVisualElement.class);

    private final Lookup context;

    private final FileObject sourceFile;

    private StyledDocument sourceDoc;
    
    private final Object lock = new Object();
    private final IHtmlView htmlView;

    private final DocumentListener sourceDocListener;
    
    private final PropertyChangeListener htmlViewListener;

    private final RequestProcessor.Task updateTask;

//    private NbSvgPreviewDataObject obj;
    private JToolBar toolbar = new JToolBar();
    private transient MultiViewElementCallback callback;

    public NbSvgPreviewVisualElement(Lookup context) {
        this.context = context;
        this.callback = null;
        this.sourceDoc = null;
        this.sourceDocListener = new DocumentHandler();
        this.updateTask = RP.create(new Runnable() {
            @Override
            public void run() {
                doUpdatePreview();
            }
        });

        DataObject dataObj = context.lookup(DataObject.class);
        if (dataObj != null) {
            sourceFile = dataObj.getPrimaryFile();
        } else {
            sourceFile = null;
        }

        htmlView = new HtmlViewFactory().createHtmlView();
        htmlViewListener = new PropertyChangeHandler();

//        toolbar = new JToolBar();
//        toolbar.setFloatable(false);
//        toolbar.addSeparator();
//        toolbar.add(new PreviewExternalAction(context));

//        obj = lkp.lookup(NbSvgPreviewDataObject.class);
//        assert obj != null;
//        initComponents();
    }

    private void updatePreview() {
        updateTask.schedule(300);
    }

    private void doUpdatePreview() {
        final StyledDocument localSourceDoc = getSourceDocument();
        if (localSourceDoc != null) {
            final String previewText = renderPreview();
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    htmlView.setContent(previewText);
                }
            });
        }
    }

    private String renderPreview() {
        IRenderable renderable = context.lookup(IRenderable.class);
        String previewText;
        try {
            Set<RenderOption> renderOptions = EnumSet.of(
                    RenderOption.PREFER_EDITOR,
                    RenderOption.RESOLVE_IMAGE_URLS);
            if (!htmlView.isHtmlFullySupported()) {
                renderOptions.add(RenderOption.SWING_COMPATIBLE);
            }
            previewText = renderable.renderAsHtml(renderOptions);
        } catch (IOException ex) {
            previewText = "Preview rendering failed: " + ex.getMessage();
        }
        return previewText;
    }

    private StyledDocument getSourceDocument() {
        synchronized (lock) {
            return sourceDoc;
        }
    }

    @Override
    public String getName() {
        return "NbSvgPreviewVisualElement";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolbar;
    }

    @Override
    public Action[] getActions() {
        return new Action[0];
    }

    @Override
    public Lookup getLookup() {
        return context;
    }

    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
    }

    @Override
    public void componentShowing() {
    }

    @Override
    public void componentHidden() {
    }

    @Override
    public void componentActivated() {
    }

    @Override
    public void componentDeactivated() {
    }

    @Override
    public UndoRedo getUndoRedo() {
        return UndoRedo.NONE;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
        this.callback = callback;
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

    private class DocumentHandler implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            updatePreview();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updatePreview();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updatePreview();
        }
    }

    private class PropertyChangeHandler implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName() == null
                    || IHtmlView.PROP_STATUS_MESSAGE.equals(evt.getPropertyName())) {
                String statusMessage = htmlView.getStatusMessage();
                StatusDisplayer.getDefault().setStatusText(statusMessage);
            }
        }
    }
    
//     @NbBundle.Messages({
//        "NAME_PreviewExternalAction=Preview in external browser"
//    })
//    public static class PreviewExternalAction extends AbstractAction {
//        private static final long serialVersionUID = 1L;
//
//        @StaticResource
//        private static final String ICON_PATH = "flow/netbeans/markdown/resources/action-view.png";
//
//        private final Lookup context;
//
//        public PreviewExternalAction(Lookup context) {
//            super(Bundle.NAME_PreviewExternalAction(), ImageUtilities.loadImageIcon(ICON_PATH, false));
//            putValue(Action.SHORT_DESCRIPTION, Bundle.NAME_PreviewExternalAction());
//            this.context = context;
//        }
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            NbSvgPreviewDataObject dataObject = context.lookup(NbSvgPreviewDataObject.class);
//            MarkdownViewHtmlAction viewAction = new MarkdownViewHtmlAction(dataObject);
//            viewAction.actionPerformed(null);
//        }
//    }
}
