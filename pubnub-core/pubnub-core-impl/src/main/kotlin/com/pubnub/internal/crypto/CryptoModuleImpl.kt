package com.pubnub.internal.crypto

import com.pubnub.api.PubNubError
import com.pubnub.api.PubNubException
import com.pubnub.api.crypto.CryptoModule
import com.pubnub.api.crypto.cryptor.Cryptor
import com.pubnub.api.crypto.data.EncryptedData
import com.pubnub.api.crypto.data.EncryptedStreamData
import com.pubnub.internal.crypto.cryptor.HeaderParser
import com.pubnub.internal.crypto.cryptor.LEGACY_CRYPTOR_ID
import com.pubnub.internal.crypto.cryptor.ParseResult
import java.io.BufferedInputStream
import java.io.InputStream
import java.io.SequenceInputStream

private const val SIZE_OF_CRYPTOR_ID = 4

class CryptoModuleImpl internal constructor(
    @get:JvmSynthetic
    internal val primaryCryptor: Cryptor,
    @get:JvmSynthetic
    internal val cryptorsForDecryptionOnly: List<Cryptor> = listOf(),
) : CryptoModule {
    private val headerParser: HeaderParser = HeaderParser()

    override fun encrypt(data: ByteArray): ByteArray {
        val cryptorId = primaryCryptor.id()
        validateData(data)
        validateCryptorIdSize(cryptorId)
        val (metadata, encryptedData) = primaryCryptor.encrypt(data)

        return if (cryptorId.contentEquals(LEGACY_CRYPTOR_ID)) {
            encryptedData
        } else {
            val cryptorHeader = headerParser.createCryptorHeader(cryptorId, metadata)
            cryptorHeader + encryptedData
        }
    }

    override fun decrypt(encryptedData: ByteArray): ByteArray {
        validateData(encryptedData)
        val parsedData: ParseResult<out ByteArray> = headerParser.parseDataWithHeader(encryptedData)
        val decryptedData: ByteArray =
            when (parsedData) {
                is ParseResult.NoHeader -> {
                    getDecryptedDataForLegacyCryptor(encryptedData)
                }

                is ParseResult.Success -> {
                    getDecryptedDataForCryptorWithHeader(parsedData)
                }
            }
        return decryptedData
    }

    override fun encryptStream(stream: InputStream): InputStream {
        val bufferedInputStream = validateStreamAndReturnBuffered(stream)
        val (metadata, encryptedData) = primaryCryptor.encryptStream(bufferedInputStream)
        return if (primaryCryptor.id().contentEquals(LEGACY_CRYPTOR_ID)) {
            encryptedData
        } else {
            val cryptorHeader: ByteArray = headerParser.createCryptorHeader(primaryCryptor.id(), metadata)
            SequenceInputStream(cryptorHeader.inputStream(), encryptedData)
        }
    }

    override fun decryptStream(encryptedData: InputStream): InputStream {
        val bufferedInputStream = validateStreamAndReturnBuffered(encryptedData)
        return when (val parsedHeader = headerParser.parseDataWithHeader(bufferedInputStream)) {
            ParseResult.NoHeader -> {
                val decryptor = cryptorsForDecryptionOnly.firstOrNull { it.id().contentEquals(LEGACY_CRYPTOR_ID) }
                decryptor?.decryptStream(EncryptedStreamData(stream = bufferedInputStream)) ?: throw PubNubException(
                    errorMessage = "LegacyCryptor not registered",
                    pubnubError = PubNubError.UNKNOWN_CRYPTOR,
                )
            }

            is ParseResult.Success -> {
                val decryptor =
                    cryptorsForDecryptionOnly.first {
                        it.id().contentEquals(parsedHeader.cryptoId)
                    }
                decryptor.decryptStream(
                    EncryptedStreamData(
                        metadata = parsedHeader.cryptorData,
                        stream = parsedHeader.encryptedData,
                    ),
                )
            }
        }
    }

    private fun validateCryptorIdSize(cryptorId: ByteArray) {
        if (cryptorId.size != SIZE_OF_CRYPTOR_ID) {
            throw PubNubException(
                errorMessage = "CryptorId should be exactly 4 bytes long",
                pubnubError = PubNubError.UNKNOWN_CRYPTOR,
            )
        }
    }

    private fun getDecryptedDataForLegacyCryptor(encryptedData: ByteArray): ByteArray {
        return getCryptorById(LEGACY_CRYPTOR_ID)?.decrypt(EncryptedData(data = encryptedData)) ?: throw PubNubException(
            errorMessage = "LegacyCryptor not available",
            pubnubError = PubNubError.UNKNOWN_CRYPTOR,
        )
    }

    private fun getDecryptedDataForCryptorWithHeader(parsedHeader: ParseResult.Success<out ByteArray>): ByteArray {
        val decryptedData: ByteArray
        val cryptorId = parsedHeader.cryptoId
        val cryptorData = parsedHeader.cryptorData
        val pureEncryptedData = parsedHeader.encryptedData
        val cryptor = getCryptorById(cryptorId)
        decryptedData =
            cryptor?.decrypt(EncryptedData(cryptorData, pureEncryptedData))
                ?: throw PubNubException(errorMessage = "No cryptor found", pubnubError = PubNubError.UNKNOWN_CRYPTOR)
        return decryptedData
    }

    private fun getCryptorById(cryptorId: ByteArray): Cryptor? {
        validateCryptorIdSize(cryptorId)
        return cryptorsForDecryptionOnly.firstOrNull { it.id().contentEquals(cryptorId) }
    }

    private fun validateData(data: ByteArray) {
        if (data.isEmpty()) {
            throw PubNubException(
                errorMessage = "Encryption/Decryption of empty data not allowed.",
                pubnubError = PubNubError.ENCRYPTION_AND_DECRYPTION_OF_EMPTY_DATA_NOT_ALLOWED,
            )
        }
    }

    private fun validateStreamAndReturnBuffered(stream: InputStream): BufferedInputStream {
        val bufferedInputStream = stream.buffered()
        bufferedInputStream.checkMinSize(1) {
            throw PubNubException(
                errorMessage = "Encryption/Decryption of empty data not allowed.",
                pubnubError = PubNubError.ENCRYPTION_AND_DECRYPTION_OF_EMPTY_DATA_NOT_ALLOWED,
            )
        }
        return bufferedInputStream
    }
}

