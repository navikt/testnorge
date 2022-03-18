import DollyService from './services/dolly/DollyService'
import ProfilService from './services/profil/ProfilService'
import TpsfService from './services/tpsf/TpsfService'
import SigrunService from './services/sigrun/SigrunService'
import KrrService from './services/krr/KrrService'
import ArenaService from './services/arena/ArenaService'
import InstService from './services/inst/InstService'
import UdiService from './services/udi/UdiService'
import PensjonService from './services/pensjon/PensjonService'
import AaregService from './services/aareg/AaregService'
import InntektstubService from './services/inntektstub/InntektstubService'
import Norg2Service from './services/norg2/Norg2Service'
import BrregstubService from './services/brregstub/BrregstubService'
import VarslingerService from './services/varslinger/VarslingerService'
import OrganisasjonForvalterService from '~/service/services/organisasjonforvalter/OrganisasjonForvalterService'
import OrganisasjonService from '~/service/services/organisasjonservice/OrganisasjonService'
import MiljoeService from '~/service/services/miljoe/MiljoeService'
import PersonOrganisasjonTilgangService from '~/service/services/personOrganisasjonTilgang/PersonOrganisasjonTilgangService'
import BrukerService from '~/service/services/bruker/BrukerService'
import PdlForvalterService from '~/service/services/pdl/PdlForvalterService'
import TpsMessagingService from '~/service/services/tpsmessaging/TpsMessagingService'
import SessionService from '~/service/services/session/SessionService'

export const DollyApi = DollyService
export const ProfilApi = ProfilService
export const TpsfApi = TpsfService
export const TpsMessagingApi = TpsMessagingService
export const SigrunApi = SigrunService
export const KrrApi = KrrService
export const ArenaApi = ArenaService
export const InstApi = InstService
export const UdiApi = UdiService
export const PensjonApi = PensjonService
export const AaregApi = AaregService
export const InntektstubApi = InntektstubService
export const Norg2Api = Norg2Service
export const BrregstubApi = BrregstubService
export const VarslingerApi = VarslingerService
export const OrgforvalterApi = OrganisasjonForvalterService
export const OrgserviceApi = OrganisasjonService
export const MiljoeApi = MiljoeService
export const PersonOrgTilgangApi = PersonOrganisasjonTilgangService
export const BrukerApi = BrukerService
export const PdlforvalterApi = PdlForvalterService
export const SessionApi = SessionService

export default {
	DollyApi: DollyService,
	ProfilApi: ProfilService,
	TpsfApi: TpsfService,
	SigrunApi: SigrunService,
	KrrApi: KrrService,
	ArenaApi: ArenaService,
	InstApi: InstService,
	UdiApi: UdiService,
	PensjonApi: PensjonService,
	AaregApi: AaregService,
	InntektstubApi: InntektstubService,
	Norg2Api: Norg2Service,
	BrregstubApi: BrregstubService,
	VarslingerApi: VarslingerService,
	OrgforvalterApi: OrganisasjonForvalterService,
	OrgserviceApi: OrganisasjonService,
	MiljoeApi: MiljoeService,
	PersonOrgTilgangApi: PersonOrganisasjonTilgangService,
	BrukerApi: BrukerService,
	PdlforvalterApi: PdlForvalterService,
	SessionApi: SessionService,
}
