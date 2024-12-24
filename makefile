# Makefile for projectname  

# Define the SBT command  
SBT = sbt  

# Define the main classes for the project  
VERILOG_MAIN = projectname.MyTopLevelVerilog
VHDL_MAIN = projectname.MyTopLevelVhdl
SIM_MAIN = projectname.MyTopLevelSim

# Define targets  
.PHONY: all verilog vhdl sim clean  wave

all: verilog 

# Command to generate Verilog  
verilog:  
	$(SBT) "runMain $(VERILOG_MAIN)"  

# Command to generate VHDL  
vhdl:  
	$(SBT) "runMain $(VHDL_MAIN)"  

# Command to run the testbench  
sim:  
	$(SBT) "runMain $(SIM_MAIN)"  

wave:
	gtkwave "./simWorkspace/Adder4Bit/test/wave.vcd"

# Command to clean the project  
clean:
ifeq ($(OS),Windows_NT)	
	@if exist verdiLog rmdir /s /q verdiLog
	@if exist hw/gen/*.v del /s /q *.v
else
	@rm -rf *.v
endif