internal fun CryptoModule.encryptString(inputString: String): String =
    String(
        com.pubnub.internal.vendor.Base64.encode(
            encrypt(inputString.toByteArray()),
            com.pubnub.internal.vendor.Base64.NO_WRAP,
        ),
    )

internal fun CryptoModule.decryptString(inputString: String): String =
    decrypt(com.pubnub.internal.vendor.Base64.decode(inputString, com.pubnub.internal.vendor.Base64.NO_WRAP)).toString(
        Charsets.UTF_8,
    )

// this method read data from stream and allows to read them again in subsequent reads without manual reset or repositioning
internal fun BufferedInputStream.checkMinSize(
    size: Int,
    exceptionBlock: (Int) -> Unit,
) {
    mark(size + 1)

    val readBytes = readNBytez(size)
    reset()
    if (readBytes.size < size) {
        exceptionBlock(size)
    }
}

internal fun BufferedInputStream.readExactlyNBytez(
    size: Int,
    exceptionBlock: (Int) -> Unit,
): ByteArray {
    val readBytes = readNBytez(size)
    if (readBytes.size < size) {
        exceptionBlock(size)
    }
    return readBytes
}

internal fun InputStream.readNBytez(len: Int): ByteArray {
    var remaining: Int = len
    var n: Int
    val originalArray = ByteArray(remaining)
    var nread = 0

    while (read(originalArray, nread, Integer.min(originalArray.size - nread, remaining)).also { n = it } > 0) {
        nread += n
        remaining -= n
    }
    return originalArray.copyOf(nread)
}
