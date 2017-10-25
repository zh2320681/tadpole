package com.wellcent.tadpole.presenter.exception

//用户位空的异常
class NilUserException : RuntimeException(){ }
//业务的异常
class BussinssException(msg:String) : RuntimeException(msg){ }
//认证失效的异常
class AuthFailureException : RuntimeException(){ }