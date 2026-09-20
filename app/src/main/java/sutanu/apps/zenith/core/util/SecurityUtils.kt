package sutanu.apps.zenith.core.util

import java.security.MessageDigest

object SecurityUtils {
    /**
     * Hashes a raw PIN using SHA-256 with a Zenith application salt.
     */
    fun hashPin(pin: String): String {
        if (pin.isEmpty()) return ""
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest("ZenithSalt_$pin".toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
