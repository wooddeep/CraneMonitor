package com.example.x6.serialportlib;

import android.content.Context;

import java.io.FileDescriptor;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


public class SerialPort {
	Context context;

	private static SerialPort serialPort;
	private SerailPortOpt serialportopt;
	private InputStream mInputStream;
	public boolean isOpen = false;
	

	String data;


	public SerialPort(String devNum, int speed, int dataBits, int stopBits,
                      int parity, boolean isNonblock) {
		serialportopt = new SerailPortOpt();
		openSerial(devNum, speed, dataBits, stopBits, parity,isNonblock);
	}


	private boolean openSerial(String devNum, int speed, int dataBits,
                               int stopBits, int parity,boolean isNonblock) {
		serialportopt.mDevNum = devNum;
		serialportopt.mDataBits = dataBits;
		serialportopt.mSpeed = speed;
		serialportopt.mStopBits = stopBits;
		serialportopt.mParity = parity;


		FileDescriptor fd = serialportopt.open485Dev(serialportopt.mDevNum,isNonblock);
		if (fd == null) {
			return false;
		} else {

			serialportopt.setSpeed(fd, speed);
			serialportopt.setParity(fd, dataBits, stopBits, parity);
			mInputStream = serialportopt.getInputStream();
			isOpen = true;
			return true;
		}
	}


	public void closeSerial() {
		if (serialportopt.mFd != null) {
			serialportopt.closeDev(serialportopt.mFd);
			isOpen = false;
		}
	}

	public InputStream getInputStream() {
		return serialportopt.getInputStream();
	}

	public OutputStream getOutputStream() {
		return serialportopt.getOutputStream();
	}

	public SerailPortOpt getSerialportopt() {
		return serialportopt;
	}

	public void sendData(byte[] bufData) {
		try {
			/*serialportopt.writeBytes(type.equals("HEX") ? HexString2Bytes(data
					.length() % 2 == 1 ? data += "0" : data.replace(" ", ""))
					: HexString2Bytes(toHexString(data)));*/
			serialportopt.write485Bytes(bufData);
		} catch (Exception e) {

		}
	}


	public String receiveData(String type) {
		byte[] buf = new byte[1024];
		int size;
		if (mInputStream == null) {
			return null;
		}
		size = serialportopt.readBytes(buf);
		if (size > 0) {
			try {
				data = type.equals("HEX") ? bytesToHexString(buf, size)
						: new String(buf, 0, size, "gb2312").trim().toString();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return data;
		} else {
			return null;
		}
	}


	private String toHexString(String s) {
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			String s4 = Integer.toHexString(ch);
			str = str + s4;
		}
		return str;
	}


	private static byte[] HexString2Bytes(String src) {
		byte[] ret = new byte[src.length() / 2];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < tmp.length / 2; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}


	public static String bytesToHexString(byte[] src, int size) {
		String ret = "";
		if (src == null || size <= 0) {
			return null;
		}
		for (int i = 0; i < size; i++) {
			String hex = Integer.toHexString(src[i] & 0xFF);
			if (hex.length() < 2) {
				hex = "0" + hex;
			}
			hex += " ";
			ret += hex;
		}
		return ret.toUpperCase();
	}


	private static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

}
