/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.office.extractor;

import java.io.File;
import java.io.FileInputStream;


import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.parser.data.FileData;

public class ExcelParse
{
	private static final Logger LOG = LoggerFactory.getLogger(ExcelParse.class);
	private static final char SEGMENT_CHAR = ' ';

	public ExcelParse()
	{
	}

	private FileData extractor(File filePath)
	{
		FileData fData = new FileData();
		fData.setName(filePath.getName());
		StringBuffer sBuffer = new StringBuffer();
		HSSFWorkbook workbook = null;
		try
		{
			workbook = new HSSFWorkbook(new FileInputStream(filePath));
			for (int iSheets = 0; iSheets < workbook.getNumberOfSheets(); ++iSheets)
			{
				HSSFSheet sheet = workbook.getSheetAt(iSheets);
				for (int iRow = 0; iRow < sheet.getLastRowNum(); ++iRow)
				{
					HSSFRow row = sheet.getRow(iRow);
					for (int iCell = 0; iCell < row.getLastCellNum(); ++iCell)
					{
						HSSFCell cell = row.getCell(iCell);
						if (null != cell)
						{
							if (0 == cell.getCellType())
							{
								sBuffer.append(String.valueOf(cell.getNumericCellValue()));
								sBuffer.append(SEGMENT_CHAR);
							} else if (1 == cell.getCellType())
							{
								sBuffer.append(cell.getStringCellValue().trim());
								sBuffer.append(SEGMENT_CHAR);
							}
						}
					}
				}
			}
			fData.setContent(sBuffer.toString());
		} catch (Exception e)
		{
			LOG.error("", e);
		}
		return fData;
	}
}
