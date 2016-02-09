package org.chrisle.netbeans.plugins.nbsvgpreview;

import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import org.netbeans.api.annotations.common.CheckForNull;

/**
 *
 * @author chrl
 */
public interface IHtmlView {
    static final String PROP_STATUS_MESSAGE = "StatusMessage";

    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);

    JComponent getComponent();

    void setContent(String content);

    boolean isHtmlFullySupported();

    @CheckForNull
    String getStatusMessage();
}