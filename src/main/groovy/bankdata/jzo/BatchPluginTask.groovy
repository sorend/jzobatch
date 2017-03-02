package bankdata.jzo

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import groovy.text.SimpleTemplateEngine

class BatchPluginTask extends DefaultTask {

	@TaskAction
	void generateJCL() {
		BatchPluginExtension ext = project.extensions.jcl
		
		def mainClassName = project["mainClassName"]
		if (!mainClassName) {
			if (!ext.mainClass) {
				throw new Exception("No mainClass defined in jcl")
			} else {
				mainClassName = ext.mainClass
			}
		}
		
		def mainArgs = "*"
		if (ext.args) {
			def argStr = ext.args.join("\n")
			mainArgs = "MAINARGS DD *\n${argStr}"
		}
		
		def datasetsList = []
		ext.datasets.each { k, v ->
			datasetsList << "//${k} DD ${v}"
		}
		def datasets = "*"
		if (!datasetsList.empty) {
			datasets = datasetsList.join("\n")
		}
		
		def template = this.getClass().getResource("/jcl.template").text
		def binding = [
			mainClass: mainClassName,
			mainArgs: mainArgs,
			datasets: datasets,
			envScriptPath: '/whatever/testing',
		]
		
		def simple = new SimpleTemplateEngine()
		def output = simple.createTemplate(template).make(binding).toString()

		def outputFile = project.file("${project.buildDir}/run.jcl")
		outputFile.text = output
		
		logger.info "Wrote JCL to ${outputFile}"
	}
}
