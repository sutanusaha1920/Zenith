package sutanu.apps.zenith.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SecurityUtilsTest {

    @Test
    fun `hashPin with empty pin returns empty string`() {
        val result = SecurityUtils.hashPin("")
        assertEquals("", result)
    }

    @Test
    fun `hashPin with valid 6 digit pin produces 64 character SHA-256 hex string`() {
        val hash = SecurityUtils.hashPin("123456")
        assertEquals(64, hash.length)
        assertTrue(hash.matches(Regex("[0-9a-f]{64}")))
    }

    @Test
    fun `hashPin produces consistent hash for same pin`() {
        val hash1 = SecurityUtils.hashPin("654321")
        val hash2 = SecurityUtils.hashPin("654321")
        assertEquals(hash1, hash2)
    }

    @Test
    fun `hashPin produces different hashes for different pins`() {
        val hash1 = SecurityUtils.hashPin("111111")
        val hash2 = SecurityUtils.hashPin("222222")
        assertNotEquals(hash1, hash2)
    }
}
