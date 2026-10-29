package com.example.moviles.util

/** Reglas de validación reutilizables por todos los formularios. Devuelven null si el valor es válido. */
object Validadores {

    private val REGEX_EMAIL = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val REGEX_NOMBRE = Regex("^[\\p{L}][\\p{L} .'-]*$")

    // ---------- RUT chileno (algoritmo módulo 11) ----------
    fun esRutValido(rut: String): Boolean {
        val limpio = rut.replace(".", "").replace("-", "").trim().uppercase()
        if (limpio.length !in 8..9) return false
        val cuerpo = limpio.dropLast(1)
        val dv = limpio.last()
        if (!cuerpo.all { it.isDigit() }) return false

        var suma = 0
        var multiplicador = 2
        for (c in cuerpo.reversed()) {
            suma += c.digitToInt() * multiplicador
            multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
        }
        val resto = 11 - (suma % 11)
        val esperado = when (resto) {
            11 -> '0'
            10 -> 'K'
            else -> resto.digitToChar()
        }
        return dv == esperado
    }

    /** "123456785" -> "12.345.678-5" */
    fun formatearRut(rut: String): String {
        val limpio = rut.replace(".", "").replace("-", "").trim().uppercase()
        if (limpio.length < 2) return rut
        val cuerpo = limpio.dropLast(1).reversed().chunked(3).joinToString(".").reversed()
        return "$cuerpo-${limpio.last()}"
    }

    fun rutError(rut: String): String? = when {
        rut.isBlank() -> "El RUT es obligatorio"
        !esRutValido(rut) -> "RUT inválido. Ejemplo: 12.345.678-5"
        else -> null
    }

    // ---------- Otros campos ----------
    fun nombreError(nombre: String): String? = when {
        nombre.isBlank() -> "El nombre es obligatorio"
        !REGEX_NOMBRE.matches(nombre.trim()) -> "Solo se permiten letras"
        nombre.trim().split(" ").filter { it.isNotBlank() }.size < 2 -> "Ingresa nombre y apellido"
        else -> null
    }

    fun emailError(email: String): String? = when {
        email.isBlank() -> "El correo es obligatorio"
        !REGEX_EMAIL.matches(email.trim()) -> "Correo inválido. Ejemplo: nombre@empresa.cl"
        else -> null
    }

    fun passwordError(password: String): String? = when {
        password.isBlank() -> "La contraseña es obligatoria"
        password.length < 8 -> "Mínimo 8 caracteres"
        !password.any { it.isLetter() } -> "Debe incluir al menos una letra"
        !password.any { it.isDigit() } -> "Debe incluir al menos un número"
        else -> null
    }

    fun confirmarPasswordError(password: String, confirmacion: String): String? = when {
        confirmacion.isBlank() -> "Confirma tu contraseña"
        password != confirmacion -> "Las contraseñas no coinciden"
        else -> null
    }

    /** Acepta 9 dígitos (9XXXXXXXX) o con prefijo 56. Ignora espacios, + y guiones. */
    fun telefonoError(telefono: String): String? {
        val digitos = telefono.filter { it.isDigit() }
        return when {
            telefono.isBlank() -> "El teléfono es obligatorio"
            digitos.length == 9 && digitos.startsWith("9") -> null
            digitos.length == 11 && digitos.startsWith("569") -> null
            else -> "Teléfono inválido. Ejemplo: +56 9 1234 5678"
        }
    }

    fun codigoError(codigo: String): String? = when {
        codigo.isBlank() -> "Ingresa el código"
        codigo.length != 6 || !codigo.all { it.isDigit() } -> "El código tiene 6 dígitos"
        else -> null
    }
}
