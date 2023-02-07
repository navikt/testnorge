import DollyService from '@/service/services/dolly/DollyService'
import TpsfService from '@/service/services/tpsf/TpsfService'
import SigrunService from '@/service/services/sigrun/SigrunService'
import KrrService from '@/service/services/krr/KrrService'
import ArenaService from '@/service/services/arena/ArenaService'
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
import SkjermingService from '@/service/services/skjerming/SkjermingService'

export const DollyApi = DollyService
export const TpsfApi = TpsfService
export const TpsMessagingApi = TpsMessagingService
export const SigrunApi = SigrunService
export const KrrApi = KrrService
export const ArenaApi = ArenaService
export const PensjonApi = PensjonService
export const InntektstubApi = InntektstubService
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
