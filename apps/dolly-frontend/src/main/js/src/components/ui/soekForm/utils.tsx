import { codeToNorskLabel } from '@/utils/DataFormatter'

export const getLabel = (value: any, lagreSoekRequest: any, path: string, label: string) => {
	const liste = lagreSoekRequest[path] ?? []
	const existingOption = liste?.find((i) => i.value === value)
	if (existingOption) {
		return existingOption.label
	} else if (label?.includes(':')) {
		return label
	}
	return `${label}: ${value.label ?? codeToNorskLabel(value)}`
}
