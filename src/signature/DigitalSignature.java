package signature;

import Hesh.SHA1;
import java.math.BigInteger;

public class DigitalSignature {
    private BigInteger S,q,p,e,d,m1,m ;

    public DigitalSignature (BigInteger q, BigInteger p, BigInteger d ) {
        this.q = q ;
        this.p = p ;
        this.d = d;
        BigInteger eulerFunction = (q.subtract(BigInteger.ONE)).multiply(p.subtract(BigInteger.ONE)) ;
        e = generatePrivateKey(eulerFunction,d);
    }

    // K0 = e , Kc = d
    public BigInteger createSignature ( BigInteger m ) {
        return fast_exp(m.mod(q.multiply(p)),d,q.multiply(p)) ;
    }

    public boolean checkSignature (byte[] text, BigInteger S) {
         m1 = new SHA1().getIntHash(text).mod(q.multiply(p));
         m = fast_exp(S,e,q.multiply(p)) ;
        //System.out.println(e);
        //System.out.println(d);
        return m.compareTo(m1) == 0;

    }

    private BigInteger generatePrivateKey (BigInteger a, BigInteger b ) {
        BigInteger d0 = a;
        BigInteger d1 = b;
        BigInteger x0 = BigInteger.ONE;
        BigInteger x1 = BigInteger.ZERO;
        BigInteger y0 = BigInteger.ZERO;
        BigInteger y1 = BigInteger.ONE;


        while(d1.compareTo(BigInteger.ONE) > 0) { //  IF d1 > 1
            BigInteger q = d0.divide(d1); // q = d0/d1
            BigInteger d2 = d0.mod(d1); // d2 = d0 % d1;
            BigInteger x2 = x0.subtract(q.multiply(x1)); //x2 = x0 - q*x1;
            BigInteger y2 = y0.subtract(q.multiply(y1));  // y2 = y0 - q*y1;
            d0 = d1;
            d1 = d2;
            x0 = x1;
            x1 = x2;
            y0 = y1;
            y1 = y2;
        }
        if ( y1.compareTo(BigInteger.ZERO) < 0 ) y1 = y1.add(a);

        return (y1);
    }

    private BigInteger fast_exp (BigInteger a, BigInteger z, BigInteger n ) {

        BigInteger x = BigInteger.ONE ;

        while (z.compareTo(BigInteger.ZERO) != 0 ) {
            while ( z.mod(BigInteger.TWO).compareTo(BigInteger.ZERO) == 0 ) {
                z = z.divide(BigInteger.TWO) ;
                a =  a.multiply(a).mod(n) ;
            }
            z = z.subtract(BigInteger.ONE) ;
            x = x.multiply(a).mod(n) ;
        }
        return x ;
    }

    public BigInteger getM1() {
        return m1;
    }
    public BigInteger getM() {
        return m;
    }
}
