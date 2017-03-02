package bankdata.jzo

import org.gradle.api.Plugin
import org.gradle.api.Project

class BatchPlugin implements Plugin<Project> {

	void apply(Project project) {
		
		def extension = project.extensions.create("jcl", BatchPluginExtension)
		
		def jclTask = project.task("jcl", type: BatchPluginTask,
			description: "Generates JCL for the batch project.")
		
		// make jcl task run after jar
		project.tasks.getByName("jar").dependsOn(jclTask)
		
		def distributions = project.distributions
		if (distributions) {
			distributions.main.contents.from(jclTask) {
				into "jcl"
			}
		}
		else {
			throw new Exception("Distributions not found!")
		}
	}
}

