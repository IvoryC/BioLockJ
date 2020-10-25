# Module script for: biolockj.module.report.r.R_CalculateStats

main <- function(level, countsFile, metadataFile) {
   
   message("Running script for level: ", level)
   message("Using input counts table: ", countsFile)
   message("Using metadata table: ", metadataFile)

   countTable = readBljTable(countsFile)
   if ( is.null(countTable) ) stop("There was a problem with reading the input counts table: ", countsFile)
   message("Read in counts table with [", nrow(countTable), "] rows and [", ncol(countTable), "] columns.")
   
   metaTable = readBljTable(metadataFile)
   if ( is.null(metaTable) ) stop("There was a problem with reading the metadata table: ", metadataFile)
   message("Read in metadata table with [", nrow(metaTable), "] rows and [", ncol(metaTable), "] columns.")

   merged = mergeTables(countTable, metaTable)
   
   # all features in the input table
   measuredFeatures = names(countTable)
   # all features in the output table.  Ends up being measuredFeatures - (any untestable features).
   reportFeatures = c()
   
   basicDF = data.frame(matrix(data=measuredFeatures, nrow=length(measuredFeatures), ncol=1 ) )
   row.names(basicDF) = measuredFeatures
   
   parametricPvals = basicDF
   nonParametricPvals = basicDF
   rSquaredVals = basicDF
   adjParPvals = basicDF
   adjNonParPvals = basicDF
   
   binaryFields = getBinaryFields()
   message( "binaryFields: ", paste0(binaryFields, collapse=", ") )
   nominalFields = getNominalFields()
   message( "nominalFields: ", paste0(nominalFields, collapse=", ") )
   numericFields = getNumericFields()
   message( "numericFields: ", paste0(numericFields, collapse=", ") )

   cutoffValue = getProperty("r.rareOtuThreshold", 1)
   if( cutoffValue < 1 ) {
      message("Because the cutoff < 1, it is interpreted as a fraction of countTable rows.")
      cutoffValue = cutoffValue * nrow(countTable)
   } 
   message("cutoffValue = ", cutoffValue)
   
   logLevel=getProperty("pipeline.logLevel", "DEBUG")
   if (logLevel=="DEBUG") message( "Message statements from within for-loops will be included." )
   else message( "Message statements from within for-loops will be NOT included." )
   
   for( feature in measuredFeatures ){
      if( sum( countTable[,feature] > 0 ) < cutoffValue ) {
         message( c( "Excluding: ", feature, "; only found in ", sum( countTable[,feature] > 0 ), "samples.") )
      }else{
         reportFeatures = c(reportFeatures, feature)
         if( length( binaryFields ) > 0 ) {
            for( field in binaryFields ) {
               if (logLevel=="DEBUG") message( c( "Calculate pvalues for: ", feature, " ~ ", field ) )
               att = as.factor( metaTable[,field] )
               vals = levels( att )
               if( everyGroupHasData( merged, feature, field, vals ) ) { 
                  myLm = lm( countTable[,feature] ~ att, na.action=na.exclude )
                  parametricPvals[feature, field] = t.test( countTable[att==vals[1], feature], countTable[att==vals[2], feature] )$p.value
                  nonParametricPvals[feature, field] = pvalue( do_wilcox_test( countTable, feature, att, vals ) )
                  rSquaredVals[feature, field] = summary( myLm )$r.squared
               } else {
                  parametricPvals[feature, field] = 1
                  nonParametricPvals[feature, field] = 1
                  rSquaredVals[feature, field] = 0
               }
            }
         }

         if( length( nominalFields ) > 0 ) {
            for( field in nominalFields ) {
               if (logLevel=="DEBUG") message( c( "Calculate pvalues for: ", feature, " ~ ", field ) )
               att = as.factor( metaTable[,field] )
               vals = levels( att )
               if( everyGroupHasData( merged, feature, field, vals ) ) { 
                     myLm = lm( countTable[,feature] ~ att, na.action=na.exclude )
                     myAnova = anova( myLm )
                     parametricPvals[feature, field] = myAnova$"Pr(>F)"[1]
                     nonParametricPvals[feature, field] = kruskal.test( countTable[,feature] ~ att, na.action=na.exclude )$p.value
                     rSquaredVals[feature, field] = summary( myLm )$r.squared
               } else {
                     parametricPvals[feature, field] = 1
                     nonParametricPvals[feature, field] = 1
                     rSquaredVals[feature, field] = 0
               }
            }
         }
         
         if( length( numericFields ) > 0 ) {
            for( field in numericFields ) {
               if (logLevel=="DEBUG") message( c( "Calculate pvalues for: ", feature, " ~ ", field ) )
               att = as.numeric( metaTable[,field] )
               parametricPvals[feature, field] = Kendall( countTable[,feature], att)$sl[1]
               nonParametricPvals[feature, field] = cor.test( countTable[,feature], att, na.action=na.exclude )$p.value
               rSquaredVals[feature, field] = cor( countTable[,feature], att, use="na.or.complete", method="kendall" )^2 
            }
         }
      }
   }

   if( length( reportFeatures ) == 0 ) {
      message( "No OTU Names found, verify empty vector: ", reportFeatures )
      return( NULL )
   }
   
   # remove the starter column
   parametricPvals = parametricPvals[reportFeatures,2:length(parametricPvals)]
   nonParametricPvals = nonParametricPvals[reportFeatures,2:length(nonParametricPvals)]
   rSquaredVals = rSquaredVals[reportFeatures,2:length(rSquaredVals)]

   message("")
   message( "Calculate ADJUSTED P_VALS" )
   method = getProperty("r_CalculateStats.pAdjustMethod")
   scopeName = getProperty("r_CalculateStats.pAdjustScope")
   scope = getP_AdjustLen(scopeName, reportFeatures)
   message("P values are ajusted using the [", method, "] method, with scope configured to [", scopeName, "] which means the calculation is done using [n=", scope, "].")
   
   message("creating tables to hold adjusted p-vlues...")
   adjParDF = data.frame(matrix(data=NA, 
                                nrow=nrow(parametricPvals), 
                                ncol=ncol(parametricPvals) ), 
                         row.names=row.names(parametricPvals) )
   names(adjParDF) = names(parametricPvals)
   
   adjNonParDF = data.frame(matrix(data=NA, 
                                   nrow=nrow(nonParametricPvals), 
                                   ncol=ncol(nonParametricPvals) ), 
                            row.names=row.names(nonParametricPvals)  )
   names(adjNonParDF) = names(nonParametricPvals)

   message("populating adjusted p-vlue tables...")
   for( rf in c(binaryFields, nominalFields, numericFields) ) {
      message( "Calculate adjusted pval for report field: ", rf )
      adjParDF[,rf] = p.adjust( parametricPvals[,rf], method=method, n=scope )
      adjNonParDF[,rf] = p.adjust( nonParametricPvals[,rf], method=method, n=scope )
   }
   message("Done calculating adjusted p-values!")

   message("")
   message( "measuredFeatures [",length(measuredFeatures), "]: ", paste0(measuredFeatures, collapse=", ") )
   message("")
   message( "reportFeatures [",length(reportFeatures), "]: ", paste0(reportFeatures, collapse=", ") )
   message("")

   message("Saving tables to module output folder...")
   buildSummaryTables( parametricPvals, level, statsFileSuffix( TRUE, FALSE ) )
   buildSummaryTables( nonParametricPvals, level, statsFileSuffix( FALSE, FALSE ) )
   buildSummaryTables( rSquaredVals, level, statsFileSuffix() )
   buildSummaryTables( adjParDF, level, statsFileSuffix( TRUE ) )
   buildSummaryTables( adjNonParDF, level, statsFileSuffix( FALSE ) )
   message("Done saving tables!")

}

