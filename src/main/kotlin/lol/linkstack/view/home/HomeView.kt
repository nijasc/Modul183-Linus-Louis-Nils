package lol.linkstack.view.home

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.card.Card
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import jakarta.annotation.PostConstruct
import lol.linkstack.constants.CssEvent
import lol.linkstack.constants.CssProperty
import lol.linkstack.constants.CssToken
import lol.linkstack.constants.Routes
import org.springframework.security.core.context.SecurityContextHolder

@Route(Routes.HOME)
@AnonymousAllowed
class HomeView : VerticalLayout() {

    @PostConstruct
    fun init() {
        isPadding = false
        setWidthFull()
        justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER

        add(
            buildHeroSection(),
            buildFeaturesSection(),
            buildHowItWorksSection(),
            buildCallToActionSection()
        )
    }

    private fun buildHeroSection(): VerticalLayout {
        val isLoggedIn = isUserLoggedIn()
        val title = if (isLoggedIn) "Welcome back to Linkstack!" else "Your Ultimate Link Sharing Platform"

        val actions = if (isLoggedIn) {
            HorizontalLayout(
                Button("Go to Dashboard", Icon(VaadinIcon.DASHBOARD)).apply {
                    addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE)
                    addClickListener { ui.ifPresent { it.page.setLocation(Routes.DASHBOARD) } }
                }
            )
        } else {
            HorizontalLayout(
                Button("Get Started", Icon(VaadinIcon.ARROW_RIGHT)).apply {
                    addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE)
                    addClickListener { ui.ifPresent { it.page.setLocation(Routes.SIGNUP) } }
                },
                Button("Login", Icon(VaadinIcon.SIGN_IN)).apply {
                    addThemeVariants(ButtonVariant.LUMO_LARGE)
                    addClickListener { ui.ifPresent { it.page.setLocation(Routes.LOGIN) } }
                }
            ).apply { isSpacing = true }
        }.apply {
            alignItems = FlexComponent.Alignment.CENTER
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
            style.set(CssProperty.MARGIN_TOP, "1rem")
        }

        return VerticalLayout(
            H1(title).apply {
                style.set(CssProperty.FONT_SIZE, HERO_TITLE_SIZE)
                style.set(CssProperty.MARGIN_BOTTOM, "0.5rem")
                style.set(CssProperty.TEXT_ALIGN, "center")
            },
            Paragraph("Share your favorite links with the world. Connect your gaming profiles, showcase your social media, and build your digital presence.").apply {
                style.set(CssProperty.FONT_SIZE, "1.2rem")
                style.set(CssProperty.COLOR, CssToken.LUMO_SECONDARY_TEXT_COLOR)
                style.set(CssProperty.MAX_WIDTH, "600px")
                style.set(CssProperty.TEXT_ALIGN, "center")
            },
            actions
        ).apply {
            isPadding = false
            alignItems = FlexComponent.Alignment.CENTER
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
            defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER
            style.set(CssProperty.PADDING, "4rem 1rem")
            style.set(CssProperty.BACKGROUND, CssToken.GRADIENT_PRIMARY_SECONDARY)
            setWidthFull()
        }
    }

    private fun buildFeaturesSection(): VerticalLayout {
        return VerticalLayout(
            buildSectionHeading("Features"),
            HorizontalLayout(
                buildFeatureCard(
                    VaadinIcon.LINK, "Link Management",
                    "Add, edit, and organize your links with custom icons and colors"
                ),
                buildFeatureCard(
                    VaadinIcon.COMMENT_O, "Comments",
                    "Engage with your audience through comments on your profile"
                ),
                buildFeatureCard(
                    VaadinIcon.PAINTBRUSH, "Custom Styling",
                    "Personalize your page with custom colors and themes"
                )
            ).apply {
                justifyContentMode = FlexComponent.JustifyContentMode.CENTER
                alignItems = FlexComponent.Alignment.CENTER
                style.set(CssProperty.FLEX_WRAP, "wrap")
                isPadding = false
                setWidthFull()
            }
        ).apply {
            isPadding = false
            alignItems = FlexComponent.Alignment.CENTER
            defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER
            style.set(CssProperty.PADDING, "3rem 1rem")
            setWidthFull()
        }
    }

    private fun buildFeatureCard(icon: VaadinIcon, title: String, description: String): Card {
        return Card().apply {
            style.set(CssProperty.PADDING, "1.5rem")
            style.set(CssProperty.MARGIN, "0.5rem")
            style.set(CssProperty.TRANSITION, "transform 0.3s, box-shadow 0.3s")
            width = CARD_WIDTH
            element.addEventListener(CssEvent.MOUSE_ENTER) {
                style.set(CssProperty.TRANSFORM, "translateY(-4px)")
                style.set(CssProperty.BOX_SHADOW, CssToken.SHADOW_HOVER)
            }
            element.addEventListener(CssEvent.MOUSE_LEAVE) {
                style.set(CssProperty.TRANSFORM, "translateY(0)")
                style.set(CssProperty.BOX_SHADOW, CssToken.SHADOW_LIGHT)
            }
            add(
                VerticalLayout(
                    Icon(icon).apply {
                        style.set(CssProperty.FONT_SIZE, FEATURE_ICON_SIZE)
                        style.set(CssProperty.COLOR, CssToken.LUMO_PRIMARY_COLOR)
                    },
                    H2(title).apply {
                        style.set(CssProperty.FONT_SIZE, "1.3rem")
                        style.set(CssProperty.MARGIN, "0.5rem 0")
                        style.set(CssProperty.TEXT_ALIGN, "center")
                    },
                    Paragraph(description).apply {
                        style.set(CssProperty.COLOR, CssToken.LUMO_SECONDARY_TEXT_COLOR)
                        style.set(CssProperty.MARGIN, "0")
                        style.set(CssProperty.TEXT_ALIGN, "center")
                    }
                ).apply {
                    isPadding = false
                    alignItems = FlexComponent.Alignment.CENTER
                    defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER
                }
            )
        }
    }

    private fun buildHowItWorksSection(): VerticalLayout {
        return VerticalLayout(
            buildSectionHeading("How It Works"),
            HorizontalLayout(
                buildStepCard("1", VaadinIcon.USER, "Create Account", "Sign up for free in seconds"),
                buildStepCard("2", VaadinIcon.LINK, "Add Links", "Add your favorite links with custom icons"),
                buildStepCard("3", VaadinIcon.SHARE, "Share Profile", "Share your @username with the world")
            ).apply {
                justifyContentMode = FlexComponent.JustifyContentMode.CENTER
                alignItems = FlexComponent.Alignment.CENTER
                style.set(CssProperty.FLEX_WRAP, "wrap")
                isPadding = false
                setWidthFull()
            }
        ).apply {
            isPadding = false
            alignItems = FlexComponent.Alignment.CENTER
            defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER
            style.set(CssProperty.PADDING, "3rem 1rem")
            style.set(CssProperty.BACKGROUND_COLOR, CssToken.LUMO_CONTRAST_5PCT)
            setWidthFull()
        }
    }

    private fun buildStepCard(step: String, icon: VaadinIcon, title: String, description: String): Card {
        return Card().apply {
            style.set(CssProperty.PADDING, "1.5rem")
            style.set(CssProperty.MARGIN, "0.5rem")
            style.set(CssProperty.TEXT_ALIGN, "center")
            width = STEP_CARD_WIDTH
            add(
                VerticalLayout(
                    Span(step).apply {
                        style.set(CssProperty.FONT_SIZE, "2rem")
                        style.set(CssProperty.FONT_WEIGHT, "bold")
                        style.set(CssProperty.COLOR, CssToken.LUMO_PRIMARY_COLOR)
                    },
                    Icon(icon).apply { style.set(CssProperty.FONT_SIZE, "2rem") },
                    H2(title).apply {
                        style.set(CssProperty.FONT_SIZE, "1.2rem")
                        style.set(CssProperty.TEXT_ALIGN, "center")
                    },
                    Paragraph(description).apply {
                        style.set(CssProperty.COLOR, CssToken.LUMO_SECONDARY_TEXT_COLOR)
                        style.set(CssProperty.TEXT_ALIGN, "center")
                    }
                ).apply {
                    isPadding = false
                    alignItems = FlexComponent.Alignment.CENTER
                    defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER
                }
            )
        }
    }

    private fun buildCallToActionSection(): VerticalLayout {
        return VerticalLayout(
            H2("Ready to get started?").apply { style.set(CssProperty.TEXT_ALIGN, "center") },
            Paragraph("Join thousands of users sharing their links with Linkstack.").apply {
                style.set(CssProperty.COLOR, CssToken.LUMO_SECONDARY_TEXT_COLOR)
                style.set(CssProperty.TEXT_ALIGN, "center")
            },
            Button("Create Your Profile", Icon(VaadinIcon.PLUS_CIRCLE)).apply {
                addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE)
                addClickListener { ui.ifPresent { it.page.setLocation(Routes.SIGNUP) } }
            }
        ).apply {
            isPadding = false
            alignItems = FlexComponent.Alignment.CENTER
            defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER
            style.set(CssProperty.PADDING, "4rem 1rem")
            setWidthFull()
        }
    }

    private fun buildSectionHeading(text: String): H2 = H2(text).apply {
        style.set(CssProperty.MARGIN_BOTTOM, "2rem")
        style.set(CssProperty.TEXT_ALIGN, "center")
    }

    private fun isUserLoggedIn(): Boolean {
        val auth = SecurityContextHolder.getContext().authentication ?: return false
        return auth.isAuthenticated && auth.name != ANONYMOUS
    }

    companion object {
        private const val ANONYMOUS = "anonymousUser"
        private const val HERO_TITLE_SIZE = "3rem"
        private const val FEATURE_ICON_SIZE = "2.5rem"
        private const val CARD_WIDTH = "280px"
        private const val STEP_CARD_WIDTH = "250px"
    }
}
