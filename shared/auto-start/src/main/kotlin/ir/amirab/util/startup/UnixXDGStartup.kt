package ir.amirab.util.startup

import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

class UnixXDGStartup(
    name: String,
    path: String,
    args: List<String>,
) : AbstractStartupManager(
    name = name,
    path = path,
    args = args,
) {

    private fun getIconFilePath(): String? {
        return runCatching {
            val file = File(path)
            val name = file.name
            return file
                .parentFile.parentFile
                .resolve("lib/$name.png")
                .takeIf { it.exists() }?.path
        }.getOrNull()
    }

    private fun getAutoStartFile(): File {
        if (!autostartDir.exists()) {
            autostartDir.mkdirs()
        }
        return File(autostartDir, this.name + ".desktop")
    }

    @Throws(Exception::class)
    override fun install() {
        val out = PrintWriter(FileWriter(getAutoStartFile()))
        out.println("[Desktop Entry]")
        out.println("Type=Application")
        out.println("Name=" + this.name)
        out.println("Exec=" + getExecutableWithArgs())
        getIconFilePath()?.let {
            out.println("Icon=$it")
        }        
        out.println("Terminal=false")
        out.println("NoDisplay=true")
        out.close()
    }

    override fun uninstall() {
        getAutoStartFile().delete()
    }

    companion object {
        val autostartDir: File
            get() {
                val home = System.getProperty("user.home")

                return File("$home/.config/autostart/")
            }
    }
}
