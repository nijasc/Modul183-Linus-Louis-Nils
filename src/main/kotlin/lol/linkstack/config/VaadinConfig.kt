package lol.linkstack.config

import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.dependency.StyleSheet
import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.component.page.ColorScheme
import com.vaadin.flow.component.page.ColorScheme.Value
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.aura.Aura
import com.vaadin.flow.theme.lumo.Lumo

@StyleSheet(Aura.STYLESHEET)
@ColorScheme(Value.DARK)
class VaadinConfig : AppShellConfigurator {
}