package com.skycloud.generator.main;

import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.ShellRunner;
import java.util.List;

/**
 * The class Pagination plugin.
 * @author
 */
public class PaginationPlugin extends PluginAdapter {

	/**
	 * Validate boolean.
	 *
	 * @param warnings the warnings
	 *
	 * @return the boolean
	 */
	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	private static void generate() {
		String config = PaginationPlugin.class.getClassLoader().getResource("generatorConfig.xml").getFile();
		String[] arg = {"-configfile", config, "-overwrite"};
		ShellRunner.main(arg);
	}

	/**
	 * The entry point of application.
	 *
	 * @param args the input arguments
	 */
	public static void main(String[] args) {
		generate();
	}
}