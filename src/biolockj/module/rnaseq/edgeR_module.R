

# if (!requireNamespace("BiocManager", quietly = TRUE))
#   install.packages("BiocManager")
# 
# BiocManager::install("edgeR")
# browseVignettes("edgeR")
library(edgeR)

source("../BioLockJ_Lib.R")

## optional
#library("BiocParallel")
## if using BiocParallel
# register(MulticoreParam( getProperty("script.numThreads", 1) ))


##############################################

writeLines(c("", "Finding inputs..."))

# arg 1 - counts table file
# arg 2 - metadata file
# arg 3 - scriptString - a string to distinguish the output of this script from others in the same module
args = commandArgs(trailingOnly=TRUE)

writeLines(paste("counts table:", args[1]))
writeLines(paste("metadata:", args[2]))
writeLines(paste("script string:", args[3]))

designString = getProperty("edger.designFormula")
writeLines(paste("design:", designString))
design = as.formula(designString) 


##############################################

writeLines(c("", "Reading inputs..."))

countData = t(readBljTable(args[1]))
metaData = readBljTable(args[2])


##############################################

writeLines(c("", "Launching edgeR..."))

dgList <- DGEList(counts=countData, genes=rownames(countData))
countsPerMillion <- cpm(dgList)
countCheck <- countsPerMillion > 1
keep <- which(rowSums(countCheck) >= 2)
dgList <- dgList[keep,]
dgList <- calcNormFactors(dgList, method="TMM")
sampleType<-metaData$design
designMat <- model.matrix(~sampleType)
dgList <- estimateGLMCommonDisp(dgList, design=designMat)
dgList <- estimateGLMTrendedDisp(dgList, design=designMat)
dgList <- estimateGLMTagwiseDisp(dgList, design=designMat)
fit <- glmFit(dgList, designMat)
lrt <- glmLRT(fit)

##############################################

writeLines(c("", "Extracting and writting results..."))
  
res <- lrt$table
resOrdered <- res[order(res$PValue),]

out_design_name<-gsub("[+]","_",design)
out_design_name<-gsub("~","",out_design_name)

outfile <- paste0(c("../output/", args[3], out_design_name, ".tsv"), collapse = "")
write.table(cbind(row.names(resOrdered), as.data.frame(resOrdered)),
    file=outfile,
    sep="\t",
    quote=FALSE,
    row.names = FALSE,
    col.names = TRUE)
  

  
writeLines(paste("Saving to file:", outfile))
writeLines("Summary:")
summary(resOrdered)


##############################################

writeLines(c("", "Getting citation info ..."))

citeR = citation()
cite = citation("edgeR")
writeLines(c(citeR$textVersion, cite$textVersion))
writeLines(c(citeR$textVersion, cite$textVersion), "../temp/citation.txt")

writeLines(c("", "", "Logging session info..."))
sessionInfo()
writeLines(c("", "", "Done!"))
