package biolockj.module.seq;

import biolockj.module.BioModule;

/**
 * @Deprecated - specify sequence output types using {@link biolockj.module.io.ModuleIO#getOutputTypes()}
 * Indicates a module that produce sequence files as their output, 
 * thus making it a valid input module for any SeqModule.
 * @author ieclabau
 *
 */
@Deprecated
public interface SequenceOutputModule extends BioModule {

}
