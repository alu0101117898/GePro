package repository

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import networking.CommentClient
import util.errorhandling.NetworkError
import util.errorhandling.Result
import data.Comment
import data.CommentUpdateData
import kotlinx.datetime.Clock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.jsonObject
import util.jsonConfig

object CommentRepository {
    private val client = CommentClient(HttpClient(CIO))

    suspend fun getComments(taskId: String): Result<List<Comment>, NetworkError> {
        return client.getComments(taskId)
    }

    suspend fun createComment(taskId: String, commentData: CommentUpdateData): Result<Comment, NetworkError> {
        return when (val result = client.createComment(taskId, commentData)) {
            is Result.Success -> {
                try {
                    var comment = util.jsonConfig.decodeFromString(Comment.serializer(), result.data)
                    if (comment.user == null || comment.user!!.username.isEmpty()) {
                        val currentUser = CommentClient(HttpClient(CIO)).getCurrentUser()
                        if (currentUser != null) {
                            comment = comment.copy(user = currentUser)
                        }
                    }
                    Result.Success(comment)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Result.Error(NetworkError.SERIALIZATION)
                }
            }
            is Result.Error -> Result.Error(result.error)
            Result.Loading -> Result.Error(NetworkError.UNKNOWN)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun updateComment(commentId: String, commentData: CommentUpdateData): Result<Comment, NetworkError> {
        return when (val result = client.updateComment(commentId, commentData)) {
            is Result.Success -> {
                try {
                    // Intentamos decodificar la respuesta
                    var comment = jsonConfig.decodeFromString(Comment.serializer(), result.data)
                    // Si el texto del comentario viene vacío, usamos el valor enviado
                    if (comment.comment_text.isEmpty()) {
                        comment = comment.copy(comment_text = commentData.comment_text)
                    }
                    // Si la fecha viene en 0, asignamos la fecha actual (o mantenemos la original si la tienes almacenada)
                    if (comment.date == 0L) {
                        comment = comment.copy(date = Clock.System.now().toEpochMilliseconds())
                    }
                    // Si el campo user viene nulo o vacío, obtenemos el usuario actual
                    if (comment.user == null || comment.user!!.username.isEmpty()) {
                        val currentUser = CommentClient(HttpClient(CIO)).getCurrentUser()
                        if (currentUser != null) {
                            comment = comment.copy(user = currentUser)
                        }
                    }
                    Result.Success(comment)
                } catch (e: kotlinx.serialization.MissingFieldException) {
                    // Otras correcciones en caso de campos faltantes...
                    try {
                        val jsonObject = jsonConfig.parseToJsonElement(result.data).jsonObject.toMutableMap()
                        if (!jsonObject.containsKey("id")) {
                            jsonObject["id"] = kotlinx.serialization.json.JsonPrimitive(commentId)
                        }
                        val fixedJson = kotlinx.serialization.json.JsonObject(jsonObject)
                        var comment = jsonConfig.decodeFromString(Comment.serializer(), jsonConfig.encodeToString(fixedJson))
                        // Fusionar datos: si el comentario viene vacío, usamos el enviado
                        if (comment.comment_text.isEmpty()) {
                            comment = comment.copy(comment_text = commentData.comment_text)
                        }
                        if (comment.date == 0L) {
                            comment = comment.copy(date = Clock.System.now().toEpochMilliseconds())
                        }
                        if (comment.user == null || comment.user!!.username.isEmpty()) {
                            val currentUser = CommentClient(HttpClient(CIO)).getCurrentUser()
                            if (currentUser != null) {
                                comment = comment.copy(user = currentUser)
                            }
                        }
                        Result.Success(comment)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        Result.Error(NetworkError.SERIALIZATION)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Result.Error(NetworkError.SERIALIZATION)
                }
            }
            is Result.Error -> Result.Error(result.error)
            Result.Loading -> Result.Error(NetworkError.UNKNOWN)
        }
    }


    suspend fun deleteComment(commentId: String): Result<Unit, NetworkError> {
        return client.deleteComment(commentId)
    }
}
