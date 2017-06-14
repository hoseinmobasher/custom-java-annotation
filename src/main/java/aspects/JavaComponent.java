package aspects;

import aspects.annotation.*;

@Component(name = "JavaComponent")
public class JavaComponent {
    @BaseMethod(priority = 1)
    @Before
    public void before1() {
        System.out.println("Before Priority Low");
    }

    @BaseMethod(priority = 2)
    @Before
    public void before2() {
        System.out.println("Before Priority High");
    }

    @BaseMethod
    @Algorithm
    public void algorithm() {
        System.out.println("Algorithm");
    }

    @BaseMethod(priority = 1)
    @After
    public void after1() {
        System.out.println("After Priority Low");
    }

    @BaseMethod(priority = 2)
    @After
    public void after2() {
        System.out.println("After Priority High");
    }

    @BaseMethod(priority = 3)
    @After(enabled = false)
    public void after3() {
        System.out.println("After Priority Highest : never run!!!");
    }
}
