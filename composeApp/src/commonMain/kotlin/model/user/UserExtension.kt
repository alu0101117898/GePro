package model.user

fun model.User.toTaskUser(): model.task.User {
    return model.task.User(
        id = this.id,
        username = this.username,
        email = this.email,
        color = this.color,
        initials = this.initials,
        profilePicture = this.profilePicture
    )
}
