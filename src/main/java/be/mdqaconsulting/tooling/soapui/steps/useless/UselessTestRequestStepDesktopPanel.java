package be.mdqaconsulting.tooling.soapui.steps.useless;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.analytics.Analytics;
import com.eviware.soapui.analytics.SoapUIActions;
import com.eviware.soapui.impl.rest.RestRequestInterface;
import com.eviware.soapui.impl.support.AbstractHttpRequest;
import com.eviware.soapui.impl.support.HttpUtils;
import com.eviware.soapui.impl.support.components.ModelItemXmlEditor;
import com.eviware.soapui.impl.support.panels.AbstractHttpXmlRequestDesktopPanel;
import com.eviware.soapui.impl.wsdl.panels.teststeps.AssertionsPanel;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestRunContext;
import com.eviware.soapui.impl.wsdl.teststeps.RestTestRequestInterface;
import com.eviware.soapui.impl.wsdl.teststeps.actions.AddAssertionAction;
import com.eviware.soapui.model.iface.Request.SubmitException;
import com.eviware.soapui.model.iface.Submit;
import com.eviware.soapui.model.testsuite.Assertable.AssertionStatus;
import com.eviware.soapui.model.testsuite.AssertionError;
import com.eviware.soapui.model.testsuite.AssertionsListener;
import com.eviware.soapui.model.testsuite.LoadTestRunner;
import com.eviware.soapui.model.testsuite.TestAssertion;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.monitor.support.TestMonitorListenerAdapter;
import com.eviware.soapui.security.SecurityTestRunner;
import com.eviware.soapui.support.DocumentListenerAdapter;
import com.eviware.soapui.support.ListDataChangeListener;
import com.eviware.soapui.support.StringUtils;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.components.JComponentInspector;
import com.eviware.soapui.support.components.JInspectorPanel;
import com.eviware.soapui.support.components.JInspectorPanelFactory;
import com.eviware.soapui.support.components.JUndoableTextField;
import com.eviware.soapui.support.components.JXToolBar;
import com.eviware.soapui.support.log.JLogList;

/**
 * Simple DesktopPanel that provides a basic UI for configuring the EMailTestStep. Should perhaps be improved with
 * a "Test" button and a log panel.
 */

