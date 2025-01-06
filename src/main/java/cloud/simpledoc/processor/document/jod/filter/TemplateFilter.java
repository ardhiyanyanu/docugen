package cloud.simpledoc.processor.document.jod.filter;

import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XIndexAccess;
import com.sun.star.container.XNameAccess;
import com.sun.star.lang.XComponent;
import com.sun.star.text.XText;
import com.sun.star.text.XTextTable;
import com.sun.star.text.XTextTableCursor;
import com.sun.star.text.XTextTablesSupplier;
import com.sun.star.uno.UnoRuntime;
import jakarta.validation.constraints.NotNull;
import org.jodconverter.core.office.OfficeContext;
import org.jodconverter.local.filter.Filter;
import org.jodconverter.local.filter.FilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TemplateFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(TemplateFilter.class);

    private Map data;

    public TemplateFilter(Map data) {
        this.data = data;
    }

    @Override
    public void doFilter(@NotNull OfficeContext context, @NotNull XComponent document, @NotNull FilterChain chain) throws Exception {
        try {
            replaceText(document);
            replaceTable(document);
        } catch(Exception e) {
            logger.error("error when replacing template", e);
        }

        // Invoke the next filter in the chain
        chain.doFilter(context, document);
    }

    private void replaceText(XComponent document) throws Exception {
        com.sun.star.util.XReplaceDescriptor xReplaceDescr = null;
        com.sun.star.util.XReplaceable xReplaceable = null;

        xReplaceable = UnoRuntime.queryInterface(com.sun.star.util.XReplaceable.class, document);

        // You need a descriptor to set properties for Replace
        xReplaceDescr = xReplaceable.createReplaceDescriptor();

        logger.info("Change all occurrences of ...");
        for( int iArrayCounter = 0; iArrayCounter < data.keySet().size();
             iArrayCounter++ )
        {
            logger.info(data.keySet().toArray()[iArrayCounter] + " -> " + data.values().toArray()[iArrayCounter]);
            // Set the properties the replace method need
            xReplaceDescr.setSearchString("%%"+data.keySet().toArray()[iArrayCounter].toString()+"%%");
            xReplaceDescr.setReplaceString(data.values().toArray()[iArrayCounter].toString());

            // Replace all words
            xReplaceable.replaceAll( xReplaceDescr );
        }
    }

    private void replaceTable(XComponent document) throws Exception {
        XTextTablesSupplier xTablesSupplier = UnoRuntime.queryInterface(XTextTablesSupplier.class, document);
        XNameAccess xNamedTables = xTablesSupplier.getTextTables();
        
        XIndexAccess xIndexedTables = UnoRuntime.queryInterface(XIndexAccess.class, xNamedTables);

        for (int i = 0; i < xIndexedTables.getCount(); i++) {
            Object table = xIndexedTables.getByIndex(i);

            XTextTable xTableProps = UnoRuntime.queryInterface(XTextTable.class, table);
            
            for (int j = 0; j < xTableProps.getRows().getCount(); j++) {
                boolean isReplaceable = false;
                List<ColumnExplore> index = new ArrayList<>();

                for (int k = 0; k < xTableProps.getColumns().getCount(); k++) {
                    String cellName = getCharForNumber(k+1) + (j+1);
                    XText xCellText = (XText) UnoRuntime.queryInterface (
                        XText.class, xTableProps.getCellByName(getCharForNumber(k+1) + (j+1)));

                    if (xCellText != null) {
                        Pattern r = Pattern.compile("\\{(.*)\\}");
                    Matcher m = r.matcher(xCellText.getString());
                    if (m.find()) {
                        isReplaceable = true;
                        XTextTableCursor cellCursor = xTableProps.createCursorByCellName(cellName);
                        XPropertySet prop = UnoRuntime.queryInterface(
                        XPropertySet.class, cellCursor);
                        XPropertySet cellProperties = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, xTableProps.getCellByName(getCharForNumber(k+1) + (j+1)));

                        index.add(new ColumnExplore(getCharForNumber(k+1), m.group(1), Prop.converToProp(prop), Prop.converToProp(cellProperties)));
                    }
                    }
                }

                if (isReplaceable) {
                    index.forEach(ix -> {
                        String[] pathList = ix.fullPath.split("\\.");
                        System.out.println(pathList);
                        if (pathList.length == 2) {
                            List<Map> temp = (List<Map>) data.get(pathList[0]);
                            System.out.println(temp);
                            ix.result = temp.stream().map(t -> t.get(pathList[1])).collect(Collectors.toList());
                            System.out.println(temp.stream().map(t -> t.get(pathList[1])).collect(Collectors.toList()));
                        }
                    });
                    int dataSize = index.stream().map(ix -> ix.result.size()).reduce((acc, item) -> Math.max(acc, item)).orElse(0);
                    xTableProps.getRows().insertByIndex(j, dataSize - 1);
                    for (int k=0; k<dataSize; k++) {
                        for (int l=0; l<index.size(); l++) {
                            int rowIndex = j+1+k;
                            String cellName = index.get(l).column + rowIndex;
                            
                            XText xCellText = (XText) UnoRuntime.queryInterface (
                        XText.class, xTableProps.getCellByName(cellName));
                            if (xCellText != null) {
                                xCellText.setString(index.get(l).result.get(k).toString());
                            }

                            XPropertySet cellProperties = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, xTableProps.getCellByName(index.get(l).column + rowIndex));
                            index.get(l).cellProp.forEach(p -> {
                                try {
                                    cellProperties.setPropertyValue(p.name, p.content);
                                } catch (Exception ex) {
                                    logger.error("ERROR IN SETTING " + p.name);
                                }
                            });
                            
                            XTextTableCursor cellCursor = xTableProps.createCursorByCellName(cellName);
                            XPropertySet prop = UnoRuntime.queryInterface(XPropertySet.class, cellCursor);
                            
                            index.get(l).prop.forEach(p -> {
                                try {
                                    prop.setPropertyValue(p.name, p.content);
                                } catch (Exception ex) {
                                    // Do nothing
                                }
                            });
                        }
                    }
                    j += dataSize - 1;
                }
            }
        }
    }

    private String getCharForNumber(int i) {
        return i > 0 && i < 27 ? String.valueOf((char)(i + 64)) : null;
    }
}

class ColumnExplore {
    String column;
    String fullPath;
    List<Prop> prop;
    List<Prop> cellProp;
    List result;

    public ColumnExplore(String column, String fullPath, List<Prop> prop, List<Prop> cellProp) {
        this.column = column;
        this.fullPath = fullPath;
        this.prop = prop;
        this.cellProp = cellProp;
    }
}

class Prop {
    String name;
    Object content;

    public Prop(String name, Object content) {
        this.name = name;
        this.content = content;
    }

    public static List<Prop> converToProp(XPropertySet properties) {
        return List.of(properties.getPropertySetInfo().getProperties()).stream().map(p -> {
            try {
                return new Prop(p.Name, properties.getPropertyValue(p.Name));
            } catch (Exception ex) {
                System.out.println("ERROR IN SETTING " + p.Name + " : " + p.Attributes + " : " + p.Type.getTypeName() + " : " + ex.getMessage());
            }
            return null;
        }).filter(p -> p != null).collect(Collectors.toList());
    }
}
