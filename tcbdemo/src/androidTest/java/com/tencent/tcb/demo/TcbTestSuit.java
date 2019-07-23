package com.tencent.tcb.demo;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        FunctionServiceTest.class,
        StorageServiceTest.class,
        DbDocTest.class,
        DbCollectionTest.class,
        DbCommandTest.class
})
public class TcbTestSuit {
}
