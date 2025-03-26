
⸻

🚫 Note:

This plugin cannot be run on Windows OS.

⸻

# How to Run the Plugin:
	1.	Clone the Project:
Make sure you’ve cloned the repository to your local machine.
	2.	Set Up the Run Configuration:
	•	Go to Edit Configurations in your IDE.
	•	Click on Add New Configuration ➔ Gradle.
	•	Set Tasks and Arguments to: runIde
  •	Click Apply ➔ OK.

	3.	Run the Project:
Now, simply run the project.

⸻

# If you encounter this error:
Execution failed for task ':runIde'.  
Unsupported JVM architecture was selected for running Gradle tasks: x86_64. Supported architectures: aarch64.
👉 Solution:
Change the SDK to one that supports your CPU architecture (aarch64). Ensure your environment is correctly configured.

⸻
