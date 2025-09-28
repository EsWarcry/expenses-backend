package com.alvaro.gastos.dto;

public class RequestContext {
    private static ThreadLocal<Integer> currentId = new ThreadLocal<>();

    public static Integer getCurrentId() {
        return currentId.get();
    }

    public static void setCurrentId(Integer id) {
        currentId.set(id);
    }

    public static void clear(){
        currentId.remove();
    }
}
