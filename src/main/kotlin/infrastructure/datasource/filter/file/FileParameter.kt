package kz.kff.infrastructure.datasource.filter.file

import kz.kff.core.db.table.file.FileTable
import kz.kff.domain.datasource.db.filter.BaseQueryParameter
import kotlin.reflect.KClass

class FileParameter(filterClass: KClass<FileFilter>) : BaseQueryParameter<FileTable, FileFilter>(filterClass) {
}