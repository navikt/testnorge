import { useHentLagredeSoek } from '@/utils/hooks/useSoek'
import { Chips, VStack } from '@navikt/ds-react'
import * as _ from 'lodash-es'
import { codeToNorskLabel } from '@/utils/DataFormatter'

export enum soekType {
	dolly = 'DOLLY',
	tenor = 'TENOR',
}

export const SisteSoek = ({
	soekType,
	formValues,
	handleChange,
	handleChangeAdresse,
	handleChangeList,
}) => {
	const { lagredeSoek, loading, error } = useHentLagredeSoek(soekType)
	const lagredeSoekData = []
	lagredeSoek?.forEach((soek) => {
		Object.entries(soek?.soekVerdi)?.forEach((verdi) => {
			if (verdi[0] === 'registreRequest' && Array.isArray(verdi[1])) {
				verdi[1]?.forEach((item) => {
					if (!lagredeSoekData?.some((i) => i.value === item.value)) {
						lagredeSoekData.push(item)
					}
				})
			} else if (verdi[1]?.path && !lagredeSoekData?.some((item) => item.path === verdi[1]?.path)) {
				lagredeSoekData.push(verdi[1])
			}
		})
	})

	const isSelected = (option) => {
		const { path, value } = option
		const formValue = _.get(formValues, path)
		if (path === 'registreRequest') {
			return formValue?.includes(value)
		} else if (path === 'miljoer') {
			return (
				formValue.length === value.length &&
				formValue.every((item) => value?.map((v) => v.value).includes(item))
			)
		}
		return formValue === value
	}

	const handleClick = (option) => {
		if (option.path === 'registreRequest') {
			const registreValues = _.get(formValues, option.path) || []
			handleChangeList(
				!registreValues?.includes(option.value)
					? [...registreValues, { value: option.value, label: codeToNorskLabel(option.value) }]
					: registreValues?.filter((item) => item !== option.value),
				option.path,
			)
		} else if (option.path === 'miljoer') {
			const miljoerValues = _.get(formValues, option.path) || []
			const miljoerErLike =
				miljoerValues?.length === option.value?.length &&
				miljoerValues?.every((v) => option.value?.map((i) => i.value).includes(v))
			handleChangeList(
				miljoerErLike
					? miljoerValues?.filter((i) => option.value?.map((v) => v.value) === i.value)
					: option.value,
				option.path,
			)
		} else if (option.path.includes('adresse') || option.path.includes('harDeltBosted')) {
			const pathArray = option.path?.split('.')
			handleChangeAdresse(
				_.get(formValues, option.path) !== option.value ? option.value : null,
				pathArray[pathArray.length - 1],
			)
		} else {
			handleChange(
				_.get(formValues, option.path) !== option.value ? option.value : null,
				option.path?.split('.')[1].trim(),
			)
		}
	}

	return (
		<VStack gap="3" style={{ marginBottom: '15px' }}>
			{/*<h4>SISTE SØK:</h4>*/}
			<Chips>
				{lagredeSoekData?.slice(0, 10).map((option, idx) => (
					<Chips.Toggle
						key={option.label}
						selected={isSelected(option)}
						onClick={() => handleClick(option)}
					>
						{option.label}
					</Chips.Toggle>
				))}
			</Chips>
		</VStack>
	)
}
