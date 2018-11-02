/*
	Legg til flere services fra andre registre etterhvert
*/
import DollyService from './services/dolly/DollyService'
import TpsfService from './services/tpsf/TpsfService'
import SigrunService from './services/sigrun/SigrunService'

export const DollyApi = DollyService
export const TpsfApi = TpsfService
export const SigrunApi = SigrunService

export default {
	DollyApi: DollyService,
	TpsfApi: TpsfService,
	SigrunApi: SigrunService
}
