package lol.linkstack.config

import com.vaadin.flow.component.dependency.StyleSheet
import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.component.page.ColorScheme
import com.vaadin.flow.component.page.ColorScheme.Value
import com.vaadin.flow.theme.lumo.Lumo

@StyleSheet(Lumo.STYLESHEET)
@ColorScheme(Value.DARK)
class VaadinConfig : AppShellConfigurator