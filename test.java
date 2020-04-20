public class test {

    /** 
     * 字节转换为浮点 
     *  
     * @param b 字节（至少4个字节） 
     * @param index 开始位置 
     * @return 
     */  
    public static float byte2float(byte[] b, int index) {    
        int l;                                             
        l = b[index + 0];                                  
        l &= 0xff;                                         
        l |= ((long) b[index + 1] << 8);                   
        l &= 0xffff;                                       
        l |= ((long) b[index + 2] << 16);                  
        l &= 0xffffff;                                     
        l |= ((long) b[index + 3] << 24);                  
        return Float.intBitsToFloat(l);                    
    }  


    public static void main(String [] args) {

        byte [] in = new byte [] {(byte)0xC3, (byte)0xE5, (byte)0x82, (byte)0x43};
        float f = byte2float(in, 0);
        System.out.printf("##f = %f\n", f);
        System.out.println("hello world");
    } 

}