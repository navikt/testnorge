import Request from './services/Request'
import { DollyApi } from './Api'

const ConfigService = {
	fetchConfig() {
		return DollyApi.getConfig()
			.then(res => {
				window.dollyConfig = res.data
			})
			.catch(err => {
				console.warn('Dolly config er ikke satt!')
				return err
			})
	},
	verifyConfig() {
		return Boolean(window.dollyConfig)
	},
	getDatesourceUrl(dataSource) {
		if (!ConfigService.verifyConfig()) return undefined
		const dollyConfig = window.dollyConfig
		switch (dataSource) {
			case 'tpsf':
				return dollyConfig.tpsfUrl
			default:
				return undefined
		}
	}
}

export default ConfigService
