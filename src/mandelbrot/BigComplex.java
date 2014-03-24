package mandelbrot;


import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * <p>
 * Represents an immutable complex number, using two BigDecimals to hold
 * the real component and the imaginary component.
 * </p>
 *
 * @author Joey Bloom
 */
public class BigComplex
{
    private final BigDecimal a;
    private final BigDecimal b;

    /**
     * Constructs a BigComplex to represent the complex number
     * a + bi
     * @param a the real component
     * @param b the imaginary component
     */
    public BigComplex(BigDecimal a, BigDecimal b)
    {
        this.a = a;
        this.b = b;
    }

    /**
     * Returns the real component
     * @return a
     */
    public BigDecimal a()
    {
        return a;
    }

    /**
     * Returns the imaginary component
     * @return b
     */
    public BigDecimal b()
    {
        return b;
    }

    /**
     * Returns the sum of this and the parameter.
     *
     * @param augend the number to add
     * @param mc the context to use
     * @return this + augend
     */
    public BigComplex add(BigComplex augend,MathContext mc)
    {
        //(a+bi)+(c+di) = (a + c) + (b + d)i
        return new BigComplex(
            a.add(augend.a,mc),
            b.add(augend.b,mc));
    }

    /**
     * Equivalent to add(augend, MathContext.UNLIMITED)
     *
     * @param augend the number to add
     * @return this + augend
     */
    public BigComplex add(BigComplex augend)
    {
        return add(augend, MathContext.UNLIMITED);
    }

    /**
     * Returns the difference of this and the parameter.
     *
     * @param subtrahend the number to subtract
     * @param mc the context to use
     * @return this - subtrahend
     */
    public BigComplex subtract(BigComplex subtrahend, MathContext mc)
    {
        //(a+bi)-(c+di) = (a - c) + (b - d)i
        return new BigComplex(
            a.subtract(subtrahend.a,mc),
            b.subtract(subtrahend.b,mc));
    }

    /**
     * Equivalent to subtract(subtrahend, MathContext.UNLIMITED)
     *
     * @param subtrahend the number to subtract
     * @return this - subtrahend
     */
    public BigComplex subtract(BigComplex subtrahend)
    {
        return subtract(subtrahend,MathContext.UNLIMITED);
    }

    /**
     * Returns the product of this and the parameter.
     *
     * @param multiplicand the number to multiply by
     * @param mc the context to use
     * @return this * multiplicand
     */
    public BigComplex multiply(BigComplex multiplicand, MathContext mc)
    {
        //(a+bi)(c+di) = (ac - bd) + (ad + bc)i
        return new BigComplex(
            a.multiply(multiplicand.a,mc).subtract(b.multiply(multiplicand.b,mc),mc),
            a.multiply(multiplicand.b,mc).add(b.multiply(multiplicand.a,mc),mc));
    }

    /**
     * Equivalent to multiply(multiplicand, MathContext.UNLIMITED)
     * @param multiplicand the number to multiply by
     * @return this * multiplicand
     */
    public BigComplex multiply(BigComplex multiplicand)
    {
        return multiply(multiplicand,MathContext.UNLIMITED);
    }

    /**
     * Returns the quotient of this and the parameter.
     *
     * @param divisor   the number to divide by.
     *                  precondition: divisor.a() != 0 && divisor.b() != 0
     * @param mc        the context to use
     * @return this / divisor
     *
     * @throws ArithmeticException if the result is inexact but the rounding mode is UNNECESSARY or mc.precision == 0 and the quotient has a non-terminating decimal expansion.
     * @see BigDecimal#divide(java.math.BigDecimal, java.math.MathContext)
     */
    public BigComplex divide(BigComplex divisor, MathContext mc)
    {
        // (a+bi)   ( ac + bd )   ( bc - ad )
        // ------ = ----------- + ----------- i
        // (c+di)   (c^2 + d^2)   (c^2 + d^2)

        if(divisor.toString().equals("0"))
        {
            throw new ArithmeticException("Division by zero");
        }
        BigDecimal denominator = //c^2 + d^2
                 divisor.a.multiply(divisor.a)
            .add(divisor.b.multiply(divisor.b));

        return new BigComplex(
            a.multiply(divisor.a).add(b.multiply(divisor.b)).divide(denominator,64,RoundingMode.HALF_EVEN),
            b.multiply(divisor.a).subtract(a.multiply(divisor.b)).divide(denominator,64,RoundingMode.HALF_EVEN));
    }

    /**
     * Equivalent to divide(divisor, MathContext.UNLIMITED)
     *
     * @param divisor   the number to divide by.
     *                  precondition: divisor.a() != 0 && divisor.b() != 0
     * @return this / divisor
     */
    public BigComplex divide(BigComplex divisor)
    {
        return divide(divisor,MathContext.UNLIMITED);
    }

    /**
     * Returns the magnitude of the complex number.
     * @return sqrt(a^2 + b^2)
     */
    public double magnitude()
    {
        return Math.sqrt(a.multiply(a).add(b.multiply(b)).doubleValue());
    }

    /**
     * Returns the magnitude squared of the complex number.
     * @return a^2 + b^2
     */
    public double magnitudeSquared()
    {
        double doubleA = a.doubleValue();
        double doubleB = b.doubleValue();
        return doubleA * doubleA + doubleB * doubleB;
    }
    /**
     * Returns a String representation of this complex number
     * @return a String of the form "a + bi"
     */
    @Override
    public String toString()
    {
        String returnMe =  "";
        if(a.doubleValue() == 0)
        {
            if(b.doubleValue() == 0)
            {
                return "0";
            }
            else
            {
                return b + "i";
            }
        }
        returnMe += a.toString();
        double bdub = b.doubleValue();
        if(bdub == 0)
        {
            return returnMe;
        }
        else if(bdub < 0)
        {
            returnMe += " - " + b.abs() + "i";
        }
        else //if bdub > 0
        {
            returnMe += " + " + b + "i";
        }
        return returnMe;
    }

    public static void main(String[] args)
    {
        BigComplex one = new BigComplex(BigDecimal.valueOf(-3),BigDecimal.valueOf(-4));
        BigComplex two = new BigComplex(BigDecimal.valueOf(-3),BigDecimal.valueOf(4));
        BigComplex three = new BigComplex(BigDecimal.valueOf(2.003),BigDecimal.valueOf(0));
        BigComplex four = new BigComplex(BigDecimal.valueOf(0),BigDecimal.valueOf(-4.5008));

        System.out.println(one);
        System.out.println(two);
        System.out.println(three);
        System.out.println(four);

        System.out.println(one.add(two));
        System.out.println(two.add(one));
        System.out.println("-8i ? " + one.subtract(two));
        System.out.println("8i ? " + two.subtract(one));
        System.out.println(one.multiply(two));
        System.out.println(one.multiply(two).divide(two));
    }
}
