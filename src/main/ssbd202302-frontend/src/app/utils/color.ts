export class Color {
    public static readonly BLACK = new Color('BLACK', 'product.color.black');
    public static readonly RED = new Color('RED', 'product.color.red');
    public static readonly GREEN = new Color('GREEN', 'product.color.green');
    public static readonly BLUE = new Color('BLUE', 'product.color.blue');
    public static readonly BROWN = new Color('BROWN', 'product.color.brown');
    public static readonly WHITE = new Color('WHITE', 'product.color.white');
 
    private constructor(public readonly value: string, public readonly displayName: string) {}
  }
