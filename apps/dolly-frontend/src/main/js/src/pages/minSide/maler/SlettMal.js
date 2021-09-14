import { api } from './api'

export const slettMal = (malId, setMaler) => {
	return api.slettMal(malId).then(() => {
		setMaler((maler) => maler.filter((mal) => mal.id !== malId))
	})
}
