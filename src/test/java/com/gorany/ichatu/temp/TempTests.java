package com.gorany.ichatu.temp;

public class TempTests {

    static class A{
        int num = 10;
        String string = "origin";

        public A() {

        }

        public void print(){
            System.out.println("A num = " + num);
        }

        public A(int num, String string) {
            this.num = num;
            this.string = string;
        }

        @Override
        public String toString() {
            return "A{" +
                    "num=" + num +
                    ", string='" + string + '\'' +
                    '}';
        }
    }
    static class B extends A{

        int num = 20;

        @Override
        public void print(){
            System.out.println("B num = " + num);
        }
    }

    static void isCallByRef(A a){

        System.out.println("a = " + a);

        a = new A(100, "change");

        System.out.println("a = " + a);
    }
    public static void main(String[] args) {

        A a = new A();
        System.out.println("a = " + a);

        System.out.println("=== call method ===\n");
        isCallByRef(a);
        System.out.println("\n=== call method ===");

        System.out.println("a = " + a);

        A b = new B();

        b.print();
    }


}
