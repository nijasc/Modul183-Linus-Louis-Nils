package lol.linkstack.constants

object JsScripts {

    const val WINDOW_LOCATION_ORIGIN = "return window.location.origin"

    private val COPY_TO_CLIPBOARD_TEMPLATE = """
        const text = '%s';
        const fallback = () => {
            const ta = document.createElement('textarea');
            ta.value = text;
            ta.style.position = 'fixed';
            ta.style.opacity = '0';
            document.body.appendChild(ta);
            ta.focus(); ta.select();
            try { document.execCommand('copy'); } finally { document.body.removeChild(ta); }
        };
        if (navigator.clipboard && window.isSecureContext) {
            navigator.clipboard.writeText(text).catch(fallback);
        } else { fallback(); }
    """.trimIndent()

    fun copyToClipboard(text: String): String {
        val escaped = text.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n")
        return COPY_TO_CLIPBOARD_TEMPLATE.format(escaped)
    }
}
