import { Environment } from './Types'

const generateEnvironment = (label: string, start: number, length: number): Environment[] => {
	const envList = []

	for (let i = start; i <= length; i++) {
		envList.push({
			id: `${label + i}`,
			label: `${label + i}`.toUpperCase()
		})
	}
	return envList
}

export default class EnvironmentManager {
	getAllEnvironments(): Environment[] {
		const UList = generateEnvironment('u', 6, 6)
		const TList = generateEnvironment('t', 0, 13)
		const QList = generateEnvironment('q', 0, 11)

		const envs = [...UList, ...TList, ...QList]

		// TEMP: Set all as disabled except u6
		const envsWithDisabled = envs.map(f => {
			// if (f.id === 'u6') return f

			if (f.id.includes('t')) return f

			return {
				...f,
				disabled: true
			}
		})

		return envsWithDisabled
	}
}
