import { DollyApi } from './Api'
import config from "~/config";

const ConfigService = {
	fetchConfig() {
		return DollyApi.getConfig().then(res => {
			window.dollyConfig = res.data
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
			case 'sigrun':
				return dollyConfig.sigrunStubUrl
			case 'krr':
				return dollyConfig.krrStubUrl
			case 'arena':
				return dollyConfig.arenaForvalterUrl
			case 'inst':
				return dollyConfig.instdataUrl
			case 'udi':
				return dollyConfig.udiStubUrl
			case 'fasteData':
				return config.services.testnorgeStatiskForvalter
			default:
				return undefined
		}
	}
}

export default ConfigService
