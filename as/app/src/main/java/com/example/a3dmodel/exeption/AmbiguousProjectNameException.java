package com.example.a3dmodel.exeption;

public class AmbiguousProjectNameException extends ProjectException {
    public AmbiguousProjectNameException() {
        super();
    }

    public AmbiguousProjectNameException(String message) {
        super(message);
    }
}
