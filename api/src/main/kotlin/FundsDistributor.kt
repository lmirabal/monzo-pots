package lmirabal

import lmirabal.model.DistributionManifest

interface FundsDistributor {
    fun distribute(manifest: DistributionManifest)
}