/*
	Legg til flere services fra andre registre etterhvert
*/
import DollyService from './services/dolly/DollyService'
import TpsfService from './services/tpsf/TpsfService'
import SigrunService from './services/sigrun/SigrunService'
import KrrService from './services/krr/KrrService'
import ArenaService from './services/arena/ArenaService'
import Inst2Service from './services/inst2/Inst2Service'

export const DollyApi = DollyService
export const TpsfApi = TpsfService
export const SigrunApi = SigrunService
export const KrrApi = KrrService
export const ArenaApi = ArenaService
export const Inst2Api = Inst2Service

export default {
	DollyApi: DollyService,
	TpsfApi: TpsfService,
	SigrunApi: SigrunService,
	KrrApi: KrrService,
	ArenaApi: ArenaService,
	Inst2Api: Inst2Service
}
