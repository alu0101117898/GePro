package model.user

import model.team.User

fun User.toTaskUser(): model.task.User {
    return model.task.User(
        id = this.id,
        username = this.username,
        email = this.email,
        color = this.color,
        initials = this.initials,
        profilePicture = this.profilePicture
    )
}
