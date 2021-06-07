# suggested build command:
# name=r_deseq2
# cd ${BLJ}
# docker build -t biolockjdevteam/${name} . -f resources/docker/dockerfiles/${name}.Dockerfile 

FROM bioconductor/bioconductor_docker:latest
ARG DEBIAN_FRONTEND=noninteractive

#1.) Install R Packages
# Rscript -e 'if (!requireNamespace("BiocManager", quietly = TRUE)) install.packages("BiocManager")' && \
RUN Rscript -e 'BiocManager::install("edgeR")'

#2.) Install R Packages
RUN Rscript -e "install.packages('ggpubr', dependencies = c('Depends', 'Imports'))" && \
	Rscript -e "install.packages('properties', dependencies = c('Depends', 'Imports'))" && \
	Rscript -e "install.packages('stringr', dependencies = c('Depends', 'Imports'))"
	
RUN Rscript -e "library('ggpubr'); library('properties'); library('stringr'); library('edgeR'); "
	
#3.) Cleanup
RUN	apt-get clean && \
	find / -name *python* | xargs rm -rf && \
	rm -rf /tmp/* && \
	rm -rf /usr/share/* && \
	rm -rf /var/cache/* && \
	rm -rf /var/lib/apt/lists/* && \
	rm -rf /var/log/*
