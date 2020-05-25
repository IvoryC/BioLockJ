
Normalize taxa tables based on formula:

$$ counts_{normalized} = \frac{counts_{raw}}{n} \frac{\sum (x)}{N} +1 $$

Where:                             			

* \( counts_{raw} \) = raw count; the cell value before normalizing 
* \( n \) = number of sequences in the sample (total within a sample)
* \( \sum (x) \) = total number of counts in the table (total across samples)
* \( N \) = total number of samples



Typically the data is put on a \( Log_{10} \) scale, so the full forumula is:

$$ counts_{final} = Log_{10} \biggl( \frac{counts_{raw}}{n} \frac{\sum (x)}{N} +1 \biggr) $$

The \( counts_{final} \) values will be in output dir of the `LogTransformTaxaTables` module.  The \( counts_{normalized} \) values will be in the output of the `NormalizeTaxaTables` module.


For further explanation regarding the normalization scheme, please read The ISME Journal 2013 paper by Dr. Anthony Fodor: ["Stochastic changes over time and not founder effects drive cage effects in microbial community assembly in a mouse model"](https://www.ncbi.nlm.nih.gov/pmc/articles/PMC3806260/)

If _report.logBase_ is not null, then the `LogTransformTaxaTables` will be added as a post-requisite module.
