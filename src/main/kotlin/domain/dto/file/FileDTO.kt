package kz.kff.domain.dto.file

import jakarta.validation.constraints.Max
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.serializers.LocalDateTimeIso8601Serializer
import kotlinx.serialization.Serializable
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import kz.kff.core.db.table.file.FileTable
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.dto.BaseCreateDTO
import kz.kff.domain.dto.BaseOneCreateDTO
import kz.kff.domain.dto.BaseUpdateDTO

@Serializable
data class FileRDTO(
    val id: Long,
    val originalName:String,
    val uniqueFileName:String,
    val fullPath:String,
    val directory:String? = null,
    val extension:String? = null,
    val mimeType:String? = null,
    val storageLocal: Boolean,
    val fileSizeByte: Long,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    val updatedAt: LocalDateTime,
)

@Serializable
data class FileCDTO(
    @field:NotNull(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:NotBlank(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    val originalName:String,
    @field:NotNull(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:NotBlank(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    val uniqueFileName:String,
    @field:NotNull(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:NotBlank(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    val fullPath:String,
    val directory:String? = null,
    val extension:String? = null,
    val mimeType:String? = null,
    val storageLocal: Boolean,
    @field:Max(Long.MAX_VALUE, message = LocalizedMessageConstraints.ValidationLongMaxSizeExceed)
    val fileSizeByte: Long,
) : BaseCreateDTO<FileTable>, BaseUpdateDTO<FileTable>, BaseOneCreateDTO<FileTable>