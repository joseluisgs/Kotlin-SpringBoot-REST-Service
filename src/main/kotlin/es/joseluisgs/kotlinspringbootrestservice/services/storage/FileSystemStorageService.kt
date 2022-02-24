package es.joseluisgs.kotlinspringbootrestservice.services.uploads

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.FileSystemUtils
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


@Service
class FileSystemStorageService(
    @Value("\${upload.root-location}") path: String
) : StorageService {
    // Directorio raiz de nuestro almacén de ficheros
    private val rootLocation: Path

    // Inicializador
    init {
        rootLocation = Paths.get(path)
    }

    override fun store(file: MultipartFile): String {
        val filename: String = StringUtils.cleanPath(file.originalFilename.toString())
        val extension: String = StringUtils.getFilenameExtension(filename).toString()
        val justFilename = filename.replace(".$extension", "")
        val storedFilename = System.currentTimeMillis().toString() + "_" + justFilename + "." + extension
        try {
            if (file.isEmpty) {
                throw StorageException("Fallo al almacenar un fichero vacío $filename")
            }
            if (filename.contains("..")) {
                // This is a security check
                throw StorageException(
                    "No se puede lamacenar un fichero fuera del path permitido "
                            + filename
                )
            }
            file.inputStream.use { inputStream ->
                Files.copy(
                    inputStream, rootLocation.resolve(storedFilename),
                    StandardCopyOption.REPLACE_EXISTING
                )
                return storedFilename
            }
        } catch (e: IOException) {
            throw StorageException("Fallo al lamacenar fichero $filename", e)
        }
    }

    /**
     * Método que devuelve la ruta de todos los ficheros que hay
     * en el almacenamiento secundario del proyecto.
     */
    override fun loadAll(): Stream<Path> {
        return try {
            Files.walk(rootLocation, 1)
                .filter { path -> !path.equals(rootLocation) }
                .map(rootLocation::relativize)
        } catch (e: IOException) {
            throw StorageException("Fallo al leer los ficheros almacenados", e)
        }
    }

    /**
     * Método que es capaz de cargar un fichero a partir de su nombre
     * Devuelve un objeto de tipo Path
     */
    override fun load(filename: String?): Path {
        return rootLocation.resolve(filename)
    }

    /**
     * Método que es capaz de cargar un fichero a partir de su nombre
     * Devuelve un objeto de tipo Resource
     */
    override fun loadAsResource(filename: String?): Resource {
        return try {
            val file: Path = load(filename)
            val resource: Resource = UrlResource(file.toUri())
            if (resource.exists() || resource.isReadable()) {
                resource
            } else {
                throw StorageFileNotFoundException(
                    "No se puede leer fichero: $filename"
                )
            }
        } catch (e: MalformedURLException) {
            throw StorageFileNotFoundException("No se puede leer fichero: $filename", e)
        }
    }

    /**
     * Método que elimina todos los ficheros del almacenamiento
     * secundario del proyecto.
     */
    override fun deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile())
    }

    /**
     * Método que inicializa el almacenamiento secundario del proyecto
     */
    override fun init() {
        try {
            Files.createDirectories(rootLocation)
        } catch (e: IOException) {
            throw StorageException("No se puede inicializar el sistema de almacenamiento", e)
        }
    }

    override fun delete(filename: String) {
        val justFilename: String = StringUtils.getFilename(filename)
        try {
            val file: Path = load(justFilename)
            Files.deleteIfExists(file)
        } catch (e: IOException) {
            throw StorageException("Error al eliminar un fichero", e)
        }
    }

    override fun getUrl(filename: String): String {
        return MvcUriComponentsBuilder // El segundo argumento es necesario solo cuando queremos obtener la imagen
            // En este caso tan solo necesitamos obtener la URL
            .fromMethodName(FilesRestController::class.java, "serveFile", filename, null)
            .build().toUriString()
    }
}