import { useHentLagredeSoek } from '@/utils/hooks/useSoek'
import { Chips, VStack } from '@navikt/ds-react'
import * as _ from 'lodash-es'
import { codeToNorskLabel } from '@/utils/DataFormatter'
import { isDate, isSameDay } from 'date-fns'

export enum soekType {
	dolly = 'DOLLY',
	tenor = 'TENOR',
}

const listOptions = [
	'registreRequest',
	'utenlandskPersonIdentifikasjon',
	'roller',
	'inntekt.inntektstyper',
	'inntekt.forskuddstrekk',
]

export const SisteSoek = ({
	type,
	formValues,
	handleChange,
	handleChangeAdresse,
	handleChangeList,
}) => {
	const { lagredeSoek, loading, error } = useHentLagredeSoek(type)
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
		} else if (path === 'miljoer') {
			return (
				formValue?.length === value.length &&
				formValue?.every((item) => value?.map((v) => v.value).includes(item))
			)
		} else if (formValue?.length > 8 && isDate(new Date(formValue))) {
			return isSameDay(new Date(formValue), new Date(value))
		}
		return formValue === value
	}

	const handleClick = (option) => {
		// console.log('option: ', option) //TODO - SLETT MEG
		// console.log('formValues: ', formValues) //TODO - SLETT MEG
		// console.log("option.path?.split('.'): ", option.path?.split('.')) //TODO - SLETT MEG
		//TODO: Handleclick generell for lister?

		if (listOptions.includes(option.path)) {
			const listValues = _.get(formValues, option.path) || []
			// console.log('listValues: ', listValues) //TODO - SLETT MEG
			handleChangeList(
				!listValues?.includes(option.value)
					? [...listValues, { value: option.value, label: codeToNorskLabel(option.value) }]
					: listValues?.filter((item) => item !== option.value),
				option.path,
				//TODO: Label
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
				//TODO: Label
			)
		} else if (option.path.includes('adresse') || option.path.includes('harDeltBosted')) {
			const pathArray = option.path?.split('.')
			handleChangeAdresse(
				_.get(formValues, option.path) !== option.value ? option.value : null,
				pathArray[pathArray.length - 1],
			)
		} else {
			const splitPath = option.path?.split('.')
			handleChange(
				_.get(formValues, option.path) !== option.value ? option.value : null,
				// option.path?.split('.')[1].trim(),
				splitPath[splitPath.length - 1]?.trim(),
			)
		}
	}

	//TODO: Denne ser ut til aa funke naa. Gjoer det samme paa handleClick?
	const handleClickTenor = (option) => {
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
			{/*<h4>SISTE SØK:</h4>*/}
			<Chips>
				{lagredeSoekData?.slice(0, 10).map((option, idx) => (
					<Chips.Toggle
						key={option.label}
						selected={isSelected(option)}
						onClick={() =>
							type === soekType.tenor ? handleClickTenor(option) : handleClick(option)
						}
					>
						{option.label}
					</Chips.Toggle>
				))}
			</Chips>
		</VStack>
	)
}
