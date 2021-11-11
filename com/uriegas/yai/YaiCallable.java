package com.uriegas.yai;

import java.util.*;

public interface YaiCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}
