import { useHentLagredeSoek } from '@/utils/hooks/useSoek'
import { Chips, VStack } from '@navikt/ds-react'
import * as _ from 'lodash-es'
import { isDate, isSameDay, isValid } from 'date-fns'

export enum soekType {
	dolly = 'DOLLY',
	tenor = 'TENOR',
}

const listOptions = [
	'registreRequest',
	'miljoer',
	'utenlandskPersonIdentifikasjon',
	'roller',
	'inntekt.inntektstyper',
	'inntekt.forskuddstrekk',
]

export const SisteSoek = ({ type, formValues, handleChange, handleChangeList }) => {
	const { lagredeSoek } = useHentLagredeSoek(type)

	const lagredeSoekData = []
	lagredeSoek?.forEach((soek) => {
		Object.entries(soek?.soekVerdi)?.forEach((verdi) => {
			if (listOptions.includes(verdi[0]) && Array.isArray(verdi[1])) {
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
		if (listOptions.includes(path)) {
			return formValue?.includes(value)
		} else if (
			formValue?.length > 8 &&
			isDate(new Date(formValue)) &&
			isValid(new Date(formValue))
		) {
			return isSameDay(new Date(formValue), new Date(value))
		}
		return formValue === value
	}

	const handleClick = (option) => {
		const formValue = _.get(formValues, option.path)
		if (listOptions.includes(option.path)) {
			const listValues = formValue || []
			handleChangeList(
				!listValues?.includes(option.value)
					? [...listValues, option.value]
					: listValues?.filter((item) => item !== option.value),
				option.path,
				option.label,
			)
		} else if (formValue?.length > 8 && isDate(new Date(formValue))) {
			handleChange(
				!isSameDay(new Date(formValue), new Date(option.value)) ? option.value : null,
				option.path,
				option.label,
			)
		} else {
			handleChange(formValue !== option.value ? option.value : null, option.path, option.label)
		}
	}

	return (
		<VStack gap="3" style={{ marginBottom: '15px' }}>
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
