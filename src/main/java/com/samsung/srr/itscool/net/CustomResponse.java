package com.samsung.srr.itscool.net;

/**
 * Created by raiym on 1/14/16 at 11:54 AM.
 */
public class CustomResponse<T> {
    public int error;
    public String message;
    public T data;

    public CustomResponse(int error, String message, T data) {
        this.error = error;
        this.message = message;
        this.data = data;
    }
}
