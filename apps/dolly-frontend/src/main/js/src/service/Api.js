import DollyService from './services/dolly/DollyService'
import TpsfService from './services/tpsf/TpsfService'
import SigrunService from './services/sigrun/SigrunService'
import KrrService from './services/krr/KrrService'
import ArenaService from './services/arena/ArenaService'
import PensjonService from './services/pensjon/PensjonService'
import InntektstubService from './services/inntektstub/InntektstubService'
import Norg2Service from './services/norg2/Norg2Service'
import BrregstubService from './services/brregstub/BrregstubService'
import VarslingerService from './services/varslinger/VarslingerService'
import OrganisasjonForvalterService from '~/service/services/organisasjonforvalter/OrganisasjonForvalterService'
import OrganisasjonService from '~/service/services/organisasjonservice/OrganisasjonService'
import PersonOrganisasjonTilgangService from '~/service/services/personOrganisasjonTilgang/PersonOrganisasjonTilgangService'
import BrukerService from '~/service/services/bruker/BrukerService'
import PdlForvalterService from '~/service/services/pdl/PdlForvalterService'
import TpsMessagingService from '~/service/services/tpsmessaging/TpsMessagingService'
import SessionService from '~/service/services/session/SessionService'
import KontoregisterService from '~/service/services/kontoregister/KontoregisterService'
import SkjermingService from '~/service/services/skjerming/SkjermingService'

export const DollyApi = DollyService
export const TpsfApi = TpsfService
export const TpsMessagingApi = TpsMessagingService
export const SigrunApi = SigrunService
export const KrrApi = KrrService
export const ArenaApi = ArenaService
export const PensjonApi = PensjonService
export const InntektstubApi = InntektstubService
export const Norg2Api = Norg2Service
export const BrregstubApi = BrregstubService
export const VarslingerApi = VarslingerService
export const OrgforvalterApi = OrganisasjonForvalterService
export const OrgserviceApi = OrganisasjonService
export const PersonOrgTilgangApi = PersonOrganisasjonTilgangService
export const BrukerApi = BrukerService
export const PdlforvalterApi = PdlForvalterService
export const SessionApi = SessionService
export const BankkontoApi = KontoregisterService
export const SkjermingApi = SkjermingService

export default {
	DollyApi: DollyService,
	TpsfApi: TpsfService,
	SigrunApi: SigrunService,
	KrrApi: KrrService,
	ArenaApi: ArenaService,
	PensjonApi: PensjonService,
	InntektstubApi: InntektstubService,
	Norg2Api: Norg2Service,
	BrregstubApi: BrregstubService,
	VarslingerApi: VarslingerService,
	OrgforvalterApi: OrganisasjonForvalterService,
	OrgserviceApi: OrganisasjonService,
	PersonOrgTilgangApi: PersonOrganisasjonTilgangService,
	BrukerApi: BrukerService,
	PdlforvalterApi: PdlForvalterService,
	SessionApi: SessionService,
	BankkontoApi: KontoregisterService,
	SkjermingApi: SkjermingService,
}
