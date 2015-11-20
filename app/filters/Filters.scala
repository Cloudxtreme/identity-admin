package filters

import javax.inject.Inject

import play.api.http.HttpFilters
import play.filters.csrf.CSRFFilter
import play.filters.headers.SecurityHeadersFilter

class Filters @Inject() (securityHeadersFilter: SecurityHeadersFilter, csrfFilter: CSRFFilter) extends HttpFilters {
  def filters = Seq(securityHeadersFilter, csrfFilter, StrictTransportSecurityHeaderFilter)
}
