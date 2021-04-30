# BioLockJ API                                      
                                      
BioLockJ comes with an API.                                      
                                      
For the most up-to-date information about how to use the API, see the help menu:                                      
                   
                   
`biolockj-api help`                   
                   
```                   
CMD:java -cp /Users/ieclabau/git/BioLockJ/dist/BioLockJ.jar biolockj.api.BioLockJ_API help                   
```                   
                   
`listModules` and `listApiModules` are nearly identical.  The methods that allow the API to interface with modules are in the ApiModule interface, not all BioModules implement that interface.  Once all of the build-in modules have those methods, then these two functions will be identical; the BioModule interface will absorb the ApiModule interface, and listApiModules will be depricated.                   
                   
`listAllProps` is the union of all possible output from `listProps`.                   
                   
`propInfo` returns information equivelent to calling `biolockj_api listProps` and creating a for-loop to call `biolockj_api propType $PROP`, `biolockj_api describeProp $PROP` and `biolockj_api propValue $PROP` for each PROP in the list.                   
                   
`moduleInfo` returns information equivelent to calling `biolockj_api listModules` and creating a for-loop to call `biolockj_api listProps $MODULE` and for each of its properties.                   
