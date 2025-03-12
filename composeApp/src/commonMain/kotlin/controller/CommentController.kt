package controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import repository.CommentRepository
import model.comment.Comment
import model.comment.CommentUpdateData
import util.errorhandling.NetworkError
import util.errorhandling.Result

class CommentController(private val scope: CoroutineScope) {
    fun getComments(taskId: String, onResult: (Result<List<Comment>, NetworkError>) -> Unit) {
        scope.launch {
            val result = CommentRepository.getComments(taskId)
            onResult(result)
        }
    }

    fun createComment(taskId: String, commentData: CommentUpdateData, onResult: (Result<Comment, NetworkError>) -> Unit) {
        scope.launch {
            val result = CommentRepository.createComment(taskId, commentData)
            onResult(result)
        }
    }

    fun updateComment(commentId: String, commentData: CommentUpdateData, onResult: (Result<Comment, NetworkError>) -> Unit) {
        scope.launch {
            val result = CommentRepository.updateComment(commentId, commentData)
            onResult(result)
        }
    }

    fun deleteComment(commentId: String, onResult: (Result<Unit, NetworkError>) -> Unit) {
        scope.launch {
            val result = CommentRepository.deleteComment(commentId)
            onResult(result)
        }
    }
}
