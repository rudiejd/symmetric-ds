package org.jumpmind.vaadin.ui.sqlexplorer;

import java.util.Map;

import org.jumpmind.db.model.Trigger;
import org.jumpmind.vaadin.ui.common.ReadOnlyTextAreaDialog;
import org.jumpmind.vaadin.ui.sqlexplorer.TriggerInfoPanel.Refresher;

import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class TriggerTableLayout extends VerticalLayout{

    private static final long serialVersionUID = 1L;
    
    private Trigger trigger;
    
    private Grid<String> grid;
    
    private Refresher refresher;
    
    public TriggerTableLayout(Trigger trigger, Settings settings, Refresher refresher) {
        this.trigger = trigger;
        this.refresher = refresher;
        
        createTabularLayout();
    }
    
    public void createTabularLayout() {
        this.setSizeFull();
        this.setSpacing(false);
        
        HorizontalLayout bar = new HorizontalLayout();
        bar.setWidthFull();
        bar.setMargin(new MarginInfo(false, true, false, true));

        HorizontalLayout leftBar = new HorizontalLayout();
        leftBar.setSpacing(true);
        final Span span = new Span();
        span.getElement().setProperty("innerHTML", trigger.getFullyQualifiedName());
        leftBar.add(span);
        
        bar.add(leftBar);
        bar.setVerticalComponentAlignment(Alignment.CENTER, leftBar);
        bar.expand(leftBar);
        
        MenuBar rightBar = new MenuBar();
        rightBar.addClassName(ValoTheme.MENUBAR_BORDERLESS);
        rightBar.addClassName(ValoTheme.MENUBAR_SMALL);

        MenuItem refreshButton = rightBar.addItem(new Icon(VaadinIcon.REFRESH), event -> refresher.refresh());

        bar.add(rightBar);
        bar.setVerticalComponentAlignment(Alignment.CENTER, rightBar);
        
        this.add(bar);
        
        grid = fillGrid();
        grid.setSizeFull();
        
        grid.addItemClickListener(event -> {
            if (event.getButton() == 0 && event.getColumn() != null) {
                if (event.getClickCount() == 2) {
                    String colKey = event.getColumn().getKey();
                    if (colKey.equals("property")) {
                        ReadOnlyTextAreaDialog.show("Property", event.getItem(), false);
                    } else if (colKey.equals("value")) {
                        ReadOnlyTextAreaDialog.show("Value", (String) trigger.getMetaData().get(event.getItem()), false);
                    }
                } else {
                    grid.deselectAll();
                    grid.select(event.getItem());
                }
            }
        });

        this.add(grid);
        this.expand(grid);
    }
    
    private Grid<String> fillGrid() {
        Grid<String> grid = new Grid<String>();
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.setColumnReorderingAllowed(false);
        
        Map<String, Object> metaData = trigger.getMetaData();
        grid.addColumn(property -> property).setKey("property").setHeader("Property").setWidth("250px");
        grid.addColumn(property -> String.valueOf(metaData.get(property))).setKey("value").setHeader("Value");
        
        grid.setItems(metaData.keySet());
        
        return grid;
    }    
    
}
