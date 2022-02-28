package es.joseluisgs.kotlinspringbootrestservice.controllers.productos

import es.joseluisgs.kotlinspringbootrestservice.config.APIConfig
import es.joseluisgs.kotlinspringbootrestservice.dto.productos.ProductoCreateDTO
import es.joseluisgs.kotlinspringbootrestservice.dto.productos.ProductoDTO
import es.joseluisgs.kotlinspringbootrestservice.dto.productos.ProductoListDTO
import es.joseluisgs.kotlinspringbootrestservice.errors.GeneralBadRequestException
import es.joseluisgs.kotlinspringbootrestservice.errors.productos.ProductoBadRequestException
import es.joseluisgs.kotlinspringbootrestservice.errors.productos.ProductoNotFoundException
import es.joseluisgs.kotlinspringbootrestservice.mappers.ProductosMapper
import es.joseluisgs.kotlinspringbootrestservice.repositories.CategoriasRepository
import es.joseluisgs.kotlinspringbootrestservice.repositories.ProductosRepository
import es.joseluisgs.kotlinspringbootrestservice.services.storage.StorageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping(APIConfig.API_PATH + "/productos")
// Le aplico DI por constructor
class ProductosRestController
@Autowired constructor(
    private val productosRepository: ProductosRepository,
    private val productosMapper: ProductosMapper,
    private val categoriasRepository: CategoriasRepository,
    private val storageService: StorageService
) {

    @GetMapping("")
    fun getAll(
        @RequestParam(required = false, name = "nombre") nombre: String?,
        @RequestParam(defaultValue = APIConfig.PAGINATION_INIT) page: Int,
        @RequestParam(defaultValue = APIConfig.PAGINATION_SIZE) size: Int,
        @RequestParam(defaultValue = APIConfig.PAGINATION_SORT) sort: String,
    ): ResponseEntity<ProductoListDTO> {
        // Consulto en base a las páginas
        try {
            val paging: Pageable = PageRequest.of(page, size, Sort.Direction.ASC, sort)
            val pagedResult = if (nombre != null) {
                productosRepository.findByNombreContainsIgnoreCase(nombre, paging)
            } else {
                productosRepository.findAll(paging)
            }
            val result = ProductoListDTO(
                data = productosMapper.toDTO(pagedResult.content),
                currentPage = pagedResult.number,
                totalPages = pagedResult.totalPages,
                totalElements = pagedResult.totalElements,
                sort = sort
            )
            return ResponseEntity.ok(result)
        } catch (e: Exception) {
            throw GeneralBadRequestException("Selección de Datos", "Parámetros de consulta incorrectos")
        }
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<ProductoDTO> {
        try {
            val producto = productosRepository.findById(id).get()
            return ResponseEntity.ok(productosMapper.toDTO(producto))
        } catch (e: Exception) {
            throw ProductoNotFoundException(id)
        }
    }

    @PostMapping("")
    fun create(@RequestBody producto: ProductoCreateDTO): ResponseEntity<ProductoDTO> {
        try {
            checkProductoData(producto.nombre, producto.precio, producto.categoriaId)
            val categoria = categoriasRepository.findById(producto.categoriaId).get()
            val producto = productosMapper.fromDTO(producto, categoria)
            val result = productosMapper.toDTO(productosRepository.save(producto))
            return ResponseEntity.ok(result)
        } catch (e: Exception) {
            throw GeneralBadRequestException(
                "Error: Insertar Producto",
                "Campos incorrectos. ${e.message}"
            )
        }
    }

    @PutMapping("/{id}")
    fun update(@RequestBody producto: ProductoCreateDTO, @PathVariable id: Long): ResponseEntity<ProductoDTO> {
        try {
            checkProductoData(producto.nombre, producto.precio, producto.categoriaId)
            val categoria = categoriasRepository.findById(producto.categoriaId).get()
            val update = productosRepository.findById(id).orElseGet { throw ProductoNotFoundException(id) }
            update.nombre = producto.nombre
            update.precio = producto.precio
            update.categoria = categoria
            val result = productosMapper.toDTO(productosRepository.save(update))
            return ResponseEntity.ok(result)

        } catch (e: Exception) {
            throw GeneralBadRequestException(
                "Error: Actualizar Producto",
                "Campos incorrectos o id inexistente. ${e.message}"
            )
        }
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<ProductoDTO> {
        try {
            val producto = productosRepository.findById(id).orElseGet { throw ProductoNotFoundException(id) }
            val numberProductos = productosRepository.countByLineasPedido(id)
            if (numberProductos > 0) {
                throw ProductoBadRequestException(
                    "Producto con id $id",
                    "Está asociado a $numberProductos linea(s) pedido(s)"
                )
            } else {
                productosRepository.delete(producto)
                return ResponseEntity.ok(productosMapper.toDTO(producto))
            }
        } catch (e: Exception) {
            throw GeneralBadRequestException(
                "Error: Eliminar Producto",
                "Id de producto inexistente o asociado a un pedido. ${e.message}"
            )
        }
    }

    @PostMapping(
        value = ["/create"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun nuevoProducto(
        @RequestPart("producto") productoDTO: ProductoCreateDTO,
        @RequestPart("file") file: MultipartFile
    ): ResponseEntity<ProductoDTO> {
        return try {
            // Comprobamos los campos obligatorios
            checkProductoData(productoDTO.nombre, productoDTO.precio, productoDTO.categoriaId)
            val categoria = categoriasRepository.findById(productoDTO.categoriaId).get()
            val producto = productosMapper.fromDTO(productoDTO, categoria)
            if (!file.isEmpty) {
                val imagen: String = storageService.store(file)
                val urlImagen: String = storageService.getUrl(imagen)
                producto.imagen = urlImagen
            }
            val productoInsertado = productosRepository.save(producto)
            ResponseEntity.ok(productosMapper.toDTO(productoInsertado))
        } catch (ex: ProductoNotFoundException) {
            throw GeneralBadRequestException("Insertar", "Error al insertar el producto. Campos incorrectos")
        }
    }

    @GetMapping("/slug/{slug}")
    fun findById(@PathVariable slug: String): ResponseEntity<ProductoDTO> {
        try {
            val producto = productosRepository.findAll().first { it.slug == slug }
            return ResponseEntity.ok(productosMapper.toDTO(producto))
        } catch (e: Exception) {
            throw ProductoBadRequestException(
                "No encontrado",
                "No existe ningún producto con slug: $slug"
            )
        }
    }

    private fun checkProductoData(nombre: String, precio: Double, categoriaId: Long) {
        if (nombre.trim().isBlank()) {
            throw ProductoBadRequestException("Nombre", "El nombre es obligatorio")
        }
        if (precio < 0) {
            throw ProductoBadRequestException("Precio", "El precio debe ser mayor que 0")
        }
        if (!categoriasRepository.findById(categoriaId).isPresent) {
            throw ProductoBadRequestException("Categoría", "No existe categoría con id $categoriaId")
        }
    }


}