public class UselessTestRequestStepDesktopPanel
    extends AbstractHttpXmlRequestDesktopPanel<UselessTestRequestStepInterface, UselessTestRequestInterface>
{
	private static final long serialVersionUID = 1L;

	//From httprequest setup in github
    private InternalAssertionsListener assertionsListener = new InternalAssertionsListener();
    private InternalTestMonitorListener testMonitorListener = new InternalTestMonitorListener();
    
    private boolean updating;
    
    private JUndoableTextField pathTextField;
    private JLogList logArea;
    private JComponentInspector<?> logInspector;
    private JComponentInspector<?> assertionInspector;
    private JButton addAssertionButton;
    protected boolean updatingRequest;
    private AssertionsPanel assertionsPanel;
    private JInspectorPanel inspectorPanel;
    @SuppressWarnings("rawtypes")
	private JComboBox methodCombo;
    
	public UselessTestRequestStepDesktopPanel( UselessTestRequestStepInterface testStep )
	{
		super( testStep, testStep.getTestRequest() );
		SoapUI.getTestMonitor().addTestMonitorListener(testMonitorListener);
        setEnabled(!SoapUI.getTestMonitor().hasRunningTest(testStep.getTestCase()));

        testStep.getTestRequest().addAssertionsListener(assertionsListener);

        getSubmitButton().setEnabled(getSubmit() == null && StringUtils.hasContent(getRequest().getEndpoint()));
	}
	
	/*
	 * Build the generally available UI components from SoapUI.
	 */
    protected JComponent buildLogPanel() {
        logArea = new JLogList("Request Log");

        logArea.getLogList().getModel().addListDataListener(new ListDataChangeListener() {
            @Override
            public void dataChanged(@SuppressWarnings("rawtypes") ListModel model) {
                logInspector.setTitle("Request Log (" + model.getSize() + ")");
            }
        });

        return logArea;
    }

    @SuppressWarnings("serial")
	protected AssertionsPanel buildAssertionsPanel() {
        return new AssertionsPanel(getRequest()) {
            @Override
            protected void selectError(AssertionError error) {
                ModelItemXmlEditor<?, ?> editor = getResponseEditor();
                editor.requestFocus();
            }
        };
    }
    
    @Override
    public void setContent(JComponent content) {
        inspectorPanel.setContentComponent(content);
    }

    @Override
    public void removeContent(JComponent content) {
        inspectorPanel.setContentComponent(null);
    }
    
    @Override
    protected JComponent buildContent() {
        JComponent component = super.buildContent();

        inspectorPanel = JInspectorPanelFactory.build(component);
        assertionsPanel = buildAssertionsPanel();

        assertionInspector = new JComponentInspector<JComponent>(assertionsPanel, "Assertions ("
                + getModelItem().getAssertionCount() + ")", "Assertions for this Request", true);

        inspectorPanel.addInspector(assertionInspector);

        logInspector = new JComponentInspector<JComponent>(buildLogPanel(), "Request Log (0)", "Log of requests", true);
        inspectorPanel.addInspector(logInspector);
        inspectorPanel.setDefaultDividerLocation(0.6F);
        inspectorPanel.setCurrentInspector("Assertions");

        updateStatusIcon();

        getSubmitButton().setEnabled(getSubmit() == null && StringUtils.hasContent(getRequest().getEndpoint()));

        return inspectorPanel.getComponent();
    }

    @Override
    protected JComponent buildEndpointComponent() {
        return null;
    }
    

    private void updateStatusIcon() {
        AssertionStatus status = getModelItem().getTestRequest().getAssertionStatus();
        switch (status) {
            case FAILED: {
                assertionInspector.setIcon(UISupport.createImageIcon("/failed_assertion.gif"));
                inspectorPanel.activate(assertionInspector);
                break;
            }
            case UNKNOWN: {
                assertionInspector.setIcon(UISupport.createImageIcon("/unknown_assertion.png"));
                break;
            }
            case VALID: {
                assertionInspector.setIcon(UISupport.createImageIcon("/valid_assertion.gif"));
                inspectorPanel.deactivate();
                break;
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected void addMethodCombo(JXToolBar toolbar) {
        methodCombo = new JComboBox(RestRequestInterface.HttpMethod.getMethods());

        methodCombo.setSelectedItem(getRequest().getMethod());
        methodCombo.setToolTipText("Set desired HTTP method");
        methodCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                updatingRequest = true;
                getRequest().setMethod((RestRequestInterface.HttpMethod) methodCombo.getSelectedItem());
                updatingRequest = false;
            }
        });

        toolbar.addLabeledFixed("Method", methodCombo);
        toolbar.addSeparator();
    }
    
    protected void addToolbarComponents(JXToolBar toolbar) {
        toolbar.addSeparator();

        pathTextField = new JUndoableTextField();
        pathTextField.setPreferredSize(new Dimension(300, 20));
        pathTextField.setText(getRequest().getEndpoint());
        pathTextField.setToolTipText(pathTextField.getText());
        pathTextField.getDocument().addDocumentListener(new DocumentListenerAdapter() {
            @Override
            public void update(Document document) {
                if (updating) {
                    return;
                }

                updating = true;
                String text = pathTextField.getText();
                getRequest().setEndpoint(HttpUtils.completeUrlWithHttpIfProtocolIsNotHttpOrHttpsOrPropertyExpansion(text));
                if (!text.equals(getRequest().getEndpoint())) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            pathTextField.setText(getRequest().getEndpoint());
                        }
                    });
                }

                updating = false;
            }
        });
        
        pathTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    onSubmit();
                }
            }
        });
        
        JPanel pathPanel = new JPanel(new BorderLayout(0, 0));
        pathPanel.add(getLockIcon(), BorderLayout.WEST);
        pathPanel.add(pathTextField, BorderLayout.CENTER);
        toolbar.addLabeledFixed("Request URL:", pathPanel);

        toolbar.addSeparator();
    }
    
    @Override
    protected JComponent buildToolbar() {
        addAssertionButton = createActionButton(new AddAssertionAction(getRequest()), true);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(super.buildToolbar(), BorderLayout.NORTH);

        JXToolBar toolbar = UISupport.createToolbar();
        addToolbarComponents(toolbar);

        panel.add(toolbar, BorderLayout.SOUTH);
        return panel;
    }

	@Override
	protected void insertButtons(JXToolBar toolbar) {
		toolbar.add(addAssertionButton);
	}
    
    @Override
    public void setEnabled(boolean enabled) {
        if (enabled == true) {
            enabled = !SoapUI.getTestMonitor().hasRunningLoadTest(getModelItem().getTestCase())
                    && !SoapUI.getTestMonitor().hasRunningSecurityTest(getModelItem().getTestCase());
        }

        super.setEnabled(enabled);
        addAssertionButton.setEnabled(enabled);
        assertionsPanel.setEnabled(enabled);

        if (SoapUI.getTestMonitor().hasRunningLoadTest(getRequest().getTestCase())
                || SoapUI.getTestMonitor().hasRunningSecurityTest(getModelItem().getTestCase())) {
            getRequest().removeSubmitListener(this);
        } else {
            getRequest().addSubmitListener(this);
        }
    }

	@Override
	protected Submit doSubmit() throws SubmitException {
        Analytics.trackAction(SoapUIActions.RUN_TEST_STEP_FROM_PANEL, "StepType", "Useless",
                "HTTPMethod", getRequest().getMethod().name());
        Analytics.trackAction(SoapUIActions.RUN_TEST_STEP, "HTTPMethod", getRequest().getMethod().name());

        return getRequest().submit(new WsdlTestRunContext(getModelItem()), true);
	}

	@Override
	protected String getHelpUrl() {
		return "soapui.org";
	}

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(RestTestRequestInterface.STATUS_PROPERTY)) {
            updateStatusIcon();
        } else if (evt.getPropertyName().equals("path")) {
            getSubmitButton().setEnabled(getSubmit() == null && StringUtils.hasContent(getRequest().getEndpoint()));
        } else if (evt.getPropertyName().equals(AbstractHttpRequest.ENDPOINT_PROPERTY)) {
            // fix SOAP-3369
            getSubmitButton().setEnabled(getSubmit() == null && StringUtils.hasContent(getRequest().getEndpoint()));
            // end of SOAP-3369
            if (updating) {
                return;
            }

            updating = true;
            pathTextField.setText(String.valueOf(evt.getNewValue()));
            updating = false;
        }
        super.propertyChange(evt);
    }
	
	// Private classes
    private final class InternalAssertionsListener implements AssertionsListener {
        public void assertionAdded(TestAssertion assertion) {
            assertionInspector.setTitle("Assertions (" + getModelItem().getAssertionCount() + ")");
        }

        public void assertionRemoved(TestAssertion assertion) {
            assertionInspector.setTitle("Assertions (" + getModelItem().getAssertionCount() + ")");
        }

        public void assertionMoved(TestAssertion assertion, int ix, int offset) {
            assertionInspector.setTitle("Assertions (" + getModelItem().getAssertionCount() + ")");
        }
    }
    
    private class InternalTestMonitorListener extends TestMonitorListenerAdapter {
        @Override
        public void loadTestFinished(LoadTestRunner runner) {
            setEnabled(!SoapUI.getTestMonitor().hasRunningTest(getModelItem().getTestCase()));
        }

        @Override
        public void loadTestStarted(LoadTestRunner runner) {
            if (runner.getLoadTest().getTestCase() == getModelItem().getTestCase()) {
                setEnabled(false);
            }
        }

        public void securityTestFinished(SecurityTestRunner runner) {
            setEnabled(!SoapUI.getTestMonitor().hasRunningTest(getModelItem().getTestCase()));
        }

        public void securityTestStarted(SecurityTestRunner runner) {
            if (runner.getSecurityTest().getTestCase() == getModelItem().getTestCase()) {
                setEnabled(false);
            }
        }

        @Override
        public void testCaseFinished(TestCaseRunner runner) {
            setEnabled(!SoapUI.getTestMonitor().hasRunningTest(getModelItem().getTestCase()));
        }

        @Override
        public void testCaseStarted(TestCaseRunner runner) {
            if (runner.getTestCase() == getModelItem().getTestCase()) {
                setEnabled(false);
            }
        }
    }
}
