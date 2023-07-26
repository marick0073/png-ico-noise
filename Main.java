import java.io.*;
import java.nio.*;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.util.Random;
import java.awt.image.BufferedImage;

public class ICO{

	public static void main(String[] args) throws Exception{

		magPNG("icon.png","nicon.png");
		magICO("icon.ico","nicon.ico");

	}


	private static void magICO(String ii, String oi) throws Exception{

		try(ImageInputStream iis=ImageIO.createImageInputStream(new File(ii));
			ImageOutputStream ios=ImageIO.createImageOutputStream(new File(oi))){

			iis.setByteOrder(ByteOrder.LITTLE_ENDIAN);
			ios.setByteOrder(ByteOrder.LITTLE_ENDIAN);

			int r1=iis.readUnsignedShort(); ios.writeShort(r1);
			int it=iis.readUnsignedShort(); ios.writeShort(it);
			int noi=iis.readUnsignedShort(); ios.writeShort(noi);

			int og=0;

			byte[] ib_=null;
			byte[][] nib__=new byte[noi][];

			for(int i=0;i<noi;i++){

				int   w=iis.readUnsignedByte(); ios.writeByte( w);
				int   h=iis.readUnsignedByte(); ios.writeByte( h);
				int   p=iis.readUnsignedByte(); ios.writeByte( p);
				int  r2=iis.readUnsignedByte(); ios.writeByte(r2);

				int  cp=iis.readUnsignedShort(); ios.writeShort( cp);
				int bpp=iis.readUnsignedShort(); ios.writeShort(bpp);

				int s=iis.readInt();
				int o=iis.readInt();

				iis.mark();
				iis.seek(o);
				ib_=new byte[s];
				iis.read(ib_);
				iis.reset();

				if(i==0){

					nib__[i]=magicPNG(ib_);

					ios.writeInt(nib__[i].length);
					ios.writeInt(o);

					og=nib__[i].length-s;

				}else{

					nib__[i]=magicAXO(ib_,w,h);

					ios.writeInt(nib__[i].length);
					ios.writeInt(o+og);

				}

			}

			for(int i=0;i<noi;i++)ios.write(nib__[i]);

		}

	}

	private static void magPNG(String ii, String oi) throws Exception{

		try(ImageInputStream iis=ImageIO.createImageInputStream(new File(ii));
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			ImageOutputStream ios=ImageIO.createImageOutputStream(new File(oi))){

			iis.setByteOrder(ByteOrder.LITTLE_ENDIAN);
			ios.setByteOrder(ByteOrder.LITTLE_ENDIAN);

			int r=-1;
			byte[] bb_=new byte[1024];
			while((r=iis.read(bb_))!=-1)baos.write(bb_,0,r);

			ios.write(magicPNG(baos.toByteArray()));

		}

	}

	private static Random r=new Random();

	private static byte[] magicPNG(byte[] b_) throws Exception{

		BufferedImage bi=ImageIO.read(new ByteArrayInputStream(b_));

		int w=bi.getWidth(),
			h=bi.getHeight();

		//for(int pc=r.nextInt((int)(w*h*0.0125))+1;0<pc;pc--)
			//bi.setRGB(r.nextInt(w),r.nextInt(h),0xFF000000+r.nextInt(0x1000000));

		for(int x=0;x<w;x++)
			for(int y=0;y<h;y++)
				if(r.nextInt(100)<1)
					bi.setRGB(x,y,0xFF000000+r.nextInt(0x1000000));

		ByteArrayOutputStream baos=new ByteArrayOutputStream();

		ImageIO.write(bi,"PNG",baos);

		baos.close();

		return baos.toByteArray();

	}

	private static byte[] magicAXO(byte[] b_, int w, int h) throws Exception{

		DataInputStream dis=new DataInputStream(new ByteArrayInputStream(b_));
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		DataOutputStream dos=new DataOutputStream(baos);

		byte[] hb_=new byte[40];

		dis.read(hb_);
		dos.write(hb_);

		int p=-1;
		for(int i=0;i<w*h;i++){

			p=dis.readInt();
			if(r.nextInt(100)<1)p=0xFF000000+r.nextInt(0x1000000);
			dos.writeInt(p);

		}

		int r=-1;
		byte[] bb_=new byte[1024];
		while((r=dis.read(bb_))!=-1)dos.write(bb_,0,r);

		dis.close();
		baos.close();
		dos.close();

		return baos.toByteArray();

	}

}