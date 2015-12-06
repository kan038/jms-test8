package com.sample.jms.domain.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JmsFileUtils {

	
	
	public static void main(String args[]) {
		
		
	
		
		System.out.println(UUID.randomUUID().toString());
	}
	
	
	
	/**
	 * ファイルの存在チェックを
	 * @param filePath　ファイルパス
	 * @return ファイルの存在有無
	 */
	public static boolean existsFile(String filePath){
		
		File file = new File(filePath);
		
		return file.exists();
		
	}
	
	/**
	 * 指定したパスのファイルを読込み、データをリストに入れて返却
	 * @param path 読込ファイルのパス
	 * @return 読み込んだデータリスト
	 * @throws IOException
	 */
	public static List<String> readFileToList(Path path) throws IOException {
		
		List<String> retList = new ArrayList<String>();
		BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8);
		String line;
        while ((line = br.readLine()) != null) {
        	retList.add(line);
        }
        return retList;
	}
	
	
	/**
	 * 指定したパスにListのデータを書き込む
	 * @param path 出力先ファイルのパス
	 * @param contents データ
	 * @throws IOException
	 */
	public static void writeListToFile(Path path, List<String> contents) throws IOException {
		 
         try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
             for (String s : contents) {
                 bw.write(s + System.lineSeparator());
             }
         }
	}
	
	
}
