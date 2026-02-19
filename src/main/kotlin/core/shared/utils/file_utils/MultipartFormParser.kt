package kz.kff.core.shared.utils.file_utils

import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import kotlinx.datetime.LocalDate
import kz.kff.core.exception_handlers.api.ApiBadRequestException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.service.file.FileService
import kz.kff.domain.dto.file.FileCDTO
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor

/**
 * Разбирает multipart-запрос: собирает form-поля в DTO и опционально загружает файл.
 *
 * @param multipart     входящий multipart
 * @param dtoClass      класс DTO для заполнения (должен иметь primary constructor)
 * @param fileService   сервис для сохранения файла
 * @param filePartName  имя part с файлом (default: "file")
 * @return Pair<T, FileCDTO?> — собранный DTO и метаданные файла (null, если файл не был передан)
 */
suspend fun <T : Any> receiveMultipartWithDto(
    multipart: MultiPartData,
    dtoClass: KClass<T>,
    fileService: FileService,
    filePartName: String = "file",
    skipIfNotValidate: Boolean = true,
    folder: String? = null,
): Pair<T, FileCDTO?> {
    val formFields = mutableMapOf<String, String>()
    var fileDTO: FileCDTO? = null

    multipart.forEachPart { part ->
        try {
            when {
                part is PartData.FormItem -> {
                    part.name?.let { formFields[it] = part.value }
                }
                part is PartData.FileItem && part.name == filePartName -> {
                    val fileName = part.originalFileName
                        ?: throw ApiBadRequestException(LocalizedMessageConstraints.FileNameRequiredFileUpload)
                    val mimeType = part.contentType?.toString() ?: "application/octet-stream"
                    part.streamProvider().use { inputStream ->
                        fileDTO = fileService.store(
                            fileName, mimeType, inputStream,
                            skipIfNotValidate = skipIfNotValidate,
                            folder = folder,
                        )
                    }
                }
            }
        } finally {
            part.dispose()
        }
    }

    val dto = buildDtoFromMap(dtoClass, formFields)
    return dto to fileDTO
}

/**
 * Конструирует DTO из Map<name, value> через primary constructor + рефлексию.
 * Типы конвертируются из строк автоматически.
 */
fun <T : Any> buildDtoFromMap(dtoClass: KClass<T>, fields: Map<String, String>): T {
    val ctor = dtoClass.primaryConstructor
        ?: error("DTO must have primary constructor: ${dtoClass.simpleName}")

    val args = mutableMapOf<KParameter, Any?>()

    ctor.parameters.forEach { param ->
        val name = param.name ?: return@forEach
        val rawValue = fields[name]
        val converted = convertFormValue(rawValue, param.type)
        if (converted != null || !param.isOptional) {
            args[param] = converted
        }
    }

    return ctor.callBy(args)
}

private fun convertFormValue(value: String?, type: KType): Any? {
    if (value.isNullOrBlank()) return null
    return when (type.classifier) {
        String::class    -> value
        Int::class       -> value.toIntOrNull()
        Long::class      -> value.toLongOrNull()
        Double::class    -> value.toDoubleOrNull()
        Float::class     -> value.toFloatOrNull()
        Boolean::class   -> value.toBooleanStrictOrNull()
        LocalDate::class -> runCatching { LocalDate.parse(value) }.getOrNull()
        else             -> null
    }
}
