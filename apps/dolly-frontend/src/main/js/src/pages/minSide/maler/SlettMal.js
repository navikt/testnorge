import { malerApi } from './MalerApi'

export const slettMal = (malId, setMaler) => {
	return malerApi.slettMal(malId).then(() => {
		setMaler((maler) => maler.filter((mal) => mal.id !== malId))
	})
}
