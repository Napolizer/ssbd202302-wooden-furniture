export class WoodType {
    public static readonly ACACIA = new WoodType('ACACIA', 'product.wood.type.acacia');
    public static readonly BIRCH = new WoodType('BIRCH', 'product.wood.type.birch');
    public static readonly DARK_OAK = new WoodType('DARK_OAK', 'product.wood.type.dark.oak');
    public static readonly JUNGLE = new WoodType('JUNGLE', 'product.wood.type.jungle');
    public static readonly OAK = new WoodType('OAK', 'product.wood.type.oak'); 
    public static readonly SPRUCE = new WoodType('SPRUCE', 'product.wood.type.spruce');
   
    private constructor(public readonly value: string, public readonly displayName: string) {}
  }
