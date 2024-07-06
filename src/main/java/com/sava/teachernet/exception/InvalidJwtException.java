package com.sava.teachernet.exception;

public class InvalidJwtException extends RuntimeException {

  public InvalidJwtException() {
    super();
  }

  public InvalidJwtException(String message) {
    super(message);
  }
}
