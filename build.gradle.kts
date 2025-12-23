import nl.littlerobots.vcu.plugin.resolver.VersionSelectors

plugins { alias(libs.plugins.versions) }

versionCatalogUpdate {
  versionSelector(VersionSelectors.STABLE)
  keep { keepUnusedVersions = false }
}
