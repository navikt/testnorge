/*
	Legg til flere services fra andre registre etterhvert
*/
import DollyService from './services/dolly/DollyService'
import TpsfService from './services/tpsf/TpsfService'

export const DollyApi = DollyService
export const TpsfApi = TpsfService

export default {
	DollyApi: DollyService,
	TpsfApi: TpsfService
}
