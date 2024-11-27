import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}
import java.util.Base64

object Aes256 {

  def encrypt(text: String, keyString: String, saltString: String): String = {
    val pass = new SecretKeySpec(keyString.getBytes("UTF-8"), "AES")
    val salt = new IvParameterSpec(saltString.getBytes("UTF-8"))

    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, pass, salt)

    val encrypted = cipher.doFinal(text.getBytes("UTF-8"))
    Base64.getEncoder.encodeToString(encrypted)
  }

  def decrypt(encrypted: String, keyString: String, saltString: String): String = {
    val pass = new SecretKeySpec(keyString.getBytes("UTF-8"), "AES")
    val salt = new IvParameterSpec(saltString.getBytes("UTF-8"))

    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, pass, salt)

    val decoded = Base64.getDecoder.decode(encrypted)
    new String(cipher.doFinal(decoded))
  }
}