buildSummaryTables <- function( table, level, fileNameTag ) {
   df=data.frame("OTU"=row.names(table), table, check.names=FALSE)
   fileName = getPath( getOutputDir(), paste0( level, "_", fileNameTag ) )
   message( paste( "Saving output file:", fileName ) )
   write.table( df, file=fileName, sep="\t", row.names=FALSE )
}

# P Adjust Length depends on how many p-values to include in the adjustment
# LOCAL adjusts p-values for all OTUs for 1 attribute at 1 taxonomy level
# TAXA adjusts p-values for a all OTUs and all attributes at 1 taxonomy level
# ATTRIBUTE adjusts p-values for a all OTUs and all taxonomy levels for 1 attribute
# GLOBAL adjusts p-values for a all OTUs and all attributes and all taxonomy levels
getP_AdjustLen <- function( scopeName, testFeatures ) {
   if( scopeName == "GLOBAL" ) {
      return( length(testFeatures) * length( taxaLevels() ) * length( getReportFields() ) )
   } else if( scopeName == "ATTRIBUTE" ) {
      return( length(testFeatures) * length( taxaLevels() ) )
   } else if( scopeName == "TAXA" ) {
      return( length(testFeatures) * length(getReportFields()) )
   } else {
      return( length(testFeatures) )
   } 
}

# Verify all groups have 2+ values to avoid statistical test failures
everyGroupHasData <- function( merged, feature, field, vals )  {
   for( val in vals ) {
      hasTaxa = !is.na( merged[, feature] )
      hasVal = as.vector( merged[,field] ) == val
      if( length( which( hasTaxa & hasVal == TRUE) ) < 1 ) {
         message( paste( "Skip statistical test for", feature, "no data for", field, "=", val ) )
         return( FALSE )
      }
   }
   return( TRUE )
}

# Method wilcox_test is from the coin package
# Calculates exact p-values without using heuristic algorithm for better precision
# Otherwise if ties are found the script may fail
do_wilcox_test <- function( countTable, feature, att, vals ) {
   x = countTable[att==vals[1], feature]
   y = countTable[att==vals[2], feature]
   values = c(x, y)
   group = as.factor( c( rep( "x", length(x)), rep( "y", length(y)) ) )
   return( coin::wilcox_test( values ~ group ) )
}

message("Running R version: ", R.Version()$version.string)
message("Current Working directory: ",getwd())

args = commandArgs(trailingOnly = TRUE)
message("all args: ", paste(args,sep=", ") )

source("../resources/BioLockJ_Lib.R")
importLibs( c( "coin", "Kendall" ) )

main(level = args[1],
     countsFile = args[2],
     metadataFile = args[3])

message("")
message("Done!")
message("")

sessionInfo()
