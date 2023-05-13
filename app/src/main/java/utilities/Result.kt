package utilities


//Abstract class that will help us to normalice the output
data class  Result<out T>(var status: Status, val data: T?, val message: String?) {

    enum class Status {
        SUCCESS, //Para la descarga de DAO
        ERROR, //Errores
        LOADING, //Periodo que esta cargando
        FIN
    }
    companion object {
        fun <T> success(data: T): Result<T> {
            return Result(Status.SUCCESS, data, null)
        }
        fun <T> error(message: String, data: T? = null): Result<T> {
            return Result(Status.ERROR, data, message)
        }

        fun <T> loading(data: T? = null): Result<T> {
            return Result(Status.LOADING, data, null)
        }
        fun <T> finalizado():Result<T> {
            return Result(Status.FIN,null,"Proceso finalizado.")
        }
    }
}
