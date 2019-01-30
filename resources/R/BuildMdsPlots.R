# Module script for: biolockj.module.r.BuildMdsPlots

# Import vegan library for distance plot support
# Main function generates 3 MDS plots for each attribute at each level in taxaLevels()
main <- function() { 
	importLibs( c( "vegan" ) )
	numAxis = getProperty("rMds.numAxis")
	mdsAtts = getProperty( "rMds.reportFields", c( getBinaryFields(), getNominalFields() )  )
	
	for( level in taxaLevels() ) {
		if( doDebug() ) sink( file.path( getModuleDir(), "temp", paste0("debug_BuildMdsPlots_", level, ".log") ) )
		
		taxaTable = getTaxaTable( level )
		if( is.null( taxaTable ) ) { next }
		myMDS = capscale( taxaTable~1, distance=getProperty("rMds.distance") )
		
		metaTable = getMetaData( level )
		metaColColors = getColorsByCategory( metaTable )
		
		pcoaFileName = paste0( getPath( file.path(getModuleDir(), "temp"), paste0(level, "_pcoa") ), ".tsv" )
		write.table( cbind(id=row.names(myMDS$CA$u), as.data.frame(myMDS$CA$u)), file=pcoaFileName, col.names=TRUE, row.names=FALSE, sep="\t" )
		logInfo( "Save PCoA table", pcoaFileName )
		
		eigenFileName = paste0( getPath( file.path(getModuleDir(), "temp"), paste0(level, "_eigenValues") ), ".tsv" )
		write.table( data.frame(mds=names(myMDS$CA$eig), eig=myMDS$CA$eig), file=eigenFileName, col.names=FALSE, row.names=FALSE, sep="\t")
		logInfo( "Save Eigen value table", pcoaFileName )
		
		# Make plots
		outputFile = paste0( getPath( file.path(getModuleDir(), "output"), paste0(level, "_MDS.pdf" ) ) )
		
		if (numAxis < 4 ) {
			pdf( outputFile, width = 7, height = 7)
			par( mfrow=c(2, 2) )
		}else{
			pdf( outputFile, paper="letter", width=7, height=10.5 )
			par( mfrow=c(3, 2) )
		}
		par(las=1, oma=c(2,1,2,1), mar=c(5, 4, 2, 2))
		percentVariance = as.numeric(eigenvals(myMDS)/sum( eigenvals(myMDS) ) ) * 100
		
		#for (attName in mdsAtts){
		for(i in 1:length(mdsAtts) ){
			attName = mdsAtts[i]
			metaColVals = as.character(metaTable[,attName])
			par(mfrow = par("mfrow"))
			att = as.factor(metaColVals)
			colorKey = metaColColors[[attName]]
			logInfo( c( "Using colors: ", paste(colorKey, "for", names(colorKey), collapse= ", ")) )
			position = 1
			pageNum = 1
			numAxis = min(c(numAxis, ncol(myMDS$CA$u)))
			for (x in 1:(numAxis-1)) {
				for (y in (x+1):numAxis) {
					if (position > prod(par("mfrow") ) ) {
						position = 1
						pageNum = pageNum + 1
					}
					pch=getProperty("r.pch", 20)
					plot( myMDS$CA$u[,x], myMDS$CA$u[,y], main=paste("MDS [", x, "vs", y, "]" ),
								xlab=getMdsLabel( x, percentVariance[x] ),
								ylab=getMdsLabel( y, percentVariance[y] ),
								cex=1.2, pch=pch, col=colorKey[metaColVals] )
					position = position + 1
					if ( position == 2 ){ 
						logInfo( "metaColVals", metaColVals )
						logInfo( "mdsAtts", mdsAtts )
						# put this plot at the upper right position
						# that puts the legend in a nice white space, and it makes axis 1 in line with itself in two plots (same for axis3)
						plotRelativeVariance(percentVariance, numAxis)
						legend(x="topright", title=attName,
									 legend = paste0(names(colorKey), " (n=", table(metaColVals)[names(colorKey)], ")"), 
									 col=colorKey, pch=pch, bty="n")
						addPageFooter(paste("Multidimensional Scaling Plots - colored by", attName), level, pageNum)
					}
				}
			}
		}
		plotPlainText( paste( " Each", paste0(pageNum, "-page"), "set is identical.\n Only the color scheme/legend changes." ), 0.8)
		if( doDebug() ) sink()
		dev.off()
	}
}

getMdsLabel <- function( axisNum, variance ) { 
	return( paste0("Axis ", axisNum, " (", paste0( round( variance ), "%)" ) ) )
}

plotRelativeVariance <- function(percentVariance, numAxis){
	numBars = min(c(length(percentVariance), 6)) # arbitrary choice, don't show more than 6
	numBars = max(numBars, numAxis)
	heights = percentVariance[1:numBars]
	bp = barplot(heights, col="dodgerblue1", ylim=c(0,100), names=1:numBars,
							 xlab="Axis", ylab="Variance" )
	labels = round(heights)
	near0 = which(labels < 1)
	labels[near0] = "<1"
	text(x=bp, y=heights, labels = paste(labels, "%"), pos=3, xpd=TRUE)
}
