import DollyService from '@/service/services/dolly/DollyService'
import KrrService from '@/service/services/krr/KrrService'
import PensjonService from '@/service/services/pensjon/PensjonService'
import InntektstubService from '@/service/services/inntektstub/InntektstubService'
import BrregstubService from '@/service/services/brregstub/BrregstubService'
import VarslingerService from '@/service/services/varslinger/VarslingerService'
import OrganisasjonForvalterService from '@/service/services/organisasjonforvalter/OrganisasjonForvalterService'
import OrganisasjonService from '@/service/services/organisasjonservice/OrganisasjonService'
import PersonOrganisasjonTilgangService from '@/service/services/personOrganisasjonTilgang/PersonOrganisasjonTilgangService'
import BrukerService from '@/service/services/bruker/BrukerService'
import PdlForvalterService from '@/service/services/pdl/PdlForvalterService'
import TpsMessagingService from '@/service/services/tpsmessaging/TpsMessagingService'
import SessionService from '@/service/services/session/SessionService'
import KontoregisterService from '@/service/services/kontoregister/KontoregisterService'
import KodeverkService from '@/service/services/kodeverk/KodeverkService'
import OrganisasjonTilgangService from '@/service/services/organisasjonTilgang/OrganisasjonTilgangService'

export const DollyApi = DollyService
export const TpsMessagingApi = TpsMessagingService
export const KrrApi = KrrService
export const PensjonApi = PensjonService
export const InntektstubApi = InntektstubService
export const BrregstubApi = BrregstubService
export const VarslingerApi = VarslingerService
export const OrgforvalterApi = OrganisasjonForvalterService
export const PersonOrgTilgangApi = PersonOrganisasjonTilgangService
export const BrukerApi = BrukerService
export const PdlforvalterApi = PdlForvalterService
export const SessionApi = SessionService
export const BankkontoApi = KontoregisterService
export const KodeverkApi = KodeverkService

export default {
	DollyApi: DollyService,
	KrrApi: KrrService,
	PensjonApi: PensjonService,
	InntektstubApi: InntektstubService,
	BrregstubApi: BrregstubService,
	VarslingerApi: VarslingerService,
	OrgforvalterApi: OrganisasjonForvalterService,
	OrgserviceApi: OrganisasjonService,
	OrgTilgangApi: OrganisasjonTilgangService,
	PersonOrgTilgangApi: PersonOrganisasjonTilgangService,
	BrukerApi: BrukerService,
	PdlforvalterApi: PdlForvalterService,
	SessionApi: SessionService,
	BankkontoApi: KontoregisterService,
}
